package org.nightlabs.jfire.trade.admin.ui.editor.overview;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class ProductTypeDetailPageOverviewStatusProvider 
extends AbstractProductTypeOverviewPageStatusProvider
{
	private static final String[] FETCH_GROUPS = new String[] {FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL, ProductTypeLocal.FETCH_GROUP_NESTED_PRODUCT_TYPE_LOCALS,
		ProductType.FETCH_GROUP_OWNER, ProductType.FETCH_GROUP_VENDOR, LegalEntity.FETCH_GROUP_PERSON};
	
	@Override
	protected String[] getFetchGroups() {
		return FETCH_GROUPS;
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.overview.IOverviewPageStatusProvider#resolveStatus(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	public IStatus createStatus(ProductType productType) 
	{
		StringBuilder sb = new StringBuilder();
		if (productType != null) {
			String separator = "\n"; //$NON-NLS-1$
			sb.append(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.ProductTypeDetailPageOverviewStatusProvider.label.name.text")); //$NON-NLS-1$
			sb.append(productType.getName().getText());
			sb.append(separator);
			sb.append(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.ProductTypeDetailPageOverviewStatusProvider.label.saleStatus.text")); //$NON-NLS-1$
			sb.append(getSaleStatusString(productType));
			sb.append(separator);
			sb.append(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.ProductTypeDetailPageOverviewStatusProvider.label.amountNestedProductTyps.text")); //$NON-NLS-1$
			sb.append(productType.getProductTypeLocal().getNestedProductTypeLocals().size());
			sb.append(separator);
			sb.append(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.ProductTypeDetailPageOverviewStatusProvider.label.owner.text")); //$NON-NLS-1$
			sb.append(productType.getOwner().getPerson().getDisplayName());
			sb.append(separator);
			sb.append(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.ProductTypeDetailPageOverviewStatusProvider.label.vendor.text")); //$NON-NLS-1$
			sb.append(productType.getVendor().getPerson().getDisplayName());
		}
		return new Status(IStatus.OK, getStatusPluginId(), sb.toString());
	}

	protected String getSaleStatusString(ProductType productType) {
		if (productType.isClosed()) {
			return Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.ProductTypeDetailPageOverviewStatusProvider.label.closed"); //$NON-NLS-1$
		}
		else if (productType.isSaleable()) {
			return Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.ProductTypeDetailPageOverviewStatusProvider.label.saleable"); //$NON-NLS-1$
		}
		else if (productType.isPublished()) {
			return Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.ProductTypeDetailPageOverviewStatusProvider.label.published"); //$NON-NLS-1$
		}
		else if (productType.isConfirmed()) {
			return Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.overview.ProductTypeDetailPageOverviewStatusProvider.label.confirmed"); //$NON-NLS-1$
		}
		return ""; //$NON-NLS-1$
	}	
}
