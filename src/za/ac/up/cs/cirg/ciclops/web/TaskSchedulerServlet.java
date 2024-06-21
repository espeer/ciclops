/*
 * TaskSchedulerServlet.java
 *
 * Copyright (C) 2003 - Edwin S. Peer
 *
 * Created on July 11, 2003, 5:12 PM
 */

package za.ac.up.cs.cirg.ciclops.web;

import java.io.IOException;
import java.util.Timer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author  espeer
 */
public class TaskSchedulerServlet extends HttpServlet {
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        timer = new Timer();
        timer.schedule(new ScheduleTimerTask(), 0, 60000);
    }
    
    public void destroy() {
        timer.cancel();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    }
    
    public String getServletInfo() {
        return "CiClops task rescheduler servlet";
    }

    private Timer timer;
}
