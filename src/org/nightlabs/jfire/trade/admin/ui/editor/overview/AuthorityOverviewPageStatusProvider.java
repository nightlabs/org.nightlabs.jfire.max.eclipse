package org.nightlabs.jfire.trade.admin.ui.editor.overview;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.entity.editor.overview.AbstractOverviewPageStatusProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.security.Authority;
import org.nightlabs.jfire.security.dao.AuthorityDAO;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeEditorInput;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class AuthorityOverviewPageStatusProvider 
extends AbstractOverviewPageStatusProvider 
{
	private static final String[] FETCH_GROUPS_PRODUCT_TYPE = new String[] {FetchPlan.DEFAULT, 
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL};

	private static final String[] FETCH_GROUPS_AUTHORITY = new String[] {FetchPlan.DEFAULT, 
		Authority.FETCH_GROUP_NAME};
	
	private IStatus status;
	
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.overview.IOverviewPageStatusProvider#getStatus()
	 */
	@Override
	public IStatus getStatus() {
		return status;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.overview.IOverviewPageStatusProvider#isResolveStatusDeferred()
	 */
	@Override
	public boolean isResolveStatusDeferred() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.overview.IOverviewPageStatusProvider#resolveStatus(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	public void resolveStatus(ProgressMonitor monitor) {
		if (getProductTypeID() != null) {
			ProductType productType = ProductTypeDAO.sharedInstance().getProductType(getProductTypeID(), 
					FETCH_GROUPS_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 50));
			ProductTypeLocal productTypeLocal = productType.getProductTypeLocal();
			Authority authority = null;
			if (productTypeLocal.getSecuringAuthorityID() != null) {
				authority = AuthorityDAO.sharedInstance().getAuthority(productTypeLocal.getSecuringAuthorityID(), 
						FETCH_GROUPS_AUTHORITY, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 50));				
			}
			StringBuilder sb = new StringBuilder();
			sb.append("Authority: ");
			String authorityName = null;
			if (authority != null) {
				authorityName = authority.getName().getText();	
			}
			sb.append(authorityName != null ? authorityName : "NONE");
//			int severity = authorityName != null ? IStatus.OK : IStatus.WARNING;
			int severity = IStatus.OK;
			status = new Status(severity, getStatusPluginId(), sb.toString());
		}
	}

	protected ProductTypeID getProductTypeID() {
		IEditorInput editorInput = getEntityEditor().getEditorInput();
		if (editorInput instanceof ProductTypeEditorInput) {
			ProductTypeEditorInput input = (ProductTypeEditorInput) editorInput;
			return input.getJDOObjectID();
		}
		return null;
	}
		
}
