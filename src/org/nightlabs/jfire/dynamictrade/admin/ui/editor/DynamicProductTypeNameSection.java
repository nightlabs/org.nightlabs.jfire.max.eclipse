package org.nightlabs.jfire.dynamictrade.admin.ui.editor;

import javax.jdo.JDOHelper;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTypeDAO;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeNameSection;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DynamicProductTypeNameSection
extends AbstractProductTypeNameSection
{
	public DynamicProductTypeNameSection(IFormPage page, Composite parent) {
		super(page, parent);
	}

	public DynamicProductTypeNameSection(IFormPage page, Composite parent, int style) {
		super(page, parent, style);
	}
	
	@Override
	protected ProductType retrieveExtendedProductType(ProductType type, ProgressMonitor monitor) {
		return DynamicProductTypeDAO.sharedInstance().getDynamicProductType(
				(ProductTypeID) JDOHelper.getObjectId(type),
				FETCH_GROUP_NAME,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}
}
