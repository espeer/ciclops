/*
 * ErrorBrowser.java
 *
 * Created on November 4, 2004, 8:05 PM
 */

package za.ac.up.cs.cirg.ciclops.client;

import za.ac.up.cs.cirg.ciclops.model.ErrorListModel;
import za.ac.up.cs.cirg.ciclops.services.Task;

/**
 *
 * @author  espeer
 */
public class ErrorBrowser extends javax.swing.JDialog {
    
    /** Creates new form ErrorBrowser */
    public ErrorBrowser(java.awt.Frame parent, boolean modal, Task task, ErrorListModel errors) {
        super(parent, modal);
        initComponents();
        
        this.task = task;
        this.errors = errors;
    	index = 0;
        
        displayError();
        
    }
    
    /**
	 * @param errors
	 */
	private void displayError() {
    	message.setText(errors.getError(index));        	
       
    	if (index < errors.getErrorCount() - 1) {
    		next.setEnabled(true);
    	}
    	else {
    		next.setEnabled(false);
    	}

    	if (index == 0) {
    		previous.setEnabled(false);
    	}
    	else {
    		previous.setEnabled(true);
    	}
       
	}

	/** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jScrollPane1 = new javax.swing.JScrollPane();
        message = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        previous = new javax.swing.JButton();
        next = new javax.swing.JButton();
        clear = new javax.swing.JButton();
        ok = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Simulation Errors");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jScrollPane1.setPreferredSize(new java.awt.Dimension(600, 400));
        jScrollPane1.setViewportView(message);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        previous.setText("<<");
        previous.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousActionPerformed(evt);
            }
        });

        jPanel1.add(previous);

        next.setText(">>");
        next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextActionPerformed(evt);
            }
        });

        jPanel1.add(next);

        clear.setText("Clear");
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });

        jPanel1.add(clear);

        ok.setText("Ok");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        jPanel1.add(ok);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
        dispose();
    }//GEN-LAST:event_formWindowClosing

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
    	formWindowClosing(null);
    }//GEN-LAST:event_okActionPerformed

    private void clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearActionPerformed
    	try {
    		task.clearErrors(errors.getSimulationId());
    		formWindowClosing(null);
    	}
    	catch (Exception ex) {
    		throw new UnexpectedError(ex);
    	}
    }//GEN-LAST:event_clearActionPerformed

    private void previousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousActionPerformed
    	--index;
    	displayError();
    }//GEN-LAST:event_previousActionPerformed

    private void nextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextActionPerformed
    	++index;
    	displayError();
    }//GEN-LAST:event_nextActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clear;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea message;
    private javax.swing.JButton next;
    private javax.swing.JButton ok;
    private javax.swing.JButton previous;
    // End of variables declaration//GEN-END:variables
    

    private Task task;
    private ErrorListModel errors;
    private int index;
}
