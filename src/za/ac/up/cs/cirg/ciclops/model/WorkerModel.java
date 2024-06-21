/*
 * Created on Mar 9, 2004
 */
package za.ac.up.cs.cirg.ciclops.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author espeer
 */
public class WorkerModel implements Serializable {
    
	public WorkerModel(int id) {
		this.id = id;
	}
	
    public WorkerModel(String hostName, String operatingSystem, String virtualMachine) {
        this.hostName = hostName;
        this.operatingSystem = operatingSystem;
        this.virtualMachine = virtualMachine;
    }
    
    public int getId() {
    	return id;
    }
    
    /**
     * @return Returns the hostName.
     */
    public String getHostName() {
        return hostName;
    }
    
    public void setHostName(String hostName) {
    	this.hostName = hostName;
    }

    /**
     * @return Returns the operatingSystem.
     */
    public String getOperatingSystem() {
        return operatingSystem;
    }
    
    public void setOperatingSystem(String operatingSystem) {
    	this.operatingSystem = operatingSystem;
    }

    /**
     * @return Returns the virtualMachine.
     */
    public String getVirtualMachine() {
        return virtualMachine;
    }
    
    public void setVirtualMachine(String virtualMachine) {
    	this.virtualMachine = virtualMachine;
    }

    public int getSimulation() {
    	return simulation;
    }
    
    public void setSimulation(int simulation) {
    	this.simulation = simulation;
    }
    
    public void setStatus(String status) {
    	this.status = status;
    }
    
    public String getStatus() {
    	return status;
    }
    
    public void setLastUpdate(Date lastUpdate) {
    	this.lastUpdate = lastUpdate;
    }
    
    public Date getLastUpdate() {
    	return this.lastUpdate;
    }
    
    private String hostName;
    private String operatingSystem;
    private String virtualMachine;
    private String status;
    private Date lastUpdate;
    private int simulation;
    private int id;
    
}
