/*
 * SchemaBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on Frebruary 10, 2004, 12:21 AM
 */

package za.ac.up.cs.cirg.ciclops.services;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import za.ac.up.cs.cirg.ciclops.domain.Code;
import za.ac.up.cs.cirg.ciclops.domain.CodeHome;
import za.ac.up.cs.cirg.ciclops.domain.CodeUtil;
import za.ac.up.cs.cirg.ciclops.model.SchemaModel;

/**
 * 
 * @ejb.bean name="Schema"
 *   type="Stateful"
 *   view-type="both"
 *   jndi-name="ejb/ciclops/services/Schema"
 *
 * @ejb.ejb-ref ejb-name="Code" view-type="local" 
 * 
 * @ejb.transaction type="Required"
 *
 * @ejb.util generate="physical"
 * 
 * @ejb.permission unchecked="true"
 *
 * @author  espeer
 */
public class SchemaBean implements SessionBean {
    
    /**
     * @ejb.create-method
     */
    public void ejbCreate() throws CreateException {
        setUp();
    }
    
    /**
     * @ejb.interface-method
     */
    public Document transform(String source) throws TransformerException {
    	Document destination = documentBuilder.newDocument();
    	transformer.transform(new StreamSource(new StringReader(source)), new DOMResult(destination));
        return destination;
    }
    
    /**
     * @ejb.interface-method
     */
    public String transform(Document source) throws TransformerException {
		StringWriter destination = new StringWriter();
		transformer.transform(new DOMSource(source), new StreamResult(destination));
		return destination.toString();
    }
    
    /**
     * @ejb.interface-method
     */
    public String getClassesOfType(String type) throws ClassNotFoundException {
    	ClassLoader loader = new EJBClassLoader(codeHome);
        
    	Class classType = getClass(loader, type);
        
        Document classes = documentBuilder.newDocument();
        Element root = classes.createElement(SchemaModel.CLASSES_TAG);
        
        
        try {
            Iterator i = codeHome.findAll().iterator();
            while (i.hasNext()) {
                Code code = (Code) i.next();
                try {
                    Class clazz = loader.loadClass(code.getClassName());
                    clazz.newInstance();
                    if (classType.isAssignableFrom(clazz)) {
                        root.appendChild(buildClassElement(classes, clazz));
                    }
                }
                catch (Throwable t) { }
            }
        }
        catch (FinderException ex) {
            throw new ClassNotFoundException(type);
        }
        
        classes.appendChild(root);
        
        try {
        	return transform(classes);
        }
        catch (TransformerException ex) {
        	throw new EJBException(ex);
        }
    }
    
    /**
     * @ejb.interface-method
     */
    public void validate(String xmlConfig) throws SchemaValidationException {
        try {
            validate(transform(xmlConfig));
        }
        catch (TransformerException ex) {
            throw new SchemaValidationException("Could not parse configuration", ex);
        }        
    }
    
    /**
     * @ejb.interface-method
     */
    public void validate(Document config) throws SchemaValidationException {
    	ClassLoader loader = new EJBClassLoader(codeHome);
    	try {
    		validateProperty(loader, getSchema(null), config.getDocumentElement());
    	}
    	catch (NoSuchSchemaException ex) {
    		throw new SchemaValidationException(ex);
    	}
    }
    
    /**
     * @ejb.interface-method
     */
    public String getSchemaForType(String type) throws NoSuchSchemaException {
    	try {
    		return transform(getSchema(type));
    	}
    	catch (TransformerException ex) {
    		throw new EJBException(ex);
    	}
    }
    
    private Document getSchema(String type) throws NoSuchSchemaException {
    	ClassLoader loader = new EJBClassLoader(codeHome);
    	
        if (type == null) {
            type = ROOT_KEY;
        }
        
        Document schema = documentBuilder.newDocument();
        Element root = null;
        if (type.equals(ROOT_KEY)) {
            root = buildRootSchema(schema);
        }
        else {
            try {
                root = buildSchema(schema, getClass(loader, type).newInstance());
            }
            catch (Exception ex) {
                throw new NoSuchSchemaException(ex);
            }
        }
        schema.appendChild(root);
        
        return schema;
    }
    
    private Element buildRootSchema(Document doc) {
        Element root = doc.createElement(SchemaModel.CLASS_TAG);
        root.setAttribute(SchemaModel.NAME_ATTRIBUTE, ROOT_KEY);
        
        Element property = doc.createElement(SchemaModel.PROPERTY_TAG);
        property.setAttribute(SchemaModel.FORM_ATTRIBUTE, SchemaModel.FORM_CLASS);
        property.setAttribute(SchemaModel.NAME_ATTRIBUTE, "simulation");
        property.setAttribute(SchemaModel.TYPE_ATTRIBUTE, "CiClops.Simulation");
        
        root.appendChild(property);
        
        return root;
    }
    
    private Element buildSchema(Document doc, Object instance) {
        Element classNode = buildClassElement(doc, instance.getClass());
        Method[] methods = instance.getClass().getMethods();
        for (int i = 0; i < methods.length; ++i) {
            Element property = null;
            try {
                // Build an element for the property (if it is a property)
                property = buildPropertyElement(doc, methods[i]);
            }
            catch (IllegalArgumentException ex) {
                continue; // The method is not a property or the property should be excluded
            }
            
            // Add any defaults
            Object defaultValue = getValue(instance, property.getAttribute(SchemaModel.NAME_ATTRIBUTE));
            if (defaultValue != null) {
                String form = property.getAttribute(SchemaModel.FORM_ATTRIBUTE);
                if (form.equals(SchemaModel.FORM_BASIC)) {
                    property.setAttribute(SchemaModel.DEFAULT_ATTRIBUTE, defaultValue.toString());
                }
                else if (form.equals(SchemaModel.FORM_CLASS)) {
                    property.appendChild(buildSchema(doc, defaultValue));
                }
                else if (form.equals(SchemaModel.FORM_MULTIPLE)) {
                    Iterator iter = ((Collection) defaultValue).iterator();
                    while (iter.hasNext()) {
                        property.appendChild(buildSchema(doc, iter.next()));
                    }
                }
            }
            
            classNode.appendChild(property);
        }
        
        return classNode;    
    }
    
    private Element buildClassElement(Document doc, Class clazz) {
        Element tmp = doc.createElement(SchemaModel.CLASS_TAG);
        tmp.setAttribute(SchemaModel.NAME_ATTRIBUTE, getUnqualifiedName(clazz));
        return tmp;
    }
    
    private Element buildPropertyElement(Document doc, Method method) {
        String methodName = method.getName();
        
        Class[] parameters = method.getParameterTypes();
        if (parameters.length != 1) {
            throw new IllegalArgumentException(methodName + " is not a property (wrong number of parameters");
        }
        Class parameter = promoteBasicType(parameters[0]);
        
        String propertyName = convertToProperty(methodName);
        
        String form = null;
        if (methodName.startsWith("set")) {
            if (String.class.isAssignableFrom(parameter) || Number.class.isAssignableFrom(parameter)) {
                form = SchemaModel.FORM_BASIC;
            }
            else if (parameter.getName().equals(CILIB_PACKAGE_NAME + ".Problem.DataSet")) {
            	// Special case for data sets - TODO handle subclasses of DataSet properly
            	form = SchemaModel.FORM_DATA_SET;
            }
            else {
                form = SchemaModel.FORM_CLASS;
            }
        }
        else if (methodName.startsWith("add")) {
            // This is a special kind of property supported by CILib (measurements, stopping conditions... etc)
            form = SchemaModel.FORM_MULTIPLE;
        }
        else {
            throw new IllegalArgumentException(methodName + " is not a property (invalid method name)");
        }
        
        // Honour any exclusions requested by the class
        try {
            method.getDeclaringClass().getField("_ciclops_exclude_" + propertyName);
            throw new IllegalArgumentException(propertyName + " should not be included");
        }
        catch (NoSuchFieldException ex) { /* the field did not exist - continue */ }
                
        Element property = doc.createElement(SchemaModel.PROPERTY_TAG);
        property.setAttribute(SchemaModel.FORM_ATTRIBUTE, form);
        property.setAttribute(SchemaModel.NAME_ATTRIBUTE, propertyName);
        property.setAttribute(SchemaModel.TYPE_ATTRIBUTE, getUnqualifiedName(parameter));
        
        return property;
    }
    
    private Object getValue(Object instance, String property) {
        String methodName = null;
        if (property.length() == 1) {
            methodName = "get" + property.toUpperCase();
        }
        else {
            methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
        }
        try {
            Method method = instance.getClass().getMethod(methodName, null);
            return method.invoke(instance, null);
        }
        catch (Exception ex) {
            return null;
        }
    }
           
    private Class getClass(ClassLoader loader, String name) throws ClassNotFoundException {
        try {
            String tmp = CILIB_PACKAGE_NAME + "." + name;
            codeHome.findByPrimaryKey(tmp);
            name = tmp;
        }
        catch (FinderException ex) { /* no problem - use the original name */ }
        
        return loader.loadClass(name);
    }
    
    private String getUnqualifiedName(Class clazz) {
        String name = clazz.getName();
        if (name.startsWith(CILIB_PACKAGE_NAME)) {
            return name.substring(CILIB_PACKAGE_NAME.length() + 1);
        }
        return name;
    }
    
    private String convertToProperty(String methodName) {
        // Properties are of the form setXXX(...) which is not possible if length < 4
        int length = methodName.length();
        if (length < 4) {
            throw new IllegalArgumentException(methodName + " is not a property");
        }
        
        if (length == 4) {
            return methodName.substring(3).toLowerCase();
        }
        else {
            return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        }
    }
    
    private Class promoteBasicType(Class type) {
    	if (type.equals(Boolean.TYPE)) {
    		return Boolean.class;
    	}
    	else if (type.equals(Byte.TYPE)) {
    		return Byte.class;
    	}
    	else if (type.equals(Character.TYPE)) {
    		return Character.class;
    	}
    	else if (type.equals(Double.TYPE)) {
    		return Double.class;
    	}
    	else if (type.equals(Float.TYPE)) {
    		return Float.class;
    	}
    	else if (type.equals(Integer.TYPE)) {
    		return Integer.class;
    	}
    	else if (type.equals(Long.TYPE)) {
    		return Long.class;
    	}
    	else if (type.equals(Short.TYPE)) {
    		return Short.class;
    	}
    	else if (type.equals(Void.TYPE)) {
    		return Void.class;
    	}
    	return type;
    }
    
    private void validateProperty(ClassLoader loader, Document schema, Element element) throws SchemaValidationException {
        Element schemaClassNode = schema.getDocumentElement();
        String property = element.getTagName();
        
        // Drop add from tag name (schema names multi-properties without it) 
        if (property.startsWith("add")) {
            try {
                property = convertToProperty(property);
            }
            catch (IllegalArgumentException ex) { /* Ignore - it could be for setAdd() which is valid */ }
        }
        
        Element schemaPropertyNode = getPropertyElement(schemaClassNode, property);
        
        String form = schemaPropertyNode.getAttribute(SchemaModel.FORM_ATTRIBUTE);
        String type = schemaPropertyNode.getAttribute(SchemaModel.TYPE_ATTRIBUTE);
                
        if (form.equals(SchemaModel.FORM_BASIC)) {
            validateBasicPropertyValue(loader, property, type, getPropertyValue(element, property));
        }
        else if (form.equals(SchemaModel.FORM_CLASS) || form.equals(SchemaModel.FORM_MULTIPLE) || form.equals(SchemaModel.FORM_DATA_SET)) {
            validateClassProperty(loader, element, schemaClassNode, element.getTagName(), type);        
        }
        else {
            throw new SchemaValidationException("Illegal property form in schema");
        }
    }
    
    private void validateClassProperty(ClassLoader loader, Element element, Element schemaClassNode, String property, String type) throws SchemaValidationException {
        // Check the class attribute of the element
        if (! element.hasAttribute("class")) {
            throw new SchemaValidationException("No class specified for " + property);
        }
        Class configType = null;
        try {
            Class schemaType = getClass(loader, type);
            configType = getClass(loader, element.getAttribute("class"));
            if (! schemaType.isAssignableFrom(configType)) {
                throw new ClassNotFoundException();
            }  
        }
        catch (ClassNotFoundException ex) {
            throw new SchemaValidationException("\"" + element.getAttribute("class") + "\" is not a valid type for " + property);
        }
        
        // Get the schema for the configured element
        Document configSchema = null;
        try {
            configSchema = getSchema(getUnqualifiedName(configType));
        }
        catch (NoSuchSchemaException ex) {
            throw new SchemaValidationException(ex);
        }
            
        // Handle attribute properties
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Attr attribute = (Attr) attributes.item(i);
            if (! attribute.getName().equals("class")) {
                String propertyName = attribute.getName();
                String propertyType = getPropertyElement(configSchema.getDocumentElement(), propertyName).getAttribute(SchemaModel.TYPE_ATTRIBUTE);
                validateBasicPropertyValue(loader, propertyName, propertyType, attribute.getValue());
            }
        }

        // Handle nested element properties
        Node child = element.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                validateProperty(loader, configSchema, (Element) child);
            }
            child = child.getNextSibling();
        }
    }

    private void validateBasicPropertyValue(ClassLoader loader, String property, String type, String value) throws SchemaValidationException {
        if (value == null) {
            throw new SchemaValidationException(property + " may not be null");
        }
        
        try {
            Class[] parameters = { String.class };
            Object[] values = { value.trim() };
            getClass(loader, type).getConstructor(parameters).newInstance(values);
        }
        catch (Exception ex) {
            throw new SchemaValidationException("Invalid value for " + property, ex);
        }
    }
    
    private String getPropertyValue(Element element, String property) {
        String value = null;
        
        if (element.hasAttribute("value")) {
            value = element.getAttribute("value");
        }
        else if (element.hasChildNodes()) {
            if (element.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                value =  ((Text) element.getFirstChild()).getData();
            }
        }
        
        return value;
    }
    
    private Element getPropertyElement(Element schemaClass, String name) throws SchemaValidationException {
        Node child = schemaClass.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element tmp = (Element) child;
                if (name.equals(tmp.getAttribute(SchemaModel.NAME_ATTRIBUTE))) {
                    return tmp;
                }
            }
            child = child.getNextSibling();
        }
        throw new SchemaValidationException(schemaClass.getAttribute(SchemaModel.NAME_ATTRIBUTE) + " does not define property " + name);
    }
    
    private void setUp() {
        try {
            codeHome = CodeUtil.getLocalHome();
         
            // Transormer is not thread-safe therefore Schema is a stateful session bean (1 per client)
            String xsl = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:output method=\"xml\" indent=\"yes\"/><xsl:strip-space elements=\"*\"/><xsl:template match=\"/\"><xsl:copy-of select=\".\"/></xsl:template></xsl:stylesheet>";
            transformer = TransformerFactory.newInstance().newTemplates(new StreamSource(new StringReader(xsl))).newTransformer();
//            transformer = TransformerFactory.newInstance().newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            // DocumentBuilder is not thread-safe therefore Schema is a stateful session bean (1 per client)
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            
        }
        catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    private void tearDown() {
        codeHome = null;
        transformer = null;
        documentBuilder = null;
    }
    
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
        setUp();
    }    
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
        tearDown();
    }

    public void ejbRemove() throws javax.ejb.EJBException, java.rmi.RemoteException {
        tearDown();
    }
    
    public void setSessionContext(javax.ejb.SessionContext sessionContext) throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    private CodeHome codeHome;
    private Transformer transformer;
    private DocumentBuilder documentBuilder;
    
    private static final String CILIB_PACKAGE_NAME = "net.sourceforge.cilib";
    private static final String ROOT_KEY = "<config root>";
}
