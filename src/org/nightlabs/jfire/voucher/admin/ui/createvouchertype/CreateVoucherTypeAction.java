package org.nightlabs.jfire.voucher.admin.ui.createvouchertype;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.admin.ui.tree.VoucherTypeTree;
import org.nightlabs.jfire.voucher.admin.ui.tree.VoucherTypeTreeNode;

public class CreateVoucherTypeAction
extends Action
{
	protected VoucherTypeTree tree;
	protected VoucherTypeTreeNode selectedNode = null;

	public CreateVoucherTypeAction(VoucherTypeTree tree)
	{
		super(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.CreateVoucherTypeAction.text")); //$NON-NLS-1$
		setEnabled(false);
		this.tree = tree;
		tree.addSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event)
					{
						StructuredSelection selection = (StructuredSelection) event.getSelection();
						if (selection.isEmpty())
							selectedNode = null;
						else
							selectedNode = (VoucherTypeTreeNode) selection.getFirstElement();

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

			CreateVoucherTypeWizard createProductWizard = new CreateVoucherTypeWizard(selectedNode.getJdoObject().getObjectId());
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
