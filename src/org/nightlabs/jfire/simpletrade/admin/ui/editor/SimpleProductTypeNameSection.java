package org.nightlabs.jfire.simpletrade.admin.ui.editor;

import javax.jdo.JDOHelper;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.simpletrade.admin.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeNameSection;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class SimpleProductTypeNameSection
extends AbstractProductTypeNameSection
{
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public SimpleProductTypeNameSection(IFormPage page, Composite parent, int style) {
		super(
				page, parent, style,
				Messages.getString("org.nightlabs.jfire.simpletrade.admin.ui.editor.SimpleProductTypeNameSection.title")); //$NON-NLS-1$
	}
	
	@Override
	protected ProductType retrieveExtendedProductType(ProductType type, ProgressMonitor monitor)
	{
		return ProductTypeDAO.sharedInstance().getProductType(
				(ProductTypeID)JDOHelper.getObjectId(type),
				FETCH_GROUP_NAME,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}

}
