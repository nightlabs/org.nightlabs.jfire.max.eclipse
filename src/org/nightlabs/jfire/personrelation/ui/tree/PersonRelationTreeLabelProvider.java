package org.nightlabs.jfire.personrelation.ui.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.labelprovider.ColumnSpanLabelProvider;
import org.nightlabs.jdo.ObjectID;

/**
 * This should give us more control when we need to access the Graphics Control in the super class's {@link ColumnSpanLabelProvider}.
 * Notice also that this particular Label Provider handles any further registered delegates.
 * 
 * Externalised from the original {@link PersonRelationTree}.
 * 
 * See example with org.nightlabs.jfire.personrelation.trade.ui.tucked.TuckedPersonRelationTreeLabelProvider.
 * 
 * @author Marco Schulze
 * @author khaireel
 */
public class PersonRelationTreeLabelProvider<N extends PersonRelationTreeNode> extends ColumnSpanLabelProvider {
	private final Logger logger = Logger.getLogger(PersonRelationTreeLabelProvider.class);

	private Map<Class<?>, IPersonRelationTreeLabelProviderDelegate> jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate = new HashMap<Class<?>, IPersonRelationTreeLabelProviderDelegate>();
	private Map<Class<?>, IPersonRelationTreeLabelProviderDelegate> jdoObjectClass2PersonRelationTreeLabelProviderDelegate = new HashMap<Class<?>, IPersonRelationTreeLabelProviderDelegate>();
	private Map<Class<?>, IPersonRelationTreeLabelProviderDelegate> jdoObjectClass2DelegateCache = new HashMap<Class<?>, IPersonRelationTreeLabelProviderDelegate>();

	
	public PersonRelationTreeLabelProvider(ColumnViewer columnViewer, boolean addDefaultDelegates) {
		super(columnViewer);
		if (addDefaultDelegates) {
			addPersonRelationTreeLabelProviderDelegate(new DefaultPersonRelationTreeLabelProviderDelegatePerson());
			addPersonRelationTreeLabelProviderDelegate(new DefaultPersonRelationTreeLabelProviderDelegatePersonRelation());
		}
	}


	public void addPersonRelationTreeLabelProviderDelegate(IPersonRelationTreeLabelProviderDelegate delegate)
	{
		Class<? extends ObjectID> objectIDClass = delegate.getJDOObjectIDClass();
		Class<?> objectClass = delegate.getJDOObjectClass();
		jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.put(objectIDClass, delegate);
		jdoObjectClass2PersonRelationTreeLabelProviderDelegate.put(objectClass, delegate);
		jdoObjectClass2DelegateCache.clear();
		
		if (logger.isTraceEnabled())
			logger.trace("addPersonRelationTreeLabelProviderDelegate: added " + delegate + " for objectClass " + (objectClass == null ? null : objectClass.getName()) + " and objectIDClass " + (objectIDClass == null ? null : objectIDClass.getName())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	protected Set<IPersonRelationTreeLabelProviderDelegate> getAllPersonRelationTreeLabelProviderDelegates()
	{
		Set<IPersonRelationTreeLabelProviderDelegate> result = new HashSet<IPersonRelationTreeLabelProviderDelegate>();
		result.addAll(jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.values());
		result.addAll(jdoObjectClass2PersonRelationTreeLabelProviderDelegate.values());
		return result;
	}
	
	
	protected String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
		if (jdoObject == null) {
			IPersonRelationTreeLabelProviderDelegate delegate = getDelegate(jdoObjectID);
			if (delegate != null) {
				String result = delegate.getJDOObjectText(jdoObjectID, jdoObject, spanColIndex);
				if (result != null)
					return result;
			}
			else {
				switch (spanColIndex) {
					case 0:
						return String.valueOf(jdoObjectID);
					default:
						break;
				}
			}
		}
		else {
			// We check for the delegate first in order to allow overriding the defaults following below. Marco.
			IPersonRelationTreeLabelProviderDelegate delegate = getDelegate(jdoObject);
			if (delegate != null) {
				String result = delegate.getJDOObjectText(jdoObjectID, jdoObject, spanColIndex);
				if (result != null)
					return result;
			}

			else {
				switch (spanColIndex) {
					case 0:
						return String.valueOf(jdoObject);
					default:
						break;
				}
			}
		}

		return null;
	}


	@SuppressWarnings("unchecked")
	@Override
	protected int[][] getColumnSpan(Object element) {
		if (!(element instanceof PersonRelationTreeNode))
			return null; // null means each real column assigned to one visible column
//			return new int[][] { {0}, {1}, {2}, {3}, {4}, {5} };

		N node = (N) element;
		ObjectID jdoObjectID = node.getJdoObjectID();
		Object jdoObject = node.getJdoObject();
		if (jdoObject == null) {
			IPersonRelationTreeLabelProviderDelegate delegate = getDelegate(jdoObjectID);
			if (delegate != null) {
				int[][] result = delegate.getJDOObjectColumnSpan(jdoObjectID, jdoObject);
				if (result != null)
					return result;
			}

			return new int[][] { {0, 1} };
		}
		else {
			IPersonRelationTreeLabelProviderDelegate delegate = getDelegate(jdoObject);
			if (delegate != null) {
				int[][] result = delegate.getJDOObjectColumnSpan(jdoObjectID, jdoObject);
				if (result != null)
					return result;
			}

			return null; // null means each real column assigned to one visible column
		}
	}

	/**
	 * Gets the delegate for the class of the given ID-object without taking its
	 * type-hierarchy into account as ObjectIDs should not have a type hierarchy
	 * 
	 * @param jdoObject The object to find the delegate for.
	 * @return The delegate for the object, or <code>null</code> if none could
	 *         be found.
	 */
	private IPersonRelationTreeLabelProviderDelegate getDelegate(ObjectID jdoObjectID) {
		return jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.get(jdoObjectID.getClass());
	}

	/**
	 * Gets the delegate for the class of the given objec with respect to its
	 * type-hierarchy, so a delegate registered for a super-class will be found
	 * to.
	 * 
	 * @param jdoObject The object to find the delegate for.
	 * @return The delegate for the object, or <code>null</code> if none could
	 *         be found.
	 */
	private IPersonRelationTreeLabelProviderDelegate getDelegate(Object jdoObject) {
		Class<? extends Object> clazz = jdoObject.getClass();
		if (!jdoObjectClass2DelegateCache.containsKey(clazz)) {
			IPersonRelationTreeLabelProviderDelegate delegate = jdoObjectClass2PersonRelationTreeLabelProviderDelegate.get(clazz);
			jdoObjectClass2DelegateCache.put(clazz, delegate);
			if (delegate == null) {
				// no direct delegate found for the class, search type hierarchy
				for (Class<?> delegateClazz : jdoObjectClass2PersonRelationTreeLabelProviderDelegate.keySet()) {
					if (delegateClazz.isAssignableFrom(clazz)) {
						jdoObjectClass2DelegateCache.put(clazz, jdoObjectClass2PersonRelationTreeLabelProviderDelegate.get(delegateClazz));
					}
				}
			}
		}
		return jdoObjectClass2DelegateCache.get(clazz);
	}	

	@SuppressWarnings("unchecked")
	@Override
	public String getColumnText(Object element, int spanColIndex) {
		if (element == null) {
			if (logger.isDebugEnabled())
				logger.debug("getColumnText: element is null => returning null."); //$NON-NLS-1$

			return null;
		}

		if (!(element instanceof PersonRelationTreeNode)) {
			if (logger.isDebugEnabled())
				logger.debug("getColumnText: element is not a PersonRelationTreeNode (but a " + element.getClass().getName() + ") => returning element.toString()."); //$NON-NLS-1$ //$NON-NLS-2$

			return String.valueOf(element);
		}

		N node = (N) element;
		ObjectID jdoObjectID = node.getJdoObjectID();
		Object jdoObject = node.getJdoObject();

		String result = getJDOObjectText(jdoObjectID, jdoObject, spanColIndex);
		if (logger.isDebugEnabled())
			logger.debug("getColumnText: oid=" + jdoObjectID + " o=" +jdoObject+ " => returning: " + result); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Image getColumnImage(Object element, int spanColIndex) {
		if (element == null)
			return null;

		if (!(element instanceof PersonRelationTreeNode))
			return null;

		N node = (N) element;
		ObjectID jdoObjectID = node.getJdoObjectID();
		Object jdoObject = node.getJdoObject();
		return getJDOObjectImage(jdoObjectID, jdoObject, spanColIndex);
	}

	protected Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex)
	{
		if (jdoObject == null) {
			IPersonRelationTreeLabelProviderDelegate delegate = getDelegate(jdoObjectID);
			if (delegate != null) {
				Image result = delegate.getJDOObjectImage(jdoObjectID, jdoObject, spanColIndex);
				if (result != null)
					return result;
			}
		}
		else {
			IPersonRelationTreeLabelProviderDelegate delegate = getDelegate(jdoObject);
			if (delegate != null) {
				Image result = delegate.getJDOObjectImage(jdoObjectID, jdoObject, spanColIndex);
				if (result != null)
					return result;
			}
		}

		return null;
	}


	@Override
	public void update(ViewerCell cell) {
		// TODO Auto-generated method stub
		
	}

}
