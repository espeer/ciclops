/*
 * ActiveSimulationsModel.java
 * 
 * Created on Nov 4, 2004
 *
 */
package za.ac.up.cs.cirg.ciclops.model;

import java.io.Serializable;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * @author espeer
 *
 */
public class ActiveSimulationsModel implements TableModel, Serializable {

	public ActiveSimulationsModel(SimulationModel[] simulations) {
		this.simulations = simulations;
	}

	public int getIdForRow(int row) {
		return simulations[row].getId();
	}
	
	public int getRowCount() {
		return simulations.length;
	}

	public int getColumnCount() {
		return 6;
	}

	public String getColumnName(int col) {
		switch (col) {
			case 0: return "ID";
			case 1: return "Name";
			case 2: return "Completed Samples";
			case 3: return "Total Samples";
			case 4: return "Stale";
			case 5: return "Errors";
			default: return null;
		}
	}

	public Class getColumnClass(int col) {
		switch (col) {
			case 0: return Integer.class;
			case 1: return String.class;
			case 2: return Integer.class;
			case 3: return Integer.class;
			case 4: return Boolean.class;
			case 5: return Integer.class;
			default: return null;
		}
	}	

	public boolean isCellEditable(int row, int col) {
		return false;
	}

	public Object getValueAt(int row, int col) {
		switch (col) {
			case 0: return new Integer(simulations[row].getId());
			case 1: return simulations[row].getCategory().getPath() + "/" + simulations[row].getName();
			case 2: return new Integer(simulations[row].getCompeltedSamples());
			case 3: return new Integer(simulations[row].getTotalSamples());
			case 4:  return new Boolean(simulations[row].isStale());
			case 5: return new Integer(simulations[row].getErrors());
			default: return null;
		}
	}

	public void setValueAt(Object value, int row, int col) {
	}

	public void addTableModelListener(TableModelListener arg0) {		
	}

	public void removeTableModelListener(TableModelListener arg0) {
	}
	
	private SimulationModel[] simulations;
	
}
