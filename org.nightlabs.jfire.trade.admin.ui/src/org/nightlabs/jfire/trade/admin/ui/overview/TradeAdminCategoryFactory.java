package org.nightlabs.jfire.trade.admin.ui.overview;

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.swt.graphics.Image;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public interface TradeAdminCategoryFactory
extends IExecutableExtension
{
	/**
	 * returns the name of the category
	 * @return the name of the category
	 */
	String getName();
	
	/**
	 * returns the optional image of the category, may be null
	 * @return the optional image of the category
	 */
	Image getImage();
	
//	/**
//	 * returns the {@link TradeAdminCategoryViewFactory} of the category
//	 * @return the {@link TradeAdminCategoryViewFactory} of the category
//	 */
//	TradeAdminCategoryViewFactory getFactory();
	
	/**
	 * returns the index of the category
	 * @return the index of the category
	 */
	int getIndex();

	TradeAdminCategory createTradeAdminCategory();
}
