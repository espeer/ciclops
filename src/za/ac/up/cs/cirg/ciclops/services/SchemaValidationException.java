/*
 * Created on Mar 11, 2004
 */
package za.ac.up.cs.cirg.ciclops.services;

/**
 * @author espeer
 */
public class SchemaValidationException extends Exception {
    public SchemaValidationException(String message) {
        super(message);
    }
    
    public SchemaValidationException(Throwable cause) {
        super(cause);
    }
    
    public SchemaValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
