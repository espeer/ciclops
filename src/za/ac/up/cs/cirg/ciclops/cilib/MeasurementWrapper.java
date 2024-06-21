/*
 * Created on Jun 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package za.ac.up.cs.cirg.ciclops.cilib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ejb.EJBException;

/**
 * @author espeer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MeasurementWrapper {
	
	public MeasurementWrapper(Object measurement) throws InvocationTargetException {
		measurementInstance = measurement;
		measurementClass = measurementInstance.getClass();
	}

	public String getDomain() throws InvocationTargetException {
		try {
			Method getDomainMethod = measurementClass.getMethod("getDomain", null);
			return (String) getDomainMethod.invoke(measurementInstance, null);
		}
		catch (NoSuchMethodException ex) {
			throw new EJBException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new EJBException(ex);
		}
	}
	
	public String getName() throws InvocationTargetException {
		return measurementClass.getName();
	}
	
	private Class measurementClass;
	private Object measurementInstance;
	
}
