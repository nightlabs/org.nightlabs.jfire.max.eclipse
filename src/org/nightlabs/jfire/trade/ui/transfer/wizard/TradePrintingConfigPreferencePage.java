package org.nightlabs.jfire.trade.ui.transfer.wizard;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractWorkstationConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.trade.config.TradePrintingConfigModule;

public class TradePrintingConfigPreferencePage extends AbstractWorkstationConfigModulePreferencePage {

	
	private AutomaticPrintingOptionsGroup invoiceGroup;
	private AutomaticPrintingOptionsGroup deliveryNoteGroup;

	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new AbstractConfigModuleController(this) {
			@Override
			public Set<String> getConfigModuleFetchGroups() {
				return getCommonConfigModuleFetchGroups();
			}
		
			@Override
			public Class<? extends ConfigModule> getConfigModuleClass() {
				return TradePrintingConfigModule.class;
			}
		};
	}

	@Override
	protected void createPreferencePage(Composite parent) {
		String desc = "This setting determines, whether and if so, how many invoices are printed by default after a successful payment.\nYou can change this setting for each individual payment.";
		invoiceGroup = new AutomaticPrintingOptionsGroup(parent, "Invoice printing options", "invoice", desc);
		desc = "This setting determines, whether and if so, how many delivery notes are printed by default after a successful payment.\nYou can change this setting for each individual delivery";
		deliveryNoteGroup = new AutomaticPrintingOptionsGroup(parent, "Delivery note printing options", "delivery note", desc);
	}
	
	@Override
	public void updateConfigModule() {
		TradePrintingConfigModule configModule = (TradePrintingConfigModule) getConfigModuleController().getConfigModule();
		configModule.setPrintInvoiceByDefault(invoiceGroup.getDoPrint());
		configModule.setPrintDeliveryNoteByDefault(deliveryNoteGroup.getDoPrint());
		configModule.setDeliveryNoteCopyCount(deliveryNoteGroup.getEnteredPrintCount());
		configModule.setInvoiceCopyCount(invoiceGroup.getEnteredPrintCount());
	}

	@Override
	protected void updatePreferencePage() {
		TradePrintingConfigModule configModule = (TradePrintingConfigModule) getConfigModuleController().getConfigModule();
		invoiceGroup.setDoPrint(configModule.isPrintInvoiceByDefault());
		deliveryNoteGroup.setDoPrint(configModule.isPrintDeliveryNoteByDefault());
		invoiceGroup.setEnteredPrintCount(configModule.getInvoiceCopyCount());
		deliveryNoteGroup.setEnteredPrintCount(configModule.getDeliveryNoteCopyCount());
	}

}
