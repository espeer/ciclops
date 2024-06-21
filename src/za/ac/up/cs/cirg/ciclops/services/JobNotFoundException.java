/*
 * Created on Mar 8, 2004
 */
package za.ac.up.cs.cirg.ciclops.services;

/**
 * @author espeer
 */
public class JobNotFoundException extends Exception {

    public JobNotFoundException() {
        super();
    }
    
    public JobNotFoundException(String message) {
        super(message);
    }
}
