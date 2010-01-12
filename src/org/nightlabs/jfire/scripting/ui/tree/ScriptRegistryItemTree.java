package org.nightlabs.jfire.scripting.ui.tree;

import java.util.Set;

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
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.base.ui.jdo.notification.SelectionNotificationProxy;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEventHandler;
import org.nightlabs.jfire.scripting.Script;
import org.nightlabs.jfire.scripting.ScriptCategory;
import org.nightlabs.jfire.scripting.ScriptRegistryItem;
import org.nightlabs.jfire.scripting.id.ScriptRegistryItemID;
import org.nightlabs.jfire.scripting.ui.ScriptingPlugin;



/**
 * @author Fitas Amine - fitas [at] nightlabs [dot] de
 */
public class ScriptRegistryItemTree extends ActiveJDOObjectTreeComposite<ScriptRegistryItemID, ScriptRegistryItem, ScriptRegistryItemNode>
{
	
	/**
	 * This proxy delegates to the {@link NotificationManager}.
	 * It will extract the {@link ReportRegistryItem} from the {@link ReportRegistryItemNode}. 
	 */
	protected static class SelectionProxy extends SelectionNotificationProxy {

		public SelectionProxy(ScriptRegistryItemTree source, String zone, boolean ignoreInheritance, boolean clearOnEmptySelection) {
			super(source, zone, ignoreInheritance, clearOnEmptySelection);
		}
		
		@Override
		protected Object getPersistenceCapable(Object selectionObject) {
			if (selectionObject instanceof ScriptRegistryItemNode)
				return ((ScriptRegistryItemNode)selectionObject).getJdoObject();
			return super.getPersistenceCapable(selectionObject);
		}
		
	}	
	
	protected class ContentProvider extends JDOObjectTreeContentProvider<ScriptRegistryItemID, ScriptRegistryItem, ScriptRegistryItemNode> {

		@Override
		public boolean hasJDOObjectChildren(ScriptRegistryItem jdoObject) {
			return jdoObject instanceof ScriptCategory;
		}
		
	}

	protected class LabelProvider extends JDOObjectTreeLabelProvider<ScriptRegistryItemID, ScriptRegistryItem, ScriptRegistryItemNode> {

		@Override
		protected String getJDOObjectText(ScriptRegistryItem jdoObject, int columnIndex) {
			return jdoObject.getName().getText();
		}
		
		@Override
		public Image getJDOObjectImage(ScriptRegistryItem jdoObject, int columnIndex) {
			if (jdoObject.getClass().equals(ScriptCategory.class))
				return SharedImages.getSharedImage(ScriptingPlugin.getDefault(), org.nightlabs.jfire.scripting.ui.ScriptRegistryItemTree.class, "category"); //$NON-NLS-1$			
			if (jdoObject.getClass().equals(Script.class))
				return SharedImages.getSharedImage(ScriptingPlugin.getDefault(),  org.nightlabs.jfire.scripting.ui.ScriptRegistryItemTree.class, "script"); //$NON-NLS-1$
			return super.getJDOObjectImage(jdoObject, columnIndex);
		}

		protected String getTooltipText(Object element, int columnText) {
			ScriptRegistryItem item = ((ScriptRegistryItemNode)element).getJdoObject();
			if (item.getDescription().isEmpty())
				return null;
			else 
				return item.getDescription().getText();
		}
	
	}
	
	
	private SelectionProxy selectionProxy;
	private String selectionZone;
	private boolean addSelectionProxy;
	
	// TODO: ignoreinheritance ?
	private static final boolean IGNORE_INHERITANCE = false;
	
	private ActiveScriptRegistryItemTreeController activeScriptRegistryItemTreeController;


	
	/**
	 * Create a new {@link ScriptRegistryItemTree}
	 * 
	 * @param parent 
	 * 		The parent to add the control to.
	 * @param addSelectionProxy 
	 * 		Whether to add a selection proxy that will delegate the 
	 * 		selection events to the {@link NotificationManager}.
	 * @param selectionZone
	 * 		The selection zone to use when delegating the selection events.
	 * 
	 */
	public ScriptRegistryItemTree(Composite parent, boolean addSelectionProxy, String selectionZone)
	{
		super(parent, DEFAULT_STYLE_SINGLE, true, true, false);
		this.addSelectionProxy = addSelectionProxy;
		activeScriptRegistryItemTreeController = new ActiveScriptRegistryItemTreeController() {
			
			@Override
			protected void onJDOObjectsChanged(JDOTreeNodesChangedEvent<ScriptRegistryItemID, ScriptRegistryItemNode> changedEvent)
			{
				JDOTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}
			
		};

		setInput(activeScriptRegistryItemTreeController);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				activeScriptRegistryItemTreeController.close();
				activeScriptRegistryItemTreeController = null;
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
		getTreeViewer().setComparator(new ViewerComparator() {
			@Override
			public int category(Object element) {
				if (element instanceof ScriptRegistryItemNode) {
					ScriptRegistryItem item = ((ScriptRegistryItemNode) element).getJdoObject();
					if (item instanceof ScriptCategory)
						return 1;
					else
						return 2;
				} else
					return 3; 
			}
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (! (e1 instanceof ScriptRegistryItemNode && e2 instanceof ScriptRegistryItemNode))
					return super.compare(viewer, e1, e2);
				ScriptRegistryItemNode node1 = (ScriptRegistryItemNode) e1;
				ScriptRegistryItemNode node2 = (ScriptRegistryItemNode) e2;

				return node1.getJdoObject().getName().getText().compareTo(node2.getJdoObject().getName().getText());
			}
		});
	}
	
	@Override
	public void createTreeColumns(Tree tree)
	{
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
	protected ActiveJDOObjectTreeController<ScriptRegistryItemID, ScriptRegistryItem, ScriptRegistryItemNode> getJDOObjectTreeController() {
		return activeScriptRegistryItemTreeController;
	}
	
	
	/**
	 * Returns all selected ScriptRegistryItems in a Set.
	 * @return All selected ScriptRegistryItems in a Set.
	 */
	public Set<ScriptRegistryItem> getSelectedRegistryItems() {		
		return getSelectedElements();
	}
		
}
