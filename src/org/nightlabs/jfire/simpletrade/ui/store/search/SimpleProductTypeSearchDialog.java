package org.nightlabs.jfire.simpletrade.ui.store.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.jfire.simpletrade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchComposite;
import org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeSearchDialog
extends AbstractProductTypeSearchDialog
{
	/**
	 * @param parentShell
	 */
	public SimpleProductTypeSearchDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected AbstractProductTypeSearchComposite createProductTypeSearchComposite(Composite parent) {
		return new SimpleProductTypeSearchComposite(parent, SWT.NONE);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.store.search.AbstractProductTypeSearchDialog#getProductTypeName()
	 */
	@Override
	protected String getProductTypeName() {
		return Messages.getString("org.nightlabs.jfire.simpletrade.ui.store.search.SimpleProductTypeSearchDialog.simpleProductType"); //$NON-NLS-1$
	}	
}
