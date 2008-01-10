package org.nightlabs.jfire.voucher.admin.ui.editor.price;


import java.util.HashMap;
import java.util.Map;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.trade.admin.ui.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.voucher.accounting.VoucherPriceConfig;
import org.nightlabs.jfire.voucher.admin.ui.priceconfig.CurrencyAmountTable;
import org.nightlabs.jfire.voucher.admin.ui.priceconfig.IPriceConfigValueChangedListener;
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
		super(page, parent, style, "Price Config");

		AddCurrencyConfigAction addCurrencyConfigAction = new AddCurrencyConfigAction();
		getToolBarManager().add(addCurrencyConfigAction);

		
		RemoveCurrencyConfigAction removeCurrencyConfigAction = new RemoveCurrencyConfigAction();
		getToolBarManager().add(removeCurrencyConfigAction);



		currencyAmountTable = new CurrencyAmountTable(getContainer(),false);


		currencyAmountTable.addPriceConfigValueChangedListener(new IPriceConfigValueChangedListener() {
			public void priceValueChanged()
			{
				//MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "test", "test");
				markDirty();
			}
		});

		MenuManager menuManager = new MenuManager();
		menuManager.add(addCurrencyConfigAction);
		menuManager.add(removeCurrencyConfigAction);
		
		
		Menu menu = menuManager.createContextMenu(currencyAmountTable.getTable());
		
		
		//currencyAmountTable.getTable().setMenu(menu);
		
		
		getContainer().setMenu(menu);
		
		updateToolBarManager();
		
		

	}

	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);

		Map<Currency, Long> map = currencyAmountTable.getMap();

		for (Map.Entry<Currency, Long> me : map.entrySet()) {
			voucherconfig.setPrice(me.getKey(), me.getValue());

		}




	}



	public void setVoucherType(VoucherType voucher)
	{

		voucherconfig = (VoucherPriceConfig) voucher.getPackagePriceConfig();

		Map<Currency, Long> map = new HashMap<Currency, Long>(voucherconfig.getPrices());

		currencyAmountTable.setMap(map);

		
		
	}



	protected void addCurrencyPressed() 
	{

		currencyAmountTable.addCurrency();
	}

	
	
	protected void removeCurrencyPressed() 
	{

		currencyAmountTable.removeCurrency();
	}
	
	
	
	class AddCurrencyConfigAction
	extends Action 
	{
		public AddCurrencyConfigAction() {
			super();
			setId(AddCurrencyConfigAction.class.getName());
			//setImageDescriptor(SharedImages.getSharedImageDescriptor(
			//	TradeAdminPlugin.getDefault(), AssignNewCurrencyConfigAction.class, "AssignPriceConfig")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection.AssignNewPriceConfigAction.toolTipText")); //$NON-NLS-1$
			setText("Add Currency");
		}

		@Override
		public void run() {
			//assignNewPressed();
			addCurrencyPressed(); 
			//MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "test", "test");

		}		
	}



	class RemoveCurrencyConfigAction
	extends Action 
	{
		public RemoveCurrencyConfigAction() {
			super();
			setId(RemoveCurrencyConfigAction.class.getName());
			//setImageDescriptor(SharedImages.getSharedImageDescriptor(
			//	TradeAdminPlugin.getDefault(), AssignNewCurrencyConfigAction.class, "AssignPriceConfig")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.AbstractGridPriceConfigSection.AssignNewPriceConfigAction.toolTipText")); //$NON-NLS-1$
			setText("Remove Currency");
		}

		@Override
		public void run() {
			//assignNewPressed();
			removeCurrencyPressed(); 
			//MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "test", "test");

		}		
	}







}

