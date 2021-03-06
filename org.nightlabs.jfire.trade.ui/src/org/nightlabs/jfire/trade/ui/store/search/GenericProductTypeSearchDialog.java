package org.nightlabs.jfire.trade.ui.store.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class GenericProductTypeSearchDialog
extends AbstractProductTypeSearchDialog
{
	/**
	 * @param parentShell
	 */
	public GenericProductTypeSearchDialog(Shell parentShell) {
		super(parentShell, Messages.RESOURCE_BUNDLE);
	}

	@Override
	protected AbstractProductTypeSearchComposite createProductTypeSearchComposite(
			Composite parent)
	{
		return new GenericProductTypeSearchComposite(parent, SWT.NONE);
	}

}
