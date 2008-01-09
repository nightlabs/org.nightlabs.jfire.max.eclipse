package org.nightlabs.jfire.voucher.admin.ui.editor.price;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.voucher.accounting.VoucherPriceConfig;
import org.nightlabs.jfire.voucher.admin.ui.priceconfig.CurrencyAmountTable;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author fitas [at] NightLabs [dot] de
 *
 */

public class VoucherPriceConfigSection 
extends ToolBarSectionPart 
{
	private CurrencyAmountTable currencyAmountTable;
	private VoucherPriceConfig voucherconfig;
	
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 */
	public VoucherPriceConfigSection(IFormPage page, Composite parent, int style) {
		super(page, parent, style, "PriceConfig");
		currencyAmountTable = new CurrencyAmountTable(getContainer(),false);
	
	
	}
	
	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		
		Map<Currency, Long> map = currencyAmountTable.getMap();
		
		
		for(Map.Entry<Currency, Long> me : map.entrySet()) {
			voucherconfig.setPrice(me.getKey(), me.getValue());
		}
	
	
	}
	
	
	
	public void setVoucherType(VoucherType voucher)
	{
	
     voucherconfig = (VoucherPriceConfig) voucher.getPackagePriceConfig();
     
     
     Map<Currency, Long> map = new TreeMap<Currency, Long>(voucherconfig.getPrices());
          
     
	 currencyAmountTable.setMap(map);

	
	
	}

}
