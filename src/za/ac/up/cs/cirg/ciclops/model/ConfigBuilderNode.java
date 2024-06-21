/*
 * Created on Mar 12, 2004
 */
package za.ac.up.cs.cirg.ciclops.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.tree.TreePath;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import za.ac.up.cs.cirg.ciclops.client.UnexpectedError;
import za.ac.up.cs.cirg.ciclops.services.NoSuchSchemaException;

/**
 * @author espeer
 */
public class ConfigBuilderNode {
    
    private ConfigBuilderNode(ConfigBuilderModel model, Element schema, Element config, ConfigBuilderNode parent, int index) {
        this.index = index;
        this.model = model; 
        this.parent = parent;
        this.schema = schema;
        this.config = new Config(config);
    }
    
    
    private ConfigBuilderNode(ConfigBuilderModel model, Element config, ConfigBuilderNode parent, int index) throws NoSuchSchemaException {
        this.index = index;
        this.model = model; 
        this.parent = parent;
        setConfig(config);
    }
    
    
    public ConfigBuilderNode(ConfigBuilderModel model, Element config) throws NoSuchSchemaException {
        this.index = 0;
        this.model = model;
        this.parent = null;
        setConfig(config);
    }
    
    
    public ConfigBuilderNode(ConfigBuilderModel model) {
        this.model = model;
        index = 0;
        parent = null;
    }
    
    public void setConfig(Element config) throws NoSuchSchemaException {
        this.config = new Config(config);
        this.schema = model.getSchema(config.getAttribute("class")).getDocumentElement();
    }
    
    public boolean isProperty() {
        return schema.getTagName().equals(SchemaModel.PROPERTY_TAG);
    }
    
    public boolean isClass() {
        return schema.getTagName().equals(SchemaModel.CLASS_TAG);
    }

    public String getName() {
        return schema.getAttribute(SchemaModel.NAME_ATTRIBUTE);
    }
    
    public String getConfigTagName() {
        if (getForm().equals(SchemaModel.FORM_MULTIPLE)) {
            if (getName().length() == 1) {
                return "add" + getName().toUpperCase();
            }
            else {
                return "add" + getName().substring(0, 1).toUpperCase() + getName().substring(1);
            }
        }
        else {
            return getName();
        }
    }
    
    public String toString() {
        if (schema.getAttribute(SchemaModel.FORM_ATTRIBUTE).equals(SchemaModel.FORM_MULTIPLE)) {
            if (getName().endsWith("y")) {
                return getName().substring(0, getName().length() - 1) + "ies";
            }
            else {
                return getName() + "s";
            }  
        }
        else {
            return getName();
        }
    }
    
    public String getForm() {
        if (isProperty()) {
            return schema.getAttribute(SchemaModel.FORM_ATTRIBUTE);
        }
        
        throw new UnsupportedOperationException();
    }
    
    public String getType() {
        if (isProperty()) {
            return schema.getAttribute(SchemaModel.TYPE_ATTRIBUTE);
        }
        
        throw new UnsupportedOperationException();
    }
    
    public Collection getLegalValues() {
        if (getForm().equals(SchemaModel.FORM_CLASS) || getForm().equals(SchemaModel.FORM_MULTIPLE)) {
            Collection list = model.getClassesOfType(getType());
            if (getDefault() == null) {
                list.add(null);
            }
            return list;
        }
        
        throw new UnsupportedOperationException();
    }
    
    public boolean isDefault() {
        if (isProperty()) {
            // If there is no parent class element then nothing is configured
            if (config.getClassElement() == null) {
                return true;
            }
            
            // Special case when a basic property contains an attribute in the parent class element
            if (getForm().equals(SchemaModel.FORM_BASIC) && config.getClassElement().hasAttribute(getName())) {
                return false;
            }
            
            // Otherwise, see if there is a property element under the parent class element
            return config.getPropertyElement() == null;
        }
        
        throw new UnsupportedOperationException();
    }
    
    public Object getDefault() {
        if (getForm().equals(SchemaModel.FORM_BASIC)) {
            return getBasicPropertyDefault();
        }
        else if (getForm().equals(SchemaModel.FORM_CLASS)) {
            return getClassPropertyDefault();
        }
        else if (getForm().equals(SchemaModel.FORM_MULTIPLE)) {
            return getMultiPropertyDefault();
        }
        else {
            return null;
        }
    }
    
    public boolean setValue(Object value) {
        if (config.getClassElement() == null) {
            config.setClassElement(parent.grow());
        }
        
        if (getForm().equals(SchemaModel.FORM_BASIC)) {    
            return setBasicPropertyValue(value);
        }
        else if (getForm().equals(SchemaModel.FORM_CLASS)) {
            return setClassPropertyValue(value);
        }
        else if (getForm().equals(SchemaModel.FORM_MULTIPLE)) {
            return setMultiPropertyValue((Collection) value);
        }
        else if (getForm().equals(SchemaModel.FORM_DATA_SET)) {
        	return setDataSetPropertyValue(value);
        }
        
        return false;
    }
    
    public Object getValue() {
        if (isDefault()) {
            return getDefault();
        }
        
        if (getForm().equals(SchemaModel.FORM_BASIC)) {
            return getBasicPropertyValue();
        }
        else if (getForm().equals(SchemaModel.FORM_CLASS)) {
            return getClassPropertyValue();
        }
        else if (getForm().equals(SchemaModel.FORM_MULTIPLE)) {
            return getMultiPropertyValue();
        }
        else if (getForm().equals(SchemaModel.FORM_DATA_SET)) {
        	return getDataSetPropertyValue();
        }
        else {
            return null;
        }
    }
    
    public ConfigBuilderNode getChild(int index) {        
        int count = 0;
        
        // Handle case where something is configured
        if (isProperty() && config.getPropertyElement() != null) {
            try {
                Iterator i = config.getPropertyElements().iterator();
                while (i.hasNext()) {
                    Element tmp = (Element) i.next();
                    if (count == index) {
                        return new ConfigBuilderNode(model, tmp, this, count);
                    }
                    ++count;
                }
            }
            catch (NoSuchSchemaException ex) {
                throw new UnexpectedError(ex);
            }
        }
        
        // Nothing is configured, consult the schema
        for (Node child = schema.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (count == index) {
                    if (isClass()) {
                        return new ConfigBuilderNode(model, (Element) child, config.getClassElement(), this, count);
                    } 
                    else {
                        // It is important for properties to not have a classElement configured if they have no configured value
                        // otherwise they think that they are configured when they are not and everything breaks
                        return new ConfigBuilderNode(model, (Element) child, null, this, count);
                    }
                }
                ++count ;
            }
        }
        
        throw new UnexpectedError(null);
    }
    
    public int getChildCount() {
        if (isProperty() && config.getPropertyElement() != null) {
            if (getForm().equals(SchemaModel.FORM_BASIC)) {
                return 0;
            }
            return config.getPropertyElements().size();
        }
        
        int count = 0;
        for (Node child = schema.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                ++count;
            }
        }
        
        return count;
    }

    public ConfigBuilderNode getParent() {
        return parent;
    }
    
    public int getIndex() {
        return index;
    }
    
    public TreePath getPath() {
        if (parent == null) {
            return new TreePath(this);
        }
        else {
            return parent.getPath().pathByAddingChild(this);
        }
    }
    
    private boolean setMultiPropertyValue(Collection list) {    
        boolean touched = false;
        if (config.getPropertyElements().isEmpty()) {
            Iterator i = list.iterator();
            while (i.hasNext()) {
                addClassProperty(i.next().toString());
                touched = true;
            }
        }
        else {
            Collection properties = config.getPropertyElements();
            Iterator i = properties.iterator();
            while (i.hasNext()) {
                Element property = (Element) i.next();
                if (list.contains(property.getAttribute("class"))) {
                    list.remove(property.getAttribute("class"));
                }
                else {
                    config.getClassElement().removeChild(property);
                    touched = true;
                }
            }
            i = list.iterator();
            while (i.hasNext()) {
                addClassProperty(i.next().toString());
                touched = true;
            }
        }
        
        prune();
        return touched;
    }

    private boolean setClassPropertyValue(Object value) {
        if (value == null) {
            config.removePropertyElements();
        }
        else {
            if (config.getPropertyElement() == null) {
                addClassProperty(value.toString());
            }
            else if (! getClassPropertyValue().equals(value.toString())) {
                config.getPropertyElement().setAttribute("class", value.toString());
                removeChildrenOfElement(config.getPropertyElement());
            }
            else {
                return false;
            }
        }

        prune();
        return true;
    }
    
    private boolean setDataSetPropertyValue(Object value) {
    	config.removePropertyElements();
    	if (value != null) {
    		addClassProperty("CiClops.CachedDataSet");
    		config.getPropertyElement().setAttribute("id", value.toString());
    	}
    	
    	prune();
    	return true;
    }
    
    private boolean setBasicPropertyValue(Object value) {
        if (value == null) {
            return false;
        }
      
        if (config.getPropertyElement() != null) {
            if (! config.getPropertyElement().hasChildNodes()) {
                // If something is configured in a value element, leave it there
                config.getPropertyElement().setAttribute("value", value.toString());
            }
            else {
                // Simularly maintain any text node values 
                removeChildrenOfElement(config.getPropertyElement());
                Text text = config.getPropertyElement().getOwnerDocument().createTextNode(value.toString());
                config.getPropertyElement().appendChild(text);
            }
        }
        else {
            // Finally, set the property as an attribute of the parent class element
            config.getClassElement().setAttribute(getName(), value.toString());
        }
        
        // Calculate the length of the parent tag
        int parentLength = config.getClassElement().getTagName().length();
        NamedNodeMap attributes = config.getClassElement().getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Attr attribute = (Attr) attributes.item(i);
            parentLength += attribute.getName().length() + attribute.getValue().length(); 
        }
        
        // Determine if there are any basic property elements for other properties
        boolean hasBasicPropertyElements = false;
        Node child = config.getClassElement().getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (! ((Element) child).hasAttribute("class")) {
                    hasBasicPropertyElements = true;
                    break;
                }
            }
            child = child.getNextSibling();
        }
        
        // If the parent element is too long or if other basic property elements exist then move the attributes into value elements
        if (hasBasicPropertyElements || parentLength > 70) {
            int next = 0;
            Attr attribute = (Attr) attributes.item(next);
            while (attribute != null) {
                if (attribute.getName().equals("class")) {
                    ++next;
                }
                else {
                    Element tmp = config.getClassElement().getOwnerDocument().createElement(attribute.getName());
                    tmp.setAttribute("value", attribute.getValue());
                    config.getClassElement().appendChild(tmp);
                    attributes.removeNamedItem(attribute.getName());
                }
                attribute = (Attr) attributes.item(next);
            }
        }
        
        prune();
        return true;
    }

    private Collection getMultiPropertyDefault() {
        Collection value = new LinkedList();
        Node child = schema.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                // child shjoud be a schema class element 
                value.add(((Element) child).getAttribute(SchemaModel.NAME_ATTRIBUTE));
            }
            child = child.getNextSibling();
        }
        return value;
    }
    
    private String getClassPropertyDefault() {
        Node child = schema.getFirstChild();
        if (child != null && child.getNodeType() == Node.ELEMENT_NODE) {
            // child should be a schema class element
            return ((Element) child).getAttribute(SchemaModel.NAME_ATTRIBUTE);
        }
        else {
            return null;
        }
    }
    
    private Object getBasicPropertyDefault() {
        if (schema.hasAttribute(SchemaModel.DEFAULT_ATTRIBUTE)) {
            return stringToPropertyType(schema.getAttribute(SchemaModel.DEFAULT_ATTRIBUTE));
        }
        else {
            return null;
        }
    }
    
    
    private Collection getMultiPropertyValue() {
        Collection value = new LinkedList();
        
        Iterator i = config.getPropertyElements().iterator();
        while (i.hasNext()) {
            Element tmp = (Element) i.next();
            value.add(tmp.getAttribute("class"));
        }
        
        return value;
    }
    

    private Object getClassPropertyValue() {
        if (config.getPropertyElement() == null) {
            return null;
        }
        else {
            return config.getPropertyElement().getAttribute("class");
        }
    }
    
    private Object getDataSetPropertyValue() {
    	if (config.getPropertyElement() == null) {
    		return null;
    	}
    	else {
    		return config.getPropertyElement().getAttribute("id");
    	}
    }
   
    private Object getBasicPropertyValue() {
        String value = null;
        
        // Check if class element contains value in attributes 
        if (config.getClassElement().hasAttribute(getName())) {
            value = config.getClassElement().getAttribute(getName());
        }
        
        // Check if property element contains value in a value attribute
        else if (config.getPropertyElement().hasAttribute("value")) {
            value = config.getPropertyElement().getAttribute("value");
        }
        
        // Check if element contains value as a nested text element
        else {
            Node child = config.getPropertyElement().getFirstChild();
            if (child != null && child.getNodeType() == Node.TEXT_NODE) {
                value = child.getNodeValue();
            }
        }

        return stringToPropertyType(value);
    }
    
    private void addClassProperty(String name) {
        Element element = config.getClassElement().getOwnerDocument().createElement(getConfigTagName());
        element.setAttribute("class", name);
        config.getClassElement().appendChild(element);
    }
    
    private void removeChildrenOfElement(Element element) {
        Node child = element.getFirstChild();
        while (child != null) {
            element.removeChild(child);
            child = element.getFirstChild();
        }
    }
    
    private Object stringToPropertyType(String value) {
        try {
            if (value != null) {
                Class[] parameters =  { String.class };
                Object[] values = { value.trim() };
                return Class.forName(schema.getAttribute(SchemaModel.TYPE_ATTRIBUTE)).getConstructor(parameters).newInstance(values); 
            }
        }
        catch (Exception ex) { /* if it wasn't successfully converted to one of the other basic types return the original string */ }
        
        return value;
    }

    private Element grow() {
        if (parent == null) {
            return config.getClassElement();
        }
        
        if (isClass()) {
            config.setClassElement(parent.grow());
            return config.getClassElement();
        }
        else {
            if (config.getPropertyElement() != null) {
                return config.getPropertyElement();
            }
            else {
                Element element = parent.grow();
                Element property = element.getOwnerDocument().createElement(getConfigTagName());
                Element defaultClass = (Element) schema.getFirstChild();
                property.setAttribute("class", defaultClass.getAttribute("name"));        
                element.appendChild(property);
                
                config.setClassElement(element);
                return property;
            }
        }
    }
    
    void prune() {
        if (parent == null || config.getClassElement() == null) {
            return;
        }
        
        if (isClass()) {
            config.setClassElement(null);
            parent.prune();
            return;
        }
        
        Object value = getValue();
        if (value == null || value.equals(getDefault())) {
            if (config.getClassElement().hasAttribute(getName())) {
                config.getClassElement().removeAttribute(getName());
            }
            config.removePropertyElements();
        }
        else {
            return;
        }
        
        // Check if something else is configured
        if (config.getClassElement().getAttributes().getLength() > 1) {
            return; // There are other properties under this class element
        }
        Node child = config.getClassElement().getFirstChild();
        while (child != null) {
            if (config.getPropertyElement() != child && child.getNodeType() == Node.ELEMENT_NODE) {
                return; // There are other properties under this class element
            }
            child = child.getNextSibling();
        }
        
        config.setClassElement(null);
        parent.prune();          
    }
    
    private ConfigBuilderModel model;
    private Element schema; 
    private Config config;
    private ConfigBuilderNode parent;
    private int index;
    
    // Local utility class to manage caching of property elements
    private class Config {
        
        public Config(Element parent) {
            setClassElement(parent);
        }
        
        public void setClassElement(Element parent) {
            this.parent = parent;
            property = null;
        }
        
        public void removePropertyElements() {
            while (getPropertyElement() != null) {
                removePropertyElement(getPropertyElement());
            }
        }
        
        public void removePropertyElement(Element remove) {
            if (parent != null) {
                parent.removeChild(remove);
                if (remove == property) {
                    property = null;
                }    
            }  
        }
    
        public Element getPropertyElement() {
            if (parent == null) {
                return null;
            }
            
            if (property != null) {
                return property;
            }
        
            Node child = parent.getFirstChild();
            while (child != null) {
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element tmp = (Element) child;
                    if (tmp.getTagName().equals(getConfigTagName())) {
                        property = tmp;
                        return property;
                    }
                }
                child = child.getNextSibling();
            }
            return null;
        }
        
        public Collection getPropertyElements() {
            Collection list = new LinkedList();
            
            // Start looking at the first cached occurence
            Node node = getPropertyElement();
            while (node != null) {
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element tmp = (Element) node;
                    if (tmp.getTagName().equals(getConfigTagName())) {
                        list.add(tmp);
                    }
                }
                node = node.getNextSibling();
            }
            
            return list;
        }
        
        public Element getClassElement() {
            return parent;
        }
            
        private Element parent; 
        private Element property;
    }
    
    
}
