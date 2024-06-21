/*
 * CategoryBrowserBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on Frebruary 10, 2004, 12:21 AM
 */

package za.ac.up.cs.cirg.ciclops.services;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.naming.NamingException;

import za.ac.up.cs.cirg.ciclops.domain.Category;
import za.ac.up.cs.cirg.ciclops.domain.CategoryHome;
import za.ac.up.cs.cirg.ciclops.domain.CategoryUtil;
import za.ac.up.cs.cirg.ciclops.domain.DataSet;
import za.ac.up.cs.cirg.ciclops.domain.DataSetHome;
import za.ac.up.cs.cirg.ciclops.domain.DataSetUtil;
import za.ac.up.cs.cirg.ciclops.domain.Simulation;
import za.ac.up.cs.cirg.ciclops.domain.SimulationHome;
import za.ac.up.cs.cirg.ciclops.domain.SimulationUtil;
import za.ac.up.cs.cirg.ciclops.model.BrowserItem;

/**
 * 
 * @ejb.bean name="CategoryBrowser"
 *   type="Stateless"
 *   view-type="remote"
 *   jndi-name="ejb/ciclops/services/CategoryBrowser"
 *
 * @ejb.ejb-ref ejb-name="Category" view-type="local" 
 * @ejb.ejb-ref ejb-name="Simulation" view-type="local" 
 * @ejb.ejb-ref ejb-name="DataSet" view-type="local" 
 * 
 * @ejb.transaction type="Required"
 *
 * @ejb.util generate="physical"
 * 
 * @ejb.permission unchecked="true"
 *
 * @author  espeer
 */
public class CategoryBrowserBean implements SessionBean {
    
    /**
     * @ejb.create-method
     */
    public void ejbCreate() throws CreateException {
        try {
            categoryHome = CategoryUtil.getLocalHome();
            simulationHome = SimulationUtil.getLocalHome();
            dataSetHome = DataSetUtil.getLocalHome();
        }
        catch (NamingException ex) {
            throw new EJBException(ex);
        }
    }
    
    /**
     * @ejb.interface-method
     */
    public BrowserItem getCategory(int categoryId) {
        try {
            Category category = categoryHome.findByPrimaryKey(new Integer(categoryId));
            BrowserItem tmp = new BrowserItem(BrowserItem.TYPE_CATEGORY, categoryId, category.getName());
            tmp.setPath(category.getPath());
            return tmp;
        }
        catch (FinderException ex) {
            BrowserItem tmp = new BrowserItem(BrowserItem.TYPE_CATEGORY, categoryId, "/");
            tmp.setPath("/");
            return tmp;
        }
    }
    
    /**
     * @ejb.interface-method
     */
    public Collection getChildren(int type, int categoryId) {
        switch (type) {
            case BrowserItem.TYPE_CATEGORY: return getChildCategories(categoryId);
            case BrowserItem.TYPE_SIMULATION: return getChildSimulations(categoryId);
            case BrowserItem.TYPE_DATA_SET: return getChildDataSets(categoryId);
            default: return null;    
        }
    }
            
    private Collection getChildCategories(int categoryId) {
        Collection categories = null;
        Collection children = new LinkedList();
         try {
             if (categoryId == BrowserItem.CATEGORY_ROOT) {
                 categories = categoryHome.findRootCategories();
             }
             else {
                 categories = categoryHome.findByPrimaryKey(new Integer(categoryId)).getChildren();
             }
         }
         catch (FinderException ex) {
             return children;
         }
            
         for (Iterator i = categories.iterator(); i.hasNext(); ) {
             Category category = (Category) i.next();
             BrowserItem tmp = new BrowserItem(BrowserItem.TYPE_CATEGORY, category.getId().intValue(), category.getName());
             tmp.setPath(category.getPath());
             children.add(tmp);
         }
         return children;  
    }
    
    private Collection getChildSimulations(int categoryId) {
        Collection simulations = null;
        Collection children = new LinkedList();
        try {
            if (categoryId == BrowserItem.CATEGORY_ROOT) {
                simulations = simulationHome.findRootSimulations();
            }
            else {
                simulations = categoryHome.findByPrimaryKey(new Integer(categoryId)).getSimulations();
            }
        }
        catch (FinderException ex) {
            return children;
        }
        
        for (Iterator i = simulations.iterator(); i.hasNext(); ) {
            Simulation simulation = (Simulation) i.next();
            BrowserItem tmp = new BrowserItem(BrowserItem.TYPE_SIMULATION, simulation.getId().intValue(), simulation.getName());
            if (simulation.getCategory() != null) {
                tmp.setPath(simulation.getCategory().getPath());
            }
            else{
                tmp.setPath("/");
            }
            tmp.setDescription(simulation.getDescription());
            children.add(tmp);            
        }
        return children;
    }
    
    private Collection getChildDataSets(int categoryId) {
        Collection dataSets = null;
        Collection children = new LinkedList();
        try {
            if (categoryId == BrowserItem.CATEGORY_ROOT) {
                dataSets = dataSetHome.findRootDataSets();
            }
            else {
                dataSets = categoryHome.findByPrimaryKey(new Integer(categoryId)).getDataSets();
            }
        }
        catch (FinderException ex) {
            return children;
        }
        
        for (Iterator i = dataSets.iterator(); i.hasNext(); ) {
            DataSet dataSet = (DataSet) i.next();
            BrowserItem tmp = new BrowserItem(BrowserItem.TYPE_DATA_SET, dataSet.getId().intValue(), dataSet.getName());
            if (dataSet.getCategory() != null) {
                tmp.setPath(dataSet.getCategory().getPath());
            }
            else {
                tmp.setPath("/");
            }
            tmp.setDescription(dataSet.getDescription());
            children.add(tmp);            
        }
        return children;
    }
    
    /**
     * @ejb.interface-method
     */
    public void createCategory(int parentId, String name) throws CreateException {
        Category parent = null;
        Category category = null;
        try {
            category = categoryHome.create(name);
            parent = categoryHome.findByPrimaryKey(new Integer(parentId));
            parent.getChildren().add(category);
        }
        catch (FinderException ex) { }
    }
    
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }    
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void setSessionContext(javax.ejb.SessionContext context) throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    private CategoryHome categoryHome;
    private SimulationHome simulationHome;
    private DataSetHome dataSetHome;
}
