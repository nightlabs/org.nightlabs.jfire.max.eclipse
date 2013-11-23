package org.nightlabs.jfire.personrelation.ui.tree;

import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.DrillDownAdapter;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.JDOLazyTreeNodesChangedEventHandler;
import org.nightlabs.jfire.personrelation.ui.resource.Messages;
import org.nightlabs.jfire.prop.id.PropertySetID;

/**
 * @author Marco Schulze
 * @author khaireel (at) nightlabs (dot) de
 */
public class PersonRelationTree<N extends PersonRelationTreeNode> extends AbstractTreeComposite<N>
{
	private Collection<PropertySetID> personIDs;

	// Note: (since 2010.03.28)
	// The entire PersonRelationTreeLabelProvider has been upgraded to a become fully qualified class, along with its delegates.
	// This should allow us to extend the class further in order to give more control in manipulating the Graphics Control found in 
	// the super class ColumnSpanLabelProvider.
	private PersonRelationTreeLabelProvider<N> personRelationTreeLabelProvider = null;
	protected PersonRelationTreeLabelProvider<N> createPersonRelationTreeLabelProvider(TreeViewer treeViewer) {
		// We supply the default PersonRelationTreeProvider here. Override it for more specificity.
		return new PersonRelationTreeLabelProvider<N>(treeViewer, true);
	}
	
	public void addPersonRelationTreeLabelProviderDelegate(IPersonRelationTreeLabelProviderDelegate delegate)
	{
		if (personRelationTreeLabelProvider != null)
			personRelationTreeLabelProvider.addPersonRelationTreeLabelProviderDelegate(delegate);
		
		// else we should throw something...?
	}

	protected Set<IPersonRelationTreeLabelProviderDelegate> getAllPersonRelationTreeLabelProviderDelegates()
	{
		return personRelationTreeLabelProvider == null ? null : personRelationTreeLabelProvider.getAllPersonRelationTreeLabelProviderDelegates();
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

	private PersonRelationTreeController<N> personRelationTreeController;


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

		if (isCreateContextMenu) {
			TreeViewer treeViewer = getTreeViewer();
			createContextMenu(isMenuWithDrillDownAdapter ? new DrillDownAdapter(treeViewer) : null, treeViewer.getControl());
		}

		super.setInput(personRelationTreeController);
	}

//	// ------------------------------------------------------------------------------------------------------------------->>
//	/**
//	 * Initialises the set of priorityOrderedContextMenuContributions by blending them into tree's SelectionChangeListener;
//	 * i.e. mainly, this controls the UI's enabled (or disabled) state for which ever (context) item has been selected.
//	 * Call this once, only when we are ready with all the menu items we want. For now, we handle only those {@link IViewActionDelegate},
//	 * for they have the method to handle 'selectionchanged()'.
//	 *
//	 * This also sets up the double-click behaviour, where in this setup, we assume that the (menu) items registered in the
//	 * {@link AbstractTreeComposite} has been ordered in accordance to 'first-available-default' priority, this in turn will
//	 * make them to be automatically used in this double-click context. See notes 2010.03.08. Kai.
//	 *
//	 * See first independent application usage in PersonRelationIssueTreeView.
//	 */
//	public void integratePriorityOrderedContextMenu() {
//		List<Object> orderedContextMenuContributions = getPriorityOrderedContextMenuContributions();
//		if (orderedContextMenuContributions == null || orderedContextMenuContributions.isEmpty())
//			return;
//
//		// On selection changes.
//		addSelectionChangedListener(new ISelectionChangedListener() {
//			@Override
//			public void selectionChanged(SelectionChangedEvent event) {
//				if (event.getSelection().isEmpty())
//					return;
//
//				for (Object menuItem : getPriorityOrderedContextMenuContributions()) {
//					if (menuItem instanceof IViewActionDelegate)
//						((IViewActionDelegate) menuItem).selectionChanged((IAction) menuItem, event.getSelection());
//				}
//			}
//		});
//
//		// On double-click: 'first-available-default' priority execution.
//		addDoubleClickListener(new IDoubleClickListener() {
//			@Override
//			public void doubleClick(DoubleClickEvent event) {
//				if (event.getSelection().isEmpty())
//					return;
//
//				for (Object menuItem : getPriorityOrderedContextMenuContributions())
//					if (menuItem instanceof IAction) {
//						IAction menuAction = (IAction) menuItem;
//						if (menuAction.isEnabled()) {
//							menuAction.run();
//							return;
//						}
//					}
//			}
//		});
//	}
//
//	// ------------------------------------------------------------------------------------------------------------------->>

	/**
	 * Override this method in order to control whether the TreeController and
	 * LabelProvider should add the default delegates for PersonRelations.
	 * @return In this case, <code>true</code>.
	 */
	protected boolean isAddDefaultDelegates() {
		return true;
	}

	protected PersonRelationTreeController<N> createPersonRelationTreeController() {
		return new PersonRelationTreeController<N>(isAddDefaultDelegates()) {
			@Override
			protected void onJDOObjectsChanged(JDOLazyTreeNodesChangedEvent<ObjectID, N> changedEvent) {
				JDOLazyTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}

			@SuppressWarnings("unchecked")
			@Override
			protected N createNode() {
				return (N) new PersonRelationTreeNode();
			}
		};
	}

	public PersonRelationTreeController<N> getPersonRelationTreeController() {
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
		personRelationTreeLabelProvider = createPersonRelationTreeLabelProvider(treeViewer);
		
		treeViewer.setContentProvider(new PersonRelationTreeContentProvider<N>());
		treeViewer.setLabelProvider(personRelationTreeLabelProvider.getLabelProviderToUseForTree());
	}

	public void setInputPersonIDs(Collection<PropertySetID> personIDs, PropertySetID source)
	{
		assertSWTThread();
		assertNotDisposed();

		super.setInput(null);
		this.personIDs = personIDs;
		for (IPersonRelationTreeLabelProviderDelegate delegate : getAllPersonRelationTreeLabelProviderDelegates()) {
			delegate.clear();
		}
		
		getPersonRelationTreeController().clear();
		
		for (IPersonRelationTreeControllerDelegate delegate: getPersonRelationTreeController().getPersonRelationTreeControllerDelegates()) {
			delegate.setRootPersonIDs(personIDs);
		}
		
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
