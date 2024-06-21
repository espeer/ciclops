/*
 * Created on Mar 23, 2004
 */
package za.ac.up.cs.cirg.ciclops.model;

import java.io.Serializable;

/**
 * @author espeer
 */
public class BrowserItem implements Serializable {

	public BrowserItem() {
		type = TYPE_CATEGORY;
		id = CATEGORY_ROOT;
		name = "/";
		path = "/";
	}
	
    public BrowserItem(int type, int id, String name) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.description = "";
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }
    
    
    
    /**
     * @return Returns the path.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path The path to set.
     */
    public void setPath(String path) {
        this.path = path;
    }

    public String toString() {
    	return name;
    	/*
        StringBuffer tmp = new StringBuffer();
        switch (type) {
            case TYPE_CATEGORY: tmp.append("<C> "); break;
            case TYPE_SIMULATION: tmp.append("<S> "); break;
            case TYPE_DATA_SET: tmp.append("<D> "); break;
            default: tmp.append("<U> "); break;
        }
        tmp.append(name);
        return tmp.toString();
        */
    }

    private int id;
    private int type;
    private String path;
    private String name;
    private String description;
    
    public static final int TYPE_CATEGORY = 1;
    public static final int TYPE_SIMULATION = 2;
    public static final int TYPE_DATA_SET = 3;
    
    public static final int CATEGORY_ROOT = -1;
}
