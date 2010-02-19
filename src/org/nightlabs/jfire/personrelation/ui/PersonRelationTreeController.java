package org.nightlabs.jfire.personrelation.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.ActiveJDOObjectLazyTreeController;
import org.nightlabs.jfire.jdo.notification.TreeNodeMultiParentResolver;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.PersonRelationParentResolver;
import org.nightlabs.jfire.personrelation.PersonRelationType;
import org.nightlabs.jfire.personrelation.dao.PersonRelationDAO;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.ui.resource.Messages;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class PersonRelationTreeController
extends ActiveJDOObjectLazyTreeController<ObjectID, Object, PersonRelationTreeNode>
{
	private static final Logger logger = Logger.getLogger(PersonRelationTreeController.class);

	public static final String[] FETCH_GROUPS_PERSON = {
		FetchPlan.DEFAULT, // we only need the display-name, hence this should be sufficient
	};

	public static final String[] FETCH_GROUPS_PERSON_RELATION = {
		FetchPlan.DEFAULT,
		PersonRelation.FETCH_GROUP_PERSON_RELATION_TYPE,
		PersonRelationType.FETCH_GROUP_NAME,
		PersonRelation.FETCH_GROUP_FROM_ID,
		PersonRelation.FETCH_GROUP_TO,
		PersonRelation.FETCH_GROUP_FROM_PERSON_RELATION_IDS,
		Person.FETCH_GROUP_DATA_FIELDS,	// FIXME Make this into one of the delegates.
	};

	private volatile Collection<PropertySetID> rootPersonIDs;

	private List<PersonRelationTreeControllerDelegate> personRelationTreeControllerDelegates = new ArrayList<PersonRelationTreeControllerDelegate>();
	private List<PersonRelationTreeControllerDelegate> _personRelationTreeControllerDelegates = null;

	public List<PersonRelationTreeControllerDelegate> getPersonRelationTreeControllerDelegates() {
		List<PersonRelationTreeControllerDelegate> delegates = _personRelationTreeControllerDelegates;
		if (delegates == null) {
			synchronized (personRelationTreeControllerDelegates) {
				delegates = _personRelationTreeControllerDelegates;
				if (delegates == null) {
					delegates = new ArrayList<PersonRelationTreeControllerDelegate>(personRelationTreeControllerDelegates);
					delegates = Collections.unmodifiableList(delegates);
					_personRelationTreeControllerDelegates = delegates;
				}
			}
		}
		return delegates;
	}

	public void addPersonRelationTreeControllerDelegate(PersonRelationTreeControllerDelegate delegate)
	{
		synchronized (personRelationTreeControllerDelegates) {
			unregisterChangeListener();
			personRelationTreeControllerDelegates.add(delegate);

			PersonRelationParentResolver personRelationParentResolver = (PersonRelationParentResolver) getTreeNodeMultiParentResolver();
			TreeNodeMultiParentResolver parentResolverDelegate = delegate.getPersonRelationParentResolverDelegate();
			if (parentResolverDelegate != null)
				personRelationParentResolver.addDelegate(parentResolverDelegate);

			_personRelationTreeControllerDelegates = null;
			jdoObjectClasses = null;
			registerChangeListener();
			registerJDOLifecycleListener();
		}
		clear();
	}

//	public void removePersonRelationTreeControllerDelegate(PersonRelationTreeControllerDelegate delegate)
//	{
//		synchronized (personRelationTreeControllerDelegates) {
//			unregisterChangeListener();
//			personRelationTreeControllerDelegates.remove(delegate);
//			_personRelationTreeControllerDelegates = null;
//			jdoObjectClasses = null;
//			registerChangeListener();
//			registerJDOLifecycleListener();
//
// Here, we would have to remove the TreeNodeMultiParentResolver-delegates. But that's too much work and this method is probably not necessary at all.
//
//		}
//		clear();
//	}

	public Collection<PropertySetID> getRootPersonIDs() {
		return rootPersonIDs;
	}
	public void setRootPersonIDs(Collection<PropertySetID> rootPersonIDs) {
		this.rootPersonIDs = rootPersonIDs;
		clear();
	}

	@Override
	protected PersonRelationTreeNode createNode() {
		return new PersonRelationTreeNode();
	}

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver() {
		throw new UnsupportedOperationException("This method should not be called because we're using a TreeNode*Multi*ParentResolver!"); //$NON-NLS-1$
	}

	@Override
	protected TreeNodeMultiParentResolver createTreeNodeMultiParentResolver() {
		return new PersonRelationParentResolver();
	}

//	@Override
//	protected JDOLifecycleListener createJDOLifecycleListener(Set<? extends ObjectID> parentObjectIDs) {
//		return super.createJDOLifecycleListener(parentObjectIDs);
//	}
//
//	@Override
//	protected IJDOLifecycleListenerFilter createJDOLifecycleListenerFilter(Set<? extends ObjectID> parentObjectIDs) {
//		return new TreeLifecycleListenerFilter(
//				new Class[] { PersonRelation.class }, true,
//				parentObjectIDs, getTreeNodeMultiParentResolver(),
//				new JDOLifecycleState[] { JDOLifecycleState.NEW }
//		);
//	}

	@Override
	protected Class<?> getJDOObjectClass() {
		throw new UnsupportedOperationException("This method should not be called because we have overridden 'getJDOObjectClasses()' below."); //$NON-NLS-1$
//		return PersonRelation.class;
	}

	private Set<Class<? extends Object>> jdoObjectClasses = null;

	@Override
	protected Set<Class<? extends Object>> getJDOObjectClasses() {
		Set<Class<? extends Object>> classes = jdoObjectClasses;
		if (classes == null) {
			synchronized (personRelationTreeControllerDelegates) {
				classes = jdoObjectClasses;
				if (classes == null) {
					List<PersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
					classes = new HashSet<Class<? extends Object>>();
					classes.add(PersonRelation.class);
					for (PersonRelationTreeControllerDelegate delegate : delegates) {
						classes.addAll(delegate.getJDOObjectClasses());
					}
					jdoObjectClasses = classes;
				}
			}
		}
		return classes;
	}

	@Override
	protected Collection<Object> retrieveJDOObjects(Set<ObjectID> objectIDs, ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTreeController.task.retrievingObjects.name"), 110); //$NON-NLS-1$
		try {
			Set<PropertySetID> personIDs = null;
			Set<PersonRelationID> personRelationIDs = null;

			Set<ObjectID> objectIDsLeft = new HashSet<ObjectID>(objectIDs);

			for (ObjectID objectID : objectIDs) {
				if (objectID instanceof PropertySetID) {
					if (personIDs == null)
						personIDs = new HashSet<PropertySetID>();

					personIDs.add((PropertySetID) objectID);
					objectIDsLeft.remove(objectID);
				}
				else if (objectID instanceof PersonRelationID) {
					if (personRelationIDs == null)
						personRelationIDs = new HashSet<PersonRelationID>();

					personRelationIDs.add((PersonRelationID) objectID);
					objectIDsLeft.remove(objectID);
				}
			}

			Collection<Object> result = new ArrayList<Object>(objectIDs.size());

			int tixPerson = personIDs != null && !personIDs.isEmpty() ? 100 : 0;
			int tixRelation = personRelationIDs != null && !personRelationIDs.isEmpty() ? 100 : 0;

			tixPerson = 100 * 100 / Math.max(1, tixPerson + tixRelation);
			tixRelation = 100 * 100 / Math.max(1, tixPerson + tixRelation);

			monitor.worked(10);

			if (personIDs != null && !personIDs.isEmpty()) {
				result.addAll(
						PropertySetDAO.sharedInstance().getPropertySets(
								personIDs,
								FETCH_GROUPS_PERSON, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, tixPerson)
						)
				);
			}
			else
				monitor.worked(tixPerson);

			if (personRelationIDs != null && !personRelationIDs.isEmpty()) {
				result.addAll(
						PersonRelationDAO.sharedInstance().getPersonRelations(
								personRelationIDs,
								FETCH_GROUPS_PERSON_RELATION, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, tixRelation)
						)
				);
			}
			else
				monitor.worked(tixRelation);


			List<PersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
			for (PersonRelationTreeControllerDelegate delegate : delegates) {
				if (objectIDsLeft.isEmpty())
					break;

				Collection<? extends Object> objects = delegate.retrieveJDOObjects(Collections.unmodifiableSet(objectIDsLeft), monitor);
				if (objects != null) {
					for (Object object : objects) {
						Object objectID = JDOHelper.getObjectId(object);
						if (objectID == null)
							throw new IllegalStateException("JDOHelper.getObjectId(object) returned null! delegate=" + delegate + " object=" + object); //$NON-NLS-1$ //$NON-NLS-2$

						objectIDsLeft.remove(objectID);
						result.add(object);
					}
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("retrieveJDOObjects: objectIDs.size=" + objectIDs.size() + " result.size=" + result.size()); //$NON-NLS-1$ //$NON-NLS-2$
				if (logger.isTraceEnabled()) {
					for (Object object : result) {
						logger.trace("retrieveJDOObjects:   * " + object); //$NON-NLS-1$
					}
				}
			}

			return result;
		} finally {
			monitor.done();
		}
	}


	@Override
	protected Map<ObjectID, Long> retrieveChildCount(Set<ObjectID> parentIDs, ProgressMonitor monitor) {
		return null;
	}

	@Override
	protected Map<ObjectID, Long> retrieveChildCount(Set<PersonRelationTreeNode> parentNodes, Set<ObjectID> parentIDs, ProgressMonitor monitor) {
		Collection<PropertySetID> rootPersonIDs = this.rootPersonIDs;
		if (rootPersonIDs == null)
			return Collections.emptyMap();

		monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTreeController.task.retrievingChildCounts.name"), 110); //$NON-NLS-1$
		try {
			Set<PropertySetID> personIDs = null;
			Set<PersonRelationID> personRelationIDs = null;

			Map<ObjectID, Long> result = new HashMap<ObjectID, Long>(parentIDs.size());

			for (ObjectID objectID : parentIDs) {
				if (objectID == null) {
					result.put(objectID, new Long(rootPersonIDs.size()));
				}
				else if (objectID instanceof PropertySetID) {
					if (personIDs == null)
						personIDs = new HashSet<PropertySetID>();

					personIDs.add((PropertySetID) objectID);
				}
				else if (objectID instanceof PersonRelationID) {
					if (personRelationIDs == null)
						personRelationIDs = new HashSet<PersonRelationID>();

					personRelationIDs.add((PersonRelationID) objectID);
				}
			}

			int tixPerson = personIDs != null && !personIDs.isEmpty() ? 100 : 1;
			int tixRelation = personRelationIDs != null && !personRelationIDs.isEmpty() ? 100 : 1;

			tixPerson = 100 * 100 / Math.max(1, tixPerson + tixRelation);
			tixRelation = 100 * 100 / Math.max(1, tixPerson + tixRelation);

			monitor.worked(10);

			if (personIDs != null && !personIDs.isEmpty()) {
				ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tixPerson);
				subMonitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTreeController.task.retrievingChildCounts.name"), personIDs.size()); //$NON-NLS-1$
				for (PropertySetID personID : personIDs) {
					long personRelationCount = PersonRelationDAO.sharedInstance().getPersonRelationCount(
							null, personID, null, new NullProgressMonitor()
					);
					result.put(personID, personRelationCount);
					subMonitor.worked(1);
				}
				subMonitor.done();
			}
			else
				monitor.worked(1);

			if (personRelationIDs != null && !personRelationIDs.isEmpty()) {
				List<PersonRelation> personRelations = PersonRelationDAO.sharedInstance().getPersonRelations(
						personRelationIDs,
						new String[] {
								FetchPlan.DEFAULT,
								PersonRelation.FETCH_GROUP_TO_ID,
						},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, tixRelation / 2)
				);

				ProgressMonitor subMonitor = new SubProgressMonitor(monitor, tixRelation / 2);
				subMonitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTreeController.task.retrievingChildCounts.name"), personRelations.size()); //$NON-NLS-1$

				// In order to count with the filter, where we ensure that none of the children has been repeated,
				// we need to retrieve a bit more for our comparisons. Needs a bit more fine-tuning.
				for (PersonRelation personRelation : personRelations) {
					PersonRelationID personRelationID = (PersonRelationID) JDOHelper.getObjectId(personRelation);
					long personRelationCount = 0;

					// Revised version. We now check our reference to the Set of parentNodes, to make sure we have the correct Node.
					PersonRelationTreeNode node = null;
					List<PersonRelationTreeNode> treeNodes = getTreeNodeList(personRelationID);
					if (treeNodes == null) {
						// Guard. Revert to the original methods.
						// But this SHOULD NEVER HAPPEN.
						personRelationCount = PersonRelationDAO.sharedInstance().getPersonRelationCount(
								null, personRelation.getToID(), null, new NullProgressMonitor()
						);
						result.put(personRelationID, personRelationCount);
						subMonitor.worked(1);
					}
					else {
						if (treeNodes.size() == 1)
							node = treeNodes.get(0);
						else {
							for (PersonRelationTreeNode treeNode : treeNodes)
								if (parentNodes.contains(treeNode)) {
									node = treeNode;
									break;
								}
						}

						List<PropertySetID> propertySetIDsToRoot = node.getPropertySetIDsToRoot();
						Collection<PersonRelationID> childPersonRelationIDs = PersonRelationDAO.sharedInstance().getPersonRelationIDs(
								null, personRelation.getToID(), null,
								new SubProgressMonitor(monitor, 80)
						);

						// -------------------------------------------------------------------------------------------------- ++ ------>>
						List<PersonRelation> cRelns = PersonRelationDAO.sharedInstance().getPersonRelations(
								childPersonRelationIDs, new String[] { FetchPlan.DEFAULT, PersonRelation.FETCH_GROUP_TO_ID },
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 20)
						);

						Iterator<PersonRelation> cReln_iter = cRelns.iterator();
						while (cReln_iter.hasNext()) {
							PropertySetID cReln_propID = cReln_iter.next().getToID();
							if (!propertySetIDsToRoot.contains(cReln_propID))
								personRelationCount++;
						}

						result.put(personRelationID, personRelationCount);
						subMonitor.worked(1);
						// -------------------------------------------------------------------------------------------------- ++ ------>>
					}
				}

//				for (PersonRelation personRelation : personRelations) {
//					PersonRelationID personRelationID = (PersonRelationID) JDOHelper.getObjectId(personRelation);
//					long personRelationCount = PersonRelationDAO.sharedInstance().getPersonRelationCount(
//							null, personRelation.getToID(), null, new NullProgressMonitor()
//					);
//					result.put(personRelationID, personRelationCount);
//					subMonitor.worked(1);
//				}
				subMonitor.done();
			}
			else
				monitor.worked(1);

			List<PersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
			for (PersonRelationTreeControllerDelegate delegate : delegates) {
				Map<ObjectID, Long> delegateChildCountMap = delegate.retrieveChildCount(parentIDs, new SubProgressMonitor(monitor, 50));
				for (Map.Entry<ObjectID, Long> me : delegateChildCountMap.entrySet()) {
					ObjectID parentID = me.getKey();
					Long childCount = me.getValue();
					if (childCount == null)
						childCount = 0L;

					Long resultChildCount = result.get(parentID);

					if (resultChildCount == null)
						resultChildCount = childCount;
					else
						resultChildCount += childCount;

					result.put(parentID, resultChildCount);
				}
			}

			return result;
		} finally {
			monitor.done();
		}
	}


	// -------------------------------------------------------------------------------------------------- ++ ------>>
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDs(PersonRelationTreeNode parentNode, ProgressMonitor monitor) {
		// The filter: Don't add child if its ID is already listed in the parentNode's path to the root.
		Collection<PropertySetID> rootPersonIDs = this.rootPersonIDs;
		if (rootPersonIDs == null)
			return Collections.emptyList();

		Collection<ObjectID> result = new ArrayList<ObjectID>();
		ObjectID parentID = parentNode.getJdoObjectID();

		monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTreeController.task.retrievingChildIDs.name"), 100); //$NON-NLS-1$
		try {
			if (parentID == null) {
				result.addAll(rootPersonIDs);
			}
			else if (parentID instanceof PropertySetID) {
				Collection<PersonRelationID> childPersonRelationIDs = PersonRelationDAO.sharedInstance().getPersonRelationIDs(
						null, (PropertySetID)parentID, null,
						new SubProgressMonitor(monitor, 80)
				);

				result.addAll(childPersonRelationIDs);
			}
			else if (parentID instanceof PersonRelationID) {
				List<PropertySetID> propertySetIDsToRoot = parentNode.getPropertySetIDsToRoot(); // This is safer, and faster. And we know we are dealing with the correct node. Kai.

				Collection<PersonRelationID> personRelationIDs = Collections.singleton((PersonRelationID)parentID);
				List<PersonRelation> personRelations = PersonRelationDAO.sharedInstance().getPersonRelations(
						personRelationIDs, new String[] { FetchPlan.DEFAULT, PersonRelation.FETCH_GROUP_TO_ID, PersonRelation.FETCH_GROUP_FROM_ID },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 20)
				);

				if (!personRelations.isEmpty()) {
					PersonRelation personRelation = personRelations.iterator().next();
					Collection<PersonRelationID> childPersonRelationIDs = PersonRelationDAO.sharedInstance().getPersonRelationIDs(
							null, personRelation.getToID(), null,
							new SubProgressMonitor(monitor, 80)
					);

					// -------------------------------------------------------------------------------------------------- ++ ------>>
					List<PersonRelation> cRelns = PersonRelationDAO.sharedInstance().getPersonRelations(
							childPersonRelationIDs, new String[] { FetchPlan.DEFAULT, PersonRelation.FETCH_GROUP_TO_ID },
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 20)
					);

					Iterator<PersonRelationID> cID_iter = childPersonRelationIDs.iterator();
					Iterator<PersonRelation> cReln_iter = cRelns.iterator();
					while (cID_iter.hasNext()) {
						PersonRelationID cID = cID_iter.next();
						PropertySetID cReln_propID = cReln_iter.next().getToID();

						if (!propertySetIDsToRoot.contains(cReln_propID))
							result.add(cID);
					}
					// -------------------------------------------------------------------------------------------------- ++ ------>>
				}
			}


			List<PersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
			for (PersonRelationTreeControllerDelegate delegate : delegates) {
				Collection<? extends ObjectID> childObjectIDs = delegate.retrieveChildObjectIDs(parentID, new SubProgressMonitor(monitor, 20));
				if (childObjectIDs != null)
					result.addAll(childObjectIDs);
			}

			return result;
		} finally {
			monitor.done();
		}
	}


	@Override
	protected Collection<ObjectID> retrieveChildObjectIDs(ObjectID parentID, ProgressMonitor monitor) {
		// The filter: Don't add child if its ID is already listed in the parentNode's path to the root.
		// Taken care directly from the other method: retrieveChildObjectIDs(parentNode, monitor).
		return null;
	}
	// -------------------------------------------------------------------------------------------------- ++ ------>>

}
