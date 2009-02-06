package org.nightlabs.jfire.trade.admin.ui.editor.authority;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.inheritance.Inheritable;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.admin.ui.editor.authority.AuthorityPageControllerHelper;
import org.nightlabs.jfire.base.admin.ui.editor.authority.InheritedSecuringAuthorityResolver;
import org.nightlabs.jfire.security.id.AuthorityID;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
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
		ProductTypeLocal.FETCH_GROUP_FIELD_METADATA_MAP
	};

	private ProductType loadProductType(ProgressMonitor monitor) {
		return ProductTypeDAO.sharedInstance().getProductType(getProductTypeID(),
				FETCH_GROUPS_PRODUCT_TYPE_WITH_AUTHORITY,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}

	@Override
	protected ProductType retrieveEntity(ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.authority.AuthorityPageController.job.loadAuthority"), 100); //$NON-NLS-1$
		try {
			ProductType productType = loadProductType(new SubProgressMonitor(monitor, 30));
			setControllerObject(productType);
			authorityPageControllerHelper.load(productType.getProductTypeLocal(), new SubProgressMonitor(monitor, 70));

			return productType;
		} finally {
			monitor.done();
		}
	}

	private AuthorityPageControllerHelper authorityPageControllerHelper = new AuthorityPageControllerHelper() {
		@Override
		protected InheritedSecuringAuthorityResolver createInheritedSecuringAuthorityResolver() {
			return new InheritedSecuringAuthorityResolver() {
				@Override
				public AuthorityID getInheritedSecuringAuthorityID(ProgressMonitor monitor) {
					if (getControllerObject().getExtendedProductTypeID() == null)
						return null;

					ProductType extendedProductType = getExtendedProductType(
							monitor,
							getControllerObject().getExtendedProductTypeID());

					return extendedProductType.getProductTypeLocal().getSecuringAuthorityID();
				}
//
//				@Override
//				public boolean isInitiallyInherited(ProgressMonitor monitor) {
//					return getControllerObject().getProductTypeLocal().getFieldMetaData(FieldName.securingAuthorityID).isValueInherited();
//				}
				
				@Override
				public Inheritable retrieveSecuredObjectInheritable(ProgressMonitor monitor) {
					return loadProductType(monitor).getProductTypeLocal();
				}
			};
		}
	};

	public AuthorityPageControllerHelper getAuthorityPageControllerHelper() {
		return authorityPageControllerHelper;
	}

	@Override
	protected ProductType storeEntity(ProductType controllerObject, ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.authority.AuthorityPageController.job.saveAuthority"), 100); //$NON-NLS-1$
		authorityPageControllerHelper.store(new SubProgressMonitor(monitor, 70));
		return getControllerObject();
	}

}
