package org.nightlabs.jfire.reporting.ui.layout;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
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

public class ReportRegistryItemTree extends ActiveJDOObjectTreeComposite<ReportRegistryItemID, ReportRegistryItem, ReportRegistryItemNode>
{
	
	public static class SelectionProxy extends SelectionNotificationProxy {

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
	
	private SelectionProxy selectionProxy;
	private String selectionZone;
	private boolean addSelectionProxy;
	
	// TODO: ignoreinheritance ?
	private static final boolean IGNORE_INHERITANCE = false;
	
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
	
	private ActiveReportRegistryItemTreeController activeReportRegistryItemTreeController;

	public ReportRegistryItemTree(Composite parent, boolean addSelectionProxy, String selectionZone)
	{
		super(parent, DEFAULT_STYLE_SINGLE, true, true, false);
		this.addSelectionProxy = addSelectionProxy;
		activeReportRegistryItemTreeController = new ActiveReportRegistryItemTreeController() {
			
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
	
	@Override
	public void init() {
		super.init();
		if (addSelectionProxy) {
			selectionProxy = new SelectionProxy(this, selectionZone, IGNORE_INHERITANCE, false);
			getTreeViewer().addSelectionChangedListener(selectionProxy);
		}
		getTreeViewer().setSorter(new ViewerSorter());
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
}
