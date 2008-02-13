package org.nightlabs.jfire.dynamictrade.ui.store.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeSearchDialog
extends AbstractProductTypeSearchDialog
{
	/**
	 * @param parentShell
	 */
	public DynamicProductTypeSearchDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected AbstractProductTypeSearchComposite createProductTypeSearchComposite(Composite parent) {
		return new DynamicProductTypeSearchComposite(parent, SWT.NONE);
	}

}
