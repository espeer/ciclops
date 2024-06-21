/*
 * CategoryBrowserBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on Frebruary 10, 2004, 12:21 AM
 */

package za.ac.up.cs.cirg.ciclops.services;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.NamingException;

import za.ac.up.cs.cirg.ciclops.domain.Category;
import za.ac.up.cs.cirg.ciclops.domain.CategoryHome;
import za.ac.up.cs.cirg.ciclops.domain.CategoryUtil;
import za.ac.up.cs.cirg.ciclops.domain.DataSet;
import za.ac.up.cs.cirg.ciclops.domain.DataSetHome;
import za.ac.up.cs.cirg.ciclops.domain.DataSetUtil;
import za.ac.up.cs.cirg.ciclops.model.BrowserItem;
import za.ac.up.cs.cirg.ciclops.model.DataSetModel;

/**
 * 
 * @ejb.bean name="DataSetEditor"
 *   type="Stateless"
 *   view-type="remote"
 *   jndi-name="ejb/ciclops/services/DataSetEditor"
 *
 * @ejb.ejb-ref ejb-name="Category" view-type="local" 
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
public class DataSetEditorBean implements SessionBean {
    
    /**
     * @ejb.create-method
     */
    public void ejbCreate() throws CreateException {
        try {
            categoryHome = CategoryUtil.getLocalHome();
            dataSetHome = DataSetUtil.getLocalHome();
        }
        catch (NamingException ex) {
            throw new EJBException(ex);
        }
    }
        
    /**
     * @ejb.interface-method
     */
    public DataSetModel getDataSet(int id) throws FinderException {
        DataSet dataSet = dataSetHome.findByPrimaryKey(new Integer(id));
        DataSetModel model = new DataSetModel(dataSet.getId().intValue());
        model.setName(dataSet.getName());
        model.setDescription(dataSet.getDescription());
        model.setData(dataSet.getData());
        Category category = dataSet.getCategory();
        BrowserItem tmp = null;
        if (category == null) {
            tmp = new BrowserItem(BrowserItem.TYPE_CATEGORY, BrowserItem.CATEGORY_ROOT, "/");
            tmp.setPath("/");
        }
        else {
            tmp = new BrowserItem(BrowserItem.TYPE_CATEGORY, category.getId().intValue(), category.getName());
            tmp.setPath(category.getPath());
        }
        model.setCategory(tmp);
        return model;
    }
    
    /**
     * @ejb.interface-method
     */
    public void saveDataSet(DataSetModel model) throws CreateException, FinderException {
        DataSet dataSet = null;
        if (model.getId() == DataSetModel.NEW_ID) {
            dataSet = dataSetHome.create(model.getName(), model.getData());
        }
        else {
            dataSet = dataSetHome.findByPrimaryKey(new Integer(model.getId()));
            dataSet.setName(model.getName());
            dataSet.updateData(model.getData());
        }
        
        dataSet.setDescription(model.getDescription());
        try {
            Category category = categoryHome.findByPrimaryKey(new Integer(model.getCategory().getId()));
            dataSet.setCategory(category);
        }
        catch (FinderException ex) {
            dataSet.setCategory(null);
        }
    }
    
    /**
     * @ejb.interface-method
     */
    public void deleteDataSet(int id) throws FinderException, RemoveException {
    	dataSetHome.findByPrimaryKey(new Integer(id)).remove();
    }
    
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }    
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
        context = null;
    }
    
    public void setSessionContext(javax.ejb.SessionContext context) throws javax.ejb.EJBException, java.rmi.RemoteException {
        this.context = context;
    }

    private CategoryHome categoryHome;
    private DataSetHome dataSetHome;
    private SessionContext context;
}
