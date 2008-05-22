package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorityPageControllerHelper;
import org.nightlabs.jfire.security.id.AuthorityID;
import org.nightlabs.jfire.security.id.AuthorityTypeID;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class AuthorityPageController
extends AbstractProductTypePageController<ProductType>
{

	public AuthorityPageController(EntityEditor editor) {
		super(editor);
	}

	@Override
	public ProductType getExtendedProductType(ProgressMonitor monitor, ProductTypeID extendedProductTypeID) {
		return ProductTypeDAO.sharedInstance().getProductType(extendedProductTypeID,
				FETCH_GROUPS_PRODUCT_TYPE_WITH_AUTHORITY,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS_PRODUCT_TYPE_WITH_AUTHORITY;
	}

	private static final String[] FETCH_GROUPS_PRODUCT_TYPE_WITH_AUTHORITY = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID,
		ProductTypeLocal.FETCH_GROUP_SECURING_AUTHORITY,
	};

	@Override
	protected ProductType retrieveEntity(ProgressMonitor monitor)
	{
		monitor.beginTask("Loading product type and authority", 100);

		ProductType productType = ProductTypeDAO.sharedInstance().getProductType(getProductTypeID(),
				FETCH_GROUPS_PRODUCT_TYPE_WITH_AUTHORITY,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new SubProgressMonitor(monitor, 30));

		AuthorityTypeID authorityTypeID = (AuthorityTypeID) JDOHelper.getObjectId(productType.getProductTypeLocal().getSecuringAuthorityType());
		AuthorityID authorityID = (AuthorityID) JDOHelper.getObjectId(productType.getProductTypeLocal().getSecuringAuthority());
		authorityPageControllerHelper.load(authorityTypeID, authorityID, new SubProgressMonitor(monitor, 70));

		monitor.done();

		return productType;
	}

	private AuthorityPageControllerHelper authorityPageControllerHelper = new AuthorityPageControllerHelper();

	public AuthorityPageControllerHelper getAuthorityPageControllerHelper() {
		return authorityPageControllerHelper;
	}

	@Override
	protected ProductType storeEntity(ProductType controllerObject, ProgressMonitor monitor) {
		// TODO implement storing
		return null;
	}

}
