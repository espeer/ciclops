/*
 * CategoryBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on March 7, 2004, 10:11 PM
 */

package za.ac.up.cs.cirg.ciclops.domain;

import java.util.Collection;

import javax.ejb.CreateException;
import javax.ejb.EntityBean;

/**
 * @author  espeer
 *
 * @ejb.bean name="Category"
 *   type="CMP"
 *   cmp-version="${ejb.cmp.version}"
 *   view-type="local"
 *   local-jndi-name="ejb/ciclops/domain/Category"
 *   reentrant="false"
 *   primkey-field="id"
 *
 * @ejb.home local-class="${package}.domain.CategoryHome"
 * @ejb.interface local-class="${package}.domain.Category" 
 * 
 * @ejb.persistence table-name="category"
 *
 * @ejb.transaction type="Required"
 * 
 * @ejb.util generate="physical"
 *  
 * @ejb.finder signature="java.util.Collection findRootCategories()"
 *      query="SELECT OBJECT(c) FROM Category c WHERE c.parent IS NULL"
 * 
 * @jboss.read-ahead strategy="on-find" page-size="100"  
 *
 * @jboss.entity-command name="mysql-get-generated-keys"
 *
 * @jboss.persistence post-table-create="ALTER TABLE category type = innodb"
 * 
 * @ejb.permission unchecked="true"
 */

public abstract class CategoryBean implements EntityBean {
        
    /** 
     * @ejb:create-method
     */
    public Integer ejbCreate(String name) throws CreateException {
        setName(name);
        
        return null;
    }
    
    public void ejbPostCreate(String name) throws CreateException {
    }
    
    /**
     * @ejb.interface-method
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
     * @ejb.interface-method
     * @ejb.persistence column-name="name"
     * @jboss-method-attributes read-only="true"
     */
    public abstract String getName();
    public abstract void setName(String name);
    
    /**
     * @ejb.interface-method
     * 
     * @ejb.relation name="CategoryCategoryRelation"
     *    role-name="CategoryIsASubCategory"
     *    cascade-delete="yes"
     * 
     * @jboss.relation fk-column="parent"
     *   related-pk-field="id"
     *   fk-constraint="false"
     */
    public abstract za.ac.up.cs.cirg.ciclops.domain.Category getParent();
    public abstract void setParent(za.ac.up.cs.cirg.ciclops.domain.Category parent);

    /**
     * @ejb.interface-method
     * 
     * @ejb.relation name="CategoryCategoryRelation"
     *   role-name="CategoryHasSubCategories"
     */
    public abstract Collection getChildren();
    public abstract void setChildren(Collection categories);
    
    /**
     * @ejb.interface-method
     */
    public String getPath() {
        StringBuffer path = new StringBuffer();
        Category current = getParent();
        while (current != null) {
            path.insert(0, current.getName());
            path.insert(0, '/');
            current = current.getParent();
        }
        path.append("/");
        path.append(getName());
        return path.toString();
    }
    
    /**
     * @ejb.interface-method
     * 
     * @ejb.relation name="CategorySimulationRelation"
     *   role-name="CategoryContainsSimulations"
     */
    public abstract Collection getSimulations();
    public abstract void setSimulations(Collection simulations);
    
    /**
     * @ejb.interface-method
     * 
     * @ejb.relation name="CategoryDataSetRelation"
     *   role-name="CategoryContainsDataSets"
     */
    public abstract Collection getDataSets();
    public abstract void setDataSets(Collection dataSets);
    
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
