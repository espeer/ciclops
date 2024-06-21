/*
 * Created on Apr 8, 2004
 */
package za.ac.up.cs.cirg.ciclops.model;

import java.io.Serializable;

/**
 * @author espeer
 */
public class SimulationModel implements Serializable {
	
    public static final int NEW_ID = -1;

    public SimulationModel() {
    	this.id = NEW_ID;
    	configuration = "<simulation class=\"CiClops.Simulation\"/>";
    	totalSamples = 100;
    }
    
    public SimulationModel(int id) {
        this.id = id;
    }
    
    /**
     * @return Returns the category.
     */
    public BrowserItem getCategory() {
        return category;
    }
    
    /**
     * @param category The category to set.
     */
    public void setCategory(BrowserItem category) {
        this.category = category;
    }

    /**
     * @return Returns the configuration.
     */
    public String getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration The configuration to set.
     */
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @deprecated 
     * @return Returns the numberOfSamples.
     */
    public int getNumberOfSamples() {
        return totalSamples;
    }

    /**
     * @deprecated
     * @param numberOfSamples The numberOfSamples to set.
     */
    public void setNumberOfSamples(int numberOfSamples) {
        this.totalSamples = numberOfSamples;
    }

    public void setTotalSamples(int totalSamples) {
    	this.totalSamples = totalSamples;
    }
    
   public int getTotalSamples() {
   		return totalSamples;
   }
    
    public void setCompletedSamples(int completedSamples) {
    	this.completedSamples = completedSamples;
    }
    
    public int getCompeltedSamples() {
    	return completedSamples;
    }
    
    public int getErrors() {
    	return errors;
    }
    
    public void setErrors(int errors) {
    	this.errors = errors;
    }
    
    public void setStale(boolean stale) {
    	this.stale = stale;
    }
    
    public boolean isStale() {
    	return stale;
    }
    
    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    private int id;
    private String name;
    private String description;
    private String configuration;
    private int totalSamples;
    private int completedSamples;
    private int errors;
    private boolean stale;
    
    private BrowserItem category;
    
}
