/*
 * Main.java
 *
 * Created on June 7, 2004, 10:14 AM
 */

package za.ac.up.cs.cirg.ciclops.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.table.TableColumnModel;

import za.ac.up.cs.cirg.ciclops.model.ActiveSimulationsModel;
import za.ac.up.cs.cirg.ciclops.model.ActiveWorkersModel;
import za.ac.up.cs.cirg.ciclops.model.BrowserItem;
import za.ac.up.cs.cirg.ciclops.model.ErrorListModel;
import za.ac.up.cs.cirg.ciclops.services.Results;
import za.ac.up.cs.cirg.ciclops.services.Task;

/**
 *
 * @author  espeer
 */
public class Main extends javax.swing.JFrame {
    
    /** Creates new form Main */
    public Main() {
        initComponents();
        
        try {
        	task = ServerContext.instance().getTaskHome().create();
        	
        	updateTables();
        	
            ActionListener tableUpdater = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                	try {
                		updateTables();
                	}
                	catch (Exception ex) {
                		throw new UnexpectedError(ex);
                	}
                			
                }
            };
            new Timer(10000, tableUpdater).start();

        }
        catch (Exception ex) {
        	throw new UnexpectedError(ex);
        }
        
    }
    
    private void updateTables() throws RemoteException, FinderException {
    	simulationsModel = task.getActiveSimulations();
    	workersModel = task.getActiveWorkers();
    	
    	simulations.setModel(simulationsModel);
    	workers.setModel(workersModel);
    	
        TableColumnModel columns = simulations.getColumnModel();
        columns.getColumn(0).setPreferredWidth(5);
        columns.getColumn(1).setPreferredWidth(300);
        columns.getColumn(2).setPreferredWidth(70);
        columns.getColumn(3).setPreferredWidth(70);
        columns.getColumn(4).setPreferredWidth(15);
        columns.getColumn(5).setPreferredWidth(15);
        
        columns = workers.getColumnModel();
        columns.getColumn(0).setPreferredWidth(5);
        columns.getColumn(1).setPreferredWidth(100);
        columns.getColumn(2).setPreferredWidth(100);
		columns.getColumn(3).setPreferredWidth(150);
		columns.getColumn(4).setPreferredWidth(20);
		columns.getColumn(5).setPreferredWidth(20);
		columns.getColumn(6).setPreferredWidth(80);
        
    	
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        simulations = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        workers = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newSimulation = new javax.swing.JMenuItem();
        newDataSet = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        openSimulation = new javax.swing.JMenuItem();
        openDataSet = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        exportSimulation = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        scheduleCVS = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        exit = new javax.swing.JMenuItem();

        setTitle("CiClops Client");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOneTouchExpandable(true);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(750, 250));
        simulations.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        simulations.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        simulations.setPreferredSize(new java.awt.Dimension(1600, 1280));
        simulations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                simulationsMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(simulations);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(750, 250));
        workers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        workers.setPreferredSize(new java.awt.Dimension(1600, 1280));
        workers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                workersMouseClicked(evt);
            }
        });

        jScrollPane2.setViewportView(workers);

        jSplitPane1.setRightComponent(jScrollPane2);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        newSimulation.setText("New Simulation");
        newSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSimulationActionPerformed(evt);
            }
        });

        fileMenu.add(newSimulation);

        newDataSet.setText("New Data Set");
        newDataSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newDataSetActionPerformed(evt);
            }
        });

        fileMenu.add(newDataSet);

        fileMenu.add(jSeparator1);

        openSimulation.setText("Open Simulation");
        openSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSimulationActionPerformed(evt);
            }
        });

        fileMenu.add(openSimulation);

        openDataSet.setText("Open Data Set");
        openDataSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openDataSetActionPerformed(evt);
            }
        });

        fileMenu.add(openDataSet);

        fileMenu.add(jSeparator2);

        exportSimulation.setText("Export Results");
        exportSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportSimulationActionPerformed(evt);
            }
        });

        fileMenu.add(exportSimulation);

        fileMenu.add(jSeparator3);

        scheduleCVS.setText("Shedule CVS Update");
        scheduleCVS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scheduleCVSActionPerformed(evt);
            }
        });

        fileMenu.add(scheduleCVS);

        fileMenu.add(jSeparator4);

        exit.setText("Exit");
        exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitActionPerformed(evt);
            }
        });

        fileMenu.add(exit);

        jMenuBar1.add(fileMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }//GEN-END:initComponents

    private void scheduleCVSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scheduleCVSActionPerformed
    	try {
    		task.scheduleCVSUpdate();
    	}
    	catch (RemoteException ex) {
    		throw new UnexpectedError(ex);
    	}
    }//GEN-LAST:event_scheduleCVSActionPerformed

    private void workersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_workersMouseClicked
    	if (evt.getClickCount() == 2) {
    		int id = workersModel.getIdForRow(workers.getSelectedRow());
    		
        	if (JOptionPane.showConfirmDialog(this, "Are you sure you want to kill/remove this worker?", "Kill/Remove Worker", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) { 
        		try {
        			task.killWorker(id);
            		updateTables();
        		}
        		catch (Exception ex) {
        			throw new UnexpectedError(ex);
        		}
        	}
    	}
    }//GEN-LAST:event_workersMouseClicked

    private void simulationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_simulationsMouseClicked
    	if (evt.getClickCount() == 2) {
    		int id = simulationsModel.getIdForRow(simulations.getSelectedRow());
    		
    		try {
    			ErrorListModel errors = task.getErrorList(id);
    			if (errors.getErrorCount() > 0) {
    				ErrorBrowser browser = new ErrorBrowser(this, true, task, task.getErrorList(id));
    				browser.setVisible(true);
    				updateTables();
    			}
    		}
    		catch (Exception ex) {
    			throw new UnexpectedError(ex);
    		}
    		
    	}
    }//GEN-LAST:event_simulationsMouseClicked

    private void exportSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSimulationActionPerformed
        BrowserDialog browser = new BrowserDialog(this, true, BrowserItem.TYPE_SIMULATION);
        browser.setVisible(true);
        BrowserItem item = browser.getSelected();
        if (item != null) {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            	try {
            		BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(chooser.getSelectedFile()));
    			
            		Results results = ServerContext.instance().getResultsHome().create();
    			
            		os.write(results.getExportResults(item.getId()));
            		os.close();
            	}
            	catch (IOException ex) {
            		JOptionPane.showMessageDialog(this, ex.getMessage());
            	}
            	catch (CreateException ex) {
            		JOptionPane.showMessageDialog(this, ex.getMessage());
            	}
            	catch (FinderException ex) {
            		JOptionPane.showMessageDialog(this, ex.getMessage());
            	}
            }
        }
    }//GEN-LAST:event_exportSimulationActionPerformed

    private void exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitActionPerformed
    	exitForm(null);
    }//GEN-LAST:event_exitActionPerformed

    private void openDataSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openDataSetActionPerformed
    	BrowserDialog browser = new BrowserDialog(this, true, BrowserItem.TYPE_DATA_SET);
    	browser.show();
    	BrowserItem item = browser.getSelected();
    	if (item != null) {
    		DataSetDialog dataSet = new DataSetDialog(this, true, item);
    		dataSet.show();
    	}
    	
    }//GEN-LAST:event_openDataSetActionPerformed

    private void openSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSimulationActionPerformed
    	BrowserDialog browser = new BrowserDialog(this, true, BrowserItem.TYPE_SIMULATION);
    	browser.show();
    	BrowserItem item = browser.getSelected();
    	if (item != null) {
    		SimulationDialog simulation = new SimulationDialog(this, true, item);
    		simulation.show();
    	}
    }//GEN-LAST:event_openSimulationActionPerformed

    private void newDataSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newDataSetActionPerformed
    	new DataSetDialog(this, true, new BrowserItem()).show();
    }//GEN-LAST:event_newDataSetActionPerformed

    private void newSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSimulationActionPerformed
    	new SimulationDialog(this, true, new BrowserItem()).show();
    }//GEN-LAST:event_newSimulationActionPerformed

    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fileMenuActionPerformed
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
    	try {
    		ServerContext.instance().logout();
    	}
    	catch (Exception ex) {
    		throw new UnexpectedError(ex);
    	}
        System.exit(0);
    }//GEN-LAST:event_exitForm
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws Exception {
    	ServerContext.instance().login(new URL("http://localhost:8080/CiClops/"));
    	new Main().show();
    	
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem exit;
    private javax.swing.JMenuItem exportSimulation;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenuItem newDataSet;
    private javax.swing.JMenuItem newSimulation;
    private javax.swing.JMenuItem openDataSet;
    private javax.swing.JMenuItem openSimulation;
    private javax.swing.JMenuItem scheduleCVS;
    private javax.swing.JTable simulations;
    private javax.swing.JTable workers;
    // End of variables declaration//GEN-END:variables
    
    private Task task;
    private ActiveSimulationsModel simulationsModel;
    private ActiveWorkersModel workersModel;
    
}
