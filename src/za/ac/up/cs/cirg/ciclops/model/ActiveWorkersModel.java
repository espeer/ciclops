/*
 * ActiveWorkersModel.java
 * 
 * Created on Nov 4, 2004
 *
 */
package za.ac.up.cs.cirg.ciclops.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Locale;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * @author espeer
 *
 */
public class ActiveWorkersModel implements TableModel, Serializable {

	public ActiveWorkersModel(WorkerModel[] workers) {
		this.workers = workers;
		df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.UK);
	}

	public int getIdForRow(int row) {
		return workers[row].getId();
	}
	
	public int getRowCount() {
		return workers.length;
	}

	public int getColumnCount() {
		return 7;
	}

	public String getColumnName(int col) {
		switch (col) {
			case 0: return "ID";
			case 1: return "Host";
			case 2: return "Operating System";
			case 3: return "Virtual Machine";
			case 4: return "Simulation";
			case 5: return "Status";
			case 6: return "Last Update";
			default: return null;
		}
	}

	public Class getColumnClass(int col) {
		switch (col) {
			case 0: return Integer.class;
			case 1: return String.class;
			case 2: return String.class;
			case 3: return String.class;
			case 4: return Integer.class;
			case 5: return String.class;
			case 6: return String.class;
			default: return null;
		}
	}	

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
			case 0: return new Integer(workers[row].getId());
			case 1: return workers[row].getHostName();
			case 2: return workers[row].getOperatingSystem();
			case 3: return workers[row].getVirtualMachine();
			case 4: return new Integer(workers[row].getSimulation());
			case 5: return workers[row].getStatus();
			case 6: return df.format(workers[row].getLastUpdate());
			default: return null;
		}
	}

	public void setValueAt(Object value, int row, int col) {
	}

	public void addTableModelListener(TableModelListener arg0) {		
	}

	public void removeTableModelListener(TableModelListener arg0) {
	}
	
	private WorkerModel[] workers;
	private DateFormat df;
}
