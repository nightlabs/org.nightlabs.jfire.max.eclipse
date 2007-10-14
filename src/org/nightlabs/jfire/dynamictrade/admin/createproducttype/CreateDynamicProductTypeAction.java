package org.nightlabs.jfire.dynamictrade.admin.createproducttype;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.dynamictrade.admin.resource.Messages;
import org.nightlabs.jfire.dynamictrade.admin.tree.DynamicProductTypeTree;
import org.nightlabs.jfire.dynamictrade.admin.tree.DynamicProductTypeTreeNode;

public class CreateDynamicProductTypeAction
		extends Action
{
	protected DynamicProductTypeTree tree;
	protected DynamicProductTypeTreeNode selectedNode = null;

	public CreateDynamicProductTypeAction(DynamicProductTypeTree dynamicProductTypeTree)
	{
		super(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.createproducttype.CreateDynamicProductTypeAction.text")); //$NON-NLS-1$
		setEnabled(false);
		this.tree = dynamicProductTypeTree;
		tree.addSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event)
					{
						StructuredSelection selection = (StructuredSelection) event.getSelection();
						if (selection.isEmpty())
							selectedNode = null;
						else
							selectedNode = (DynamicProductTypeTreeNode) selection.getFirstElement();

						setEnabled(selectedNode != null && selectedNode.getJdoObject().isInheritanceBranch());
					}
				});
	}

	public void run()
	{
		try {
			if (selectedNode == null)
				throw new IllegalStateException("No node selected!"); //$NON-NLS-1$

			CreateDynamicProductTypeWizard createProductWizard = new CreateDynamicProductTypeWizard(selectedNode);
			DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(
					tree.getTreeViewer().getControl().getShell(),
//					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					createProductWizard);
			wizardDialog.open();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
