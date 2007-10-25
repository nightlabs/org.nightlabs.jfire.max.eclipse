package org.nightlabs.jfire.reporting.ui.layout;

import java.util.Locale;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ui.notification.SelectionNotificationProxy;
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
			return jdoObject.getName().getText(Locale.getDefault().getLanguage());
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
	@Implement
	public void createTreeColumns(Tree tree)
	{
//		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
	}

	@Override
	@Implement
	public void setTreeProvider(TreeViewer treeViewer)
	{
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new LabelProvider());
	}
	
//	/**
//	 * Returns the (first) selected ReportRegistryItem or null.
//	 * @return The (first) selected ReportRegistryItem or null.
//	 */
//	public ReportRegistryItem getSelectedRegistryItem() {
//		if (getTree().getSelectionCount() == 1) {
//			return ((ReportRegistryItemNode)getTree().getSelection()[0].getData()).getJdoObject();
//		}
//		return null;
//	}
//
//	/**
//	 * Returns all selected ReportRegistryItems in a Set.
//	 * @return All selected ReportRegistryItems in a Set.
//	 */
//	public Set<ReportRegistryItem> getSelectedRegistryItems() {
//		Set<ReportRegistryItem> result = new HashSet<ReportRegistryItem>();
//		TreeItem[] items = getTree().getSelection();
//		for (int i = 0; i < items.length; i++) {
//			result.add(((ReportRegistryItemNode)items[i].getData()).getJdoObject());
//		}
//		return result;
//	}

	@Override
	protected ActiveJDOObjectTreeController<ReportRegistryItemID, ReportRegistryItem, ReportRegistryItemNode> getJDOObjectTreeController() {
		return activeReportRegistryItemTreeController;
	}	
}
