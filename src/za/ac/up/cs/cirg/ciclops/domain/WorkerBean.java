/*
 * WorkerBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on March 5, 2004, 10:11 PM
 */

package za.ac.up.cs.cirg.ciclops.domain;

import java.util.Collection;
import java.util.Date;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;

/**
 * @author  espeer
 *
 * @ejb.bean name="Worker"
 *   type="CMP"
 *   cmp-version="${ejb.cmp.version}"
 *   view-type="local"
 *   local-jndi-name="ejb/ciclops/domain/Worker"
 *   reentrant="false"
 *   primkey-field="id"
 *
 * @ejb.home local-class="${package}.domain.WorkerHome"
 * @ejb.interface local-class="${package}.domain.Worker" 
 *
 * @ejb.persistence table-name="worker"
 *
 * @ejb.transaction type="Required"
 *
 * @ejb.finder signature="Collection findAll()"
 *   query="SELECT OBJECT(obj) FROM Worker obj"
 *
 * @ejb.util generate="physical"
 *
 * @jboss.entity-command name="mysql-get-generated-keys"
 * 
 * @jboss.persistence post-table-create="ALTER TABLE worker type = innodb"
 *
 * @ejb.permission unchecked="true"
 */

public abstract class WorkerBean implements EntityBean {
        
    /** 
     * @ejb.create-method
     */
    public Integer ejbCreate() throws CreateException {
         
        return null;
    }
    
    public void ejbPostCreate() throws CreateException {
    }

    /**
     * @ejb.persistence
     * @ejb.pk-field
     * @ejb.interface-method
     * 
     * @jboss.column-name name="id"
     * @jboss.persistence auto-increment="true"
     * @jboss.method-attributes read-only="true"
     */
    public abstract Integer getId();
    public abstract void setId(Integer id);
    
    /**
     * @ejb.persistence column-name="host_name"
     * 
     * @ejb.interface-method
     * @jboss.method-attributes read-onl    y="true"
     */
    public abstract String getHostName();
    /**
     * @ejb.interface-method
     */
    public abstract void setHostName(String host);
    
    /**
     * @ejb.persistence column-name="operating_system"
     * 
     * @ejb.interface-method
     * @jboss.method-attributes read-only="true"
     */
    public abstract String getOperatingSystem();
    /**
     * @ejb.interface-method
     */
    public abstract void setOperatingSystem(String os);
    
    /**
     * @ejb.persistence column-name="vm"
     * 
     * @ejb.interface-method
     * @jboss.method-attributes read-only="true"
     */
    public abstract String getVirtualMachine();
    /**
     * @ejb.interface-method
     */
    public abstract void setVirtualMachine(String vm);
    
    /**
     * @ejb.persistence column-name="status"
     * @jboss.method-attributes read-only="true"
     */
    public abstract String getStatusString();
    public abstract void setStatusString(String status);
    
    /**
     * @ejb.interface-method
     * @jboss.method-attributes read-only="true"
     */
    public String getStatus() {
        return getStatusString();
    }
    
    /**
     * @ejb.interface-method
     */
    public void setStatus(String status) {
        setStatusUpdatedAsLong(new Date().getTime());
        setStatusString(status);
    }
    
    /**
     * @ejb.interface-method
     * @jboss-method.attributes read-only="true"
     */
    public Date getStatusUpdated() {
        return new Date(getStatusUpdatedAsLong());
    }
    
    /**
     * @ejb.persistence column-name="status_updated"
     * @jboss.method-attributes read-only="true"
     */
    public abstract long getStatusUpdatedAsLong();
    public abstract void setStatusUpdatedAsLong(long time);
    
    
    /**
     * @ejb.interface-method
     * 
     * @ejb.relation name="SimulationWorkerRelation"
     *    role-name="WorkerWorksOnSimulation"
     * 
     * @jboss.relation fk-column="simulation"
     *   related-pk-field="id"
     *   fk-constraint="false"
     * 
     * @jboss.method-attributes read-only="true"
     */
    public abstract za.ac.up.cs.cirg.ciclops.domain.Simulation getSimulation();
    /**
     * @ejb.interface-method
     */
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
