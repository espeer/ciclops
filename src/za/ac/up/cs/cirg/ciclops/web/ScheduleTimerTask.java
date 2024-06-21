/*
 * ScheduleTimerTask.java
 *
 * Copyright (C) 2003 - Edwin S. Peer
 *
 * Created on July 11, 2003, 5:32 PM
 */

package za.ac.up.cs.cirg.ciclops.web;

import java.net.URL;

import javax.ejb.EJBException;

import za.ac.up.cs.cirg.ciclops.services.CodeManagerLocal;
import za.ac.up.cs.cirg.ciclops.services.CodeManagerUtil;

/**
 * @author  espeer
 */
public class ScheduleTimerTask extends java.util.TimerTask {
    
    /** Creates a new instance of RescheduleTimerTask */
    public ScheduleTimerTask() {
        try {
            code = CodeManagerUtil.getLocalHome().create();
        }
        catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    
    public void run() {
        try {
            code.loadCodeFromJar(new URL("jar:file:///tmp/cilib.jar!/"));
        }
        catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    
    private CodeManagerLocal code;
}
