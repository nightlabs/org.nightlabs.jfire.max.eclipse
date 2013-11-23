package org.nightlabs.jfire.trade.admin.ui.editor.overview;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class MoneyflowConfigOverviewPageStatusProvider 
extends AbstractProductTypeOverviewPageStatusProvider 
{
	private static final String[] FETCH_GROUPS = new String[] {FetchPlan.DEFAULT, 
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL, ProductTypeLocal.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE,
		LocalAccountantDelegate.FETCH_GROUP_NAME};
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.overview.AbstractProductTypeOverviewPageStatusProvider#createStatus(org.nightlabs.jfire.store.ProductType)
	 */
	@Override
	protected IStatus createStatus(ProductType productType) 
	{
		if (productType != null) {
			StringBuilder sb = new StringBuilder();
			LocalAccountantDelegate localAccountantDelegate = productType.getProductTypeLocal().getLocalAccountantDelegate();
			sb.append(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.MoneyflowConfigOverviewPageStatusProvider.label.localAccountantDelegate.text")); //$NON-NLS-1$
			String name = Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.MoneyflowConfigOverviewPageStatusProvider.label.none.text"); //$NON-NLS-1$
			if (localAccountantDelegate != null) {
				name = localAccountantDelegate.getName().getText();
			}
			sb.append(name);
			
			int severity = IStatus.OK;
			if (localAccountantDelegate == null)
				severity = IStatus.WARNING;
			
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
