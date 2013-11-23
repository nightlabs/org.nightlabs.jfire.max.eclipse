package org.nightlabs.jfire.personrelation.ui.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.ActiveJDOObjectLazyTreeController;
import org.nightlabs.jfire.jdo.notification.TreeNodeMultiParentResolver;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.personrelation.PersonRelationParentResolverProxy;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.ui.resource.Messages;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public abstract class PersonRelationTreeController<N extends PersonRelationTreeNode>
extends ActiveJDOObjectLazyTreeController<ObjectID, Object, N>
{
	private static final Logger logger = Logger.getLogger(PersonRelationTreeController.class);
	
	
	public PersonRelationTreeController(boolean addDefaultDelegate) {
		if (addDefaultDelegate) {
			addPersonRelationTreeControllerDelegate(new DefaultPersonRelationTreeControllerDelegate<N>(this));
		}
	}

	private List<IPersonRelationTreeControllerDelegate> personRelationTreeControllerDelegates = new ArrayList<IPersonRelationTreeControllerDelegate>();
	private List<IPersonRelationTreeControllerDelegate> _personRelationTreeControllerDelegates = null;

	public List<IPersonRelationTreeControllerDelegate> getPersonRelationTreeControllerDelegates() {
		List<IPersonRelationTreeControllerDelegate> delegates = _personRelationTreeControllerDelegates;
		if (delegates == null) {
			synchronized (personRelationTreeControllerDelegates) {
				delegates = _personRelationTreeControllerDelegates;
				if (delegates == null) {
					delegates = new ArrayList<IPersonRelationTreeControllerDelegate>(personRelationTreeControllerDelegates);
					delegates = Collections.unmodifiableList(delegates);
					_personRelationTreeControllerDelegates = delegates;
				}
			}
		}
		return delegates;
	}

	public void addPersonRelationTreeControllerDelegate(IPersonRelationTreeControllerDelegate delegate)
	{
		synchronized (personRelationTreeControllerDelegates) {
			unregisterChangeListener();
			personRelationTreeControllerDelegates.add(delegate);

			PersonRelationParentResolverProxy personRelationParentResolver = (PersonRelationParentResolverProxy) getTreeNodeMultiParentResolver();
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

	/**
	 * Returns the delegate of the given class, if present, <code>null</code> otherwise.
	 * 
	 * @param <T> 
	 * @param clazz
	 * @return
	 */
	public <T extends IPersonRelationTreeControllerDelegate> T getPersonRelationTreeControllerDelegate(Class<T> clazz)
	{
		List<IPersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
		for (IPersonRelationTreeControllerDelegate delegate : delegates) {
			if (clazz.isInstance(delegate)) {
				return (T) delegate;
			}
		}
		return null;
	}	

	@Override
	public void clear() {
		super.clear();
	}
	
	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver() {
		throw new UnsupportedOperationException("This method should not be called because we're using a TreeNode*Multi*ParentResolver!"); //$NON-NLS-1$
	}

	@Override
	protected TreeNodeMultiParentResolver createTreeNodeMultiParentResolver() {
		return new PersonRelationParentResolverProxy();
	}


	@Override
	protected Class<?> getJDOObjectClass() {
		throw new UnsupportedOperationException("This method should not be called because we have overridden 'getJDOObjectClasses()' below."); //$NON-NLS-1$
	}

	private Set<Class<? extends Object>> jdoObjectClasses = null;

	@Override
	protected Set<Class<? extends Object>> getJDOObjectClasses() {
		Set<Class<? extends Object>> classes = jdoObjectClasses;
		if (classes == null) {
			synchronized (personRelationTreeControllerDelegates) {
				classes = jdoObjectClasses;
				if (classes == null) {
					List<IPersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
					classes = new HashSet<Class<? extends Object>>();
					for (IPersonRelationTreeControllerDelegate delegate : delegates) {
						classes.addAll(delegate.getJDOObjectClasses());
					}
					jdoObjectClasses = classes;
				}
			}
		}
		return classes;
	}

	@Override
	protected Collection<Object> retrieveJDOObjects(Set<ObjectID> objectIDs, ProgressMonitor monitor) {
		List<IPersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTreeController.task.retrievingObjects.name"), 10 * delegates.size()); //$NON-NLS-1$
		try {
			Set<PropertySetID> personIDs = null;
			Set<PersonRelationID> personRelationIDs = null;

			Set<ObjectID> objectIDsLeft = new HashSet<ObjectID>(objectIDs);
			Collection<Object> result = new ArrayList<Object>(objectIDs.size());
			
			for (IPersonRelationTreeControllerDelegate delegate : delegates) {
				if (objectIDsLeft.isEmpty())
					break;

				Collection<? extends Object> objects = delegate.retrieveJDOObjects(Collections.unmodifiableSet(objectIDsLeft), new SubProgressMonitor(monitor, 10));
				if (objects != null) {
					for (Object object : objects) {
						Object objectID = delegate.getJDOObjectID(object);
						if (objectID == null)
							throw new IllegalStateException("delegate.getJDOObjectID(object) returned null! delegate=" + delegate + " object=" + object); //$NON-NLS-1$ //$NON-NLS-2$

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
	
	/**
	 * This is the abstract method from the super class, which we don't use in this {@link PersonRelationTreeController}'s implementation.
	 * Instead, we are in favour of the other method: retrieveChildCount(Set<ObjectID> parentIDs, ProgressMonitor monitor).
	 * See comments in the superclass {@link ActiveJDOObjectLazyTreeController}.
	 */
	@Override
	protected Map<ObjectID, Long> retrieveChildCount(Set<ObjectID> parentIDs, ProgressMonitor monitor) { return null; }

	@Override
	protected Map<ObjectID, Long> retrieveChildCount(Set<N> parentNodes, Set<ObjectID> parentIDs, ProgressMonitor monitor) {
		List<IPersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
		Map<ObjectID, Long> result = new HashMap<ObjectID, Long>(parentIDs.size());
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTreeController.task.retrievingChildCounts.name"), 10 * delegates.size()); //$NON-NLS-1$
		try {
			for (IPersonRelationTreeControllerDelegate delegate : delegates) {
				Map<ObjectID, Long> delegateChildCountMap = delegate.retrieveChildCount(parentNodes, parentIDs, new SubProgressMonitor(monitor, 10));
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
	/**
	 * This is the abstract method from the super class, which we don't use in this {@link PersonRelationTreeController} implementation.
	 * Instead, we are in favour of the other method: retrieveChildObjectIDs(N parentNode, ProgressMonitor monitor).
	 * See comments in the superclass {@link ActiveJDOObjectLazyTreeController}.
	 */
	@Override
	protected Collection<ObjectID> retrieveChildObjectIDs(ObjectID parentID, ProgressMonitor monitor) { return null; }

	@Override
	protected Collection<ObjectID> retrieveChildObjectIDs(N parentNode, ProgressMonitor monitor) {
		Collection<ObjectID> result = new ArrayList<ObjectID>(); // Note: It is from the index position of this Collection that we know the position of the child-nodes.
		                                                         //       And if we are to impose any ordering of the nodes, this should be the place to affect it. Kai.

		List<IPersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTreeController.task.retrievingChildIDs.name"), 10 * delegates.size()); //$NON-NLS-1$
		try {
			for (IPersonRelationTreeControllerDelegate delegate : delegates) {
				Collection<? extends ObjectID> childObjectIDs = delegate.retrieveChildObjectIDs(parentNode, new SubProgressMonitor(monitor, 20));
				if (childObjectIDs != null)
					result.addAll(childObjectIDs);
			}

			return result;
		} finally {
			monitor.done();
		}
	}

	@Override
	protected ObjectID getJDOObjectID(Object jdoObject) {
		ObjectID superResult = super.getJDOObjectID(jdoObject);
		if (superResult != null)
			return superResult;
		List<IPersonRelationTreeControllerDelegate> delegates = getPersonRelationTreeControllerDelegates();
		for (IPersonRelationTreeControllerDelegate delegate : delegates) {
			ObjectID delegateResult = delegate.getJDOObjectID(jdoObject);
			if (delegateResult != null)
				return delegateResult;
		}
		return null;
	}
}
