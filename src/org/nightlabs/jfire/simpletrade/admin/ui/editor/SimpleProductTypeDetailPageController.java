package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.JFireEjbUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.dao.SimpleProductTypeDAO;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeDetailPageController
//extends AbstractSimpleProductTypePageController
extends AbstractProductTypeDetailPageController<SimpleProductType>
{
	private static final Logger logger = Logger.getLogger(SimpleProductTypeDetailPageController.class);
	private static final String[] FETCH_GROUPS;
	
	static {
		List<String> fetchGroups = new LinkedList<String>();
		for (String fetchGroup : FETCH_GROUPS_DEFAULT)
			fetchGroups.add(fetchGroup);
		
		fetchGroups.add(ProductType.FETCH_GROUP_DELIVERY_CONFIGURATION);
		FETCH_GROUPS = fetchGroups.toArray(new String[fetchGroups.size()]);
	}
	
	/**
	 * @param editor
	 */
	public SimpleProductTypeDetailPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public SimpleProductTypeDetailPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}
	
	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController#retrieveProductType(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected SimpleProductType retrieveProductType(ProgressMonitor monitor) {
		return SimpleProductTypeDAO.sharedInstance().getSimpleProductType(getProductTypeID(),
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				monitor);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController#storeProductType(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected SimpleProductType storeProductType(SimpleProductType productType, ProgressMonitor monitor)
	{
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("ProductType (" + productType + ") -name before store");
				for (Map.Entry<String, String> entry : productType.getName().getTexts()) {
					logger.debug("  " + entry.getKey() + " = " + entry.getValue());
				}
			}
			SimpleTradeManager stm = JFireEjbUtil.getBean(SimpleTradeManager.class, Login.getLogin().getInitialContextProperties());
			// take the simple product type from the controller, as this is the same instance
			// which was set to the GUI elements and which they edit directly
			SimpleProductType spt = stm.storeProductType(productType, true, getEntityFetchGroups(), getEntityMaxFetchDepth());
			if (logger.isDebugEnabled()) {
				logger.debug("ProductType (" + spt + ") -name after store");
				for (Map.Entry<String, String> entry : spt.getName().getTexts()) {
					logger.debug("  " + entry.getKey() + " = " + entry.getValue());
				}
			}
			return spt;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public SimpleProductType getExtendedProductType(ProgressMonitor monitor , ProductTypeID extendedProductTypeID)
	{
		return SimpleProductTypeDAO.sharedInstance().getSimpleProductType(extendedProductTypeID,
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				monitor);
	}

}
