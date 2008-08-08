package org.nightlabs.jfire.trade.ui.transfer.wizard;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractWorkstationConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.trade.config.TradePrintingConfigModule;
import org.nightlabs.jfire.trade.ui.resource.Messages;

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
		String desc = Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.TradePrintingConfigPreferencePage.description.invoicePrintingOptions"); //$NON-NLS-1$
		invoiceGroup = new AutomaticPrintingOptionsGroup(parent, Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.TradePrintingConfigPreferencePage.group.invoicePrintingOptions.title"), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.TradePrintingConfigPreferencePage.invoice"), desc); //$NON-NLS-1$ //$NON-NLS-2$
		desc = Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.TradePrintingConfigPreferencePage.description.deliveryNotePrintingOptions"); //$NON-NLS-1$
		deliveryNoteGroup = new AutomaticPrintingOptionsGroup(parent, Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.TradePrintingConfigPreferencePage.group.deliveryNotePrintingOptions"), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.TradePrintingConfigPreferencePage.deliveryNote"), desc); //$NON-NLS-1$ //$NON-NLS-2$
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
