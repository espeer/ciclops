/*
 * Created on Feb 15, 2004
 *
 */
package za.ac.up.cs.cirg.ciclops.worker;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import za.ac.up.cs.cirg.ciclops.services.CodeManagerHome;
import za.ac.up.cs.cirg.ciclops.services.SupervisorHome;


/**
 * @author espeer
 *
 */
public class ServerContext implements CallbackHandler {
	
		private ServerContext()  {
			
		}
		
		public void login(URL server) throws LoginException, CreateException, RemoteException, NamingException {
			System.setProperty("java.security.auth.login.config", server.toExternalForm() + "/login.config");
			
			Properties jndiProps = new Properties() ;
			jndiProps.setProperty("java.naming.provider.url", server.getHost()) ;
			jndiProps.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory") ;
			jndiProps.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces" ) ;
			
			loginContext = new LoginContext("client", this);
			loginContext.login();

			Context context = new InitialContext(jndiProps);
			try {
				Object ref = context.lookup(SupervisorHome.JNDI_NAME);
				supervisorHome = (SupervisorHome) PortableRemoteObject.narrow(ref, SupervisorHome.class);
				
				ref = context.lookup(CodeManagerHome.JNDI_NAME);
				codeManagerHome = (CodeManagerHome) PortableRemoteObject.narrow(ref, CodeManagerHome.class);
			}
			finally {
				context.close();
			}	
		}
		
		public void logout() throws LoginException, RemoteException, RemoveException {
			loginContext.logout();
        }
		
		public static ServerContext instance() {
		 	return instance;
		}
		
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			for (int i = 0; i < callbacks.length; ++i) {
				if (callbacks[i] instanceof NameCallback) {
					NameCallback nc = (NameCallback) callbacks[i];
					nc.setName("worker");
				}
				else if (callbacks[i] instanceof PasswordCallback) {
					PasswordCallback pc = (PasswordCallback) callbacks[i];
					pc.setPassword("ooFi4kiei".toCharArray());
				}
				else {
					throw new UnsupportedCallbackException(callbacks[i]);
				}
			}
		}
		  
		public SupervisorHome getSupervisorHome() {
			return supervisorHome;
		}
		
		public CodeManagerHome getCodeManagerHome() {
			return codeManagerHome;
		}
		
		private static ServerContext instance = new ServerContext();
		
		private LoginContext loginContext;
		private SupervisorHome supervisorHome;
		private CodeManagerHome codeManagerHome;
}
