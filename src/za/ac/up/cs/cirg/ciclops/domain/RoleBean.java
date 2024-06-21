/*
 * PrincipalBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on March 8, 2004, 10:53 PM
 */

package za.ac.up.cs.cirg.ciclops.domain;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;

/**
 * @author  espeer
 *
 * @ejb.bean name="Role"
 *   type="CMP"
 *   cmp-version="${ejb.cmp.version}"
 *   view-type="local"
 *   local-jndi-name="ejb/ciclops/domain/Role"
 *   reentrant="false"
 *   primkey-field="id"
 *
 * @ejb.home local-class="${package}.domain.RoleHome"
 * @ejb.interface local-class="${package}.domain.Role"
 *
 * @ejb.persistence table-name="Roles"
 *
 * @ejb.transaction type="Required"
 *
 * @ejb.util generate="physical"
 *
 * @jboss.persistence post-table-create="ALTER TABLE Roles type = innodb"
 *  
 * @jboss.read-ahead strategy="on-find" page-size="1000"
 * 
 * @jboss.entity-command name="mysql-get-generated-keys"
 *
 * @ejb.permission unchecked="true"
 */

public abstract class RoleBean implements EntityBean {
        
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
     * 
     * @jboss.column-name name="id"
     * @jboss.persistence auto-increment="true"
     * @jboss.method-attributes read-only="true"
     */
    public abstract Integer getId();
    public abstract void setId(Integer id);
    
    /**
     * @ejb.relation name="PricipalRoleRelation"
     *    role-name="RoleOfPrincipal"
     * 
     * @jboss.relation fk-column="PrincipalID"
     *   related-pk-field="userName"
     *   fk-constraint="true"
     * 
     * @jboss.method-attributes read-only="true"
     */
    public abstract za.ac.up.cs.cirg.ciclops.domain.Principal getPricipal();
    public abstract void setPricipal(za.ac.up.cs.cirg.ciclops.domain.Principal principal);

    /**
     * @ejb.persistence column-name="Role"
     * @ejb.interface-method
     * @jboss.method-attributes read-only="true"
     */
    public abstract String getRole();
    public abstract void setRole(String role);
    
    /**
     * @ejb.persistence column-name="RoleGroup"
     * @ejb.interface-method
     * @jboss.method-attributes read-only="true"
     */
    public abstract String getRoleGroup();
    public abstract void setRoleGroup(String role);
        
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
