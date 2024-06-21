/*
 * SimulationEditorBean.java
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import za.ac.up.cs.cirg.ciclops.domain.Category;
import za.ac.up.cs.cirg.ciclops.domain.CategoryHome;
import za.ac.up.cs.cirg.ciclops.domain.CategoryUtil;
import za.ac.up.cs.cirg.ciclops.domain.InvalidConfigurationException;
import za.ac.up.cs.cirg.ciclops.domain.Simulation;
import za.ac.up.cs.cirg.ciclops.domain.SimulationHome;
import za.ac.up.cs.cirg.ciclops.domain.SimulationUtil;
import za.ac.up.cs.cirg.ciclops.model.BrowserItem;
import za.ac.up.cs.cirg.ciclops.model.SimulationModel;

/**
 * 
 * @ejb.bean name="SimulationEditor"
 *   type="Stateless"
 *   view-type="remote"
 *   jndi-name="ejb/ciclops/services/SimulationEditor"
 *
 * @ejb.ejb-ref ejb-name="Simulation" view-type="local" 
 * @ejb.ejb-ref ejb-name="Category" view-type="local" 
 * 
 * @ejb.transaction type="Required"
 *
 * @ejb.util generate="physical"
 * 
 * @ejb.permission unchecked="true"
 *
 * @author  espeer
 */
public class SimulationEditorBean implements SessionBean {
    
    /**
     * @ejb.create-method
     */
    public void ejbCreate() throws CreateException {
    	setUp();
        try {
        	categoryHome = CategoryUtil.getLocalHome();
            simulationHome = SimulationUtil.getLocalHome();
        }
        catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    /**
     * @ejb.interface-method
     */
    public SimulationModel getSimulation(int id) throws FinderException {
        Simulation simulation = simulationHome.findByPrimaryKey(new Integer(id));
        SimulationModel model = new SimulationModel(simulation.getId().intValue());
        model.setName(simulation.getName());
        model.setDescription(simulation.getDescription());
        model.setNumberOfSamples(simulation.getTotalSamples());
        model.setConfiguration(simulation.getXMLConfiguration());
        Category category = simulation.getCategory();
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
    public void saveSimulation(SimulationModel model) throws CreateException, FinderException {
        Simulation simulation = null;
        
        try {
        	if (model.getId() == SimulationModel.NEW_ID) {
        		simulation = simulationHome.create(model.getName(), model.getConfiguration());
        	}
        	else {
        		simulation = simulationHome.findByPrimaryKey(new Integer(model.getId()));
        		simulation.setName(model.getName());
        		simulation.updateXMLConfiguration(model.getConfiguration());
        	}
        }
        catch (InvalidConfigurationException ex) {
        	// This should never happen because the ConfigBuilder validates the configuration first
        	throw new EJBException(ex);
        }
        
        simulation.setDescription(model.getDescription());
        simulation.setTotalSamples(model.getNumberOfSamples());
        try {
            Category category = categoryHome.findByPrimaryKey(new Integer(model.getCategory().getId()));
            simulation.setCategory(category);
        }
        catch (FinderException ex) {
            simulation.setCategory(null);
        }
    }
    
    /**
     *@ejb.interface-method
     */
    public void deleteSimulation(int id) throws FinderException, RemoveException {
    	simulationHome.findByPrimaryKey(new Integer(id)).remove();
    }
    
    private void setUp() {
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    private void tearDown() {
        transformer = null;
        documentBuilder = null;
    }

    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    	setUp();
    }    
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    	tearDown();
    }

    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
    	tearDown();
    }
    
    public void setSessionContext(javax.ejb.SessionContext sessionContext) throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    private SimulationHome simulationHome;
    private CategoryHome categoryHome;

    private Transformer transformer;
    private DocumentBuilder documentBuilder;
}
