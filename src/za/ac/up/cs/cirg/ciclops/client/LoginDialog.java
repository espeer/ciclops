/*
 * Created on Feb 12, 2004
 *
 */
package za.ac.up.cs.cirg.ciclops.client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author espeer
 *
 */
public class LoginDialog extends JDialog implements ActionListener {

		public LoginDialog() {
			this.setModal(true);
			
			JPanel center = new JPanel();
			
			ok = new JButton("Ok");
			ok.addActionListener(this);
			
			user = new JTextField();
			user.setColumns(20);
			password = new JPasswordField();
			password.setColumns(20);
			
			JPanel p = new JPanel();
			p.setLayout(new GridLayout(2, 1));
			p.add(new JLabel("User:"));
			p.add(new JLabel("Password:"));
			center.add(p);
			
			p = new JPanel();
			p.setLayout(new GridLayout(2, 1));
			p.add(user);
			p.add(password);
			center.add(p);
			
			this.getContentPane().add("Center", center);
			
			p = new JPanel();
			p.add(ok);
			this.getContentPane().add("South", p);
			
			this.pack();
			this.show();
		}
		
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == ok) {
				this.hide();
			}
		}

		public String getUser() {
			return user.getText();
		}
		
		public String getPassword() {
			return password.getText();
		}
		
		private JButton ok;
		private JTextField user;
		private JTextField password;
}
