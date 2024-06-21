/*
 * SimulationBean.java
 *
 * Copyright (C) 2004 - Edwin S. Peer
 *
 * Created on March 5, 2004, 10:11 PM
 */

package za.ac.up.cs.cirg.ciclops.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.NamingException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import za.ac.up.cs.cirg.ciclops.cilib.SimulationWrapper;
import za.ac.up.cs.cirg.ciclops.services.EJBClassLoader;
import za.ac.up.cs.cirg.ciclops.services.SchemaLocal;
import za.ac.up.cs.cirg.ciclops.services.SchemaUtil;
import za.ac.up.cs.cirg.ciclops.services.SchemaValidationException;

/**
 * @author  espeer
 *
 * @ejb.bean name="Simulation"
 *   type="CMP"
 *   cmp-version="${ejb.cmp.version}"
 *   view-type="local"
 *   local-jndi-name="ejb/ciclops/domain/Simulation"
 *   reentrant="false"
 *   primkey-field="id"
 *
 * @ejb.home local-class="${package}.domain.SimulationHome"
 * @ejb.interface local-class="${package}.domain.Simulation" 
 *
 * @ejb.persistence table-name="simulation"
 *
 * @ejb.transaction type="Required"
 *
 * @ejb.finder signature="Collection findAll()"
 *   query="SELECT OBJECT(obj) FROM Simulation obj"
 *
 * @ejb.finder signature="java.util.Collection findRootSimulations()"
 *   query="SELECT OBJECT(s) FROM Simulation s WHERE s.category IS NULL"
 *
 * @ejb.util generate="physical"
 *
 * @jboss.entity-command name="mysql-get-generated-keys"
 * 
 * @jboss.persistence post-table-create="ALTER TABLE simulation type = innodb"
 *
 *@ejb.finder signature="Collection findIncompleteOrStaleSimulations()"
 *  query="SELECT OBJECT(sim) FROM Simulation sim WHERE sim.completedSamples < sim.totalSamples OR sim.stale = true"
 *
 * @ejb.finder signature="Collection findIncompleteSimulations()"
 *   query="SELECT OBJECT(sim) FROM Simulation sim WHERE sim.completedSamples < sim.totalSamples"
 * 
 * @ejb.finder signature="Collection findStaleSimulations()"
 *   query="SELECT OBJECT(sim) FROM Simulation sim WHERE sim.stale = true"
 *
 * @ejb.permission unchecked="true"
 */

public abstract class SimulationBean implements EntityBean {
        
    /** 
     * @ejb.create-method
     */
    public Integer ejbCreate(String name, Document configuration) throws CreateException, InvalidConfigurationException {
    	setName(name);
        setCompletedSamples(0);
        
        return null;
    }
    
    public void ejbPostCreate(String name, Document configuration) throws CreateException, InvalidConfigurationException {
        updateConfiguration(configuration);
        setStale(false);
    }
    
    /**
     * @ejb.create-method
     */
    public Integer ejbCreate(String name, String XMLConfiguration) throws CreateException, InvalidConfigurationException {
    	setName(name);
        setCompletedSamples(0);
        
        return null;
    }
    
    public void ejbPostCreate(String name, String XMLConfiguration) throws CreateException, InvalidConfigurationException {
        updateXMLConfiguration(XMLConfiguration);
        setStale(false);    	
    }
            
    /**
     * @ejb.persistence
     * @ejb.pk-field
     * @ejb.interface-method
     * 
     * @jboss.column-name name="id"
     * @jboss.persistence auto-increment="true"
     * @jboss.method-attributes read-only="true"
     */
    public abstract Integer getId();
    public abstract void setId(Integer id);

    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="name"
     * @jboss-method-attributes read-only="true"
     */
    public abstract String getName();
    /**
     * @ejb.interface-method
     */
    public abstract void setName(String name);
    
    /**
     * @ejb.persistence column-name="description"
     * @jboss-method-attributes read-only="true"
     */
    public abstract Object getDescriptionAsObject();
    public abstract void setDescriptionAsObject(Object description);
    
    /**
     * @ejb.interface-method
     */
    public String getDescription() {
    	Object tmp = getDescriptionAsObject();
    	return tmp == null ? "" : tmp.toString();
    }
    /**
     *@ejb.interface-method
     */
    public void setDescription(String description) {
    	setDescriptionAsObject(description);
    }
    
    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="total_samples"
     * @jboss.method-attributes read-only="true"
     */
    public abstract int getTotalSamples();
    /**
     * @ejb.interface-method
     */
    public abstract void setTotalSamples(int samples);
    
    /**
     * @ejb.interface-method
     * @ejb.persistence column-name="completed_samples"
     * @jboss.method-attributes = read-only="true"
     */
    public abstract int getCompletedSamples();
    /**
     * @ejb.interface-method
     */
    public abstract void setCompletedSamples(int samples);
    
    /**
     * @ejb.persistence column-name="stale"
     * @jboss.method-attributes read-only="true"
     */
    public abstract boolean getStale();
    /**
     * @ejb.interface-method
     */
    public abstract void setStale(boolean stale);
    /**
     * @ejb.interface-method
     * @jboss.method-attributes read-only="true"
     */    
    public boolean isStale() {
    	return getStale();
    }
    
    /**
     * @ejb.interface-method
     */
    public void clearSamples() throws RemoveException {
    	Iterator i = getSamples().iterator();
    	while (i.hasNext()) {
    		Sample sample = (Sample) i.next();
    		sample.remove();
    		i = getSamples().iterator();
    	}
    	
    	setStale(false);
    	setCompletedSamples(0);
    }
    
    /**
     * @ejb.interface-method
     */
    public void clearErrors() throws RemoveException {
    	Iterator i = getErrors().iterator();
    	while (i.hasNext()) {
    		Error error = (Error) i.next();
    		error.remove();
    		i = getErrors().iterator();
    	}    	
    }
    
    /**
     * @ejb.persistence column-name="configuration"
     * @jboss.method-attributes read-only="true"
     */
    public abstract Object getConfigurationAsObject();
    public abstract void setConfigurationAsObject(Object configuration);
    
    /**
     * @ejb.interface-method
     * @jboss.method-attributes read-only="true"
     */
    public Document getConfiguration() {
    	try {
    		return schema.transform(getConfigurationAsObject().toString());
    	}
    	catch (TransformerException ex) {
    		throw new EJBException(ex); // this should never happen
    	}
    }
    
    /**
     * @ejb.interface-method
     * @jboss.method-attributes read-only="true"
     */
    public String getXMLConfiguration() {
    	Object tmp = getConfigurationAsObject();
    	return tmp == null ? "" : tmp.toString();
    }
    
    /**
     * @ejb.interface-method
     */
    public String getFormattedXMLConfiguration() {
    	return indentXML(getXMLConfiguration());
    }
    
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
    
    /**
     * @ejb.interface-method
     */
    public void updateConfiguration(Document configuration) throws InvalidConfigurationException {
    	try {
    		schema.validate(configuration);
    	}
    	catch (SchemaValidationException ex) {
    		throw new InvalidConfigurationException(ex);
    	}
    		
    	try {
    		String config = schema.transform(configuration);
    		
    		Object current = getConfigurationAsObject();
    		if (current == null || ! config.equals(current.toString())) {
    			setDataSetDependencies(getDataSetDependencies(configuration));
    			setCodeDependencies(getCodeDependencies(configuration));
    			
    			setConfigurationAsObject(config);

    			if (current != null) {
    				setStale(true);
    			}
    		}
    	}
    	catch (TransformerException ex) {
    		throw new InvalidConfigurationException(ex);
    	}
    }
    
    /**
     * @ejb.interface-method
     */
    public String[] getMeasurements() {
    	NodeList list = getConfiguration().getElementsByTagName("addMeasurement");
    	String[] measurements = new String[list.getLength()];
    	for (int i = 0; i < list.getLength(); ++i) {
    		Element tmp = (Element) list.item(i);
    		measurements[i] = tmp.getAttribute("class");
    	}
    	return measurements;
    }
    
    /**
     * @ejb.interface-method
     */
    public void updateXMLConfiguration(String configuration) throws InvalidConfigurationException {
    	try {
    		updateConfiguration(schema.transform(configuration));
    	}
    	catch (TransformerException ex) {
    		throw new InvalidConfigurationException(ex);
    	}
    }
    
    private Set getDataSetDependencies(Document configuration) {
    	Set dependencies = new HashSet();
    	collectDataSetDependencies(dependencies, configuration.getDocumentElement());
    	return dependencies;
    }
    
    private void collectDataSetDependencies(Set dependencies, Element node) {
    	if (node.getAttribute("class").equals("Problem.DataSet")) {
    		try {
    			dependencies.add(dataSetHome.findByPrimaryKey(new Integer(node.getAttribute("id"))));
    		}
    		catch (NumberFormatException ex) {	}
    		catch (FinderException ex) { }
    	}
    	
    	NodeList children = node.getChildNodes();
    	for (int i = 0; i < children.getLength(); ++i) {
    		Node child = children.item(i);
    		if (child.getNodeType() == Node.ELEMENT_NODE) {
    			collectDataSetDependencies(dependencies, (Element) child);
    		}
    	}
    }
    
    private Set getCodeDependencies(Document configuration) {
    	EJBClassLoader loader = new EJBClassLoader(codeHome, true);
    	
    	try {
    		SimulationWrapper simulation = new SimulationWrapper(loader, configuration);
    		simulation.initialise();
    	}
    	catch (InvocationTargetException ex) {
    		throw new EJBException(ex);
    	}
    		
    	return loader.getLog();
    }
    
    /**
     * @ejb.relation name="SimulationSampleRelation"
     *    role-name="SimulationHasSamples"
     * 
     * @ejb.interface-method
     */       
    public abstract Collection getSamples();
    public abstract void setSamples(Collection samples);
    
    /**
     * @ejb.relation name="SimulationWorkerRelation"
     *    role-name="SimulationIsWorkedOnByWorkers" 
     */
    public abstract Collection getWorkers();
    public abstract void setWorkers(Collection workers);
    
    /**
     * @ejb.interface-method
     */
    public void registerWorker(za.ac.up.cs.cirg.ciclops.domain.Worker worker) throws ReservationException {
        
    	if (isStale()) {
    		throw new ReservationException("Simulation is stale");
    	}
    	
    	if (! getErrors().isEmpty()) {
    		throw new ReservationException("Simulation has errors");
    	}
    	
        int samples = getTotalSamples();
        int completed = getCompletedSamples();
        
        if (completed >= samples) {
            throw new ReservationException("Simulation is already complete");
        }
        
        Collection workers = getWorkers();
        if (completed + workers.size() < samples) {
            workers.add(worker);
        }
        else {
            throw new ReservationException("Simulation already has enough workers");
        }
    }
    
    /**
     * @ejb.interface-method
     */
    public void deregisterWorker(za.ac.up.cs.cirg.ciclops.domain.Worker worker) {
    	setCompletedSamples(getCompletedSamples() + 1);
    	worker.setSimulation(null);
    }
    
    /**
     * @ejb.interface-method
     * 
     * @ejb.relation name="SimulationDataSetRelation"
     *   role-name="SimulationDependsOnDataSets"
     * 
     * @jboss.relation fk-column="data_set"
     *    related-pk-field="id"
     *    fk-constraint="true"
     * 
     * @jboss.relation-table table-name="simulation_data_set" 
     *    create-table="true"
     * 
     */
    public abstract java.util.Set getDataSetDependencies();
    public abstract void setDataSetDependencies(java.util.Set dataSets);
    
    /**
     * @ejb.interface-method
     * 
     * @ejb.relation name="CategorySimulationRelation"
     *    role-name="SimulationInACategory"
     *    cascade-delete="yes"
     * 
     * @jboss.relation fk-column="category"
     *   related-pk-field="id"
     *   fk-constraint="false"
     */
    public abstract za.ac.up.cs.cirg.ciclops.domain.Category getCategory();
    /**
     * @ejb.interface-method
     */
    public abstract void setCategory(za.ac.up.cs.cirg.ciclops.domain.Category category);
    
    /**
     * @ejb.relation name="CodeSimulationRelation"
     *    role-name="SimulationDependsOnCode"
     * 
     * @jboss.relation fk-column="code"
     *    related-pk-field="className"
     *    fk-constraint="true"
     *
     * @jboss.relation-table table-name="code_simulation" 
     *    create-table="true"
     * 
     * TODO: Calculate these with a DependencyClassLoader
     */
    public abstract java.util.Set getCodeDependencies();
    public abstract void setCodeDependencies(java.util.Set codeDependencies);
    
    /**
     * @ejb.relation name="SimulationErrorRelation"
     *    role-name="SimulationMayHaveErrors"
     * 
     * @jboss.method-attributes read-only="true"
     * 
     * @ejb.interface-method
     */
    public abstract Collection getErrors();
    public abstract void setErrors(Collection errors);
    
    public void ejbActivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    	setup();
    }    
    
    public void ejbLoad() throws javax.ejb.EJBException, java.rmi.RemoteException {
    	
    }
    
    public void ejbPassivate() throws javax.ejb.EJBException, java.rmi.RemoteException {
    	tearDown();
    }
    
    public void ejbRemove() throws javax.ejb.RemoveException, javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void ejbStore() throws javax.ejb.EJBException, java.rmi.RemoteException {
    }
    
    public void setEntityContext(javax.ejb.EntityContext entityContext) throws javax.ejb.EJBException, java.rmi.RemoteException {
        this.context = entityContext;

        setup();

    }
    
	private void setup() {
		// Schema's transformers are not thread safe - SimulationBean may not be re-entrant
    	try {
    		schema = SchemaUtil.getLocalHome().create();
    	}
    	catch (Exception ex) {
    		throw new EJBException(ex);
    	}      
    	
    	try {
    		dataSetHome = DataSetUtil.getLocalHome();
    		codeHome = CodeUtil.getLocalHome();
    	}
    	catch (NamingException ex) {
    		throw new EJBException(ex);
    	}
	}

	public void unsetEntityContext() throws javax.ejb.EJBException, java.rmi.RemoteException {
    	context = null;
        
        tearDown();
    	
    }
    
    /**
	 * 
	 */
	private void tearDown() {
		dataSetHome = null;
        codeHome = null;
        
        try {
    		schema.remove();
    		schema = null;
        }
    	catch (RemoveException ex) {
    		throw new EJBException(ex);
    	}
	}

	private EntityContext context;
    private SchemaLocal schema;
    private DataSetHome dataSetHome;
    private CodeHome codeHome;
}
