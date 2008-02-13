package org.nightlabs.jfire.voucher.editor2d.ui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.voucher.admin.ui.tree.VoucherTypeTree;
import org.nightlabs.jfire.voucher.admin.ui.tree.VoucherTypeTreeNode;
import org.nightlabs.jfire.voucher.editor2d.ui.VoucherDetailComposite;
import org.nightlabs.jfire.voucher.editor2d.ui.resource.Messages;
import org.nightlabs.jfire.voucher.editor2d.ui.scripting.VoucherScriptResultProvider;
import org.nightlabs.jfire.voucher.scripting.PreviewParameterValuesResult;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherChooseDialog
extends CenteredDialog
{
	/**
	 * @param parentShell
	 */
	public VoucherChooseDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText(Messages.getString("org.nightlabs.jfire.voucher.editor2d.ui.dialog.VoucherChooseDialog.title")); //$NON-NLS-1$
		newShell.setSize(600, 400);
	}

	private VoucherTypeTree voucherTypeTree = null;
	private Group detailGroup = null;
	private VoucherDetailComposite detailComp = null;
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp.setLayout(new GridLayout(2, false));
		
		SashForm sash = new SashForm(comp, SWT.HORIZONTAL);
		sash.setLayout(new GridLayout());
		sash.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Voucher
		Group voucherGroup = new Group(sash, SWT.NONE);
		voucherGroup.setText(Messages.getString("org.nightlabs.jfire.voucher.editor2d.ui.dialog.VoucherChooseDialog.group.voucher")); //$NON-NLS-1$
		voucherGroup.setLayout(new GridLayout());
		voucherGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		voucherTypeTree = new VoucherTypeTree(voucherGroup,
				AbstractTreeComposite.DEFAULT_STYLE_SINGLE | SWT.BORDER);
		voucherTypeTree.addSelectionChangedListener(voucherSelectionListener);
//		voucherTypeTree.getTreeViewer().expandToLevel(2);
		
		// Voucher Details
		detailGroup = new Group(sash, SWT.NONE);
		detailGroup.setText(Messages.getString("org.nightlabs.jfire.voucher.editor2d.ui.dialog.VoucherChooseDialog.group.details")); //$NON-NLS-1$
		detailGroup.setLayout(new GridLayout());
		detailGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		detailComp = new VoucherDetailComposite(detailGroup, SWT.NONE);
				
		// By default deactivate all details until an event has been selected
		RCPUtil.setControlEnabledRecursive(detailGroup, false);
		
		return comp;
	}
	
	private ISelectionChangedListener voucherSelectionListener = new ISelectionChangedListener(){
		public void selectionChanged(SelectionChangedEvent event) {
			if (!event.getSelection().isEmpty())
			{
				Object firstElement = ((StructuredSelection) event.getSelection()).getFirstElement();
				if (firstElement instanceof VoucherTypeTreeNode) {
					VoucherTypeTreeNode voucherTypeTreeNode = (VoucherTypeTreeNode) firstElement;
					VoucherType selectedVoucherType = voucherTypeTreeNode.getJdoObject();
					PreviewParameterValuesResult ppvr = VoucherScriptResultProvider.sharedInstance().
						getPreviewParameterValuesResult(selectedVoucherType);
					detailComp.setPreviewParameterValuesResult(ppvr);
					RCPUtil.setControlEnabledRecursive(detailGroup, true);
					getButton(IDialogConstants.OK_ID).setEnabled(true);
				}
			}
		}
	};
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	@Override
	protected void okPressed()
	{
		VoucherScriptResultProvider.sharedInstance().setSelectedCurrency(
				detailComp.getSelectedCurrency());
		StructuredSelection selection = (StructuredSelection) voucherTypeTree.getSelection();
		VoucherTypeTreeNode voucherTypeTreeNode = (VoucherTypeTreeNode) selection.getFirstElement();
		VoucherType voucherType = voucherTypeTreeNode.getJdoObject();
		VoucherScriptResultProvider.sharedInstance().setSelectedObject(voucherType);
		
		super.okPressed();
	}
}