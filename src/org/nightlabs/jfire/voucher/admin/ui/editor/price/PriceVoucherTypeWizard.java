package org.nightlabs.jfire.voucher.admin.ui.editor.price;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.priceconfig.FetchGroupsPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IPackagePriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.PriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.admin.ui.createvouchertype.SelectVoucherPriceConfigPage;
import org.nightlabs.jfire.voucher.dao.VoucherPriceConfigDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author fitas [at] NightLabs [dot] de
 *
 */
public class PriceVoucherTypeWizard
extends DynamicPathWizard
{
	private ProductTypeID parentVoucherTypeID;
	private VoucherType voucherType;
	
	public PriceVoucherTypeWizard(ProductTypeID parentVoucherTypeID ,VoucherType   vouchertype)
	{
		this.voucherType =  vouchertype;
		this.parentVoucherTypeID = parentVoucherTypeID;
	}

	private SelectVoucherPriceConfigPage selectVoucherPriceConfigPage;

	@Override
	public void addPages()
	{
		selectVoucherPriceConfigPage = new SelectVoucherPriceConfigPage(parentVoucherTypeID);
		addPage(selectVoucherPriceConfigPage);
	}
	
	@Override
	public boolean performFinish()
	{
		IPackagePriceConfig packagePriceConfig = null;
		boolean inherit = false;
		switch (selectVoucherPriceConfigPage.getMode()) {
			case INHERIT:
				packagePriceConfig = selectVoucherPriceConfigPage.getInheritedPriceConfig();
				inherit = true;
				break;
			case CREATE:
				packagePriceConfig = selectVoucherPriceConfigPage.createPriceConfig();
				break;
			case SELECT:
				packagePriceConfig = selectVoucherPriceConfigPage.getSelectedPriceConfig();
				break;
			case NONE:
				break;
			default:
				throw new IllegalStateException("What's that?!"); //$NON-NLS-1$
		}
		
		if (packagePriceConfig != null && JDOHelper.isDetached(packagePriceConfig)) {
			packagePriceConfig = VoucherPriceConfigDAO.sharedInstance().getVoucherPriceConfig((PriceConfigID)JDOHelper.getObjectId(packagePriceConfig) ,
					new String[] { FetchPlan.DEFAULT, FetchGroupsPriceConfig.FETCH_GROUP_EDIT, PriceConfig.FETCH_GROUP_NAME},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		}
		voucherType.setPackagePriceConfig(packagePriceConfig);
		voucherType.getFieldMetaData(ProductType.FieldName.packagePriceConfig).setValueInherited(inherit);
		
		return true;
	}

}
