/*
 * CategoryBrowserDialog.java
 *
 * Created on March 24, 2004, 11:31 AM
 */

package za.ac.up.cs.cirg.ciclops.client;

import java.awt.Frame;

import javax.swing.JOptionPane;

import za.ac.up.cs.cirg.ciclops.model.BrowserItem;
import za.ac.up.cs.cirg.ciclops.model.BrowserModel;

/**
 *
 * @author  espeer
 */
public class BrowserDialog extends javax.swing.JDialog {
    
    /** Creates new form CategoryBrowserDialog */
    public BrowserDialog(java.awt.Frame parent, boolean modal, int type) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        newCategory.setVisible(true);
        description.setVisible(type != BrowserItem.TYPE_CATEGORY);
        newSimulation.setVisible(type == BrowserItem.TYPE_SIMULATION);
        newDataSet.setVisible(type == BrowserItem.TYPE_DATA_SET);
        model = new BrowserModel(type);
        categories.setModel(model);
        categories.setSelectionModel(model);
        contents.setModel(model);
        selected = null;
        /*
        timer = new Timer(30000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.refresh();
            }
        });
        timer.start();
        */
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        categories = new javax.swing.JTree();
        jScrollPane1 = new javax.swing.JScrollPane();
        contents = new javax.swing.JList();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        description = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        newCategory = new javax.swing.JButton();
        newSimulation = new javax.swing.JButton();
        newDataSet = new javax.swing.JButton();
        ok = new javax.swing.JButton();
        cancel = new javax.swing.JButton();

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        setTitle("Category Browser");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setOneTouchExpandable(true);
        jScrollPane2.setPreferredSize(new java.awt.Dimension(120, 363));
        jScrollPane2.setViewportView(categories);

        jSplitPane1.setLeftComponent(jScrollPane2);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(400, 131));
        contents.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        contents.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                contentsValueChanged(evt);
            }
        });
        contents.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                contentsMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(contents);

        jSplitPane1.setRightComponent(jScrollPane1);

        jPanel1.add(jSplitPane1);

        getContentPane().add(jPanel1);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));

        description.setEditable(false);
        description.setRows(6);
        jScrollPane3.setViewportView(description);

        jPanel2.add(jScrollPane3);

        getContentPane().add(jPanel2);

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.X_AXIS));

        newCategory.setText("New Category");
        newCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCategoryActionPerformed(evt);
            }
        });

        jPanel4.add(newCategory);

        newSimulation.setText("New Simulation");
        newSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSimulationActionPerformed(evt);
            }
        });

        jPanel4.add(newSimulation);

        newDataSet.setText("New Data Set");
        newDataSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newDataSetActionPerformed(evt);
            }
        });

        jPanel4.add(newDataSet);

        jPanel4.add(javax.swing.Box.createHorizontalGlue());
        ok.setText("Ok");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        jPanel4.add(ok);

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        jPanel4.add(cancel);

        getContentPane().add(jPanel4);

        pack();
    }//GEN-END:initComponents

    private void newSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSimulationActionPerformed
    	new SimulationDialog(parent, true, model.getSelectedCategory()).show();
    	model.refresh();
    }//GEN-LAST:event_newSimulationActionPerformed

    private void contentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_contentsMouseClicked
        if (evt.getClickCount() == 2) { 
            int index = contents.locationToIndex(evt.getPoint());
            selected = (BrowserItem) contents.getModel().getElementAt(index);
            closeDialog(null);
        }
    }//GEN-LAST:event_contentsMouseClicked

    private void contentsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_contentsValueChanged
        BrowserItem bo = (BrowserItem) contents.getSelectedValue();
        description.setText(bo.getDescription());
    }//GEN-LAST:event_contentsValueChanged

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
    	closeDialog(null);
    }//GEN-LAST:event_cancelActionPerformed

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
    	selected = (BrowserItem) contents.getSelectedValue();
    	closeDialog(null);
    }//GEN-LAST:event_okActionPerformed

    private void newDataSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newDataSetActionPerformed
        new DataSetDialog(parent, true, model.getSelectedCategory()).show();
        model.refresh();
    }//GEN-LAST:event_newDataSetActionPerformed

    private void newCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCategoryActionPerformed
        String category = (String) JOptionPane.showInputDialog(this, "Category name:");
        if (category != null) {
            model.createCategory(category);
        }
    }//GEN-LAST:event_newCategoryActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    
    public BrowserItem getSelected() {
    	return selected;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JTree categories;
    private javax.swing.JList contents;
    private javax.swing.JTextArea description;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton newCategory;
    private javax.swing.JButton newDataSet;
    private javax.swing.JButton newSimulation;
    private javax.swing.JButton ok;
    // End of variables declaration//GEN-END:variables
    
    // private Timer timer;
    private BrowserModel model;
    private Frame parent;
    private BrowserItem selected;
}
