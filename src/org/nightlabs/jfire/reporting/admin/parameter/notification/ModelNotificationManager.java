package org.nightlabs.jfire.reporting.admin.parameter.notification;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.nightlabs.jdo.ObjectID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ModelNotificationManager 
{
	public static final String PROP_CREATE = "Create"; //$NON-NLS-1$
	public static final String PROP_DELETE = "Delete"; //$NON-NLS-1$
	public static final String PROP_DELETE_CONNECTION = "DeleteConnection"; //$NON-NLS-1$
	public static final String PROP_CREATE_CONNECTION = "CreateConnection";	 //$NON-NLS-1$
	public static final String PROP_MOVE = "Move"; //$NON-NLS-1$
	public static final String PROP_CONNECT = "Connect"; //$NON-NLS-1$
	public static final String PROP_CONSUMER_KEY = "ConsumerKey"; //$NON-NLS-1$
	public static final String PROP_NAME = "Name"; //$NON-NLS-1$
	public static final String PROP_DESCRIPTION = "Description"; //$NON-NLS-1$
	public static final String PROP_MESSAGE = "Message"; //$NON-NLS-1$
	public static final String PROP_PAGE_INDEX = "PageIndex"; //$NON-NLS-1$
	public static final String PROP_PAGE_ORDER = "PageOrder"; //$NON-NLS-1$
	public static final String PROP_PARAMETER_ID = "ParameterID"; //$NON-NLS-1$
	public static final String PROP_PARAMETER_TYPE = "ParameterType"; //$NON-NLS-1$
	public static final String PROP_PROVIDER = "Provider"; //$NON-NLS-1$
	public static final String PROP_CONSUMER = "Consumer"; //$NON-NLS-1$
	public static final String PROP_OUTPUT_TYPE = "OutputType"; //$NON-NLS-1$
	public static final String PROP_ALLOW_OUTPUT_NULL_VALUE = "AllowOutputNullValue"; //$NON-NLS-1$
	public static final String PROP_SHOW_MESSAGE_IN_HEADER = "ShowMessageInHeader"; //$NON-NLS-1$

	private static ModelNotificationManager sharedInstance;
	public static ModelNotificationManager sharedInstance() {
		if (sharedInstance == null) {
			synchronized (ModelNotificationManager.class) {
				if (sharedInstance == null)
					sharedInstance = new ModelNotificationManager();
			}
		}
		return sharedInstance;
	}
	
	protected ModelNotificationManager() {
		 globalPropertyChangeListener = new PropertyChangeSupport(this);
	}
	
	private Map<ObjectID, PropertyChangeSupport> objectID2Listener = new HashMap<ObjectID, PropertyChangeSupport>();
	private PropertyChangeSupport globalPropertyChangeListener;
	
//	public void addPropertyChangeListener(Object model, PropertyChangeListener listener) 
//	{
//		ObjectID objectID = (ObjectID) JDOHelper.getObjectId(model); 
//		addPropertyChangeListener(objectID, listener);
//	}
	
	public void addPropertyChangeListener(ObjectID objectID, PropertyChangeListener listener) 
	{
		if (objectID == null) {
			globalPropertyChangeListener.addPropertyChangeListener(listener);
			return;
		}
		PropertyChangeSupport pcs = objectID2Listener.get(objectID);
		if (pcs == null)
			pcs = new PropertyChangeSupport(objectID);			
		
		pcs.addPropertyChangeListener(listener);
		objectID2Listener.put(objectID, pcs);		
	}

//	public void removePropertyChangeListener(Object model, PropertyChangeListener listener) 
//	{
//		ObjectID objectID = (ObjectID) JDOHelper.getObjectId(model); 
//		removePropertyChangeListener(objectID, listener);
//	}
	
	public void removePropertyChangeListener(ObjectID objectID, PropertyChangeListener listener) 
	{
		if (objectID == null) {
			globalPropertyChangeListener.removePropertyChangeListener(listener);
			return;
		}
		PropertyChangeSupport pcs = objectID2Listener.get(objectID);
		if (pcs != null) {
			pcs.removePropertyChangeListener(listener);
			objectID2Listener.put(objectID, pcs);
		}		
	}
	
//	public void notify(Object model, String propertyName, Object oldValue, Object newValue) 
//	{
//		ObjectID objectID = (ObjectID) JDOHelper.getObjectId(model);
//		notify(objectID, propertyName, oldValue, newValue);
//	}
	
	public void notify(ObjectID objectID, String propertyName, Object oldValue, Object newValue) 
	{
		PropertyChangeSupport pcs = objectID2Listener.get(objectID);
		if (pcs != null)
			pcs.firePropertyChange(propertyName, oldValue, newValue);
		globalPropertyChangeListener.firePropertyChange(propertyName, oldValue, newValue);		
	}
	
}
