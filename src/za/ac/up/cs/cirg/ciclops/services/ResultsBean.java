/*
 * ResultsBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on November 3, 2004
 */

package za.ac.up.cs.cirg.ciclops.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;

import za.ac.up.cs.cirg.ciclops.cilib.DomainWrapper;
import za.ac.up.cs.cirg.ciclops.domain.CodeHome;
import za.ac.up.cs.cirg.ciclops.domain.CodeUtil;
import za.ac.up.cs.cirg.ciclops.domain.Measurement;
import za.ac.up.cs.cirg.ciclops.domain.Sample;
import za.ac.up.cs.cirg.ciclops.domain.Simulation;
import za.ac.up.cs.cirg.ciclops.domain.SimulationHome;
import za.ac.up.cs.cirg.ciclops.domain.SimulationUtil;

/**
 * 
 * @ejb.bean name="Results"
 *   type="Stateless"
 *   view-type="remote"
 *   jndi-name="ejb/ciclops/services/Results"
 *
 * @ejb.ejb-ref ejb-name="Code" view-type="local"
 * @ejb.ejb-ref ejb-name="Simulation" view-type="local"
 * @ejb.ejb-ref ejb-name="Sample" view-type="local"
 * @ejb.ejb-ref ejb-name="Measurement" view-type="local" 
 * 
 * @ejb.transaction type="Required"
 *
 * @ejb.util generate="physical"
 * 
 * @ejb.permission unchecked="true"
 *
 * @author  espeer
 */
public class ResultsBean implements SessionBean {
    
    /**
     * @ejb.create-method
     */
    public void ejbCreate() throws CreateException {
        try {
            simulationHome = SimulationUtil.getLocalHome();
            codeHome = CodeUtil.getLocalHome();
        }
        catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    /**
     * @ejb.interface-method
     */
    public byte[] getExportResults(int id) throws FinderException {
    	try {
    		EJBClassLoader loader = new EJBClassLoader(codeHome);
    		
    		String newline = System.getProperty("line.separator");
    		
    		Simulation simulation = simulationHome.findByPrimaryKey(new Integer(id));
        
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		OutputStreamWriter osw = new OutputStreamWriter(bos);
        
    		osw.write("# Simulation: " + simulation.getName() + newline);
    		int completed = simulation.getCompletedSamples();
    		int total = simulation.getTotalSamples();
    		osw.write("# Total samples: " + String.valueOf(total));
    		if (total == completed) {
    			osw.write(newline);
    		}
    		else {
    			osw.write(" (" + String.valueOf(completed) + " completed)" + newline);
    		}
    		
    		osw.write("#" + newline + "# Description: " + newline + "# ");
    		osw.write(simulation.getDescription().replaceAll(newline, newline + "# "));
    		osw.write(newline + "# " + newline + "# Configuration:" + newline + "# ");
    		osw.write(simulation.getFormattedXMLConfiguration().replaceAll(newline, newline + "# "));
    		osw.write(newline + "# " + newline);
    		String[] measurements = simulation.getMeasurements();
    		
    		int columns = completed * measurements.length;
    		
    		int rows = 0;
    		String[] domains = new String[columns];
    		Object[] deserialised = new Object[columns];
    		
    		Iterator i = simulation.getSamples().iterator();
    		int s = 0;
    		while (i.hasNext()) {
    			Sample sample = (Sample) i.next();
    			Iterator j = sample.getMeasurements().iterator();
    	   		int m = 0;
    	   		while (j.hasNext()) {
    				Measurement measurement = (Measurement) j.next();
    				ObjectInputStream ois = measurement.getDataStream();
    				int tmp = (completed * m) + s;
    				domains[tmp] = measurement.getDomain();
    				DomainWrapper domain = new DomainWrapper(loader, domains[tmp]);
    				int dimension = domain.getDimension();
    				if (dimension > rows) {
    					rows = dimension;
    				}
    				deserialised[tmp] = domain.deserialise(ois);
					++m;
    			}
    			++s;
    		}
    		
    		int sample = 1;
    		for (int c = 0; c < columns; ++c) {
    			int measurement = c / completed;;
    			osw.write("# Column " + String.valueOf(c + 1) + ": " + measurements[measurement] + " for sample " + String.valueOf(sample) + " with domain " + domains[c] + newline);
    			if (++sample > completed) {
    				sample = 1;
    			}
    		}
    		
    		for (int r = 0; r < rows; ++r) {
    			for (int c = 0; c < columns; ++c) {
    				if (r < Array.getLength(deserialised[c])) {
    					osw.write(Array.get(deserialised[c], r).toString());
    				}
    				if (c != (columns - 1)) {
    					osw.write(',');
    				}
    			}
    			osw.write(newline);
    		}
    		
    		osw.close();
    		return bos.toByteArray();
    	}
    	catch (IOException ex) {
    		throw new EJBException(ex);
    	}
    	catch (InvocationTargetException ex) {
    		throw new EJBException(ex);
    	}
    }
   
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }    
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void setSessionContext(javax.ejb.SessionContext sessionContext) throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    private SimulationHome simulationHome;
    private CodeHome codeHome; 
}
