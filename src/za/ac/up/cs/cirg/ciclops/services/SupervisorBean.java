/*
 * SupervisorBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on March 8, 2004, 12:21 AM
 */

package za.ac.up.cs.cirg.ciclops.services;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.naming.NamingException;

import za.ac.up.cs.cirg.ciclops.domain.DataSet;
import za.ac.up.cs.cirg.ciclops.domain.DataSetHome;
import za.ac.up.cs.cirg.ciclops.domain.DataSetUtil;
import za.ac.up.cs.cirg.ciclops.domain.ErrorHome;
import za.ac.up.cs.cirg.ciclops.domain.ErrorUtil;
import za.ac.up.cs.cirg.ciclops.domain.MeasurementHome;
import za.ac.up.cs.cirg.ciclops.domain.MeasurementUtil;
import za.ac.up.cs.cirg.ciclops.domain.ReservationException;
import za.ac.up.cs.cirg.ciclops.domain.Sample;
import za.ac.up.cs.cirg.ciclops.domain.SampleHome;
import za.ac.up.cs.cirg.ciclops.domain.SampleUtil;
import za.ac.up.cs.cirg.ciclops.domain.Simulation;
import za.ac.up.cs.cirg.ciclops.domain.SimulationHome;
import za.ac.up.cs.cirg.ciclops.domain.SimulationUtil;
import za.ac.up.cs.cirg.ciclops.domain.Worker;
import za.ac.up.cs.cirg.ciclops.domain.WorkerUtil;
import za.ac.up.cs.cirg.ciclops.model.DataSetCacheEntry;
import za.ac.up.cs.cirg.ciclops.model.MeasurementModel;
import za.ac.up.cs.cirg.ciclops.model.WorkerModel;

/**
 * 
 * @ejb.bean name="Supervisor"
 *   type="Stateful"
 *   view-type="remote"
 *   jndi-name="ejb/ciclops/services/Supervisor"
 * 
 * @ejb.ejb-ref ejb-name="Simulation" view-type="local"
 * @ejb.ejb-ref ejb-name="DataSet" view-type="local"
 * @ejb.ejb-ref ejb-name="Sample" view-type="local"
 * @ejb.ejb-ref ejb-name="Measurement" view-type="local"
 * @ejb.ejb-ref ejb-name="Worker" view-type="local"
 * @ejb.ejb-ref ejb-name="Error" view-type="local"
 * 
 * 
 * @ejb.transaction type="Required"
 *
 * @ejb.util generate="physical"
 * 
 * @ejb.permission unchecked="true"
 *
 * @author  espeer
 */
public class SupervisorBean implements SessionBean {
    
    /**
     * @ejb.create-method
     */
    public void ejbCreate(WorkerModel details) throws CreateException {
        try {
        	dataSetHome = DataSetUtil.getLocalHome();
            simulationHome = SimulationUtil.getLocalHome();
            sampleHome = SampleUtil.getLocalHome();
            measurementHome = MeasurementUtil.getLocalHome();
            errorHome = ErrorUtil.getLocalHome();
            
            worker = WorkerUtil.getLocalHome().create();
            
            worker.setHostName(details.getHostName());
            worker.setOperatingSystem(details.getOperatingSystem());
            worker.setVirtualMachine(details.getVirtualMachine());
            
            worker.setStatus("Active");
        }
        catch (NamingException ex) {
            throw new EJBException(ex);
        }
    }
    
    /**
     * @ejb.interface-method
     */
    public String getJob() throws JobNotFoundException {
        
    	// Try to return an incomplete simulation
    	try {
            Iterator i = simulationHome.findIncompleteSimulations().iterator();
            while (i.hasNext()) {
                try {
                	Simulation simulation = (Simulation) i.next();
                    simulation.registerWorker(worker);
                    worker.setStatus("Working");
                    return simulation.getXMLConfiguration();
                }
                catch (ReservationException ex) { /* Try to get a reservation for the next Simulation */ }
            }
        }
        catch (FinderException ex) { }
        
        // Since there are no more pending simulations recompute a stale simulation 
        try {
        	Iterator i = simulationHome.findStaleSimulations().iterator();
        	if (i.hasNext()) {
        		Simulation simulation = (Simulation) i.next();
        		simulation.clearSamples();
        		simulation.registerWorker(worker);
                worker.setStatus("Working");
        		return simulation.getXMLConfiguration();
        	}
        }
        catch (FinderException ex) { }
        catch (ReservationException ex) { }
        catch (RemoveException ex) { }
        
        worker.setStatus("Idle");
        
        throw new JobNotFoundException("Nothing to do");
    }
    
    /**
     * @ejb.interface-method
     */
    public void storeResults(MeasurementModel[] sampleData) throws CreateException {
        Simulation simulation = worker.getSimulation();
        Sample sample = sampleHome.create(simulation);
        
        for (int i = 0; i < sampleData.length; ++i) {
        	measurementHome.create(sample, sampleData[i].getDomain(), sampleData[i].getData());
        }
        
        simulation.deregisterWorker(worker);
        worker.setStatus("Active");
    }
    
    /**
     *@ejb.interface-method
     */
    public void handleError(Throwable throwable) throws CreateException {
    	errorHome.create(worker, throwable);
    }
    
    /**
     * @ejb.interface-method
     */
    public byte[] getData(int dataSetId) throws FinderException {
    	DataSet dataSet = dataSetHome.findByPrimaryKey(new Integer(dataSetId));
    	return dataSet.getData();
    }
    
    /**
     * @ejb.interface-method
     */
    public Collection getDataSetCacheEntries() {
    	Collection list = new LinkedList();
    	Simulation simulation = worker.getSimulation();
    	Iterator i = simulation.getDataSetDependencies().iterator();
    	while (i.hasNext()) {
    		DataSet dataSet = (DataSet) i.next();
    		list.add(new DataSetCacheEntry(dataSet.getId().intValue(), dataSet.getVersion()));
    	}
    	return list;
    }
    
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
        worker.setStatus("Active");
    }    
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
        worker.setStatus("Passive");
    }

    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
        try {
            worker.remove();
        }
        catch (RemoveException ex) {
            throw new EJBException(ex);
        }
    }
    
    public void setSessionContext(javax.ejb.SessionContext sessionContext) throws javax.ejb.EJBException, java.rmi.RemoteException {
    	
    }
   
    private DataSetHome dataSetHome;
    private SimulationHome simulationHome;
    private SampleHome sampleHome;
    private MeasurementHome measurementHome;
    private ErrorHome errorHome;
    
    private Worker worker;
    
}
