/*
 * Created on Nov 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package za.ac.up.cs.cirg.ciclops.model;

import java.io.Serializable;

/**
 * @author espeer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class MeasurementModel implements Serializable {

	public MeasurementModel(String domain, byte[] data) {
		this.domain = domain;
		this.data = data;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public byte[] getData() {
		return data;
	}
	
	private byte[] data;
	private String domain;
}
