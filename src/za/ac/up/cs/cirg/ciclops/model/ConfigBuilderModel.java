/*
 * ConfigBuilderModel.java
 *
 * Created on March 10, 2004, 10:33 PM
 */

package za.ac.up.cs.cirg.ciclops.model;

import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import za.ac.up.cs.cirg.ciclops.client.ServerContext;
import za.ac.up.cs.cirg.ciclops.client.UnexpectedError;
import za.ac.up.cs.cirg.ciclops.services.NoSuchSchemaException;
import za.ac.up.cs.cirg.ciclops.services.Schema;
import za.ac.up.cs.cirg.ciclops.services.SchemaValidationException;

/**
 *
 * @author  espeer
 */
public class ConfigBuilderModel implements javax.swing.tree.TreeModel, javax.swing.table.TableModel {
    
    /** Creates a new instance of SimulationEditorModel */
    public ConfigBuilderModel(String configuration) throws SchemaValidationException {
        try {
            schema = ServerContext.instance().getSchemaHome().create();
        }
        catch (Exception ex) {
            throw new UnexpectedError(ex);
        }
        schemaCache = new HashMap();
        
        tableModelListeners = new LinkedList();
        treeModelListeners = new LinkedList();
      
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException ex) {
            throw new UnexpectedError(ex);
        }

        try {
            String xsl = "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:output method=\"xml\" indent=\"yes\"/><xsl:strip-space elements=\"*\"/><xsl:template match=\"/\"><xsl:copy-of select=\".\"/></xsl:template></xsl:stylesheet>";
            transformer = TransformerFactory.newInstance().newTemplates(new StreamSource(new StringReader(xsl))).newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        }
        catch (TransformerConfigurationException ex) {
            throw new UnexpectedError(ex);
        }
        
        root = new ConfigBuilderNode(this);
        setConfiguration(configuration);
    }
    
    public void dispose() {
    	try {
    		schema.remove();
    	}
    	catch (Exception ex) {
    		throw new UnexpectedError(ex);
    	}
    }
    
    public String getConfiguration() {
        try {
            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(config), new StreamResult(sw));
            return indentXML(sw.toString());
        }
        catch (TransformerException ex) {
            throw new UnexpectedError(ex);
        }
    }
    
    public void setConfiguration(String configuration) throws SchemaValidationException {
        try {
            schema.validate(configuration);
        }
        catch (RemoteException ex) {
            throw new UnexpectedError(ex);
        }
        config = documentBuilder.newDocument();
        try {
            transformer.transform(new StreamSource(new StringReader(configuration)), new DOMResult(config));
        }
        catch (TransformerException ex) {
            throw new UnexpectedError(ex); // Should never happen since shema.validate(...) also does a transform
        }
        try {
            root.setConfig(config.getDocumentElement());
            current = root;
        }
        catch (NoSuchSchemaException ex) {
            
        }
        
        minimise(root);
        
        fireTableChanged();
        fireTreeStructureChanged(root.getPath());
    }
    
    private void minimise(ConfigBuilderNode node) {
        for (int i = 0; i < node.getChildCount(); ++i) {
            ConfigBuilderNode child = node.getChild(i);
            if (isLeaf(child)) {
                child.prune();
            }
            else {
                minimise(child);
            }
        }
    }
    
    public void setDisplayedNode(ConfigBuilderNode current) {
        if (current == null) {
            return;
        }
        
        if (current.isClass()) {
            this.current = current;    
        }
        else {
            if (current.getParent() == null) {
                return;
            }
            this.current = current.getParent();
        }
        
        fireTableChanged();
    }
    
    public ConfigBuilderNode getDisplayedNode() {
        return current;
    }
    
    /**** TableModel methods begin ****/
    
    public void addTableModelListener(javax.swing.event.TableModelListener tableModelListener) {
        tableModelListeners.add(tableModelListener);
    }
    
    public Class getColumnClass(int col) {
        return Object.class;
    }
    
    public int getColumnCount() {
        return 2;
    }
    
    public String getColumnName(int col) {
        switch(col) {
        	case 0: return "Property";
        	case 1: return "Value";
        	default: return null;
        }
    }
    
    public int getRowCount() {
        return current.getChildCount();
    }
    
    public Object getValueAt(int row, int col) {
        ConfigBuilderNode child = current.getChild(row);
        switch (col) {
        case 0: return child;
        case 1: return child.getValue();
        }

        return null;
    }
    
    public boolean isCellEditable(int row, int col) {
        switch (col) {
        case 0: return false;
        case 1: return true;
        default: return false;
        }
    }
    
    public void removeTableModelListener(javax.swing.event.TableModelListener tableModelListener) {
        tableModelListeners.remove(tableModelListener);
    }
    
    public void setValueAt(Object value, int row, int col) {
        
        if (current.getChild(row).setValue(value)) {
            fireTableChanged();
            fireTreeStructureChanged(current.getChild(row).getPath());
        }
    }
    
    /**** TableModel methods end ****/
    
    /**** TreeModel methods begin ****/
    
    public void addTreeModelListener(javax.swing.event.TreeModelListener treeModelListener) {
        treeModelListeners.add(treeModelListener);
    }
    
    public Object getChild(Object obj, int index) {
        ConfigBuilderNode mo = (ConfigBuilderNode) obj;
        return mo.getChild(index);
    }
    
    public int getChildCount(Object obj) {
        ConfigBuilderNode mo = (ConfigBuilderNode) obj;
        return mo.getChildCount();
    }
    
    public int getIndexOfChild(Object parent, Object child) {
        if (parent == null || child == null) {
            return -1;
        }
        return ((ConfigBuilderNode) child).getIndex();
    }
    
    public Object getRoot() {
        return root;
    }
    
    public boolean isLeaf(Object obj) {
        ConfigBuilderNode mo = (ConfigBuilderNode)  obj;
        return mo.getChildCount() == 0;
    }
    
    public void removeTreeModelListener(javax.swing.event.TreeModelListener treeModelListener) {
        treeModelListeners.remove(treeModelListener);
    }
    
    public void valueForPathChanged(javax.swing.tree.TreePath treePath, Object obj) {
        throw new UnsupportedOperationException(); // This TreeModel is not mutable
    }
    
    /**** TreeModel methods end ****/
    
    protected Document getSchema(String type) throws NoSuchSchemaException {
        if (schemaCache.containsKey(type)) {
            return (Document) schemaCache.get(type);
        }
        
        Document doc = documentBuilder.newDocument();
        
        try {
            String tmp = schema.getSchemaForType(type);
            transformer.transform(new StreamSource(new StringReader(tmp)), new DOMResult(doc));
            schemaCache.put(type, doc);
            return doc;
        } 
        catch (RemoteException ex) {
            throw new UnexpectedError(ex);
        }
        catch (TransformerException ex) {
            throw new UnexpectedError(ex);
        }
     }
    
     protected Collection getClassesOfType(String type) {
        Collection list = new LinkedList();
        try {
            Document doc = documentBuilder.newDocument();
            transformer.transform(new StreamSource(new StringReader(schema.getClassesOfType(type))), new DOMResult(doc));
            Node child = doc.getDocumentElement().getFirstChild();
            while (child != null) {
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    // child should be a schema class element
                    list.add(((Element) child).getAttribute(SchemaModel.NAME_ATTRIBUTE));
                }
                child = child.getNextSibling();
            }
            return list;
        }
        catch (Exception ex) {
            return list;
        }
    }
    
    private void fireTableChanged() {
       Iterator i = tableModelListeners.iterator();
       while (i.hasNext()) {
           ((TableModelListener) i.next()).tableChanged(new TableModelEvent(this));
       }
    }
    
    private void fireTreeStructureChanged(TreePath path) {
        Iterator i = treeModelListeners.iterator();
        while (i.hasNext()) {
            ((TreeModelListener) i.next()).treeStructureChanged(new TreeModelEvent(this, path));
        }
    }
    
    private Document config;
    
    private Map schemaCache;
    private Schema schema;
    
    private Transformer transformer;
    private DocumentBuilder documentBuilder;
    
    private Collection tableModelListeners;
    private Collection treeModelListeners;
    
    private ConfigBuilderNode root;
    private ConfigBuilderNode current;
    
    // TODO: This is an ungly hack -- figure out how to get the xsl stylesheet for format this stuff properly
    private String indentXML(String src) {
        try {
            int level = 0;
            StringBuffer dest = new StringBuffer(src.length());
            for (int i = 0; i < src.length(); ++i) {
                dest.append(src.charAt(i));
                if (src.charAt(i) == '<' && src.charAt(i + 1) != '/') {
                    ++level;
                }
                else if (src.charAt(i) == '<' && src.charAt(i + 1) == '/') {
                    --level;
                }   
                else if (src.charAt(i) == '/' && src.charAt(i + 1) == '>') {
                    --level;
                }
                else if (src.charAt(i) == '\n') {
                    int tmp = level;
                    if (i < (src.length() - 2) && src.charAt(i + 1) == '<' && src.charAt(i + 2) == '/') {
                        --tmp;
                    }
                    for (int j = 0; j < tmp; ++j) {
                        dest.append("    "); 
                    }
                }
            }
            return dest.toString();
        }
        catch (StringIndexOutOfBoundsException ex) {
            return src;
        }
    }
    
}
