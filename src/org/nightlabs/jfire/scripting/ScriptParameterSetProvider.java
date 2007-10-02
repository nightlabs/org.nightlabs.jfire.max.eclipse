/**
 * 
 */
package org.nightlabs.jfire.scripting;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.FetchPlan;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.JDOObjectProvider;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.jdo.controller.JDOObjectController;
import org.nightlabs.jfire.scripting.id.ScriptParameterSetID;

/**
 * Accessor for ScriptParameterSets.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class ScriptParameterSetProvider extends JDOObjectProvider {
	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ScriptParameterSetProvider.class);

	public static String[] DEFAULT_FETCH_GROUPS = new String[]{
		FetchPlan.DEFAULT,
		ScriptParameterSet.FETCH_GROUP_NAME,
		ScriptParameterSet.FETCH_GROUP_PARAMETERS
	};
	
	/**
	 * Listeners to structure change
	 */
	private Collection<ScriptRegistryListener> registryListeners = new HashSet<ScriptRegistryListener>();
	
	/**
	 * The controller for the registry, changes when the structrue was changed
	 */
	private JDOObjectController registryController;
	
//	/**
//	 * Updates the structrue tree and notifies all listeners 
//	 * when the registry changed on the server.
//	 */
//	private NotificationListener parameterSetChangeListener = new NotificationListenerWorkerThreadAsync() {
//		public void notify(NotificationEvent evt) {
//			logger.info("registryChangeListener got notified with event "+evt);
//			for (Object subject : evt.getSubjects()) {
//				Object o = ((DirtyObjectID)subject).getObjectID();
//				
//				logger.info("registryChangeListener with subject "+o);
//				if (o instanceof JDOObjectControllerID) {
//					if (registryController == null) {
//						getParameterSets();
//						break;
//					}
//					JDOManager jdoManager = null;
//					try {
//						jdoManager = JDOManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//					} catch (Exception e) {						
//						throw new RuntimeException(e);
//					}
//					JDOObjectSyncResult syncResult = null;
//					try {
//						syncResult = jdoManager.syncJDOObjectChanges(
//								ScriptRegistry.SINGLETON_ID, 
//								registryController.getControllerVersion(),
//								null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
//							);
//					} catch (Exception e) {
//						throw new RuntimeException(e);
//					}
//					registryController = syncResult.getJdoObjectController();
//					boolean changed = false;
//					for (JDOObjectChangeEvent event : syncResult.getChangeEvents()) {
//						if (event instanceof ScriptParameterSetChangeEvent) {
//							ScriptParameterSetChangeEvent parameterEvent = (ScriptParameterSetChangeEvent)event;							
//							if (ScriptParameterSetChangeEvent.EVENT_TYPE_SET_ADDED.equals(parameterEvent.getEventType())) {
//								changed = true;
//								if (parameterSets == null)
//									return; // TODO: Do something better
//								parameterSets.add(getScriptParameterSet(parameterEvent.getScriptParameterSetID()));
//							}
//						}
//					}
//					if (changed)
//						notifyRegistryListeners();
//				}
//			}
//		}		
//	};	
	
	
	private Collection<ScriptParameterSet> parameterSets;
	// TODO: listen to login-state
	
	protected boolean isLoaded() {
		return parameterSets != null;
	}
	
	/**
	 * Returns the collection of all Parametersets
	 * for the organisation of the currently logged in 
	 * user.
	 */
	public Collection<ScriptParameterSet> getParameterSets() {
		if (isLoaded())
			return parameterSets;
		
		try {
			parameterSets = ScriptingPlugin.getScriptManager().getScriptParameterSets(
					Login.getLogin().getOrganisationID(),
					DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
//		// Get the registry controller keeping 
//		// track of all changes
//		JDOManager jdoManager = null;
//		try {
//		  jdoManager = JDOManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		try {
//			registryController = jdoManager.getJDOObjectController(ScriptRegistry.SINGLETON_ID, JDOObjectController.DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		Cache.sharedInstance().put(null, registryController, JDOObjectController.DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//		JDOLifecycleManager.sharedInstance().addNotificationListener(JDOObjectController.class, parameterSetChangeListener);
		
		
		return parameterSets;
	}
	
	/**
	 * 
	 */
	protected ScriptParameterSetProvider() {
		super();
	}	
	
	
	@Override
	protected Object retrieveJDOObject(String scope, Object objectID, String[] fetchGroups, int maxFetchDepth) throws Exception {
		if (objectID instanceof ScriptParameterSetID)
			return ScriptingPlugin.getScriptManager().getScriptParameterSet((ScriptParameterSetID)objectID, fetchGroups, maxFetchDepth);
		return null;
	}
	
	public ScriptParameterSet getScriptParameterSet(String organisationID, long parameterSetID, String[] fetchGroups, int maxFetchDepth) {
		return (ScriptParameterSet)getJDOObject(null, ScriptParameterSetID.create(organisationID, parameterSetID), fetchGroups, maxFetchDepth);
	}

	public ScriptParameterSet getScriptParameterSet(String organisationID, long parameterSetID, String[] fetchGroups) {
		return getScriptParameterSet(organisationID, parameterSetID, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
	}

	public ScriptParameterSet getScriptParameterSet(long parameterSetID) {
		try {
			return getScriptParameterSet(Login.getLogin().getOrganisationID(), parameterSetID, DEFAULT_FETCH_GROUPS);
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Add a listener to changes of the ScriptRegistry structure
	 * @param registryListener The listener to add
	 */
	public void addScriptRegistryListener(ScriptRegistryListener registryListener) {
		registryListeners.add(registryListener);
	}
	
	/**
	 * Removes the given listener
	 */
	public void remveReportRegistryListener(ScriptRegistryListener registryListener) {
		registryListeners.remove(registryListener);
	}
	
	private void notifyRegistryListeners() {
		for (ScriptRegistryListener listener : new HashSet<ScriptRegistryListener>(registryListeners)) {
			try {
				listener.scriptRegistryChanged();
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	
	private static ScriptParameterSetProvider sharedInstance;
	
	public static ScriptParameterSetProvider sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new ScriptParameterSetProvider();
		return sharedInstance;
	}

}
