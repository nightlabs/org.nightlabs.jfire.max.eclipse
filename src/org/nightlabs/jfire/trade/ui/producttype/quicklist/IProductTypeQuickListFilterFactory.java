/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import org.eclipse.core.runtime.IExecutableExtension;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IProductTypeQuickListFilterFactory extends IExecutableExtension {
	
	/**
	 * Create a new {@link IProductTypeQuickListFilter}.
	 * 
	 * @return A new {@link IProductTypeQuickListFilter}.
	 */
	IProductTypeQuickListFilter createProductTypeQuickListFilter();

	/**
	 * @return The id of this {@link IProductTypeQuickListFilterFactory}.
	 */
	String getId();
	
	/**
	 * Returns the index of this {@link IProductTypeQuickListFilterFactory},
	 * it is used to sort the factories.
	 * 
	 * @return The index of this {@link IProductTypeQuickListFilterFactory}.
	 */
	int getIndex();
}
