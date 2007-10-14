package org.nightlabs.jfire.voucher.store.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog;

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

}
