/*
 * TaskBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on November 3, 2004
 */

package za.ac.up.cs.cirg.ciclops.services;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;

import za.ac.up.cs.cirg.ciclops.domain.Category;
import za.ac.up.cs.cirg.ciclops.domain.Simulation;
import za.ac.up.cs.cirg.ciclops.domain.SimulationHome;
import za.ac.up.cs.cirg.ciclops.domain.SimulationUtil;
import za.ac.up.cs.cirg.ciclops.domain.Worker;
import za.ac.up.cs.cirg.ciclops.domain.WorkerHome;
import za.ac.up.cs.cirg.ciclops.domain.WorkerUtil;
import za.ac.up.cs.cirg.ciclops.model.ActiveSimulationsModel;
import za.ac.up.cs.cirg.ciclops.model.ActiveWorkersModel;
import za.ac.up.cs.cirg.ciclops.model.BrowserItem;
import za.ac.up.cs.cirg.ciclops.model.ErrorListModel;
import za.ac.up.cs.cirg.ciclops.model.SimulationModel;
import za.ac.up.cs.cirg.ciclops.model.WorkerModel;

/**
 * 
 * @ejb.bean name="Task"
 *   type="Stateless"
 *   view-type="remote"
 *   jndi-name="ejb/ciclops/services/Task"
 *
 * @ejb.ejb-ref ejb-name="Simulation" view-type="local"
 * @ejb.ejb-ref ejb-name="Worker" view-type="local"
 * @ejb.ejb-ref ejb-name="Error" view-type="local" 
 * 
 * @ejb.transaction type="Required"
 *
 * @ejb.util generate="physical"
 * 
 * @ejb.permission unchecked="true"
 *
 * @author  espeer
 */
public class TaskBean implements SessionBean {
    
    /**
     * @ejb.create-method
     */
    public void ejbCreate() throws CreateException {
        try {
            simulationHome = SimulationUtil.getLocalHome();
            workerHome = WorkerUtil.getLocalHome();
        }
        catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    /**
     * @ejb.interface-method
     */
    public ActiveSimulationsModel getActiveSimulations() throws FinderException {
    	Collection simulations = simulationHome.findIncompleteOrStaleSimulations();
    	SimulationModel[] model = new SimulationModel[simulations.size()];
    	
    	Iterator i = simulations.iterator();
    	for (int k = 0; i.hasNext(); ++k) {
    		Simulation simulation = (Simulation) i.next();
    		model[k] = new SimulationModel(simulation.getId().intValue());
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
            model[k].setCategory(tmp);
            
            model[k].setName(simulation.getName());
            model[k].setCompletedSamples(simulation.getCompletedSamples());
            model[k].setTotalSamples(simulation.getTotalSamples());
            model[k].setStale(simulation.isStale());
            model[k].setErrors(simulation.getErrors().size());

    	}
    	
    	return new ActiveSimulationsModel(model);
    }
    
    /**
     * @ejb.interface-method
     */
    public ActiveWorkersModel getActiveWorkers() throws FinderException {
    	Collection workers = workerHome.findAll();
    	WorkerModel[] model = new WorkerModel[workers.size()];
    	
    	Iterator i = workers.iterator();
    	for (int k = 0; i.hasNext(); ++k) {
    		Worker worker = (Worker) i.next();
    		model[k] = new WorkerModel(worker.getId().intValue());
    		model[k].setHostName(worker.getHostName());
    		model[k].setOperatingSystem(worker.getOperatingSystem());
    		model[k].setVirtualMachine(worker.getVirtualMachine());
    		Simulation simulation = worker.getSimulation();
    		model[k].setSimulation(simulation == null ? -1 : simulation.getId().intValue());
    		model[k].setStatus(worker.getStatus());
    		model[k].setLastUpdate(worker.getStatusUpdated());
    	}
    	
    	return new ActiveWorkersModel(model);
    }
    
    /**
     * @ejb.interface-method
     */
    public void killWorker(int id) throws FinderException, RemoveException {
    	workerHome.findByPrimaryKey(new Integer(id)).remove();
    }
    
    /**
     * @ejb.interface-method
     */
    public void clearErrors(int simulationId) throws FinderException, RemoveException {
    	simulationHome.findByPrimaryKey(new Integer(simulationId)).clearErrors();
    }
    
    /**
     * @ejb.interface-method
     */
   public ErrorListModel getErrorList(int simulationId) throws FinderException {
   		Collection errors = simulationHome.findByPrimaryKey(new Integer(simulationId)).getErrors();
   		String[] model = new String[errors.size()];
   		
   		Iterator i = errors.iterator();
   		for (int k = 0; i.hasNext(); ++k) {
   			za.ac.up.cs.cirg.ciclops.domain.Error error = (za.ac.up.cs.cirg.ciclops.domain.Error) i.next();
   			model[k] = error.getMessage();
   		}
   		
   		return new ErrorListModel(simulationId, model);
   }
   
   /**
    * @ejb.interface-method
    */
   public void scheduleCVSUpdate() {
   		try {
   			new File("/tmp/ciclops_fetch_cilib_from_cvs").createNewFile();
   		}
   		catch (IOException ex) {
   			throw new EJBException(ex);
   		}
   }
    
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }    
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void setSessionContext(javax.ejb.SessionContext sessionContext) throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    private SimulationHome simulationHome;
    private WorkerHome workerHome; 
}
