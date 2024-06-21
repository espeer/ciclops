/*
 * Created on Jun 14, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package za.ac.up.cs.cirg.ciclops.cilib;

import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.ejb.EJBException;

/**
 * @author espeer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DomainWrapper {
	
	public DomainWrapper(ClassLoader loader, String domain) throws InvocationTargetException {
		try {
			Class factoryClass = loader.loadClass("net.sourceforge.cilib.Domain.ComponentFactory");

			Method instanceMethod = factoryClass.getMethod("instance", null);
			Object factory = instanceMethod.invoke(null, null);
			
			Method newComponentMethod = factoryClass.getMethod("newComponent", new Class[] { String.class });
			component = newComponentMethod.invoke(factory, new Object[] { domain });
			
			Class componentClass = component.getClass();
			
			deserialiseMethod = componentClass.getMethod("deserialise", new Class[] { ObjectInputStream.class });
			getDimensionMethod = componentClass.getMethod("getDimension", null);
		}
		catch (ClassNotFoundException ex) {
			throw new EJBException(ex);
		}
		catch (NoSuchMethodException ex) {
			throw new EJBException(ex);
		}
		catch (IllegalAccessException ex) {
			throw new EJBException(ex);
		}
	}

	public Object deserialise(ObjectInputStream ois) throws InvocationTargetException {
		try {
			return deserialiseMethod.invoke(component, new Object[] { ois });
		}
		catch (IllegalAccessException ex) {
			throw new EJBException(ex);
		}
	}
	
	public int getDimension() throws InvocationTargetException {
		try {
			return ((Integer) getDimensionMethod.invoke(component, null)).intValue();
		}
		catch (IllegalAccessException ex) {
			throw new EJBException(ex);
		}
	}
	
	private Object component;
	private Method deserialiseMethod;
	private Method getDimensionMethod;
	
}
