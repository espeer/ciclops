/*
 * GUIDGeneratorBean.java
 *
 * Copyright (C) 2003 - Edwin S Peer
 *
 * Created on June 7, 2003, 5:12 PM
 */

package za.ac.up.cs.cirg.ciclops.services;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.Random;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;

/**
 * @ejb.bean name="GUIDGenerator"
 *   type="Stateless"
 *   view-type="local"
 *   local-jndi-name="ejb/OptiBench/services/GUIDGenerator"
 *
 * @ejb.home local-class="${package}.services.GUIDGeneratorHome"
 * @ejb.interface local-class="${package}.services.GUIDGenerator"
 *
 * @ejb.util generate="physical"
 *  
 * @ejb.permission unchecked="true"
 * 
 * @author  espeer
 */
public class GUIDGeneratorBean implements SessionBean {

    /**
     * @ejb.create-method
     */
    public void ejbCreate() throws CreateException {
        random = new SecureRandom();
        constant = new StringBuffer(20);
        
        constant.append("-");

        byte[] address = getNetworkAddress();
        for (int i = 0; i < 4; ++i) {
        	constant.append(hex[(address[i] & 0xf0) >> 4]);
     	    constant.append(hex[address[i] & 0xf]);
     	    if ((i % 2) == 1) {
     	    	constant.append("-");
     	    }
        }

        int hash = System.identityHashCode(this); 
        for (int i = 28; i >= 0; i -= 4) {
        	constant.append(hex[(hash & (0xf << i)) >>> i]);
        	if (i == 16) {
        		constant.append("-");
        	}
        }
    }
   
    /**
     * @ejb.interface-method
     */
    public String getGUID() {
        StringBuffer guid = new StringBuffer(36);

        long time = System.currentTimeMillis();

        // drop the least significant 5 bits ~ 32 millisecond granularity
        for (int i = 33; i > 1; i -= 4) {
        	guid.append(hex[(int) (time & (0xfL << i)) >>> i]);
        }

        guid.append(constant);

        int rand = random.nextInt();

        for (int i = 28; i >= 0; i -= 4) {
        	guid.append(hex[(rand & (0xf << i)) >>> i]);
        }

        return guid.toString();
    }
    
    private byte[] getNetworkAddress() {
        byte[] address = null;

	try {
	    address = InetAddress.getLocalHost().getAddress();
	}
	catch (UnknownHostException ex) {
            throw new EJBException(ex);
        }

	try {
	    Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
	    while (interfaces.hasMoreElements()) {
		Enumeration addresses = ((NetworkInterface) interfaces.nextElement()).getInetAddresses();
		while (addresses.hasMoreElements()) {
		    InetAddress addr = ((InetAddress) addresses.nextElement());
		    if (! addr.isLoopbackAddress()) {
			address = addr.getAddress();
		    }
		}
	    }
	}
	catch (SocketException ex) {
            throw new EJBException(ex);
	}

	return address;
    }
    
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }    
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void setSessionContext(javax.ejb.SessionContext sessionContext) throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

    private Random random;
    private StringBuffer constant;
    final private char hex[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};      
}
