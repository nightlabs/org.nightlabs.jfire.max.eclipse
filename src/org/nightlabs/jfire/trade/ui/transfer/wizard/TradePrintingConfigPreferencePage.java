package org.nightlabs.jfire.trade.ui.transfer.wizard;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.config.AbstractConfigModuleController;
import org.nightlabs.jfire.base.ui.config.AbstractWorkstationConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.trade.config.TradePrintingConfigModule;

public class TradePrintingConfigPreferencePage extends AbstractWorkstationConfigModulePreferencePage {

	private Button printDeliveryNoteCheckbox;
	private Spinner printDeliveryNoteCountSpinner;
	private Button printInvoiceCheckbox;
	private Spinner printInvoiceCountSpinner;
	private Label invoiceInfoLabel;
	private Label deliveryNoteInfoLabel;
	
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
		{
			Group invoiceGroup = new Group(parent, SWT.BORDER);
			invoiceGroup.setText("Invoice printing options");
			invoiceGroup.setLayout(new GridLayout(3, false));
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.verticalIndent = 10;
			invoiceGroup.setLayoutData(gridData);
			
			Label label = new Label(invoiceGroup, SWT.WRAP);
			label.setText("This setting determines, whether and if so, how many invoices are printed after a successful payment.");
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			label.setLayoutData(gridData);
			
			XComposite wrapper = new XComposite(invoiceGroup, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER, LayoutDataMode.NONE, 2);
			new Label(wrapper, SWT.NONE).setText("Print invoice: ");
			
			printInvoiceCheckbox = new Button(wrapper, SWT.CHECK);
			wrapper = new XComposite(invoiceGroup, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER, LayoutDataMode.NONE, 2);
			new Label(wrapper, SWT.NONE).setText("Copies: ");
			printInvoiceCountSpinner = new Spinner(wrapper, SWT.BORDER);
			printInvoiceCountSpinner.setMinimum(0);
			printInvoiceCountSpinner.setMaximum(-1);
			printInvoiceCountSpinner.setDigits(0);
			printInvoiceCountSpinner.setIncrement(1);
			invoiceInfoLabel = new Label(invoiceGroup, SWT.RIGHT);
			invoiceInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			printInvoiceCheckbox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setConfigChanged(true);
					updateInfoLabels();
				}
			});
			printInvoiceCountSpinner.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setConfigChanged(true);
					printDeliveryNoteCheckbox.setSelection(printDeliveryNoteCountSpinner.getSelection() != 0);
					updateInfoLabels();
				}
			});
		}
		{
			Group deliveryNoteGroup = new Group(parent, SWT.BORDER);
			deliveryNoteGroup.setText("Delivery note printing options");
			deliveryNoteGroup.setLayout(new GridLayout(3, false));
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.verticalIndent = 10;
			deliveryNoteGroup.setLayoutData(gridData);
			
			Label label = new Label(deliveryNoteGroup, SWT.WRAP);
			label.setText("This setting determines, whether and if so, how many delivery notes are printed after a successful payment.");
			gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.horizontalSpan = 3;
			label.setLayoutData(gridData);
			
			XComposite wrapper = new XComposite(deliveryNoteGroup, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER, LayoutDataMode.NONE, 2);
			new Label(wrapper, SWT.NONE).setText("Print delivery note: ");
			
			printDeliveryNoteCheckbox = new Button(wrapper, SWT.CHECK);
			wrapper = new XComposite(deliveryNoteGroup, SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER, LayoutDataMode.NONE, 2);
			new Label(wrapper, SWT.NONE).setText("Copies: ");
			printDeliveryNoteCountSpinner = new Spinner(wrapper, SWT.BORDER);
			printDeliveryNoteCountSpinner.setMinimum(0);
			printDeliveryNoteCountSpinner.setMaximum(-1);
			printDeliveryNoteCountSpinner.setDigits(0);
			printDeliveryNoteCountSpinner.setIncrement(1);
			deliveryNoteInfoLabel = new Label(deliveryNoteGroup, SWT.RIGHT);
			deliveryNoteInfoLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	//		GridData gridData = new GridData(SWT.LEFTData(GridData.FILL_HORIZONTAL), SWT.CENTER, true, false, 2, 1);
	//		printingInfoLabel.setLayoutData(gridData);
			
			printDeliveryNoteCheckbox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setConfigChanged(true);
					updateInfoLabels();
				}
			});
			printDeliveryNoteCountSpinner.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setConfigChanged(true);
					printInvoiceCheckbox.setSelection(printInvoiceCountSpinner.getSelection() != 0);
					updateInfoLabels();
				}
			});
		}
	}
	
	private void updateInfoLabels() {
		int invoicesToBePrintedCount;
		if (!printInvoiceCheckbox.getSelection())
			invoicesToBePrintedCount = 0;
		else
			invoicesToBePrintedCount = printInvoiceCountSpinner.getSelection();
		
		if (invoicesToBePrintedCount == 0)
			invoiceInfoLabel.setText("No copy will be printed.");
		else {
			String copyText = invoicesToBePrintedCount == 1 ? "copy" : "copies";
			invoiceInfoLabel.setText(String.format("%d %s will be printed.", invoicesToBePrintedCount, copyText));
		}
		
		int deliveryNotesToBePrintedCount;
		if (!printDeliveryNoteCheckbox.getSelection())
			deliveryNotesToBePrintedCount = 0;
		else
			deliveryNotesToBePrintedCount = printDeliveryNoteCountSpinner.getSelection();
		
		if (deliveryNotesToBePrintedCount == 0)
			deliveryNoteInfoLabel.setText("No copy will be printed.");
		else {
			String copyText = deliveryNotesToBePrintedCount == 1 ? "copy" : "copies";
			deliveryNoteInfoLabel.setText(String.format("%d %s will be printed.", deliveryNotesToBePrintedCount, copyText));
		}
	}
	

	@Override
	public void updateConfigModule() {
		TradePrintingConfigModule configModule = (TradePrintingConfigModule) getConfigModuleController().getConfigModule();
		configModule.setPrintInvoiceByDefault(printInvoiceCheckbox.getSelection());
		configModule.setPrintDeliveryNoteByDefault(printDeliveryNoteCheckbox.getSelection());
		configModule.setDeliveryNoteCopyCount(printDeliveryNoteCountSpinner.getSelection());
		configModule.setInvoiceCopyCount(printInvoiceCountSpinner.getSelection());
	}

	@Override
	protected void updatePreferencePage() {
		TradePrintingConfigModule configModule = (TradePrintingConfigModule) getConfigModuleController().getConfigModule();
		printInvoiceCheckbox.setSelection(configModule.isPrintInvoiceByDefault());
		printDeliveryNoteCheckbox.setSelection(configModule.isPrintDeliveryNoteByDefault());
		printInvoiceCountSpinner.setSelection(configModule.getInvoiceCopyCount());
		printDeliveryNoteCountSpinner.setSelection(configModule.getDeliveryNoteCopyCount());
		
		updateInfoLabels();
	}

}
