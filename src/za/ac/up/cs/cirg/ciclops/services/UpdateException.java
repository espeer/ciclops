/*
 * UpdateException.java
 *
 * Copyright (C) 2003 - Edwin S. Peer
 *
 * Created on June 11, 2003, 9:31 PM
 */

package za.ac.up.cs.cirg.ciclops.services;

/**
 *
 * @author  espeer
 */
public class UpdateException extends Exception {
    
    /** Creates a new instance of UpdateException */
    public UpdateException() {
    }

    public UpdateException(String message) {
        super(message);
    }
    
}
