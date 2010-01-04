package org.nightlabs.jfire.personrelation.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.nightlabs.base.ui.labelprovider.ColumnSpanLabelProvider;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEventHandler;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOObjectLazyTreeContentProvider;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.PersonRelation;
import org.nightlabs.jfire.personrelation.id.PersonRelationID;
import org.nightlabs.jfire.personrelation.ui.resource.Messages;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.util.NLLocale;


public class PersonRelationTree extends AbstractTreeComposite<PersonRelationTreeNode>
{
	private static final Logger logger = Logger.getLogger(PersonRelationTree.class);

	private Collection<PropertySetID> personIDs;

	protected static class PersonRelationTreeContentProvider
	extends JDOObjectLazyTreeContentProvider<ObjectID, Object, PersonRelationTreeNode>
	{
	}

	private Map<Class<?>, PersonRelationTreeLabelProviderDelegate> jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate = new HashMap<Class<?>, PersonRelationTreeLabelProviderDelegate>();
	private Map<Class<?>, PersonRelationTreeLabelProviderDelegate> jdoObjectClass2PersonRelationTreeLabelProviderDelegate = new HashMap<Class<?>, PersonRelationTreeLabelProviderDelegate>();

	public void addPersonRelationTreeLabelProviderDelegate(PersonRelationTreeLabelProviderDelegate delegate)
	{
		Class<? extends ObjectID> objectIDClass = delegate.getJDOObjectIDClass();
		Class<?> objectClass = delegate.getJDOObjectClass();
		jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.put(objectIDClass, delegate);
		jdoObjectClass2PersonRelationTreeLabelProviderDelegate.put(objectClass, delegate);

		if (logger.isTraceEnabled())
			logger.trace("addPersonRelationTreeLabelProviderDelegate: added " + delegate + " for objectClass " + (objectClass == null ? null : objectClass.getName()) + " and objectIDClass " + (objectIDClass == null ? null : objectIDClass.getName())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	protected Set<PersonRelationTreeLabelProviderDelegate> getAllPersonRelationTreeLabelProviderDelegates()
	{
		Set<PersonRelationTreeLabelProviderDelegate> result = new HashSet<PersonRelationTreeLabelProviderDelegate>();
		result.addAll(jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.values());
		result.addAll(jdoObjectClass2PersonRelationTreeLabelProviderDelegate.values());
		return result;
	}

	protected class PersonRelationTreeLabelProvider extends ColumnSpanLabelProvider
	{
		private final Logger logger = Logger.getLogger(PersonRelationTreeLabelProvider.class);

		public PersonRelationTreeLabelProvider(ColumnViewer columnViewer) {
			super(columnViewer);
		}

		private String languageID = NLLocale.getDefault().getLanguage();

		protected String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
			if (jdoObject == null) {
				if (jdoObjectID instanceof PropertySetID) {
					PropertySetID personID = (PropertySetID) jdoObjectID;

					switch (spanColIndex) {
						case 0:
							return personID.organisationID + '/' + personID.propertySetID;
						default:
							break;
					}
				}
				else if (jdoObjectID instanceof PersonRelationID) {
					PersonRelationID personRelationID = (PersonRelationID) jdoObjectID;

					switch (spanColIndex) {
						case 0:
							return personRelationID.organisationID + '/' + personRelationID.personRelationID;
						default:
							break;
					}
				}
				else {
					PersonRelationTreeLabelProviderDelegate delegate = jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.get(jdoObjectID.getClass());
					if (delegate != null)
						return delegate.getJDOObjectText(jdoObjectID, jdoObject, spanColIndex);

					switch (spanColIndex) {
						case 0:
							return String.valueOf(jdoObjectID);
						default:
							break;
					}
				}
			}
			else {
				if (jdoObject instanceof Person) {
					Person person = (Person) jdoObject;

					switch (spanColIndex) {
						case 0:
							return person.getDisplayName();
						default:
							break;
					}
				}
				else if (jdoObject instanceof PersonRelation) {
					PersonRelation personRelation = (PersonRelation) jdoObject;

					switch (spanColIndex) {
						case 0:
							return personRelation.getPersonRelationType().getName().getText(languageID);
						case 1:
							return personRelation.getTo().getDisplayName();
						default:
							break;
					}
				}
				else {
					PersonRelationTreeLabelProviderDelegate delegate = jdoObjectClass2PersonRelationTreeLabelProviderDelegate.get(jdoObject.getClass());
					if (delegate != null)
						return delegate.getJDOObjectText(jdoObjectID, jdoObject, spanColIndex);

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

		@Override
		protected int[][] getColumnSpan(Object element) {
			if (!(element instanceof PersonRelationTreeNode))
				return new int[][] { {0}, {1}, {2}, {3}, {4}, {5} };

			PersonRelationTreeNode node = (PersonRelationTreeNode) element;
			ObjectID jdoObjectID = node.getJdoObjectID();
			Object jdoObject = node.getJdoObject();
			if (jdoObject == null) {
				PersonRelationTreeLabelProviderDelegate delegate = jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.get(jdoObjectID.getClass());
				if (delegate != null) {
					int[][] result = delegate.getJDOObjectColumnSpan(jdoObjectID, jdoObject);
					if (result != null)
						return result;
				}

				return new int[][] { {0, 1} };
			}
			else {
				if (jdoObject instanceof Person)
					return new int[][] { {0, 1} };

				if (jdoObject instanceof PersonRelation)
					return new int[][] { {0}, {1}, {2}, {3}, {4}, {5} };

				PersonRelationTreeLabelProviderDelegate delegate = jdoObjectClass2PersonRelationTreeLabelProviderDelegate.get(jdoObject.getClass());
				if (delegate != null) {
					int[][] result = delegate.getJDOObjectColumnSpan(jdoObjectID, jdoObject);
					if (result != null)
						return result;
					else
						return new int[][] { {0}, {1}, {2}, {3}, {4}, {5} };
				}

				return new int[][] { {0, 1} };
			}
		}

		@Override
		protected String getColumnText(Object element, int spanColIndex) {
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

			PersonRelationTreeNode node = (PersonRelationTreeNode) element;
			ObjectID jdoObjectID = node.getJdoObjectID();
			Object jdoObject = node.getJdoObject();

			String result = getJDOObjectText(jdoObjectID, jdoObject, spanColIndex);
			if (logger.isDebugEnabled())
				logger.debug("getColumnText: oid=" + jdoObjectID + " o=" +jdoObject+ " => returning: " + result); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			return result;
		}

		@Override
		protected Image getColumnImage(Object element, int spanColIndex) {
			if (element == null)
				return null;

			if (!(element instanceof PersonRelationTreeNode))
				return null;

			PersonRelationTreeNode node = (PersonRelationTreeNode) element;
			ObjectID jdoObjectID = node.getJdoObjectID();
			Object jdoObject = node.getJdoObject();
			return getJDOObjectImage(jdoObjectID, jdoObject, spanColIndex);
		}

		protected Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
			if ((jdoObject instanceof PersonRelation) || (jdoObject instanceof Person)) {
				if (spanColIndex == 0)
					return SharedImages.getSharedImage(PersonRelationPlugin.getDefault(), PersonRelationTreeLabelProvider.class, jdoObject.getClass().getSimpleName());
				else
					return null;
			}

			if (jdoObject == null) {
				PersonRelationTreeLabelProviderDelegate delegate = jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.get(jdoObjectID.getClass());
				if (delegate != null)
					return delegate.getJDOObjectImage(jdoObjectID, jdoObject, spanColIndex);
			}
			else {
				PersonRelationTreeLabelProviderDelegate delegate = jdoObjectClass2PersonRelationTreeLabelProviderDelegate.get(jdoObject.getClass());
				if (delegate != null)
					return delegate.getJDOObjectImage(jdoObjectID, jdoObject, spanColIndex);
			}

			return null;
		}

	}

//	protected static class PersonRelationTreeLabelProvider extends JDOObjectLazyTreeLabelProvider<ObjectID, Object, PersonRelationTreeNode>
//	{
//		private String languageID = NLLocale.getDefault().getLanguage();
//
//		@Override
//		protected String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int columnIndex) {
//			if (jdoObject == null) {
//				if (jdoObjectID instanceof PropertySetID) {
//					PropertySetID personID = (PropertySetID) jdoObjectID;
//
//					switch (columnIndex) {
//						case 0:
//							return personID.organisationID + '/' + personID.propertySetID;
//						default:
//							break;
//					}
//				}
//				else if (jdoObjectID instanceof PersonRelationID) {
//					PersonRelationID personRelationID = (PersonRelationID) jdoObjectID;
//
//					switch (columnIndex) {
//						case 0:
//							return personRelationID.organisationID + '/' + personRelationID.personRelationID;
//						default:
//							break;
//					}
//				}
//			}
//			else {
//				if (jdoObject instanceof Person) {
//					Person person = (Person) jdoObject;
//
//					switch (columnIndex) {
//						case 0:
//							return null;
//						case 1:
//							return person.getDisplayName();
//						default:
//							break;
//					}
//				}
//				else if (jdoObject instanceof PersonRelation) {
//					PersonRelation personRelation = (PersonRelation) jdoObject;
//
//					switch (columnIndex) {
//						case 0:
//							return personRelation.getPersonRelationType().getName().getText(languageID);
//						case 1:
//							return personRelation.getTo().getDisplayName();
//						default:
//							break;
//					}
//				}
//				else {
//					// TODO delegate
//				}
//			}
//
//			return null;
//		}
//
//	}

	private void assertSWTThread()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the SWT UI thread!"); //$NON-NLS-1$
	}

	private void assertNotDisposed() {
		if (isDisposed())
			throw new IllegalStateException("This PersonRelationTree is already disposed! " + this); //$NON-NLS-1$
	}

	private PersonRelationTreeController personRelationTreeController;

	public PersonRelationTree(Composite parent) {
		super(parent, SWT.VIRTUAL | SWT.FULL_SELECTION);

		personRelationTreeController = new PersonRelationTreeController() {
			@Override
			protected void onJDOObjectsChanged(JDOLazyTreeNodesChangedEvent<ObjectID, PersonRelationTreeNode> changedEvent)
			{
				JDOLazyTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}
		};
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				for (PersonRelationTreeLabelProviderDelegate delegate : getAllPersonRelationTreeLabelProviderDelegates())
					delegate.onDispose();

				personRelationTreeController.close();
				personRelationTreeController = null;
			}
		});

		super.setInput(personRelationTreeController);
	}

	public PersonRelationTreeController getPersonRelationTreeController() {
		return personRelationTreeController;
	}

	@Override
	public void createTreeColumns(Tree tree) {
		TableLayout tableLayout = new TableLayout();

		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTree.tree.column.relation.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnPixelData(120));

		column = new TreeColumn(tree, SWT.LEFT);
		column.setText(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTree.tree.column.person.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(70));

		tree.setLayout(tableLayout);

		tree.setHeaderVisible(true);
	}

	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new PersonRelationTreeContentProvider());
		treeViewer.setLabelProvider(new PersonRelationTreeLabelProvider(treeViewer));
	}

	public void setInputPersonIDs(Collection<PropertySetID> personIDs, PropertySetID source)
	{
		assertSWTThread();
		assertNotDisposed();

		super.setInput(null);
		this.personIDs = personIDs;
		for (PersonRelationTreeLabelProviderDelegate delegate : getAllPersonRelationTreeLabelProviderDelegates())
			delegate.clear();

		personRelationTreeController.setRootPersonIDs(personIDs);
		super.setInput(personRelationTreeController);

		// if source is set -> select it in the tree.
//		if (source != null)
//		{
//			Set<PersonRelationTreeNode> currentLevel = new HashSet<PersonRelationTreeNode>();
//			Set<PersonRelationTreeNode> nextLevel = new HashSet<PersonRelationTreeNode>();
//
//			for (int i=0; i < personIDs.size(); i++)
//			{
				// FIXME: We never know when the elements we need is fetched by the backround jobs, unless using the listener.
				//        But synchronising the listener to these calls is not really nice.
				//        We need some method that retrieves the shallow objects (only ObjectID) immediately. (marius)
//				PersonRelationTreeNode node = personRelationTreeController.getNode(null, i);
//			}
//
//			PersonRelationTreeNode dummyNode = personRelationTreeController.createNode();
//			dummyNode.setJdoObjectID(source);
//			do
//			{
//				if (currentLevel.contains(dummyNode))
//				{
//					setSelection(dummyNode);
//					break;
//				}
//				currentLevel = nextLevel;
//				nextLevel = new HashSet<PersonRelationTreeNode>();
//
//				for (PersonRelationTreeNode personRelationTreeNode : currentLevel)
//				{
//					int childCount = (int) personRelationTreeController.getNodeCount(personRelationTreeNode);
//					for (int i=0; i < childCount; i++)
//					{
//						nextLevel.add( personRelationTreeController.getNode(personRelationTreeNode, i) );
//					}
//				}
//
//			} while (true);
//		}
	}

	public void setInputPersonIDs(Collection<PropertySetID> personIDs)
	{
		setInputPersonIDs(personIDs, null);
	}

	public Collection<PropertySetID> getInputPersonIDs() {
		return personIDs;
	}

//	public PropertySetID getSelectedInputPersonID() {
//		Collection<PropertySetID> inputPersonIDs = getInputPersonIDs();
//		if (inputPersonIDs == null || inputPersonIDs.isEmpty())
//			return null;
//
//		PropertySetID personID = inputPersonIDs.iterator().next();
//		return personID;
//	}

	/**
	 * @deprecated Do not call this method! It's only inherited and used
	 * internally - use {@link #setInputPersonIDs(Collection)} instead.
	 */
	@Deprecated
	@Override
	public void setInput(Object input) {
		super.setInput(input);
	}
}
