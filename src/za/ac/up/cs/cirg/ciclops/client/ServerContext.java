/*
 * Created on Feb 15, 2004
 *
 */
package za.ac.up.cs.cirg.ciclops.client;

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

import za.ac.up.cs.cirg.ciclops.services.CategoryBrowserHome;
import za.ac.up.cs.cirg.ciclops.services.CodeManagerHome;
import za.ac.up.cs.cirg.ciclops.services.DataSetEditorHome;
import za.ac.up.cs.cirg.ciclops.services.ResultsHome;
import za.ac.up.cs.cirg.ciclops.services.SchemaHome;
import za.ac.up.cs.cirg.ciclops.services.SimulationEditorHome;
import za.ac.up.cs.cirg.ciclops.services.Task;
import za.ac.up.cs.cirg.ciclops.services.TaskHome;


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

			context = new InitialContext(jndiProps);
			try {
				Object ref = context.lookup(CodeManagerHome.JNDI_NAME);
				codeManagerHome = (CodeManagerHome) PortableRemoteObject.narrow(ref, CodeManagerHome.class);
				
                ref = context.lookup(CategoryBrowserHome.JNDI_NAME);
                categoryBrowserHome = (CategoryBrowserHome) PortableRemoteObject.narrow(ref, CategoryBrowserHome.class);
                
                ref = context.lookup(SimulationEditorHome.JNDI_NAME);
                simulationEditorHome = (SimulationEditorHome) PortableRemoteObject.narrow(ref, SimulationEditorHome.class);
                
                ref = context.lookup(DataSetEditorHome.JNDI_NAME);
                dataSetEditorHome = (DataSetEditorHome) PortableRemoteObject.narrow(ref, DataSetEditorHome.class);
                
				ref = context.lookup(SchemaHome.JNDI_NAME);
                schemaHome = (SchemaHome) PortableRemoteObject.narrow(ref, SchemaHome.class);
                
                ref = context.lookup(ResultsHome.JNDI_NAME);
                resultsHome = (ResultsHome) PortableRemoteObject.narrow(ref, ResultsHome.class);
                
                ref = context.lookup(TaskHome.JNDI_NAME);
                taskHome = (TaskHome) PortableRemoteObject.narrow(ref, TaskHome.class);
                
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
		
		public Context getInitialContext() {
			return context;
		}
		
		public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
			LoginDialog login = new LoginDialog();
			for (int i = 0; i < callbacks.length; ++i) {
				if (callbacks[i] instanceof NameCallback) {
					NameCallback nc = (NameCallback) callbacks[i];
					nc.setName(login.getUser());
				}
				else if (callbacks[i] instanceof PasswordCallback) {
					PasswordCallback pc = (PasswordCallback) callbacks[i];
					 pc.setPassword(login.getPassword().toCharArray());
				}
				else {
					throw new UnsupportedCallbackException(callbacks[i]);
				}
			}
		}
		  
		public CodeManagerHome getCodeManagerHome() {
			return codeManagerHome;
		}
		
		public SimulationEditorHome getSimulationEditorHome() {
			return simulationEditorHome;
		}
		
        public DataSetEditorHome getDataSetEditorHome() {
            return dataSetEditorHome;
        }
        
        public SchemaHome getSchemaHome() {
            return schemaHome;
        }
        
        public CategoryBrowserHome getCategoryBrowserHome() {
            return categoryBrowserHome;
        }
        
        public ResultsHome getResultsHome() {
        	return resultsHome;
        }
        
        public TaskHome getTaskHome() {
        	return taskHome;
        }
        
		private static ServerContext instance = new ServerContext();
		
		private LoginContext loginContext;
		private CodeManagerHome codeManagerHome;
        private CategoryBrowserHome categoryBrowserHome;
        private SimulationEditorHome simulationEditorHome;
        private DataSetEditorHome dataSetEditorHome;
        private SchemaHome schemaHome;
        private ResultsHome resultsHome;
        private TaskHome taskHome;
        
        private Context context;
}
