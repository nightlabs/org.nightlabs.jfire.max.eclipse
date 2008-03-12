/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import org.eclipse.jface.window.Window;
import org.nightlabs.base.ui.util.RCPUtil;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class AbstractProductTypeFilterSettingProvider 
implements IFilterSettingProvider 
{
	private AbstractProductTypeQuickListFilter filter;
	
	public AbstractProductTypeFilterSettingProvider(AbstractProductTypeQuickListFilter filter) {
		this.filter = filter;
	}
	
	@Override
	public IProductTypeQuickListFilter getQuickListFilter() {
		return filter;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.producttype.quicklist.IFilterSettingProvider#setFilterSettings()
	 */
	@Override
	public void setFilterSettings() {
		BaseProductTypeQuickListFilterDialog dialog = new BaseProductTypeQuickListFilterDialog(
				RCPUtil.getActiveShell());
		int returnCode = dialog.open();
		if (returnCode == Window.OK) {
			
		}
	}
 
}
