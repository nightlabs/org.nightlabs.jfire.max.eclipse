package org.nightlabs.jfire.dynamictrade.ui.template;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeComposite;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeContentProvider;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOObjectTreeNode;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEvent;
import org.nightlabs.jfire.base.ui.jdo.tree.JDOTreeNodesChangedEventHandler;
import org.nightlabs.jfire.dynamictrade.template.DynamicProductTemplate;
import org.nightlabs.jfire.dynamictrade.template.id.DynamicProductTemplateID;
import org.nightlabs.util.NLLocale;

public class DynamicProductTemplateTree
extends ActiveJDOObjectTreeComposite<
	DynamicProductTemplateID, DynamicProductTemplate, JDOObjectTreeNode<
			DynamicProductTemplateID, DynamicProductTemplate,
			DynamicProductTemplateTreeController
	>
>
{
	private static class ContentProvider extends JDOObjectTreeContentProvider<DynamicProductTemplateID, DynamicProductTemplate, JDOObjectTreeNode<DynamicProductTemplateID, DynamicProductTemplate, DynamicProductTemplateTreeController>> {
		@Override
		public boolean hasJDOObjectChildren(DynamicProductTemplate jdoObject) {
			return jdoObject.isCategory();
		}
	}

	public DynamicProductTemplateTree(Composite parent) {
		super(parent);
		controller = new DynamicProductTemplateTreeController() {
			@Override
			protected void onJDOObjectsChanged(JDOTreeNodesChangedEvent<DynamicProductTemplateID, JDOObjectTreeNode<DynamicProductTemplateID, DynamicProductTemplate, DynamicProductTemplateTreeController>> changedEvent) {
				JDOTreeNodesChangedEventHandler.handle(getTreeViewer(), changedEvent);
			}
		};
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				controller.close();
				controller = null;
			}
		});
		setInput(controller);
	}

	private DynamicProductTemplateTreeController controller;

	@Override
	protected DynamicProductTemplateTreeController getJDOObjectTreeController()
	{
		return controller;
	}

	@Override
	public void createTreeColumns(Tree tree) {
	}

	@Override
	public void setTreeProvider(TreeViewer treeViewer) {
		treeViewer.setContentProvider(new ContentProvider());
		treeViewer.setLabelProvider(new TableLabelProvider() {
			private String languageID = NLLocale.getDefault().getLanguage();

			@Override
			public String getColumnText(Object object, int colIdx) {
				if (!(object instanceof JDOObjectTreeNode)) {
					if (colIdx == 0)
						return String.valueOf(object);

					return null;
				}

				@SuppressWarnings("unchecked")
				JDOObjectTreeNode<DynamicProductTemplateID, DynamicProductTemplate, DynamicProductTemplateTreeController> node = (JDOObjectTreeNode<DynamicProductTemplateID, DynamicProductTemplate, DynamicProductTemplateTreeController>) object;

				DynamicProductTemplate dynamicProductTemplate = node.getJdoObject();

				switch (colIdx) {
					case 0:
						return dynamicProductTemplate.getName().getText(languageID);
					default:
						break;
				}

				return null;
			}
		});
	}

}
