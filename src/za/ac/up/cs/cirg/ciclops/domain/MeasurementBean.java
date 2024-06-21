/*
 * MeasurementBean.java
 *
 * Copyright (C) 2003 - Edwin S. Peer
 *
 * Created on June 6, 2003, 10:11 PM
 */

package za.ac.up.cs.cirg.ciclops.domain;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;

/**
 * @author  espeer
 *
 * @ejb.bean name="Measurement"
 *   type="CMP"
 *   cmp-version="${ejb.cmp.version}"
 *   view-type="local"
 *   local-jndi-name="ejb/ciclops/domain/Measurement"
 *   reentrant="false"
 *   primkey-field="id"
 *
 * @ejb.home local-class="${package}.domain.MeasurementHome"
 * @ejb.interface local-class="${package}.domain.Measurement" 
 * 
 * @ejb.persistence table-name="measurement"
 *
 * @ejb.transaction type="Required"
 * 
 * @ejb.util generate="physical"
 *  
 * @jboss.read-ahead strategy="on-find" page-size="100"  
 *
 * @jboss.entity-command name="mysql-get-generated-keys"
 *
 * @jboss.persistence post-table-create="ALTER TABLE measurement type = innodb"
 * 
 * @ejb.permission unchecked="true"
 */

public abstract class MeasurementBean implements EntityBean {
        
    /**
     * @ejb.interface-method 
     * @ejb:create-method
     */
    public Integer ejbCreate(za.ac.up.cs.cirg.ciclops.domain.Sample sample, String domain, byte[] data) throws CreateException {
        setDomain(domain);
        setData(data);
        
        return null;
    }
    
    public void ejbPostCreate(za.ac.up.cs.cirg.ciclops.domain.Sample sample, String domain, byte[] data) throws CreateException {
        setSample(sample);
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
     * @ejb.persistence column-name="data"
     * @jboss.method-attributes read-only="true"
     */
    public abstract byte[] getData();
    public abstract void setData(byte[] data);
    
    /**
     * @ejb.persistence column-name="domain"
     * @jboss.method-attributes read-only="true"
     * 
     * @ejb.interface-method
     */
    public abstract String getDomain();
    public abstract void setDomain(String domain);
    
    /**
     * @ejb.interface-method
     */
    public ObjectInputStream getDataStream() throws IOException {
    	return new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(getData())));
    }
    
    /**
     * @ejb.relation name="SampleMeasurementRelation"
     *   role-name="MeasurementsInSample"
     *   cascade-delete="yes"
     * 
      * @jboss.relation fk-column="sample"
     *   related-pk-field="id"
     *   fk-constraint="true"
     */
    public abstract za.ac.up.cs.cirg.ciclops.domain.Sample getSample();
    public abstract void setSample(za.ac.up.cs.cirg.ciclops.domain.Sample sample);
    
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
