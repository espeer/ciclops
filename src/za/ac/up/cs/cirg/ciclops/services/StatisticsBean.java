/*
 * StatisticsBean.java 
 *
 * Copyright (C) 2003 - Edwin S Peer
 *
 * Created on June 7, 2003, 11:04 AM
 */

package za.ac.up.cs.cirg.ciclops.services;

import java.util.Arrays;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;

/**
 * @ejb:bean name="Statistics"
 *   type="Stateless"
 *   view-type="local"
 *   local-jndi-name="ejb/OptiBench/services/Statistics"
 *
 * @ejb:home local-class="${package}.services.StatisticsHome"
 * @ejb:interface local-class="${package}.services.Statistics"
 *
 * @ejb:transaction type="Required"
 *
 * @ejb:util generate="physical"
 *
 * @jboss-net:web-service urn="Statistics"
 * 
 * @ejb.permission unchecked="true"
 *
 * @author  espeer
 */
public class StatisticsBean implements SessionBean {
        
    /**
     * @ejb:create-method
     */
    public void ejbCreate() throws CreateException {
        
    }
    
    private double gammln(double x) {
        double y = x;
        double tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int j = 0; j <= 5; ++j) {
            ser += cof[j] / ++y;
        }
        
        return -tmp + Math.log(2.506628274631005 * ser / x);
    }
    
    private double erfc(double x) {
        double z = Math.abs(x);
        double t = 1.0 / (1.0 + 0.5 * z);
        double ans = t * Math.exp(-z * z - 1.26551223 + t * (1.00002368 + t * (0.37409196 + t * (0.09678418 +
            t * (-0.18628806 + t * (0.27886807 + t * (-1.13520398 + t * (1.48851587 +
            t * (-0.82215223 + t * (0.17087277))))))))));
        return x >= 0.0 ? ans : 2.0 - ans;
    }
    
    private double erf(double x) {
        return 1.0 - erfc(x);
    }
    
    /**
     * @ejb:interface-method
     * @jboss-net:web-method
     */ 
    public double H(double x) {
        return erf(x / Math.sqrt(2.0)) / 2;
    }
    
    /**
     * @ejb:interface-method
     * @jboss-net:web-method
     */
    public double N(double x) {
        if (x < 0) {
            return 0.5 - H(-x);
        }
        else {
            return 0.5 + H(x);
        }
    }
    
    /**
     * @ejb:interface-method
     */
    public double KSNormalTest(double[] data) {
        Arrays.sort(data);
        double sum = 0;
        double sumsq = 0;
        for (int i = 0; i < data.length; ++i) {
            sum += data[i];
            sumsq += data[i] * data[i];
        }
        double ave = sum / data.length;
        double dev = Math.sqrt( (sumsq - (sum * sum / data.length)) / (data.length - 1) );
        for (int i = 0; i < data.length; ++i) {
            data[i] = (data[i] - ave) / dev;
        }
        
        double fo = 0.0;
        double en = data.length;
        double d = 0.0;
        for (int j = 0; j < data.length; ++j) {
            double fn = (j + 1) / en;
            double ff = N(data[j]);
            double dt = Math.max(Math.abs(fo - ff), Math.abs(fn - ff));
            if (dt > d) {
                d = dt;
            }
            fo = fn;
        }
        en = Math.sqrt(en);
        return probks((en + 0.12 + 0.11 / en) * d);
    }
    
    private double probks(double alam) {
        double a2 = -2.0 * alam * alam;
        double fac = 2.0;
        double sum = 0.0;
        double termbf = 0.0;
        for (int j = 1; j <= 100; ++j) {
            double term = fac * Math.exp(a2 * j * j);
            sum += term;
            if (Math.abs(term) <= 0.001 * termbf || Math.abs(term) <= 1.0e-8 * sum) {
                return sum;
            }
            fac = -fac;
            termbf = Math.abs(term);
        }
        return 1.0;
    }
    
    public void ejbPostCreate() throws CreateException {
    }
        
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }    
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void setSessionContext(javax.ejb.SessionContext sessionContext) throws javax.ejb.EJBException, java.rmi.RemoteException {
    }

    private static final double[] cof = {76.18009172947146, -86.50532032941677, 24.01409824083091,
                                          -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5};

    
}
