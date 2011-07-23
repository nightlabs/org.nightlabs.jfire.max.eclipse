package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManagerRemote;
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTypeDAO;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeDetailPageController
//extends AbstractDynamicProductTypePageController
extends AbstractProductTypeDetailPageController<DynamicProductType>
{
	/**
	 * @param editor
	 */
	public DynamicProductTypeDetailPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public DynamicProductTypeDetailPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

// Not used => commenting it out (it should be deleted). Marco.
//	public static final String[] FETCH_GROUPS = new String[] {
//		FetchPlan.DEFAULT,
//		ProductType.FETCH_GROUP_NAME,
//		ProductType.FETCH_GROUP_FIELD_METADATA_MAP,
//		ProductTypeLocal.FETCH_GROUP_FIELD_METADATA_MAP,
//		ProductTypeLocal.FETCH_GROUP_NESTED_PRODUCT_TYPE_LOCALS,
//		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
//		NestedProductTypeLocal.FETCH_GROUP_THIS_PACKAGED_PRODUCT_TYPE
//	};

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS_DEFAULT;
	}

	@Override
	protected DynamicProductType retrieveProductType(ProgressMonitor monitor) {
		return DynamicProductTypeDAO.sharedInstance().getDynamicProductType(getProductTypeID(),
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				monitor);
	}

	@Override
	public  DynamicProductType getExtendedProductType(ProgressMonitor monitor ,ProductTypeID  extendedProductTypeID)
	{
		return  DynamicProductTypeDAO.sharedInstance().getDynamicProductType(extendedProductTypeID,
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				monitor);
	}

	@Override
	protected DynamicProductType storeProductType(DynamicProductType productType, ProgressMonitor monitor)
	{
		try {
			DynamicTradeManagerRemote dtm = JFireEjb3Factory.getRemoteBean(DynamicTradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			return dtm.storeDynamicProductType(productType, true, getEntityFetchGroups(), getEntityMaxFetchDepth());
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
