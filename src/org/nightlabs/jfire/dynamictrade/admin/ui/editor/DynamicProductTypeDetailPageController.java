package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManager;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManagerUtil;
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTypeDAO;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.NestedProductTypeLocal;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
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

	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_FIELD_METADATA_MAP,
		ProductTypeLocal.FETCH_GROUP_FIELD_METADATA_MAP,
		ProductTypeLocal.FETCH_GROUP_NESTED_PRODUCT_TYPE_LOCALS,
		ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
		NestedProductTypeLocal.FETCH_GROUP_THIS_PACKAGED_PRODUCT_TYPE
	};

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
			DynamicTradeManager stm = DynamicTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			return stm.storeDynamicProductType(productType, true, getEntityFetchGroups(), getEntityMaxFetchDepth());
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
}
