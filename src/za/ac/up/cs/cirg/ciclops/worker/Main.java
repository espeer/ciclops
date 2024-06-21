/*
 * Copyright (C) 2004 - Edwin Peer
 * 
 * Created on Feb 10, 2004
 */
package za.ac.up.cs.cirg.ciclops.worker;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import za.ac.up.cs.cirg.ciclops.cilib.SimulationWrapper;
import za.ac.up.cs.cirg.ciclops.model.DataSetCacheEntry;
import za.ac.up.cs.cirg.ciclops.model.WorkerModel;
import za.ac.up.cs.cirg.ciclops.services.JobNotFoundException;
import za.ac.up.cs.cirg.ciclops.services.Supervisor;

/**
 * @author espeer
 *
 */
public class Main {
	
	public static void main(String[] args) throws Exception {
		String vm = /*System.getProperty("java.vm.vendor") + " " + */ System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version");
		String os = System.getProperty("os.name") + " " /* + System.getProperty("os.arch") + " " */ + System.getProperty("os.version");
		
		InetAddress addy = InetAddress.getLocalHost();
		Enumeration e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface iface = (NetworkInterface) e.nextElement();
			Enumeration ee = iface.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress tmp = (InetAddress) ee.nextElement();
				if (! tmp.isLoopbackAddress()) {
					addy = tmp;
				}
			}
		}
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "no");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        while (true) { 
        	try {
        		ServerContext context = ServerContext.instance();
        		context.login(new URL("http://ciclops.cs.up.ac.za:8080/CiClops/"));  // TODO: Should be set in property file		
        		Supervisor supervisor = context.getSupervisorHome().create(new WorkerModel(addy.getCanonicalHostName(), os, vm)); 
        
        		runSimulations(transformer, documentBuilder, context, supervisor);
        	}
        	catch (Exception ex) {
        		ex.printStackTrace();
        		System.err.println(ex.getClass().getName() + ": " + ex.getMessage());
        		System.out.println("Server error... Sleeping...");
        		try {
        			Thread.sleep(30000);
        		}
        		catch (InterruptedException x) { }
        	}
		}
	}
	
	private static void runSimulations(Transformer transformer, DocumentBuilder documentBuilder, ServerContext context, Supervisor supervisor) throws CreateException, RemoteException, RemoveException, LoginException {
		try {
			System.out.print("Working.");
			while (true) {
				try {
					System.out.print(".");
			
					Document configuration = documentBuilder.newDocument();
					transformer.transform(new StreamSource(new StringReader(supervisor.getJob())), new DOMResult(configuration));

					ClassLoader loader = new RemoteClassLoader();
					SimulationWrapper simulation = new SimulationWrapper(loader, configuration);
			
					String dataPath = "/home/ciclops/cache"; // TODO: Should be set in property file
					setupCache(supervisor, dataPath);
					simulation.setDataPath(dataPath); 
					
					simulation.initialise();
					simulation.run();
		
					supervisor.storeResults(simulation.getSampleData());
				}
				catch (JobNotFoundException ex) {
					System.out.println();
					System.out.println("Nothing to do... Sleeping...");
					try {
						Thread.sleep(15000);
					}
					catch (InterruptedException x) { }
					System.out.print("Working.");
				}
				catch (OutOfMemoryError ex) {
					try {
						supervisor.remove();
						context.logout();
						System.exit(1);
					}
					catch (Throwable t) {
						System.exit(10);
					}
					
				}
				catch (Throwable t) {
					supervisor.handleError(t);
				}
			}
		}
		finally {
			System.out.println();
			supervisor.remove();
			context.logout();
		}
	}

	private static void setupCache(Supervisor supervisor, String dataPath) throws ClassNotFoundException, RemoteException, FinderException, FileNotFoundException, IOException {
		HashMap cache = null;
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataPath + "/data_set.cache"));
			cache = (HashMap) ois.readObject();
		}
		catch (IOException ex) {
			cache = new HashMap();
		}
			
		Iterator i = supervisor.getDataSetCacheEntries().iterator();
		while (i.hasNext()) {
			DataSetCacheEntry entry = (DataSetCacheEntry) i.next();
			Integer version = (Integer) cache.get(new Integer(entry.getId()));
			if (version == null || version.intValue() != entry.getVersion()) {
				byte[] data = supervisor.getData(entry.getId());
				OutputStream tmp = new BufferedOutputStream(new FileOutputStream(dataPath + "/" + String.valueOf(entry.getId()) + ".ds"));
				tmp.write(data);
				tmp.close();
				cache.put(new Integer(entry.getId()), new Integer(entry.getVersion()));
			}
		}
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataPath + "/data_set.cache"));
		oos.writeObject(cache);
		oos.close();
	}
	
}
