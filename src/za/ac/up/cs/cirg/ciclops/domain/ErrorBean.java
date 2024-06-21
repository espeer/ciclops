/*
 * ErrorBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on March 7, 2004, 10:11 PM
 */

package za.ac.up.cs.cirg.ciclops.domain;

import java.lang.reflect.InvocationTargetException;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;

/**
 * @author  espeer
 *
 * @ejb.bean name="Error"
 *   type="CMP"
 *   cmp-version="${ejb.cmp.version}"
 *   view-type="local"
 *   local-jndi-name="ejb/ciclops/domain/Error"
 *   reentrant="false"
 *   primkey-field="id"
 *
 * @ejb.home local-class="${package}.domain.ErrorHome"
 * @ejb.interface local-class="${package}.domain.Error" 
 * 
 * @ejb.persistence table-name="error"
 *
 * @ejb.transaction type="Required"
 * 
 * @ejb.util generate="physical"
 *  
 * @jboss.read-ahead strategy="on-find" page-size="100"  
 *
 * @jboss.entity-command name="mysql-get-generated-keys"
 *
 * @jboss.persistence post-table-create="ALTER TABLE error type = innodb"
 * 
 * @ejb.permission unchecked="true"
 */

public abstract class ErrorBean implements EntityBean {
        
    /** 
     * @ejb:create-method
     */
    public Integer ejbCreate(Worker worker, Throwable throwable) throws CreateException {
        StringBuffer tmp = new StringBuffer(1024);
    	
        String newline = System.getProperty("line.separator");
        
        if (throwable instanceof InvocationTargetException) {
        	throwable = throwable.getCause();
        }
        
        tmp.append("Error: " + throwable.getClass().getName() + newline);
        tmp.append("Message: " + throwable.getMessage() + newline + newline);

        Throwable cause = throwable.getCause();
        if (cause != null) {
        	tmp.append("Caused by: " + cause.getClass().getName());
        	cause = cause.getCause();
        
        	while (cause != null) {
        		tmp.append(" -> " + cause.getClass().getName());
        		cause = cause.getCause();
        	}
        	
        	tmp.append(newline + newline);
        }
        
        tmp.append("Worker host: " + worker.getHostName() + newline);
        tmp.append("Worker OS: " + worker.getOperatingSystem() + newline);
        tmp.append("Worker VM: " + worker.getVirtualMachine() + newline + newline);
        
        StackTraceElement[] trace = throwable.getStackTrace();
        tmp.append("Stack trace:" + newline);
        
        for (int i = 0; i < trace.length; ++i) {
        	tmp.append("Class: " + trace[i].getClassName() + " Method: " + trace[i].getMethodName() + " Line: " + trace[i].getLineNumber() + newline);
        }
        
        setMessage(tmp.toString());
        
        return null;
    }
    
    public void ejbPostCreate(Worker worker, Throwable throwable) throws CreateException {
    	setSimulation(worker.getSimulation());
    }
    
    /**
     * @ejb.persistence
     * @ejb.pk-field
     * 
     * @jboss.column-name name="id"
     * @jboss.persistence auto-increment="true"
     * @jboss.method-attributes read-only="true"
     */
    public abstract Integer getId();
    public abstract void setId(Integer id);
    
    /**
     * @ejb.persistence column-name="message"
     * @jboss.method-attributes read-only="true"
     */
    public abstract Object getMessageAsObject();
    public abstract void setMessageAsObject(Object message);
    
    /**
     * @ejb.interface-method
     * @jboss,method-attributes read-only="true"
     */
    
    public String getMessage() {
    	return getMessageAsObject().toString();
    }
    public void setMessage(String message) {
    	setMessageAsObject(message);
    }
    
    /**
     * @ejb.relation name="SimulationErrorRelation"
     *    role-name="ErrorInSimulation"
     *    cascade-delete="yes"
     *
     * @jboss.relation fk-column="simulation"
     *   related-pk-field="id"
     *   fk-constraint="true"
     */
    public abstract za.ac.up.cs.cirg.ciclops.domain.Simulation getSimulation();
    public abstract void setSimulation(za.ac.up.cs.cirg.ciclops.domain.Simulation simulation);
        
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }    
    
    public void ejbLoad() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void ejbRemove() throws javax.ejb.RemoveException, javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void ejbStore() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void setEntityContext(javax.ejb.EntityContext entityContext) throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void unsetEntityContext() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
}
