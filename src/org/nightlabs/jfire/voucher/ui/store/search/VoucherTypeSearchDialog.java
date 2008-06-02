package org.nightlabs.jfire.voucher.ui.store.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog;
import org.nightlabs.jfire.voucher.ui.VoucherPlugin;
import org.nightlabs.jfire.voucher.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeSearchDialog
extends AbstractProductTypeSearchDialog
{
	/**
	 * @param parentShell
	 */
	public VoucherTypeSearchDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected AbstractProductTypeSearchComposite createProductTypeSearchComposite(Composite parent) {
		return new VoucherTypeSearchComposite(parent, SWT.NONE);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog#getProductTypeName()
	 */
	@Override
	protected String getProductTypeName() {
		return Messages.getString("org.nightlabs.jfire.voucher.ui.store.search.VoucherTypeSearchDialog.0"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog#create()
	 */
	@Override
	public void create() {
		super.create();
		setTitleImage(SharedImages.getSharedImage(
				VoucherPlugin.getDefault(), VoucherTypeSearchDialog.class, "", 
				ImageDimension._75x70, ImageFormat.png));
	}
	
}
