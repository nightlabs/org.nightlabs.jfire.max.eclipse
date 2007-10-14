package org.nightlabs.jfire.voucher.admin.editor;

import javax.jdo.JDOHelper;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.editor.AbstractProductTypeNameSection;
import org.nightlabs.jfire.voucher.admin.resource.Messages;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherTypeNameSection 
extends AbstractProductTypeNameSection 
{
	/**
	 * @param page
	 * @param parent
	 * @param style
	 */
	public VoucherTypeNameSection(IFormPage page, Composite parent, int style) {
		super(page, parent, style, Messages.getString("org.nightlabs.jfire.voucher.admin.editor.VoucherTypeNameSection.title")); //$NON-NLS-1$
	}

	@Override
	protected ProductType retrieveExtendedProductType(ProductType type, ProgressMonitor monitor) {
		return VoucherTypeDAO.sharedInstance().getVoucherType(
				(ProductTypeID) JDOHelper.getObjectId(type), 
				FETCH_GROUP_NAME, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
				monitor);
	}

}
