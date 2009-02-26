package org.nightlabs.jfire.trade.admin.ui.editor.endcustomertransferpolicy;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.progress.ProgressMonitor;

public class EndCustomerTransferPolicyProductTypePageController
extends AbstractProductTypePageController<ProductType>
{
	private EndCustomerTransferPolicyControllerHelper endCustomerTransferPolicyControllerHelper = new EndCustomerTransferPolicyControllerHelper();

	public EndCustomerTransferPolicyProductTypePageController(EntityEditor editor) {
		super(editor);
	}

	public EndCustomerTransferPolicyProductTypePageController(EntityEditor editor, boolean startBackgroundLoading) {
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
		endCustomerTransferPolicyControllerHelper.load(getProductTypeID(), monitor);
		return null;
	}

	@Override
	protected ProductType storeEntity(ProductType controllerObject, ProgressMonitor monitor) {
		return null;
	}

	public EndCustomerTransferPolicyControllerHelper getEndCustomerTransferPolicyControllerHelper() {
		return endCustomerTransferPolicyControllerHelper;
	}
}
