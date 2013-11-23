package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import org.nightlabs.jfire.store.id.ProductID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface IProductIDParser 
{
	/**
	 * Returns null if the prdouctIDString is valid, or a string describing what was wrong if not valid.
	 * 
	 * @param productIDString the string which represents the ProductID to check
	 * @param monitor the {@link ProgressMonitor} to display the progress
	 * @return null if the string is valid, or a string describing what was wrong if not valid
	 */
	ProductID getProductID(String productIDString, ProgressMonitor monitor);
}
