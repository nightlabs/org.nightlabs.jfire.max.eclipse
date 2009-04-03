package org.nightlabs.jfire.trade.admin.ui.tariffuserset;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.tariffuserset.TariffUserSet;
import org.nightlabs.jfire.entityuserset.id.EntityUserSetID;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class TariffUserSetPageController<ProductTypeType> 
extends AbstractProductTypePageController<ProductType>
{
	private TariffUserSetPageControllerHelper tariffUserSetPageControllerHelper;
	
	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public TariffUserSetPageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	/**
	 * @param editor
	 */
	public TariffUserSetPageController(EntityEditor editor) {
		super(editor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#getEntityFetchGroups()
	 */
	@Override
	protected String[] getEntityFetchGroups() {
		return new String[] {ProductType.FETCH_GROUP_TARIFF_USER_SET};
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#retrieveEntity(org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected ProductType retrieveEntity(ProgressMonitor monitor) 
	{
		ProductType pt = ProductTypeDAO.sharedInstance().getProductType(getProductTypeID(), getEntityFetchGroups(), 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		TariffUserSet tariffUserSet = pt.getTariffUserSet();
		EntityUserSetID entityUserSetID = (EntityUserSetID) JDOHelper.getObjectId(tariffUserSet);
		getTariffUserSetPageControllerHelper().load(entityUserSetID, null, new SubProgressMonitor(monitor, 50));
		return pt;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController#storeEntity(java.lang.Object, org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected ProductType storeEntity(ProductType controllerObject, ProgressMonitor monitor) {
		getTariffUserSetPageControllerHelper().store(monitor);
		return getControllerObject();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController#getExtendedProductType(org.nightlabs.progress.ProgressMonitor, org.nightlabs.jfire.store.id.ProductTypeID)
	 */
	@Override
	public ProductType getExtendedProductType(ProgressMonitor monitor, ProductTypeID extendedProductTypeID) {
		return ProductTypeDAO.sharedInstance().getProductType(extendedProductTypeID, getEntityFetchGroups(), 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}
	
	public TariffUserSetPageControllerHelper getTariffUserSetPageControllerHelper() {
		if (tariffUserSetPageControllerHelper == null) {
			tariffUserSetPageControllerHelper = new TariffUserSetPageControllerHelper();
		}
		return tariffUserSetPageControllerHelper;
	}
}
