/*
 * Copyright (C) 2004 - Edwin Peer
 * 
 * Created on Feb 10, 2004
 *
 */
package za.ac.up.cs.cirg.ciclops.worker;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import za.ac.up.cs.cirg.ciclops.services.CodeManager;

/**
 * @author espeer
 *
 */
public class RemoteClassLoader extends ClassLoader {
	
	public RemoteClassLoader() throws NamingException, CreateException, RemoteException {
        codeManager = ServerContext.instance().getCodeManagerHome().create();
	}

    public Class findClass(String name) throws ClassNotFoundException {
        try {
            byte[] b = codeManager.getByteCodeForClass(name);
            return defineClass(name, b, 0, b.length);    
        }
        catch (RemoteException ex) {
            throw new ClassNotFoundException(name);
        }
    }
    

    /*
	protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
		System.out.println("Loading class: " + name);
		Class loaded = findLoadedClass(name);
		
		if (versionMap.containsKey(name)) {
			System.out.println("Checking class version");
			int currentVersion = ((Integer) versionMap.get(name)).intValue();
			try {
				if (codeManager.getVersionOfClass(name) != currentVersion) {
					System.out.println("Got a stale version");
					loaded = null;
				}
			}
			catch (RemoteException ex) {
				throw new ClassNotFoundException(name + " remote exception on version check!!");
			}
		}
		
		if (loaded == null) {
			System.out.println("Failed to find current version (or otherwise) in cache, delegating to parent");
			try {
				loaded = this.getParent().loadClass(name);
			}
			catch (ClassNotFoundException ex) { System.out.println("parent didn't have it either"); }
		}
		
		if (loaded == null) {
			System.out.println("Getting class from remote database");
			try { 
				byte[] b = codeManager.getByteCodeForClass(name);
				System.out.println("Got version " + codeManager.getVersionOfClass(name));
				versionMap.put(name, new Integer(codeManager.getVersionOfClass(name)));
				loaded = defineClass(name, b, 0, b.length);
				System.out.println("Class defined");
			}
			catch (RemoteException ex) {
				System.out.println("Remote Exception caught");
				throw new ClassNotFoundException(name + " " + ex.getMessage());
			}
		}
		
		if (loaded == null) {
			throw new ClassNotFoundException(name + ": just could not find it :(");
		}
		
		if (resolve) {
			resolveClass(loaded);
		}
		
		System.out.println("Returning class: " +name);
		return loaded;
	}
	*/
	private CodeManager codeManager;
}
