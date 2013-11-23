package org.nightlabs.jfire.trade.admin.ui.editor.endcustomerreplicationpolicy;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.progress.ProgressMonitor;

public class EndCustomerReplicationPolicyProductTypePageController
extends AbstractProductTypePageController<ProductType>
{
	private EndCustomerReplicationPolicyControllerHelper endCustomerReplicationPolicyControllerHelper = new EndCustomerReplicationPolicyControllerHelper();

	public EndCustomerReplicationPolicyProductTypePageController(EntityEditor editor) {
		super(editor);
	}

	public EndCustomerReplicationPolicyProductTypePageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	@Override
	public ProductType getExtendedProductType(ProgressMonitor monitor, ProductTypeID extendedProductTypeID) {
		return null;
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return null;
	}

	@Override
	protected ProductType retrieveEntity(ProgressMonitor monitor) {
		endCustomerReplicationPolicyControllerHelper.load(getProductTypeID(), monitor);
		return null;
	}

	@Override
	protected ProductType storeEntity(ProductType controllerObject, ProgressMonitor monitor) {
		return null;
	}

	public EndCustomerReplicationPolicyControllerHelper getEndCustomerReplicationPolicyControllerHelper() {
		return endCustomerReplicationPolicyControllerHelper;
	}
}
