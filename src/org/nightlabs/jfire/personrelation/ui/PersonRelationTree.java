package org.nightlabs.jfire.personrelation.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.ui.IViewActionDelegate;
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

/**
 * @author Marco Schulze
 * @author khaireel (at) nightlabs (dot) de
 */
public class PersonRelationTree extends AbstractTreeComposite<PersonRelationTreeNode>
{
	private static final Logger logger = Logger.getLogger(PersonRelationTree.class);

	private Collection<PropertySetID> personIDs;

	protected static class PersonRelationTreeContentProvider
	extends JDOObjectLazyTreeContentProvider<ObjectID, Object, PersonRelationTreeNode>
	{
	}

	private Map<Class<?>, IPersonRelationTreeLabelProviderDelegate> jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate = new HashMap<Class<?>, IPersonRelationTreeLabelProviderDelegate>();
	private Map<Class<?>, IPersonRelationTreeLabelProviderDelegate> jdoObjectClass2PersonRelationTreeLabelProviderDelegate = new HashMap<Class<?>, IPersonRelationTreeLabelProviderDelegate>();

	public void addPersonRelationTreeLabelProviderDelegate(IPersonRelationTreeLabelProviderDelegate delegate)
	{
		Class<? extends ObjectID> objectIDClass = delegate.getJDOObjectIDClass();
		Class<?> objectClass = delegate.getJDOObjectClass();
		jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.put(objectIDClass, delegate);
		jdoObjectClass2PersonRelationTreeLabelProviderDelegate.put(objectClass, delegate);

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

	protected class PersonRelationTreeLabelProvider extends ColumnSpanLabelProvider
	{
		private final Logger logger = Logger.getLogger(PersonRelationTreeLabelProvider.class);

		public PersonRelationTreeLabelProvider(ColumnViewer columnViewer) {
			super(columnViewer);
		}

		private String languageID = NLLocale.getDefault().getLanguage();

		protected String getJDOObjectText(ObjectID jdoObjectID, Object jdoObject, int spanColIndex) {
			if (jdoObject == null) {
				IPersonRelationTreeLabelProviderDelegate delegate = jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.get(jdoObjectID.getClass());
				if (delegate != null) {
					String result = delegate.getJDOObjectText(jdoObjectID, jdoObject, spanColIndex);
					if (result != null)
						return result;
				}

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
				IPersonRelationTreeLabelProviderDelegate delegate = jdoObjectClass2PersonRelationTreeLabelProviderDelegate.get(jdoObject.getClass());
				if (delegate != null) {
					String result = delegate.getJDOObjectText(jdoObjectID, jdoObject, spanColIndex);
					if (result != null)
						return result;
				}

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
				return null; // null means each real column assigned to one visible column
//				return new int[][] { {0}, {1}, {2}, {3}, {4}, {5} };

			PersonRelationTreeNode node = (PersonRelationTreeNode) element;
			ObjectID jdoObjectID = node.getJdoObjectID();
			Object jdoObject = node.getJdoObject();
			if (jdoObject == null) {
				IPersonRelationTreeLabelProviderDelegate delegate = jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.get(jdoObjectID.getClass());
				if (delegate != null) {
					int[][] result = delegate.getJDOObjectColumnSpan(jdoObjectID, jdoObject);
					if (result != null)
						return result;
				}

				return new int[][] { {0, 1} };
			}
			else {
				IPersonRelationTreeLabelProviderDelegate delegate = jdoObjectClass2PersonRelationTreeLabelProviderDelegate.get(jdoObject.getClass());
				if (delegate != null) {
					int[][] result = delegate.getJDOObjectColumnSpan(jdoObjectID, jdoObject);
					if (result != null)
						return result;
				}

				if (jdoObject instanceof Person)
					return new int[][] { {0, 1} };

				if (jdoObject instanceof PersonRelation)
					return null; // null means each real column assigned to one visible column

				return null; // null means each real column assigned to one visible column
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

		protected Image getJDOObjectImage(ObjectID jdoObjectID, Object jdoObject, int spanColIndex)
		{
			if (jdoObject == null) {
				IPersonRelationTreeLabelProviderDelegate delegate = jdoObjectIDClass2PersonRelationTreeLabelProviderDelegate.get(jdoObjectID.getClass());
				if (delegate != null) {
					Image result = delegate.getJDOObjectImage(jdoObjectID, jdoObject, spanColIndex);
					if (result != null)
						return result;
				}
			}
			else {
				IPersonRelationTreeLabelProviderDelegate delegate = jdoObjectClass2PersonRelationTreeLabelProviderDelegate.get(jdoObject.getClass());
				if (delegate != null) {
					Image result = delegate.getJDOObjectImage(jdoObjectID, jdoObject, spanColIndex);
					if (result != null)
						return result;
				}
			}

			if (jdoObject instanceof Person) {
				return spanColIndex == 0
						? SharedImages.getSharedImage(PersonRelationPlugin.getDefault(), PersonRelationTreeLabelProvider.class, jdoObject.getClass().getSimpleName())
						: null;
			}

			if (jdoObject instanceof PersonRelation) {
				if (spanColIndex == 0) {
					String suffix = jdoObject.getClass().getSimpleName();
					return SharedImages.getSharedImage(PersonRelationPlugin.getDefault(), PersonRelationTreeLabelProvider.class, suffix);
				}
				else
					return null;
			}

			return null;
		}

	}

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


	// ------ [Constructors: With options for more control parameters; for collapse-state and for context-menus] --------->>
	public PersonRelationTree(Composite parent) {
		this(parent, true);
	}

	public PersonRelationTree(Composite parent, boolean isRestoreCollapseState) {
		this(parent, isRestoreCollapseState, true, true);
	}

	public PersonRelationTree(Composite parent, boolean isRestoreCollapseState, boolean isCreateContextMenu, boolean isMenuWithDrillDownAdapter) {
		// We have to ensure that we dont trigger the Abstract-tree's init() method before setting
		// the restoring the collapse state of the tree. That is, we trigger init() only after setting the state.
		super(parent, SWT.VIRTUAL | SWT.FULL_SELECTION, true, false, true);
		setRestoreCollapseState(isRestoreCollapseState);
		init();

		personRelationTreeController = createPersonRelationTreeController();
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				for (IPersonRelationTreeLabelProviderDelegate delegate : getAllPersonRelationTreeLabelProviderDelegates())
					delegate.onDispose();

				personRelationTreeController.close();
				personRelationTreeController = null;
			}
		});

		if (isCreateContextMenu)
			createContextMenu(isMenuWithDrillDownAdapter);

		super.setInput(personRelationTreeController);
	}

	// ------------------------------------------------------------------------------------------------------------------->>
	/**
	 * Initialises the set of priorityOrderedContextMenuContributions by blending them into tree's SelectionChangeListener;
	 * i.e. mainly, this controls the UI's enabled (or disabled) state for which ever (context) item has been selected.
	 * Call this once, only when we are ready with all the menu items we want. For now, we handle only those {@link IViewActionDelegate},
	 * for they have the method to handle 'selectionchanged()'.
	 *
	 * This also sets up the double-click behaviour, where in this setup, we assume that the (menu) items registered in the
	 * {@link AbstractTreeComposite} has been ordered in accordance to 'first-available-default' priority, this in turn will
	 * make them to be automatically used in this double-click context. See notes 2010.03.08. Kai.
	 *
	 * See first independent application usage in PersonRelationIssueTreeView.
	 */
	public void integratePriorityOrderedContextMenu() {
		List<Object> orderedContextMenuContributions = getPriorityOrderedContextMenuContributions();
		if (orderedContextMenuContributions == null || orderedContextMenuContributions.isEmpty())
			return;

		// On selection changes.
		addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty())
					return;

				for (Object menuItem : getPriorityOrderedContextMenuContributions()) {
					if (menuItem instanceof IViewActionDelegate)
						((IViewActionDelegate) menuItem).selectionChanged((IAction) menuItem, event.getSelection());
				}
			}
		});

		// On double-click: 'first-available-default' priority execution.
		addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (event.getSelection().isEmpty())
					return;

				for (Object menuItem : getPriorityOrderedContextMenuContributions())
					if (menuItem instanceof IAction) {
						IAction menuAction = (IAction) menuItem;
						if (menuAction.isEnabled()) {
							menuAction.run();
							return;
						}
					}
			}
		});
	}

	// ------------------------------------------------------------------------------------------------------------------->>



	protected PersonRelationTreeController createPersonRelationTreeController() {
		return new PersonRelationTreeController() {
			@Override
			protected void onJDOObjectsChanged(JDOLazyTreeNodesChangedEvent<ObjectID, PersonRelationTreeNode> changedEvent) {
				JDOLazyTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}
		};
	}

	public PersonRelationTreeController getPersonRelationTreeController() {
		return personRelationTreeController;
	}

	@Override
	public void createTreeColumns(Tree tree) {
		TableLayout tableLayout = new TableLayout();

		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTree.tree.column.relation.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(2));
//		tableLayout.addColumnData(new ColumnPixelData(120));

		column = new TreeColumn(tree, SWT.LEFT);
		column.setText(Messages.getString("org.nightlabs.jfire.personrelation.ui.PersonRelationTree.tree.column.person.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(3));
//		tableLayout.addColumnData(new ColumnWeightData(70));

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
		for (IPersonRelationTreeLabelProviderDelegate delegate : getAllPersonRelationTreeLabelProviderDelegates())
			delegate.clear();

		personRelationTreeController.setRootPersonIDs(personIDs);
		super.setInput(personRelationTreeController);
	}

	public void setInputPersonIDs(Collection<PropertySetID> personIDs)
	{
		setInputPersonIDs(personIDs, null);
	}

	public Collection<PropertySetID> getInputPersonIDs() {
		return personIDs;
	}


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
