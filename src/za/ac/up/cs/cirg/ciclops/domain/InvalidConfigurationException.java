/*
 * Created on Mar 8, 2004
 */
package za.ac.up.cs.cirg.ciclops.domain;

/**
 * @author espeer
 */
public class InvalidConfigurationException extends Exception {

    public InvalidConfigurationException() {
        super();
    }
    
    public InvalidConfigurationException(String message) {
        super(message);
    }
    
    public InvalidConfigurationException(Exception ex) {
    	super(ex);
    }
}
