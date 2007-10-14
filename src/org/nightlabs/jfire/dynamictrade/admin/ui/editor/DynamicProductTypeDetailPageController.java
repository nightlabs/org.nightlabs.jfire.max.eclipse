package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManager;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManagerUtil;
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTypeDAO;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.NestedProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeDetailPageController 
//extends AbstractDynamicProductTypePageController 
extends AbstractProductTypeDetailPageController
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
		ProductType.FETCH_GROUP_NESTED_PRODUCT_TYPES,
		NestedProductType.FETCH_GROUP_THIS_PACKAGED_PRODUCT_TYPE
	};

	@Override
	protected ProductType retrieveProductType(IProgressMonitor monitor) {
		return DynamicProductTypeDAO.sharedInstance().getDynamicProductType(getProductTypeID(), 
				FETCH_GROUPS_DEFAULT,  
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
				new ProgressMonitorWrapper(monitor));
	}

	@Override
	protected void storeProductType(IFormPage page, IProgressMonitor monitor) 
	{
		try {
			DynamicTradeManager stm = DynamicTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			stm.storeDynamicProductType((DynamicProductType) getProductType(), false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
}
