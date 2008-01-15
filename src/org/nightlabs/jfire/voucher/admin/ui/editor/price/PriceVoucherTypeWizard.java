package org.nightlabs.jfire.voucher.admin.ui.editor.price;


import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.admin.ui.createvouchertype.*;
import org.nightlabs.jfire.voucher.store.VoucherType;


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
		
		
		if (parentVoucherTypeID == null)
			throw new IllegalArgumentException("parentVoucherTypeID must not be null!"); //$NON-NLS-1$
	}

	private SelectVoucherPriceConfigPage selectVoucherPriceConfigPage;

	@Override
	public void addPages()
	{

		selectVoucherPriceConfigPage = new SelectVoucherPriceConfigPage(parentVoucherTypeID);
		addPage(selectVoucherPriceConfigPage);

	}

	
	@Override
	@Implement
	public boolean performFinish()
	{

		switch (selectVoucherPriceConfigPage.getMode()) {
			case INHERIT:
				voucherType.setPackagePriceConfig( selectVoucherPriceConfigPage.getInheritedPriceConfig());
				
				break;
			case CREATE:
				
				voucherType.setPackagePriceConfig(selectVoucherPriceConfigPage.createPriceConfig());
			
				break;
			case SELECT:
				
				voucherType.setPackagePriceConfig(selectVoucherPriceConfigPage.getSelectedPriceConfig());
				break;
			default:
				throw new IllegalStateException("What's that?!"); //$NON-NLS-1$
		}


		
		return true;
	}

}
