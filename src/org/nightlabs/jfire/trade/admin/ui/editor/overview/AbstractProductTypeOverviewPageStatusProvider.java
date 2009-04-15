package org.nightlabs.jfire.trade.admin.ui.editor.overview;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.entity.editor.overview.AbstractOverviewPageStatusProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.ProductTypeEditorInput;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public abstract class AbstractProductTypeOverviewPageStatusProvider 
extends AbstractOverviewPageStatusProvider 
{
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
			ProductType productType = ProductTypeDAO.sharedInstance().getProductType(getProductTypeID(), getFetchGroups(), 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			status = createStatus(productType);
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
	
	protected abstract String[] getFetchGroups();
	
	protected abstract IStatus createStatus(ProductType productType);
}
