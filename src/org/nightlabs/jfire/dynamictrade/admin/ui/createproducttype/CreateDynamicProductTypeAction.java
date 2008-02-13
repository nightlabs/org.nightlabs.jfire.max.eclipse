package org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.dynamictrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.dynamictrade.admin.ui.tree.DynamicProductTypeTree;
import org.nightlabs.jfire.dynamictrade.admin.ui.tree.DynamicProductTypeTreeNode;
import org.nightlabs.jfire.store.id.ProductTypeID;

public class CreateDynamicProductTypeAction
		extends Action
{
	protected DynamicProductTypeTree tree;
	protected DynamicProductTypeTreeNode selectedNode = null;

	public CreateDynamicProductTypeAction(DynamicProductTypeTree dynamicProductTypeTree)
	{
		super(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.createproducttype.CreateDynamicProductTypeAction.text")); //$NON-NLS-1$
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

	@Override
	public void run()
	{
		try {
			if (selectedNode == null)
				throw new IllegalStateException("No node selected!"); //$NON-NLS-1$

			CreateDynamicProductTypeWizard createProductWizard = new CreateDynamicProductTypeWizard((ProductTypeID) JDOHelper.getObjectId(selectedNode.getJdoObject()));
			DynamicPathWizardDialog wizardDialog = new DynamicPathWizardDialog(createProductWizard);
			wizardDialog.open();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
