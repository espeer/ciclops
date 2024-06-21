/*
 * Created on Jun 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package za.ac.up.cs.cirg.ciclops.cilib;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ejb.EJBException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import za.ac.up.cs.cirg.ciclops.model.MeasurementModel;

/**
 * @author espeer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SimulationWrapper {
	
	public SimulationWrapper(ClassLoader loader, Document configuration) throws InvocationTargetException {
		try {
			collectorClass = loader.loadClass("net.sourceforge.cilib.CiClops.MeasurementCollector");
			dataSetClass = loader.loadClass("net.sourceforge.cilib.CiClops.CachedDataSet");
			
			Class factoryClass = loader.loadClass("net.sourceforge.cilib.XML.XMLSimulationFactory");
			Constructor constructor = factoryClass.getConstructor(new Class[] { Document.class, Element.class });
			Object factory = constructor.newInstance(new Object[] { configuration, configuration.getDocumentElement() });
		
			Method newSimulationMethod = factoryClass.getMethod("newSimulation", null);
			simulationInstance = newSimulationMethod.invoke(factory, null);
			
			simulationClass = simulationInstance.getClass();
		}
		catch (ClassNotFoundException ex) {
			throw new EJBException(ex);
		}
		catch (NoSuchMethodException ex) {
			throw new EJBException(ex);
		}
		catch (InstantiationException ex) {
			throw new EJBException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new EJBException(ex);
		}
	}

	public void initialise() throws InvocationTargetException {
		try {
			Method initialiseMethod = simulationClass.getMethod("initialise", null);
			initialiseMethod.invoke(simulationInstance, null);
		}
		catch (NoSuchMethodException ex) {
			throw new EJBException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new EJBException(ex);
		}
	}
	
	public void run() throws InvocationTargetException {
		try {
			Method runMethod = simulationClass.getMethod("run", null);
			runMethod.invoke(simulationInstance, null);
		}
		catch (NoSuchMethodException ex) {
			throw new EJBException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new EJBException(ex);
		}
	}
	
	public MeasurementModel[] getSampleData() throws InvocationTargetException {
		try {
			Method getSampleMethod = simulationClass.getMethod("getSampleData", null);
			
			Method getDomainMethod = collectorClass.getMethod("getDomain", null);
			Method getDataMethod = collectorClass.getMethod("getData", null);
			
			Object sample = getSampleMethod.invoke(simulationInstance, null);

			int measurements = Array.getLength(sample);
			
			MeasurementModel[] model = new MeasurementModel[measurements];
			
			for (int i = 0; i < measurements; ++i) {
				Object collector = Array.get(sample, i);
				model[i] = new MeasurementModel((String) getDomainMethod.invoke(collector, null), (byte[]) getDataMethod.invoke(collector, null));
			}
			
			return model;
		}
		catch (NoSuchMethodException ex) {
			throw new EJBException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new EJBException(ex);
		}		
	}
	
	public void setDataPath(String dataPath) {
		try {
			Field dataPathField = dataSetClass.getField("CACHE_PATH");
			dataPathField.set(null, dataPath);
		}
		catch (NoSuchFieldException ex) {
			throw new EJBException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new EJBException(ex);
		}
	}

	private Class dataSetClass;
	private Class simulationClass;
	private Class collectorClass;
	private Object simulationInstance;
	
}
