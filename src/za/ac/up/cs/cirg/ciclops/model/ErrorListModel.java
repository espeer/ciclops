/*
 * Created on Nov 4, 2004
 *
 */
package za.ac.up.cs.cirg.ciclops.model;

import java.io.Serializable;

/**
 * @author espeer
 *
 */
public class ErrorListModel implements Serializable {

	public ErrorListModel(int simulation, String[] errors) {
		this.simulation = simulation;
		this.errors = errors;
	}
	
	public int getSimulationId() {
		return simulation;
	}
	
	public int getErrorCount() {
		return errors.length;
	}
	
	public String getError(int index) {
		return errors[index];
	}
	
	private String[] errors;
	private int simulation;
}
