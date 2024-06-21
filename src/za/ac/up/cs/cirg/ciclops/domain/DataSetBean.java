/*
 * DataSetBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on March 5, 2004, 10:11 PM
 */

package za.ac.up.cs.cirg.ciclops.domain;

import java.util.Arrays;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;

/**
 * @author  espeer
 *
 * @ejb.bean name="DataSet"
 *   type="CMP"
 *   cmp-version="${ejb.cmp.version}"
 *   view-type="local"
 *   local-jndi-name="ejb/ciclops/domain/DataSet"
 *   reentrant="false"
 *   primkey-field="id"
 *
 * @ejb.home local-class="${package}.domain.DataSetHome"
 * @ejb.interface local-class="${package}.domain.DataSet" 
 *
 * @ejb.persistence table-name="data_set"
 *
 * @ejb.transaction type="Required"
 *
 * @ejb.finder signature="Collection findAll()"
 *   query="SELECT OBJECT(obj) FROM DataSet obj"
 * 
 * @ejb.finder signature="java.util.Collection findRootDataSets()"
 *   query="SELECT OBJECT(ds) FROM DataSet ds WHERE ds.category IS NULL"
 *
 * @ejb.util generate="physical"
 *
 * @jboss.entity-command name="mysql-get-generated-keys"
 * 
 * @jboss.persistence post-table-create="ALTER TABLE data_set type = innodb"
 *
 * @ejb.permission unchecked="true"
 */

public abstract class DataSetBean implements EntityBean {
        
    /** 
     * @ejb.create-method
     */
    public Integer ejbCreate(String name, byte[] data) throws CreateException {
        setName(name);
        setDescription("");
        setData(data);
        setVersion(0);
        
        return null;
    }
    
    public void ejbPostCreate(String name, byte[] data) throws CreateException {
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
     * @ejb.interface-method
     * @ejb.persistence column-name="name"
     * @jboss.method-attributes read-only="true"
     */
    public abstract String getName();
    /**
     * @ejb.interface-method
     */
    public abstract void setName(String name);
    
    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="description"
     * @jboss.method-attributes read-only="true"
     */
    public abstract String getDescription();
    /**
     * @ejb.interface-method
     */
    public abstract void setDescription(String description);
        
    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="data"
     * @jboss.method-attributes read-only="true"
     */
    public abstract byte[] getData();
    public abstract void setData(byte[] data);
    
    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="version"
     * @jboss.method-attributes read-only="true"
     */
    public abstract int getVersion();
    public abstract void setVersion(int version);
    
    /**
     * @ejb.interface-method
     */
    public void updateData(byte[] data) {
        if (! Arrays.equals(data, getData())) {
            setData(data);
            setVersion(getVersion() + 1);
            Iterator i = getSimulations().iterator();
            while (i.hasNext()) {
            	Simulation dependency = (Simulation) i.next();
            	dependency.setStale(true);
            }
        }
    }
    
    /**
     * @ejb.interface-method
     * 
     * @ejb.relation name="CategoryDataSetRelation"
     *    role-name="DataSetInACategory"
     *    cascade-delete="yes"
     * 
     * @jboss.relation fk-column="category"
     *   related-pk-field="id"
     *   fk-constraint="false"
     */
    public abstract za.ac.up.cs.cirg.ciclops.domain.Category getCategory();
    /**
     * @ejb.interface-method
     */
    public abstract void setCategory(za.ac.up.cs.cirg.ciclops.domain.Category category);
    
    /**
     * @ejb.relation name="SimulationDataSetRelation"
     *    role-name="DataSetIsDependencyOfSimulation"
     * 
     * @jboss.relation fk-column="simulation"
     *    related-pk-field="id"
     *    fk-constraint="true"
     * 
     * @jboss.relation-table table-name="simulation_data_set" 
     *    create-table="true"
     */
    public abstract java.util.Set getSimulations();
    public abstract void setSimulations(java.util.Set simulations);

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
        this.context = entityContext;
    }
    
    public void unsetEntityContext() throws javax.ejb.EJBException, java.rmi.RemoteException {
        this.context = null;
    }
    
    private EntityContext context;

}
