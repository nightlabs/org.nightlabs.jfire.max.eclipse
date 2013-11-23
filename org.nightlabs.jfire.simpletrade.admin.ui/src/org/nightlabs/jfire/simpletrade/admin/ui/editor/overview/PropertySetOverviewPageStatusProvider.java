package org.nightlabs.jfire.simpletrade.admin.ui.editor.overview;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.overview.AbstractProductTypeOverviewPageStatusProvider;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class PropertySetOverviewPageStatusProvider 
extends AbstractProductTypeOverviewPageStatusProvider 
{
	private static final String[] FETCH_GROUPS = new String[] {FetchPlan.DEFAULT, SimpleProductType.FETCH_GROUP_PROPERTY_SET};
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.overview.AbstractProductTypeOverviewPageStatusProvider#createStatus(org.nightlabs.jfire.store.ProductType)
	 */
	@Override
	protected IStatus createStatus(ProductType productType) 
	{
		if (productType != null && productType instanceof SimpleProductType) 
		{
			SimpleProductType simpleProductType = (SimpleProductType) productType;
			PropertySet propertySet = simpleProductType.getPropertySet();
			StringBuilder sb = new StringBuilder();
			sb.append(Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.overview.PropertySetOverviewPageStatusProvider.label.propertySet")); //$NON-NLS-1$
			String name = Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.overview.PropertySetOverviewPageStatusProvider.label.none"); //$NON-NLS-1$
			if (propertySet != null) {
				name = propertySet.getDisplayName();
				if (name == null) {
					name = ""; //$NON-NLS-1$
				}
			}
			sb.append(name);
			int severity = IStatus.OK;
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
