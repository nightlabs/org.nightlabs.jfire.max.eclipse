package org.nightlabs.jfire.voucher.admin.ui.createvouchertype;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.admin.ui.tree.VoucherTypeTree;
import org.nightlabs.jfire.voucher.store.VoucherType;

public class VoucherTypeSelectPage
extends DynamicPathWizardPage
{
	private VoucherTypeTree voucherTypeTree;
	private VoucherType selectedVoucherType;
	
	/**
	 * @param pageName
	 */
	public VoucherTypeSelectPage()
	{
		super(VoucherTypeSelectPage.class.getName(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.VoucherTypeSelectPage.title")); //$NON-NLS-1$
		this.setDescription(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.VoucherTypeSelectPage.description")); //$NON-NLS-1$
	}
 
	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent)
	{
		final FadeableComposite page = new FadeableComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		XComposite comp = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		comp.getGridLayout().numColumns = 2;
		
		voucherTypeTree = new VoucherTypeTree(comp);
		voucherTypeTree.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
			}
		});

		return page;
	}

	private boolean isPageAdded = false;
	@Override
	public boolean canFlipToNextPage() {
		selectedVoucherType = voucherTypeTree.getFirstSelectedElement();
		
		if (selectedVoucherType != null && selectedVoucherType.getInheritanceNature() == ProductType.INHERITANCE_NATURE_BRANCH ) {
			CreateVoucherTypeNewWizard newWizard = (CreateVoucherTypeNewWizard)getWizard();
			newWizard.setParentVoucherTypeID(selectedVoucherType.getObjectId());
			
			if (!isPageAdded) {
				newWizard.addRemainingPages();
				isPageAdded = true;
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean isPageComplete() {
		return selectedVoucherType != null;
	}
	
	@Override
	public boolean canBeLastPage() {
		return false;
	}
}