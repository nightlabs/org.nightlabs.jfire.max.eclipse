package org.nightlabs.jfire.voucher.admin.ui.editor.overview;

import org.nightlabs.base.ui.entity.editor.overview.IOverviewPageStatusProvider;
import org.nightlabs.base.ui.entity.editor.overview.IOverviewPageStatusProviderFactory;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class VoucherLayoutOverviewPageStatusProviderFactory 
implements IOverviewPageStatusProviderFactory 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.overview.IOverviewPageStatusProviderFactory#createStatusProvider()
	 */
	@Override
	public IOverviewPageStatusProvider createStatusProvider() {
		return new VoucherLayoutOverviewPageStatusProvider();
	}

}
