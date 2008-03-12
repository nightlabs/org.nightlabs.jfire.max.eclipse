/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.dialog.CenteredTitleDialog;
import org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class BaseProductTypeQuickListFilterDialog 
extends CenteredTitleDialog 
{
	/**
	 * @param parentShell
	 */
	public BaseProductTypeQuickListFilterDialog(Shell parentShell) {
		super(parentShell);
	}

	private ProductTypeSearchCriteriaComposite productTypeSearchCriteriaComposite;
	
	@Override
	protected Control createDialogArea(Composite parent) {
		productTypeSearchCriteriaComposite = new ProductTypeSearchCriteriaComposite(parent, SWT.NONE);
		return productTypeSearchCriteriaComposite;
	}

}
