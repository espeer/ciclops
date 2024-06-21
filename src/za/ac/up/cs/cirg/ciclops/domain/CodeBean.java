/*
 * CodeBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on February 10, 2004, 10:53 PM
 */

package za.ac.up.cs.cirg.ciclops.domain;

import java.util.Arrays;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;

/**
 * @author  espeer
 *
 * @ejb.bean name="Code"
 *   type="CMP"
 *   cmp-version="${ejb.cmp.version}"
 *   view-type="local"
 *   local-jndi-name="ejb/ciclops/domain/Code"
 *   reentrant="false"
 *   primkey-field="className"
 *
 * @ejb.home local-class="${package}.domain.CodeHome"
 * @ejb.interface local-class="${package}.domain.Code"
 *
 * @ejb.persistence table-name="code"
 *
 * @ejb.transaction type="Required"
 *
 * @ejb.finder signature="Collection findAll()"
 *   query="SELECT OBJECT(obj) FROM Code obj"
 *
 * @ejb.util generate="physical"
 *
 * @jboss.persistence post-table-create="ALTER TABLE code type = innodb"
 *  
 * @jboss.read-ahead strategy="on-find" page-size="1000"
 * 
 * 
 * @ejb.permission unchecked="true"
 */

public abstract class CodeBean implements EntityBean {
        
    /** 
     * @ejb.create-method
     */
    public String ejbCreate(String className, byte[] byteCode) throws CreateException {
    	setClassName(className);
        setByteCode(byteCode);
        
        return null;
    }
    
    public void ejbPostCreate(String name, byte[] configuration) throws CreateException {
    }
            
    /**
     * @ejb.persistence column-name="class_name"
     * @ejb.pk-field
     * @ejb.interface-method
     * 
     * @jboss.method-attributes read-only="true"
     */
    public abstract String getClassName();
    public abstract void setClassName(String name);

    /**
     * @ejb.persistence column-name="byte_code"
     * @ejb.interface-method
     * @jboss.method-attributes read-only="true"
     */
    public abstract byte[] getByteCode();
    public abstract void setByteCode(byte[] byteCode);

    /**
     * @ejb.interface-method
     */
    public void updateByteCode(byte[] byteCode) {
    	if (! Arrays.equals(getByteCode(), byteCode)) {
    		setByteCode(byteCode);
    		Iterator i = getSimulations().iterator();
    		while (i.hasNext()) {
    			Simulation dependency = (Simulation) i.next();
    			dependency.setStale(true);
    		}
    	}
    }
   
    /**
     * @ejb.relation name="CodeSimulationRelation"
     *    role-name="CodeIsDependencyOfSimulation"
     * 
     * @jboss.relation fk-column="simulation"
     *    related-pk-field="id"
     *    fk-constraint="true"
     *
     * @jboss.relation-table table-name="code_simulation" 
     *    create-table="true"
     *
     */
    public abstract java.util.Set getSimulations();
    public abstract void setSimulations(java.util.Set dependencies);
    
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
