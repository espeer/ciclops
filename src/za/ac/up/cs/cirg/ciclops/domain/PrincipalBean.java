/*
 * PrincipalBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on March 8, 2004, 10:53 PM
 */

package za.ac.up.cs.cirg.ciclops.domain;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;


/**
 * @author  espeer
 *
 * @ejb.bean name="Principal"
 *   type="CMP"
 *   cmp-version="${ejb.cmp.version}"
 *   view-type="local"
 *   local-jndi-name="ejb/ciclops/domain/Pricipal"
 *   reentrant="false"
 *   primkey-field="userName"
 *
 * @ejb.home local-class="${package}.domain.PrincipalHome"
 * @ejb.interface local-class="${package}.domain.Principal"
 *
 * @ejb.persistence table-name="Principals"
 *
 * @ejb.transaction type="Required"
 *
 * @ejb.finder signature="Collection findAll()"
 *   query="SELECT OBJECT(obj) FROM Principal obj"
 *
 * @ejb.util generate="physical"
 *
 * @jboss.persistence post-table-create="ALTER TABLE Principals type = innodb"
 *  
 * @jboss.read-ahead strategy="on-find" page-size="1000"
 * 
 * @ejb.permission unchecked="true"
 */

public abstract class PrincipalBean implements EntityBean {
        
    /** 
     * @ejb.create-method
     */
    public String ejbCreate() throws CreateException {
        
        return null;
    }
    
    public void ejbPostCreate() throws CreateException {
    }
            
    /**
     * @ejb.interface-method
     * 
     * @ejb.persistence column-name="PrincipalID"
     * @ejb.pk-field
     * @ejb.interface-method
     * 
     * @jboss.method-attributes read-only="true"
     */
    public abstract String getUserName();
    public abstract void setUserName(String name);

    /**
     * @ejb.persistence column-name="Password"
     * @ejb.interface-method
     * @jboss.method-attributes read-only="true"
     */
    public abstract String getPassword();
    public abstract void setPassword(String password);
    
    /**
     * @ejb.relation name="PricipalRoleRelation"
     *    role-name="PrincipalInRole"
     */
    public abstract Collection getRoles();
    public abstract void setRoles(Collection roles);
    
    /**
     * @ejb.home-method
     */
    public Collection ejbHomeGetUserNames() {
        LinkedList users = new LinkedList();
        try {
            Collection pricipals = ((PrincipalHome) context.getEJBLocalHome()).findAll();
            for (Iterator i = pricipals.iterator(); i.hasNext(); ) {
                Principal principal = (Principal) i.next();
                users.add(principal.getUserName());
            }
        }
        catch (FinderException ex) { }
        return users;
    }
    
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
    
    public void setEntityContext(javax.ejb.EntityContext context) throws javax.ejb.EJBException, java.rmi.RemoteException {
        this.context = context;
    }
    
    public void unsetEntityContext() throws javax.ejb.EJBException, java.rmi.RemoteException {
        context = null;
    }
    
    private EntityContext context;
}
