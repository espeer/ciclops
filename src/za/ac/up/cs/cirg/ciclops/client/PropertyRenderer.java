/*
 * Created on Mar 10, 2004
 */
package za.ac.up.cs.cirg.ciclops.client;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import za.ac.up.cs.cirg.ciclops.model.ConfigBuilderNode;


class PropertyRenderer extends JLabel implements TableCellRenderer {
	
	public PropertyRenderer() {
		bold = this.getFont();
		plain = bold.deriveFont(Font.PLAIN);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,  boolean isSelected, boolean hasFocus, int row, int column) {
        ConfigBuilderNode node = (ConfigBuilderNode) table.getModel().getValueAt(row, 0);
        if (node.isDefault()) {
            setFont(plain);
        }
        else {
            setFont(bold);
        }
        if (value == null) {
            setText("");
        } 
        else {
            setText(value.toString());
        }
		return this;
    }
	
	private Font plain;
	private Font bold;
}