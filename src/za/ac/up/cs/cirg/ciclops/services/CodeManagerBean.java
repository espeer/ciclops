/*
 * CodeManagerBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on Frebruary 10, 2004, 12:21 AM
 */

package za.ac.up.cs.cirg.ciclops.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.ejb.SessionBean;

import za.ac.up.cs.cirg.ciclops.domain.Code;
import za.ac.up.cs.cirg.ciclops.domain.CodeHome;
import za.ac.up.cs.cirg.ciclops.domain.CodeUtil;

/**
 * 
 * @ejb.bean name="CodeManager"
 *   type="Stateless"
 *   view-type="both"
 *   jndi-name="ejb/ciclops/services/CodeManager"
 *   local-jndi-name="ejb/ciclops/services/CodeManagerLocal"
 *  
 * @ejb.ejb-ref ejb-name="Code" view-type="local" 
 * 
 * @ejb.transaction type="Required"
 *
 * @ejb.util generate="physical"
 *
 * @author  espeer
 */
public class CodeManagerBean implements SessionBean {
    
    /**
     * @ejb.create-method
     * @ejb.permission unchecked="true"
     */
    public void ejbCreate() throws CreateException {
        try {
            codeHome = CodeUtil.getLocalHome();
            loader = new EJBClassLoader(codeHome);
        }
        catch (Exception ex) {
            throw new EJBException(ex);
        }
    }    
    
    /**
     * @ejb.interface-method 
     * @ejb.permission unchecked="true" 
     */
    public void loadCodeFromJar(URL location) {
    	try {
    		JarURLConnection c = (JarURLConnection) location.openConnection();
    		c.setUseCaches(false);
    		JarFile file = c.getJarFile();
    		Enumeration e = file.entries();
    		byte[] buffer = new byte[1024];
    		while (e.hasMoreElements()) {
    			JarEntry entry = (JarEntry) e.nextElement();
    			String fileName = entry.getName();
    			if (fileName.endsWith(".class")) {
    				InputStream is = file.getInputStream(entry);
    				int len = 0;
    				ByteArrayOutputStream bos = new ByteArrayOutputStream();
    				while ((len = is.read(buffer, 0, buffer.length)) != -1) {
    					bos.write(buffer, 0, len);
    				}
    				String className = fileName.substring(0, fileName.lastIndexOf('.')).replace(File.separatorChar, '.');
    				try {
    					Code code = codeHome.findByPrimaryKey(className);
    					code.updateByteCode(bos.toByteArray());
    				}
    				catch (ObjectNotFoundException ex) {
    					codeHome.create(className, bos.toByteArray());	
    				}
    				catch (FinderException ex) {
    					throw new EJBException(ex);
    				}
    			}
    		}
    	}
    	catch (CreateException ex) {
    		throw new EJBException(ex);
    	}
    	catch (IOException ex) {
    		throw new EJBException(ex);
    	}
    }
    
    /**
     * @ejb.interface-method
     * @ejb.permission unchecked="true"
     */
    public byte[] getByteCodeForClass(String className) throws ClassNotFoundException {
    	try {
    		Code code = codeHome.findByPrimaryKey(className);
    		return code.getByteCode();
    	}
    	catch (FinderException ex) {
    		throw new ClassNotFoundException(className);
    	}
    }
    
    /**
     * @ejb.interface-method 
     * @ejb.permission role-name="standardUser"
     */
    public Collection getClasses() throws FinderException {
    	Collection list = new LinkedList();
    	Iterator i = codeHome.findAll().iterator();
    	while (i.hasNext()) {
    		Code code = (Code) i.next();
    		list.add(code.getClassName());
    	}
    	return list;
    }
    
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }    
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

   /**
     * @ejb.permission unchecked="true"
    */	
    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void setSessionContext(javax.ejb.SessionContext sessionContext) throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    private CodeHome codeHome;
    private EJBClassLoader loader;
}
