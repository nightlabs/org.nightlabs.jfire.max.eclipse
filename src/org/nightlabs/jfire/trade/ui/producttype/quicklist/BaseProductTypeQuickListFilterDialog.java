/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jdo.query.DefaultQueryProvider;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.search.BaseProductTypeQuery;
import org.nightlabs.jfire.trade.ui.store.search.ProductTypeSearchCriteriaComposite;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class BaseProductTypeQuickListFilterDialog 
extends ResizableTitleAreaDialog
{
	/**
	 * @param parentShell
	 */
	public BaseProductTypeQuickListFilterDialog(Shell parentShell) {
		super(parentShell, null);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		return new ProductTypeSearchCriteriaComposite<BaseProductTypeQuery>(
			parent, SWT.NONE, new DefaultQueryProvider(ProductType.class),
			BaseProductTypeQuery.class);
	}

}
