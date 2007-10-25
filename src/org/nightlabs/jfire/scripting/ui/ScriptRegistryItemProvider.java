/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.scripting.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.cache.Cache;
import org.nightlabs.jfire.base.ui.jdo.JDOObjectDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.jdo.controller.JDOObjectController;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptManager;
import org.nightlabs.jfire.scripting.ScriptManagerUtil;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.ScriptRegistryItemCarrier;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;

/**
 * Provides accesses to cached versions of
 * <code>ReportRegistryItem</code>s and holds a representation 
 * of the ReportRegistry structure. Register a {@link ScriptRegistryListener}
 * to be notified on changes to the structure. 
 *
 *  
 * TODO: Implement correctly and move to server project
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ScriptRegistryItemProvider 
extends JDOObjectDAO<ScriptRegistryItemID, ScriptRegistryItem> 
{

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ScriptRegistryItemProvider.class); 

	/**
	 * holds all top level nodes by their type 
	 */
	private Map<String,ScriptRegistryItemNode> topLevelNodesByType;
	
	/**
	 * The root node holding the actual top level nodes as children
	 */
	private ScriptRegistryItemNode registryItemRootNode;
	
	/**
	 * A Map of all nodes in the structure with their IDs
	 */
	private Map<ScriptRegistryItemID, ScriptRegistryItemNode> allNodes; 
	
	/**
	 * The controller for the registry, changes when the structrue was changed
	 */
	private JDOObjectController registryController;
	
	/**
	 * Listeners to structure change
	 */
	private ListenerList registryListeners = new ListenerList();
	
	// TODO: Change fetch group when recursion-depth is available
	/**
	 * Fetch groups used when fetching single items that where
	 * removed from the cache
	 */
	public static final String[] DEFAULT_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ScriptRegistryItem.FETCH_GROUP_NAME
	};
	
	public static final String[] SCRIPT_FETCH_GROUPS = DEFAULT_FETCH_GROUPS;
	
	/**
	 * Constructs a new ScriptRegistryItemProvider
	 * @see #sharedInstance()
	 */
	public ScriptRegistryItemProvider() {
		super();
	}
	
	
	private boolean wasLoaded() {
		return registryItemRootNode != null;
	}
		
//	/**
//	 * Updates the structrue tree and notifies all listeners 
//	 * when the registry changed on the server.
//	 */
//	// TODO replace NotificationListener with JDOLifeCycleListener
//	private NotificationListener registryChangeListener = new NotificationListenerWorkerThreadAsync() 
//	{
//		public void notify(NotificationEvent evt) {
//			logger.info("registryChangeListener got notified with event "+evt);
//			for (Object subject : evt.getSubjects()) {
//				Object o =( (DirtyObjectID)subject).getObjectID();
//				logger.info("registryChangeListener with subject "+o);
//				if (o instanceof JDOObjectControllerID) {
//					if (registryController == null) {
//						loadAllRegistryItems();
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
////						if (event instanceof ScriptRegistryItemChangeEvent) {
////							ScriptRegistryItemChangeEvent registryEvent = (ScriptRegistryItemChangeEvent)event;							
////							if (ScriptRegistryItemChangeEvent.EVENT_TYPE_ITEM_ADDED.equals(registryEvent.getEventType())) {
////								changed = true;
////								ScriptRegistryItemNode node = null;
////								if (registryEvent.getRelatedItemCarrier() == null) {
////									// added to the root -> new root category
////									node = new ScriptRegistryItemNode(null, registryEvent.getItemCarrier(), false, allNodes);
////									ScriptRegistryItemID itemID;
////									try {
////										itemID = new ScriptRegistryItemID(registryEvent.getItemID());
////									} catch (Exception e) {
////										throw new RuntimeException(e);
////									}
////									topLevelNodesByType.put(itemID.scriptRegistryItemType, node);
////								}
////								else {
////									// added something to a parent
////									ScriptRegistryItemNode parentNode = allNodes.get(registryEvent.getRelatedItemCarrier().getRegistryItemID());
////									if (parentNode == null)
////										throw new IllegalStateException("Have change event: itemAdded, but could not find its parent in the local tree.");
////									node = new ScriptRegistryItemNode(
////											parentNode,
////											registryEvent.getItemCarrier(),
////											false,
////											allNodes
////									);
////									parentNode.addChildCarrier(node);
////								}
////							}
////						}
//					}
//					if (changed)
//						notifyRegistryListeners();
//				}
//			}
//		}		
//	};	

	/**
	 * Initially loads the structure from the server and puts all items into cache
	 *
	 */
	private void loadAllRegistryItems() 
	{
		Login login = null;
		try {
			login = Login.getLogin();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
//		// TODO replace ChangeManager with JDOLifecycleManager
//		JDOLifecycleManager.sharedInstance().removeNotificationListener(JDOObjectController.class, registryChangeListener);

		// Get the registry structure
		ScriptManager scriptManager = null;
		try {
			scriptManager = ScriptManagerUtil.getHome(login.getInitialContextProperties()).create();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Collection<ScriptRegistryItemCarrier> topLevelItems = null;
		try {
			topLevelItems = scriptManager.getTopLevelScriptRegistryItemCarriers(null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		registryItemRootNode = new ScriptRegistryItemNode();
		topLevelNodesByType = new HashMap<String,ScriptRegistryItemNode>();
		List<ScriptRegistryItemID> allItemIDs = new ArrayList<ScriptRegistryItemID>();
		allNodes = new HashMap<ScriptRegistryItemID, ScriptRegistryItemNode>();
		for (ScriptRegistryItemCarrier carrier : topLevelItems) {
			ScriptRegistryItemNode node = new ScriptRegistryItemNode(registryItemRootNode, carrier, true, allNodes);
			topLevelNodesByType.put(carrier.getRegistryItemType(), node);
			registryItemRootNode.addChildCarrier(node);
			insertItemIDs(node, allItemIDs);
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
//			registryController = jdoManager.getJDOObjectController(
//					ScriptRegistry.SINGLETON_ID, JDOObjectController.DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		Cache.sharedInstance().put(null, registryController, JDOObjectController.DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//		JDOLifecycleManager.sharedInstance().addNotificationListener(JDOObjectController.class, registryChangeListener);
				
		List allItems = null;
		try {
			allItems = scriptManager.getScriptRegistryItems(
					allItemIDs, DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		for (Iterator iter = allItems.iterator(); iter.hasNext();) {
			ScriptRegistryItem item = (ScriptRegistryItem) iter.next();
			Cache.sharedInstance().put(null, item, DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		}
	}	
	
	private void insertItemIDs(ScriptRegistryItemNode node, List<ScriptRegistryItemID> itemIDs) {
		itemIDs.add(node.getRegistryItemID());
		for (ScriptRegistryItemNode childNode : node.getChildNodes()) {
			insertItemIDs(childNode, itemIDs);
		}
	}	

	private static Collection<ScriptRegistryItemNode> EMPTY_COLLECTION;
	
	/**
	 * Returns the top level nodes of the ReportRegistry structure.
	 * In fact this returns the whole structure as the top level
	 * nodes know their children and so on ...
	 */
	public Collection<ScriptRegistryItemNode> getTopLevelNodes() {
		if (!wasLoaded())
			loadAllRegistryItems();		
		if (topLevelNodesByType == null)
			return EMPTY_COLLECTION;
		return topLevelNodesByType.values();
	}
	
	public ScriptRegistryItemNode getTopLevelNodeForType(String scriptRegistryItemType) {
		if (!wasLoaded())
			loadAllRegistryItems();		
		if (topLevelNodesByType == null)
			return null;
		return topLevelNodesByType.get(scriptRegistryItemType);
	}
	
	/**
	 * Returns the top level nodes for the given set of ScriptRegistryItemIDs
	 */
	public Collection<ScriptRegistryItemNode> getNodes(Set<ScriptRegistryItemID> itemIDs) 
	{
		if (!wasLoaded())
			loadAllRegistryItems();		
		if (topLevelNodesByType == null)
			return EMPTY_COLLECTION;
		
		Collection<ScriptRegistryItemNode> topLevelNodes = new LinkedList<ScriptRegistryItemNode>();
		for (ScriptRegistryItemID itemID : itemIDs) {
			topLevelNodes.add(allNodes.get(itemID));
		}
		return topLevelNodes;
	}	
	
	private static ScriptRegistryItemProvider sharedInstance;
	
	/**
	 * Access <code>ScriptRegistryItem</code>s via this static
	 * instance of ScriptRegistryItemProvider.
	 */
	public static ScriptRegistryItemProvider sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new ScriptRegistryItemProvider();
		return sharedInstance;
	}
	
	@Override
	protected ScriptRegistryItem retrieveJDOObject(ScriptRegistryItemID scriptID, String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	throws Exception
	{
		if (scriptID == null)
			throw new IllegalArgumentException("Param scriptID must not be null!"); //$NON-NLS-1$
		
		ScriptManager scriptManager = ScriptManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		ScriptRegistryItem registryItem = scriptManager.getScriptRegistryItem(
				scriptID, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		return registryItem;
	}
	
	@Override
	protected Collection<ScriptRegistryItem> retrieveJDOObjects(Set<ScriptRegistryItemID> scriptIDs, String[] fetchGroups, int maxFetchDepth, IProgressMonitor monitor)
	throws Exception
	{
		if (scriptIDs == null)
			throw new IllegalArgumentException("Param scriptIDs must not be null!"); //$NON-NLS-1$
		
		List<ScriptRegistryItemID> ids = new ArrayList<ScriptRegistryItemID>(scriptIDs);
		ScriptManager scriptManager = ScriptManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		List registryItems = scriptManager.getScriptRegistryItems(
				ids, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		return registryItems;
	}
	
	/**
	 * Get the <code>ScriptRegistryItem</code> with the given itemID and
	 * {@link #DEFAULT_FETCH_GROUPS}. 
	 */
	public ScriptRegistryItem getScriptRegistryItem(ScriptRegistryItemID itemID, IProgressMonitor monitor) {
		return getJDOObject(null, itemID, DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}
	
	/**
	 * Get the <code>ScriptRegistryItem</code> with the given itemID and fetchGroups.
	 */
	public ScriptRegistryItem getScriptRegistryItem(ScriptRegistryItemID itemID, String[] fetchGroups, IProgressMonitor monitor) {
		return getJDOObject(null, itemID, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}
	
	/**
	 * Get the <code>ScriptRegistryItem</code> with the given id and 
	 * cast it to a Script.
	 * @throws ClassCastException
	 */
	public Script getScript(ScriptRegistryItemID scriptLayoutID, IProgressMonitor monitor) {
		return (Script)getScriptRegistryItem(scriptLayoutID, SCRIPT_FETCH_GROUPS, monitor); 
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
	public void removeScriptRegistryListener(ScriptRegistryListener registryListener) {
		registryListeners.remove(registryListener);
	}
	
	private void notifyRegistryListeners() 
	{
		for (int i=0; i<registryListeners.size(); i++) 
		{
			ScriptRegistryListener listener = 
				(ScriptRegistryListener) registryListeners.getListeners()[i]; 
			try {
				listener.scriptRegistryChanged();
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}
	
}
