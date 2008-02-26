package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import java.util.LinkedList;
import java.util.List;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.SimpleTradeManagerUtil;
import org.nightlabs.jfire.simpletrade.dao.SimpleProductTypeDAO;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;
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
			SimpleTradeManager stm = SimpleTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			// take the simple product type from the controller, as this is the same instance
			// which was set to the GUI elements and which they edit directly
			return stm.storeProductType(productType, true, getEntityFetchGroups(), getEntityMaxFetchDepth());
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

// Hello Fitas,
// you checked-in code that did not compile. Therefore, I commented out the following method. Now it compiles again.
//
// And please format your code nicely. Tons of empty lines are ugly. Thus, I removed the empty lines below (and Daniel beautified some other classes you changed, as well).
//
// TODO Please remove this comment when you continue working here.
//
// Marco :-)
	protected SimpleProductType getExtendedProductType(ProgressMonitor monitor)
	{
		return SimpleProductTypeDAO.sharedInstance().getSimpleProductType(getExtendedProductTypeID(),
				getEntityFetchGroups(),
				getEntityMaxFetchDepth(),
				monitor);	
		
	}

}
