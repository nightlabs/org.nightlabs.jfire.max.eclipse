package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.simpletrade.SimpleTradeManager;
import org.nightlabs.jfire.simpletrade.SimpleTradeManagerUtil;
import org.nightlabs.jfire.simpletrade.dao.SimpleProductTypeDAO;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeDetailPageController 
//extends AbstractSimpleProductTypePageController 
extends AbstractProductTypeDetailPageController
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

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController#retrieveProductType(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected ProductType retrieveProductType(IProgressMonitor monitor) {
		return SimpleProductTypeDAO.sharedInstance().getSimpleProductType(getProductTypeID(), 
				FETCH_GROUPS, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new ProgressMonitorWrapper(monitor));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeDetailPageController#storeProductType(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void storeProductType(IFormPage page, IProgressMonitor monitor) 
	{
		try {
			SimpleTradeManager stm = SimpleTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			// take the simple product type from the controller, as this is the same instance
			// which was set to the GUI elements and which they edit directly
			SimpleProductType spt = (SimpleProductType) getProductType();								
			stm.storeProductType(spt, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);			
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
