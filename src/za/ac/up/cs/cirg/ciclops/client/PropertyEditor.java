/*
 * Created on Mar 10, 2004
 */
package za.ac.up.cs.cirg.ciclops.client;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import za.ac.up.cs.cirg.ciclops.model.BrowserItem;
import za.ac.up.cs.cirg.ciclops.model.ConfigBuilderNode;
import za.ac.up.cs.cirg.ciclops.model.SchemaModel;


class PropertyEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
	
	public PropertyEditor(Frame frame) {
	    selectorButton = new JButton();
        selectorButton.addActionListener(this);
        selectorButton.setBorderPainted(false);
        this.frame = frame;
	}
	
	public Object getCellEditorValue() {
        if (component instanceof JFormattedTextField) {
            JFormattedTextField ftf = (JFormattedTextField) component;
            return ftf.getValue();
        }
        else if (component instanceof JTextField) {
            JTextField tf = (JTextField) component;
            return tf.getText();
        }
        else if (component instanceof JComboBox) {
            JComboBox cb = (JComboBox) component;
            return cb.getSelectedItem();
        }
        else if (component instanceof JButton) {
        	if (selector instanceof MultipleSelector) {
        		return ((MultipleSelector) selector).getSelection();
        	}
        	else if (selector instanceof BrowserDialog) {
        		BrowserItem selected = ((BrowserDialog) selector).getSelected();
        		if (selected != null) {
        			return new Integer(selected.getId());
        		}
        		else {
        			return null;
        		}
        	}
        } 
        return null;
    }
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (selector != null) {
            selector.setVisible(false);
            selector.dispose();
            selector = null;
        }
        
        ConfigBuilderNode node = (ConfigBuilderNode) table.getModel().getValueAt(row, 0);
		if (node.getForm().equals(SchemaModel.FORM_BASIC)) {
            try {
                Class c = Class.forName(node.getType());
                if (String.class.isAssignableFrom(c)) {
                    JTextField tf = new JTextField(value.toString());
                    tf.addActionListener(this);
                    component = tf;
                }
                else if (Number.class.isAssignableFrom(c)) {
                    JFormattedTextField ftf = new JFormattedTextField();
                    ftf.addActionListener(this);
                    ftf.setFocusLostBehavior(JFormattedTextField.REVERT);
                    if (value == null) {
                        Class[] ca = { String.class };
                        Object[] oa = { "0" };
                        ftf.setValue(Class.forName(node.getType()).getConstructor( ca ).newInstance( oa ));
                    }
                    else {
                        ftf.setValue(value);
                    }
                    component = ftf;
                }
            }
            catch (Exception ex) {
                throw new UnexpectedError(ex);
            }
        }
        else if (node.getForm().equals(SchemaModel.FORM_CLASS)) {
		    JComboBox cb = new JComboBox();
            cb.addActionListener(this);
            DefaultComboBoxModel model = new DefaultComboBoxModel();
            Iterator i = node.getLegalValues().iterator();
		    while (i.hasNext()) {
		        Object tmp = i.next();
                if (tmp == null && value == null) {
                    model.insertElementAt(tmp, 0);
                }
                else if (tmp != null && tmp.equals(value)) {
                    model.insertElementAt(tmp, 0);
                }
                else {
                    model.addElement(tmp);
                }
		    }
            cb.setModel(model);
		    component = cb;
		}
		else if (node.getForm().equals(SchemaModel.FORM_MULTIPLE)) {
            selector = new MultipleSelector(frame, true, (Collection) node.getValue(), node.getLegalValues());
            component = selectorButton;
		}
		else if (node.getForm().equals(SchemaModel.FORM_DATA_SET)) {
			selector = new BrowserDialog(frame, true, BrowserItem.TYPE_DATA_SET);
			component = selectorButton;
		}
			
		return component;
        
	}
	
	public void actionPerformed(ActionEvent e) {
        if (e.getSource() == selectorButton) {
            selector.show();
        }
       
        this.fireEditingStopped();
	}
	
	private Component component;
    private JButton selectorButton;
    private Dialog selector;
    private Frame frame;
    
}