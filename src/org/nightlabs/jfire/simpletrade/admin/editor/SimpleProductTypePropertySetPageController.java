package org.nightlabs.jfire.simpletrade.admin.editor;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.simpletrade.SimpletradePlugin;
import org.nightlabs.jfire.simpletrade.admin.resource.Messages;
import org.nightlabs.jfire.simpletrade.dao.SimpleProductTypeDAO;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypePageController;
import org.nightlabs.util.Utils;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypePropertySetPageController 
extends AbstractProductTypePageController 
{
	/**
	 * @param editor
	 */
	public SimpleProductTypePropertySetPageController(EntityEditor editor) {
		super(editor);
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public SimpleProductTypePropertySetPageController(EntityEditor editor,
			boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	@Implement
	public void doLoad(IProgressMonitor monitor) 
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.simpletrade.admin.editor.SimpleProductTypePropertySetPageController.loadProductTypeMonitor.task.name"), 2); //$NON-NLS-1$
		monitor.worked(1);		
		ProductType productType = SimpleProductTypeDAO.sharedInstance().getSimpleProductType(
				getProductTypeID(), new String[] {FetchPlan.DEFAULT, SimpleProductType.FETCH_GROUP_PROPERTY_SET, PropertySet.FETCH_GROUP_DATA_FIELDS, PropertySet.FETCH_GROUP_FULL_DATA}, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
		ProductType clonedProductType = Utils.cloneSerializable(productType);
		setProductType(clonedProductType);
		monitor.worked(1);
	}

	@Implement
	public void doSave(IProgressMonitor monitor) 
	{
		SimpleProductTypeDAO.sharedInstance().storeSimpleProductType(
				(SimpleProductType)getProductType(), new ProgressMonitorWrapper(monitor));
//		monitor.beginTask("Saving Product Properties", 2);
//		monitor.worked(1);		
//		try {
//			SimpletradePlugin.getSimpleTradeManager().storeProductType(
//					(SimpleProductType)getProductType(), false, null, -1);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		monitor.worked(1);		
	}
	
	public PropertySet getPropertySet() {
		return ((SimpleProductType)getProductType()).getPropertySet();
	}

}
