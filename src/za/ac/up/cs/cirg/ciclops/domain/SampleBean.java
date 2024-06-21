/*
 * SampleBean.java
 *
 * Copyright (C) 2003 - Edwin S. Peer
 *
 * Created on June 6, 2003, 10:11 PM
 */

package za.ac.up.cs.cirg.ciclops.domain;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;

/**
 * @author  espeer
 *
 * @ejb.bean name="Sample"
 *   type="CMP"
 *   cmp-version="${ejb.cmp.version}"
 *   view-type="local"
 *   local-jndi-name="ejb/ciclops/domain/Sample"
 *   reentrant="false"
 *   primkey-field="id"
 *
 * @ejb.home local-class="${package}.domain.SampleHome"
 * @ejb.interface local-class="${package}.domain.Sample" 
 * 
 * @ejb.persistence table-name="sample"
 *
 * @ejb.transaction type="Required"
 * 
 * @ejb.util generate="physical"
 *  
 * @jboss.read-ahead strategy="on-find" page-size="100"  
 *
 * @jboss.entity-command name="mysql-get-generated-keys"
 *
 * @jboss.persistence post-table-create="ALTER TABLE sample type = innodb"
 * 
 * @ejb.permission unchecked="true"
 */

public abstract class SampleBean implements EntityBean {
        
    /**
     * @ejb.interface-method 
     * @ejb:create-method
     */
    public Integer ejbCreate(za.ac.up.cs.cirg.ciclops.domain.Simulation simulation) throws CreateException {
        return null;
    }
    
    public void ejbPostCreate(za.ac.up.cs.cirg.ciclops.domain.Simulation simulation) throws CreateException {
        setSimulation(simulation);
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
     * @ejb.relation name="SimulationSampleRelation"
     *   role-name="SamplesOfSimulation"
     *   cascade-delete="yes"
     * 
      * @jboss.relation fk-column="simulation"
     *   related-pk-field="id"
     *   fk-constraint="true"
     */
    public abstract za.ac.up.cs.cirg.ciclops.domain.Simulation getSimulation();
    public abstract void setSimulation(za.ac.up.cs.cirg.ciclops.domain.Simulation simulation);
    
    /**
     * @ejb.relation name="SampleMeasurementRelation"
     *    role-name="SampleOfMeasurements"
     * 
     * @ejb.interface-method
     */       
    public abstract Collection getMeasurements();
    public abstract void setMeasurements(Collection Measurements);

    
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
