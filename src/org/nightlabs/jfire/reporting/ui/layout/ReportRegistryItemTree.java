package org.nightlabs.jfire.reporting.ui.layout;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.base.ui.notification.NotificationManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.base.ui.jdo.notification.SelectionNotificationProxy;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEventHandler;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.jfire.security.id.RoleID;

/**
 * An active tree of {@link ReportCategory}s and {@link ReportLayout}s.
 *  
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportRegistryItemTree extends ActiveJDOObjectTreeComposite<ReportRegistryItemID, ReportRegistryItem, ReportRegistryItemNode>
{
	/**
	 * This proxy delegates to the {@link NotificationManager}.
	 * It will extract the {@link ReportRegistryItem} from the {@link ReportRegistryItemNode}. 
	 */
	protected static class SelectionProxy extends SelectionNotificationProxy {

		public SelectionProxy(ReportRegistryItemTree source, String zone, boolean ignoreInheritance, boolean clearOnEmptySelection) {
			super(source, zone, ignoreInheritance, clearOnEmptySelection);
		}
		
		@Override
		protected Object getPersistenceCapable(Object selectionObject) {
			if (selectionObject instanceof ReportRegistryItemNode)
				return ((ReportRegistryItemNode)selectionObject).getJdoObject();
			return super.getPersistenceCapable(selectionObject);
		}
		
	}
	
	protected class ContentProvider extends JDOObjectTreeContentProvider<ReportRegistryItemID, ReportRegistryItem, ReportRegistryItemNode> {

		@Override
		public boolean hasJDOObjectChildren(ReportRegistryItem jdoObject) {
			return jdoObject instanceof ReportCategory;
		}
		
	}

	protected class LabelProvider extends JDOObjectTreeLabelProvider<ReportRegistryItemID, ReportRegistryItem, ReportRegistryItemNode> {

		@Override
		protected String getJDOObjectText(ReportRegistryItem jdoObject, int columnIndex) {
			return jdoObject.getName().getText();
		}
		
		protected String getTooltipText(Object element, int columnText) {
			ReportRegistryItem item = ((ReportRegistryItemNode)element).getJdoObject();
			if (item.getDescription().isEmpty())
				return null;
			else 
				return item.getDescription().getText();
		}
		
		@Override
		protected Image getJDOObjectImage(ReportRegistryItem jdoObject, int columnIndex) {
			if (jdoObject.getClass().equals(ReportCategory.class)) {
				if (((ReportCategory)jdoObject).isInternal())
					return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ReportRegistryItemTree.class, "category-internal"); //$NON-NLS-1$
				else
					return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ReportRegistryItemTree.class, "category-normal"); //$NON-NLS-1$
			}
			else if (jdoObject.getClass().equals(ReportLayout.class))
				return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ReportRegistryItemTree.class, "layout"); //$NON-NLS-1$
			return super.getJDOObjectImage(jdoObject, columnIndex);
		}
	}
	
	private SelectionProxy selectionProxy;
	private String selectionZone;
	private boolean addSelectionProxy;
	
	private ActiveReportRegistryItemTreeController activeReportRegistryItemTreeController;

	/**
	 * Create a new {@link ReportRegistryItemTree}.
	 * 
	 * @param parent 
	 * 		The parent to add the control to.
	 * @param addSelectionProxy 
	 * 		Whether to add a selection proxy that will delegate the 
	 * 		selection events to the {@link NotificationManager}.
	 * @param selectionZone
	 * 		The selection zone to use when delegating the selection events.
	 */
	public ReportRegistryItemTree(Composite parent, boolean addSelectionProxy, String selectionZone)
	{
		this(parent, addSelectionProxy, selectionZone, null);
	}
	
	/**
	 * Create a new {@link ReportRegistryItemTree} that will filter the items
	 * using on the basis of their associated authority and the given roleID.
	 * 
	 * @param parent 
	 * 		The parent to add the control to.
	 * @param addSelectionProxy 
	 * 		Whether to add a selection proxy that will delegate the 
	 * 		selection events to the {@link NotificationManager}.
	 * @param selectionZone
	 * 		The selection zone to use when delegating the selection events.
	 * @param filterRoleID
	 * 		The roleID that the current user requires to have for items to be visible.
	 */
	public ReportRegistryItemTree(Composite parent, boolean addSelectionProxy, String selectionZone, RoleID filterRoleID)
	{
		super(parent, DEFAULT_STYLE_SINGLE, true, true, false);
		this.addSelectionProxy = addSelectionProxy;
		activeReportRegistryItemTreeController = new ActiveReportRegistryItemTreeController(filterRoleID, getTopLevelItemIDs()) {
			
			@Override
			protected void onJDOObjectsChanged(JDOTreeNodesChangedEvent<ReportRegistryItemID, ReportRegistryItemNode> changedEvent)
			{
				JDOTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}
		};

		setInput(activeReportRegistryItemTreeController);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				activeReportRegistryItemTreeController.close();
				activeReportRegistryItemTreeController = null;
			}
		});
	}
	
	/** Defines whether the selection proxy (if created) should ignore inheritance, value is <code>false</code> */
	private static final boolean IGNORE_INHERITANCE = false;
	
	@Override
	public void init() {
		super.init();
		if (addSelectionProxy) {
			selectionProxy = new SelectionProxy(this, selectionZone, IGNORE_INHERITANCE, false);
			getTreeViewer().addSelectionChangedListener(selectionProxy);
		}
		getTreeViewer().setComparator(new ViewerComparator() {
			@Override
			public int category(Object element) {
				if (element instanceof ReportRegistryItemNode) {
					ReportRegistryItem item = ((ReportRegistryItemNode) element).getJdoObject();
					if (item instanceof ReportCategory)
						return 1;
					else
						return 2;
				} else
					return 3; 
			}
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (! (e1 instanceof ReportRegistryItemNode && e2 instanceof ReportRegistryItemNode))
					return super.compare(viewer, e1, e2);
				ReportRegistryItemNode node1 = (ReportRegistryItemNode) e1;
				ReportRegistryItemNode node2 = (ReportRegistryItemNode) e2;
				return node1.getJdoObject().getName().getText().compareTo(node2.getJdoObject().getName().getText());
			}
		});
	}
	
	@Override
	public void createTreeColumns(Tree tree)
	{
//		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
	}

	@Override
	public void setTreeProvider(TreeViewer treeViewer)
	{
		treeViewer.setContentProvider(new ContentProvider());
		ViewerColumn column = new ViewerColumn(treeViewer, treeViewer.getTree()) {
		};
		column.setLabelProvider(new CellLabelProvider() {
			LabelProvider lp = new LabelProvider();
			@Override
			public void update(ViewerCell cell) {
				cell.setText(lp.getColumnText(cell.getElement(), 0));
				cell.setImage(lp.getColumnImage(cell.getElement(), 0));
			}
			@Override
			public String getToolTipText(Object element) {
				return lp.getTooltipText(element, 0);
			}
		});
		ColumnViewerToolTipSupport.enableFor(treeViewer);
	}
	
	@Override
	protected ActiveJDOObjectTreeController<ReportRegistryItemID, ReportRegistryItem, ReportRegistryItemNode> getJDOObjectTreeController() {
		return activeReportRegistryItemTreeController;
	}

	/**
	 * Returns the {@link ReportRegistryItemID}s that should be displayed at top
	 * level of the tree. The default implementation returns <code>null</code>,
	 * indicating that those categories should be displayed at top-level, that
	 * are top-level in the datastore, too. Override this method to define a
	 * different set of top-level ids.
	 * 
	 * @return Those {@link ReportRegistryItemID}s that should be displayed at
	 *         top level in this tree, or <code>null</code> to use the top-level
	 *         items in the datastore.
	 */
	protected ReportRegistryItemID[] getTopLevelItemIDs() {
		return null;
	}
}
