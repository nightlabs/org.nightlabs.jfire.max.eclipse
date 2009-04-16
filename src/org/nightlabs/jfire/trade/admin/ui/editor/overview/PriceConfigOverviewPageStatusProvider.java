package org.nightlabs.jfire.trade.admin.ui.editor.overview;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IPackagePriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.PriceConfig;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class PriceConfigOverviewPageStatusProvider 
extends AbstractProductTypeOverviewPageStatusProvider 
{
	private static final String[] FETCH_GROUPS = new String[] {FetchPlan.DEFAULT, 
		ProductType.FETCH_GROUP_INNER_PRICE_CONFIG,
		ProductType.FETCH_GROUP_PACKAGE_PRICE_CONFIG,
		PriceConfig.FETCH_GROUP_NAME};
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.overview.AbstractProductTypeOverviewPageStatusProvider#createStatus(org.nightlabs.jfire.store.ProductType)
	 */
	@Override
	protected IStatus createStatus(ProductType productType) 
	{		
		if (productType != null) {
			String separator = "\n"; //$NON-NLS-1$
			StringBuilder sb = new StringBuilder();
			
			IInnerPriceConfig innerPriceConfig = productType.getInnerPriceConfig();
			String innerPriceConfigName = Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.PriceConfigOverviewPageStatusProvider.label.none.text"); //$NON-NLS-1$
			sb.append(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.PriceConfigOverviewPageStatusProvider.label.innerPriceConfig.text"));			 //$NON-NLS-1$
			if (innerPriceConfig != null) {
				innerPriceConfigName = innerPriceConfig.getName().getText();
			}
			sb.append(innerPriceConfigName);

			sb.append(separator);
			
			IPackagePriceConfig packagePriceConfig = productType.getPackagePriceConfig();
			String packagePriceConfigName = Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.PriceConfigOverviewPageStatusProvider.label.none.text"); //$NON-NLS-1$
			sb.append(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.PriceConfigOverviewPageStatusProvider.label.packagePriceConfig.text"));			 //$NON-NLS-1$
			if (packagePriceConfig != null) {
				packagePriceConfigName = packagePriceConfig.getName().getText();
			}
			sb.append(packagePriceConfigName);
			
			int severity = IStatus.OK;
			if (innerPriceConfig == null && packagePriceConfig == null) {
				severity = IStatus.WARNING;
			}
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
