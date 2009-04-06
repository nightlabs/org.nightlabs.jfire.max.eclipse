package org.nightlabs.jfire.entityuserset.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.entityuserset.AuthorizedObjectRef;
import org.nightlabs.jfire.entityuserset.EntityRef;
import org.nightlabs.jfire.entityuserset.EntityUserSet;
import org.nightlabs.jfire.entityuserset.dao.EntityUserSetDAO;
import org.nightlabs.jfire.entityuserset.id.EntityUserSetID;
import org.nightlabs.jfire.security.AuthorizedObject;
import org.nightlabs.jfire.security.UserLocal;
import org.nightlabs.jfire.security.UserSecurityGroup;
import org.nightlabs.jfire.security.dao.AuthorizedObjectDAO;
import org.nightlabs.jfire.security.id.AuthorizedObjectID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public abstract class EntityUserSetPageControllerHelper<Entity> 
{
	private static final String[] FETCH_GROUPS_ENTITY_USER_SET = {
		FetchPlan.DEFAULT,
		EntityUserSet.FETCH_GROUP_NAME,
		EntityUserSet.FETCH_GROUP_DESCRIPTION,
		EntityUserSet.FETCH_GROUP_AUTHORIZED_OBJECT_REFS,
		AuthorizedObjectRef.FETCH_GROUP_ENTITY_REFS
	};

	private static final String[] FETCH_GROUPS_AUTHORIZED_OBJECT = {
		FetchPlan.DEFAULT,
		AuthorizedObject.FETCH_GROUP_NAME,
		AuthorizedObject.FETCH_GROUP_DESCRIPTION,
		UserLocal.FETCH_GROUP_USER,
		UserLocal.FETCH_GROUP_USER_SECURITY_GROUPS,
		UserSecurityGroup.FETCH_GROUP_MEMBERS
	};

	/**
	 * The {@link #load(EntityUserSetID, EntityUserSet, ProgressMonitor)} method has been called (and is finished).
	 * The loaded {@link EntityUserSet} can be accessed by {@link PropertyChangeEvent#getNewValue()}.
	 */
	public static final String PROPERTY_NAME_ENTITY_USER_SET_LOADED = "entityUserSetLoaded"; //$NON-NLS-1$

	/**
	 * A {@link PropertyChangeEvent} with this property name is fired, when a authorizedObject has been removed from the
	 * currently managed {@link EntityUserSet}. The affected authorizedObject object can be accessed by
	 * {@link PropertyChangeEvent#getNewValue()}.
	 */
	public static final String PROPERTY_NAME_USER_REMOVED = "authorizedObjectRemoved"; //$NON-NLS-1$

	/**
	 * A {@link PropertyChangeEvent} with this property name is fired, when a authorizedObject has been added to the
	 * currently managed {@link EntityUserSet}. The affected authorizedObject object can be accessed by
	 * {@link PropertyChangeEvent#getNewValue()}.
	 */
	public static final String PROPERTY_NAME_USER_ADDED = "authorizedObjectAdded"; //$NON-NLS-1$

	/**
	 * A {@link PropertyChangeEvent} with this property name is fired, when an Entity has been added to 
	 * the current selected authorizedObject of the currently managed {@link EntityUserSet}. 
	 * The affected entity object can be accessed by {@link PropertyChangeEvent#getNewValue()}.
	 */
	public static final String PROPERTY_NAME_ENTITY_ADDED_TO_AUTHORIZED_OBJECT = "entityAdded"; //$NON-NLS-1$
	
	/**
	 * A {@link PropertyChangeEvent} with this property name is fired, when an Entity has been removed from 
	 * the current selected authorizedObject of the currently managed {@link EntityUserSet}. 
	 * The affected entity object can be accessed by {@link PropertyChangeEvent#getNewValue()}.
	 */
	public static final String PROPERTY_NAME_ENTITY_REMOVED_TO_AUTHORIZED_OBJECT = "entityRemoved"; //$NON-NLS-1$
	
	private PropertyChangeSupport propertyChangeSupport;	
	private EntityUserSetID entityUserSetID;
	private EntityUserSet<Entity> entityUserSet;
	private String[] entityUserSetFetchGroups;
	private Map<AuthorizedObject, Boolean> authorizedObjects;
	private Map<AuthorizedObjectID, Map<Entity, Boolean>> authorizedObjectIDToEntities;
	private boolean loaded = false;
	private volatile InheritedEntityUserSetResolver<Entity> inheritedEntityUserSetResolver;
//	private Inheritable inheritable;
	
	public EntityUserSetPageControllerHelper() {
		super();
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public EntityUserSet<Entity> getEntityUserSet() {
		checkLoaded();
		return entityUserSet;
	}
	
	public EntityUserSetID getEntityUserSetID() {
		checkLoaded();
		return entityUserSetID;
	}

	public Map<AuthorizedObject, Boolean> getAuthorizedObjects() {
		checkLoaded();
		return authorizedObjects;
	}

	public Map<Entity, Boolean> getEntities(AuthorizedObject authorizedObject) {
		checkLoaded();
		return authorizedObjectIDToEntities.get(JDOHelper.getObjectId(authorizedObject));
	}
	
	public void addAuthorizedObject(AuthorizedObject authorizedObject) {
		checkLoaded();
		authorizedObjects.put(authorizedObject, Boolean.TRUE);
		authorizedObjectIDToEntities.put((AuthorizedObjectID) JDOHelper.getObjectId(authorizedObject), new HashMap<Entity, Boolean>());
		propertyChangeSupport.firePropertyChange(PROPERTY_NAME_USER_ADDED, null, authorizedObject);
	}
	
	public void removeAuthorizedObject(AuthorizedObject authorizedObject) {
		checkLoaded();
		authorizedObjects.put(authorizedObject, Boolean.FALSE);
		authorizedObjectIDToEntities.remove((AuthorizedObjectID) JDOHelper.getObjectId(authorizedObject));
		propertyChangeSupport.firePropertyChange(PROPERTY_NAME_USER_REMOVED, null, authorizedObject);
	}

	public void addEntityToAuthorizedObject(Entity entity, AuthorizedObject authorizedObject) {
		checkLoaded();
		Map<Entity, Boolean> entityToAssignment = authorizedObjectIDToEntities.get(JDOHelper.getObjectId(authorizedObject));
		if (entityToAssignment != null) {
			entityToAssignment.put(entity, Boolean.TRUE);
			propertyChangeSupport.firePropertyChange(PROPERTY_NAME_ENTITY_ADDED_TO_AUTHORIZED_OBJECT, null, entity);
		}
	}

	public void removeEntityFromAuthorizedObject(Entity entity, AuthorizedObject authorizedObject) {
		checkLoaded();
		Map<Entity, Boolean> entityToAssignment = authorizedObjectIDToEntities.get(JDOHelper.getObjectId(authorizedObject));
		if (entityToAssignment != null) {
			entityToAssignment.put(entity, Boolean.FALSE);
			propertyChangeSupport.firePropertyChange(PROPERTY_NAME_ENTITY_REMOVED_TO_AUTHORIZED_OBJECT, null, entity);
		}
	}
	
	protected void checkLoaded() {
		if (!loaded) {
			throw new IllegalStateException("The method load(EntityUserSetID, EntityUserSet<Entity>, ProgressMonitor) has not been called yet, first call this method before calling other methods.");
		}
	}
	
	/**
	 * Get the current {@link InheritedEntityUserSetResolver} for this helper.
	 * Note, that this method might return <code>null</code>, check {@link #isManageInheritance()}
	 * to see if this helper manages inheritance.
	 * @return The current instance of {@link InheritedEntityUserSetResolver} or <code>null</code> if {@link #isManageInheritance()} is <code>false</code>.
	 */	
	public InheritedEntityUserSetResolver<Entity> getInheritedEntityUserSetResolver() 
	{
		if (inheritedEntityUserSetResolver == null) {
			synchronized (this) {
				inheritedEntityUserSetResolver = createInheritedEntityUserSetResolver();
			}
		}
		return inheritedEntityUserSetResolver;
	}
	
	/**
	 * Return the fetch groups which should be used for loading the entities.
	 * This fetch group should be used inside the implementation of {@link #loadEntities(ProgressMonitor)}.
	 *  
	 * @return the fetch groups which should be used for loading the entities
	 */
	protected abstract String[] getEntityFetchGroups();

	/**
	 * Loads a {@link Collection} containing all necessary instances of Entity which should be selectable.
	 * 
	 * @param monitor the {@link ProgressMonitor} to display the progress of loading.
	 * @return a {@link Collection} containing all necessary instances of Entity which should be selectable.
	 */
	protected abstract Collection<Entity> loadEntities(ProgressMonitor monitor);
	
	/**
	 * Creates an implementation of {@link InheritedEntityUserSetResolver} for thsi controller.
	 * @return the implementation of {@link InheritedEntityUserSetResolver} for this controller.
	 */
	protected abstract InheritedEntityUserSetResolver<Entity> createInheritedEntityUserSetResolver();
	
	/**
	 * Creates a new instance of an {@link EntityUserSet} for the entity type..
	 * @return a new {@link EntityUserSet} for the entity type.  
	 */
	protected abstract EntityUserSet<Entity> createEntityUserSet();
	
	/**
	 * Returns the {@link Class} of the entity.
	 * @return the class of the entity.
	 */
	protected abstract Class<Entity> getEntityClass();
	
	/**
	 * Loads the {@link EntityUserSet} and the fills up the corresponding data. 
	 * 
	 * @param entityUserSetID the {@link EntityUserSetID} to load the {@link EntityUserSet} for and populate the controller
	 * with the necessary data. (may be null, if newEntityUserSet is not null) 
	 * @param newEntityUserSet the newly created EntityUserSet (may be null, if entityUserSetID is not null).
	 * @param monitor the {@link ProgressMonitor} to display the progress of loading.
	 */
	public void load(EntityUserSetID entityUserSetID, EntityUserSet<Entity> newEntityUserSet, ProgressMonitor monitor) 
	{
		monitor.beginTask("Loading EntityUserSet", 100);
		try {
			authorizedObjects = new HashMap<AuthorizedObject, Boolean>();
			authorizedObjectIDToEntities = new HashMap<AuthorizedObjectID, Map<Entity,Boolean>>();

			if (entityUserSetID != null) {
				this.entityUserSetID = entityUserSetID;
				entityUserSet = EntityUserSetDAO.sharedInstance().getEntityUserSet(entityUserSetID, getEntityUserSetFetchGroups(), 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 30));			
			} 
			else if (newEntityUserSet != null) {
				entityUserSet = newEntityUserSet;
				this.entityUserSetID = EntityUserSetID.create(entityUserSet.getOrganisationID(), 
						entityUserSet.getEntityClassName(), entityUserSet.getEntityUserSetID());
			}
			
			if (entityUserSet != null) 
			{
				Collection<AuthorizedObject> authorizedObjects = AuthorizedObjectDAO.sharedInstance().getAuthorizedObjects(
						FETCH_GROUPS_AUTHORIZED_OBJECT, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 35));
				Collection<Entity> entities = loadEntities(new SubProgressMonitor(monitor, 35));				
				for (AuthorizedObject authorizedObject : authorizedObjects) {
					AuthorizedObjectID authorizedObjectID = (AuthorizedObjectID) JDOHelper.getObjectId(authorizedObject);
					AuthorizedObjectRef<Entity> authorizedObjectRef = entityUserSet.getAuthorizedObjectRef(authorizedObjectID);
					this.authorizedObjects.put(authorizedObject, authorizedObjectRef != null ? Boolean.TRUE : Boolean.FALSE);
					Map<Entity, Boolean> entityToAssigned = authorizedObjectIDToEntities.get(JDOHelper.getObjectId(authorizedObject));
					if (entityToAssigned == null) {
						entityToAssigned = new HashMap<Entity, Boolean>();
						authorizedObjectIDToEntities.put((AuthorizedObjectID) JDOHelper.getObjectId(authorizedObject), entityToAssigned);
					}
					if (authorizedObjectRef != null) {
						// collect all entities which are assigned to the AuthorisedObject
						Collection<Entity> authorisedObjectEntities = new HashSet<Entity>();
						for (EntityRef<Entity> entityRef : authorizedObjectRef.getEntityRefs()) {
							authorisedObjectEntities.add(entityRef.getEntity());
						}
						// iterate through all Entities and fill map which are assigned an which not
						for (Entity entity : entities) {
							entityToAssigned.put(entity, authorisedObjectEntities.contains(entity));
						}
					}
					else {
						// iterate through all Entities and fill map which are assigned an which not
						for (Entity entity : entities) {
							entityToAssigned.put(entity, Boolean.FALSE);
						}
					}
				}
			}
			inheritedEntityUserSetResolver = null;				
			propertyChangeSupport.firePropertyChange(PROPERTY_NAME_ENTITY_USER_SET_LOADED, null, entityUserSet);				
			loaded = true;
		} finally {
			monitor.done();
		}
	}

	protected String[] getEntityUserSetFetchGroups()
	{
		if (entityUserSetFetchGroups == null) {
			Set<String> fgs = CollectionUtil.array2HashSet(FETCH_GROUPS_ENTITY_USER_SET);
			fgs.addAll(CollectionUtil.array2HashSet(getEntityFetchGroups()));
			entityUserSetFetchGroups = CollectionUtil.collection2TypedArray(fgs, String.class);
		}
		return entityUserSetFetchGroups;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}
	
	public void store(ProgressMonitor monitor) 
	{
		checkLoaded();
		if (entityUserSet == null) {
			throw new IllegalStateException("entityUserSet is null, this should never happen!");
		}
		
		monitor.beginTask("Saving EntityUserSet", 100);
		try {
			Collection<AuthorizedObjectID> oldEntityUserSetAuthorizedObjectIDs = new HashSet<AuthorizedObjectID>(
					entityUserSet.getAuthorizedObjectRefs().size());
			for (AuthorizedObjectRef<Entity> authorizedObjectRef : entityUserSet.getAuthorizedObjectRefs()) {
				oldEntityUserSetAuthorizedObjectIDs.add(authorizedObjectRef.getAuthorizedObjectIDAsOID());
			}

			if (entityUserSet.getEntityUserSetController() == null) {
				entityUserSet.setEntityUserSetController(new EntityUserSetControllerClientImpl());
			}
			
			// add or remove edited authorizedObjects 
			for (Map.Entry<AuthorizedObject, Boolean> entry : authorizedObjects.entrySet()) {
				AuthorizedObject authorizedObject = entry.getKey();
				Boolean assigned = entry.getValue();
				AuthorizedObjectID authorizedObjectID = (AuthorizedObjectID) JDOHelper.getObjectId(authorizedObject);
				if (assigned) {
					// if the authorizedObject is not yet included do it.  
					if (!oldEntityUserSetAuthorizedObjectIDs.contains(authorizedObjectID)) {
						entityUserSet.addAuthorizedObject(authorizedObjectID); 
					}
				}
				else {
					// if the authorizedObject is still contained, remove it
					if (oldEntityUserSetAuthorizedObjectIDs.contains(authorizedObjectID)) {
						entityUserSet.removeAuthorizedObject(authorizedObjectID);
					}
				}
			}
			monitor.worked(10);
			
			// add or remove edited entities per authorizedObject 
			for (AuthorizedObjectRef<Entity> authorizedObjectRef : entityUserSet.getAuthorizedObjectRefs()) {
				AuthorizedObjectID authorizedObjectID = authorizedObjectRef.getAuthorizedObjectIDAsOID();
				Map<Entity, Boolean> entityToAssignment = authorizedObjectIDToEntities.get(authorizedObjectID);
				for (Map.Entry<Entity, Boolean> entry : entityToAssignment.entrySet()) {
					Entity entity = entry.getKey();
					Boolean value = entry.getValue();
					boolean entityFound = false;
					for (Iterator<EntityRef<Entity>> it = authorizedObjectRef.getEntityRefs().iterator(); it.hasNext(); ) {
						EntityRef<Entity> entityRef = it.next();
						Entity e = entityRef.getEntity();
						if (e.equals(entity)) {
							if (!value) {
								authorizedObjectRef.removeEntity(entity);
							}
							entityFound = true;
							break;
						}
					}
					if (!entityFound && value) {
						authorizedObjectRef.addEntity(entity);
					}
				}
			}
			monitor.worked(10);

			entityUserSet = EntityUserSetDAO.sharedInstance().storeEntityUserSet(entityUserSet, 
					true, getEntityUserSetFetchGroups(), NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 80));
		} finally {
			monitor.done();
		}
	}

	public boolean isEntityUserSetInitiallyInherited() {
		return getInheritedEntityUserSetResolver().isEntityUserSetInherited();			
	}

}
