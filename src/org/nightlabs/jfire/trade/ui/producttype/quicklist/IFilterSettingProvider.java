/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface IFilterSettingProvider 
{
	void setFilterSettings();
	
	IProductTypeQuickListFilter getQuickListFilter();
}
