/*
 * Created on Apr 8, 2004
 */
package za.ac.up.cs.cirg.ciclops.model;

import java.io.Serializable;

/**
 * @author espeer
 */
public class DataSetModel implements Serializable {

    public static final int NEW_ID = -1;

    public DataSetModel() {
    	this.id = NEW_ID;
    }
    
    public DataSetModel(int id) {
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
     * @return Returns the data.
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data The data to set.
     */
    public void setData(byte[] data) {
        this.data = data;
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
     * @return Returns the id.
     */
    public int getId() {
        return id;
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

    private int id;
    private byte[] data;
    private String name;
    private String description;
  
    private BrowserItem category;
}
