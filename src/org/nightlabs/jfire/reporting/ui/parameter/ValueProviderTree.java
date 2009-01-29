package org.nightlabs.jfire.reporting.ui.parameter;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEventHandler;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.ValueProviderCategory;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.util.NLLocale;

/**
 * Active tree displaying {@link ValueProviderCategory}s and {@link ValueProvider}.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ValueProviderTree extends ActiveJDOObjectTreeComposite<ObjectID, Object, ValueProviderTreeNode>
{
	protected class ContentProvider extends JDOObjectTreeContentProvider<ObjectID, Object, ValueProviderTreeNode> {

		@Override
		public boolean hasJDOObjectChildren(Object jdoObject) {
			return jdoObject instanceof ValueProviderCategory;
		}
		
	}

	protected class LabelProvider extends JDOObjectTreeLabelProvider<ObjectID, Object, ValueProviderTreeNode> {

		@Override
		protected String getJDOObjectText(Object jdoObject, int columnIndex) {
			if (jdoObject instanceof ValueProviderCategory) {
				return ((ValueProviderCategory)jdoObject).getName().getText(NLLocale.getDefault().getLanguage());
			}
			else if (jdoObject instanceof ValueProvider) {
				return ((ValueProvider)jdoObject).getName().getText(NLLocale.getDefault().getLanguage());
			}
			return jdoObject.toString();
//			return jdoObject.getName().getText(NLLocale.getDefault().getLanguage());
		}
		
		@Override
		protected Image getJDOObjectImage(Object jdoObject, int columnIndex) {
			if (columnIndex == 0) {
				if (jdoObject instanceof ValueProviderCategory)
					return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ValueProviderTree.class, "category"); //$NON-NLS-1$
				else if (jdoObject instanceof ValueProvider)
					return SharedImages.getSharedImage(ReportingPlugin.getDefault(), ValueProviderTree.class, "provider"); //$NON-NLS-1$
			}
			return null;
		}
		
	}
	
	private ValueProviderController providerTreeController = new ValueProviderController() {
		@Override
		protected void onJDOObjectsChanged(JDOTreeNodesChangedEvent<ObjectID, ValueProviderTreeNode> changedEvent) {
			JDOTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
		}
	};

	public ValueProviderTree(Composite parent)
	{
		super(parent, DEFAULT_STYLE_SINGLE, true, true, false);

		setInput(providerTreeController);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				providerTreeController.close();
				providerTreeController = null;
			}
		});
	}
	
	@Override
	public void init() {
		super.init();
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
		treeViewer.setLabelProvider(new LabelProvider());
	}
	
	@Override
	protected ActiveJDOObjectTreeController<ObjectID, Object, ValueProviderTreeNode> getJDOObjectTreeController() {
		return providerTreeController;
	}
}
