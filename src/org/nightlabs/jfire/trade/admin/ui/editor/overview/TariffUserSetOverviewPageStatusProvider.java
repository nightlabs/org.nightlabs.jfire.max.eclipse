package org.nightlabs.jfire.trade.admin.ui.editor.overview;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.jfire.accounting.tariffuserset.TariffUserSet;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class TariffUserSetOverviewPageStatusProvider 
extends AbstractProductTypeOverviewPageStatusProvider 
{
	private static final String[] FETCH_GROUPS = new String[] {FetchPlan.DEFAULT, ProductType.FETCH_GROUP_TARIFF_USER_SET,
		TariffUserSet.FETCH_GROUP_NAME};
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.overview.AbstractProductTypeOverviewPageStatusProvider#createStatus(org.nightlabs.jfire.store.ProductType)
	 */
	@Override
	protected IStatus createStatus(ProductType productType) 
	{
		if (productType != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.TariffUserSetOverviewPageStatusProvider.label.tariffUserSet.text")); //$NON-NLS-1$
			TariffUserSet tariffUserSet = productType.getTariffUserSet();
			String name = Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.TariffUserSetOverviewPageStatusProvider.label.none.text"); //$NON-NLS-1$
			if (tariffUserSet != null) {
				name = tariffUserSet.getName().getText();
			}
			sb.append(name);
			int severity = IStatus.OK;
//			if (tariffUserSet == null) {
//				severity = IStatus.WARNING;
//			}
			return new Status(severity, getStatusPluginId(), sb.toString());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.overview.AbstractProductTypeOverviewPageStatusProvider#getFetchGroups()
	 */
	@Override
	protected String[] getFetchGroups() {
		return FETCH_GROUPS;
	}

}
