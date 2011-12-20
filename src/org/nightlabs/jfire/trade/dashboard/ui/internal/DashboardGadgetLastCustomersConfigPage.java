package org.nightlabs.jfire.trade.dashboard.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashbardGadgetConfigPage;
import org.nightlabs.jfire.dashboard.DashboardGadgetLayoutEntry;
import org.nightlabs.jfire.trade.dashboard.DashboardGadgetLastCustomersConfig;
import org.nightlabs.jfire.trade.dashboard.DashboardLayoutConfigModuleInitialiser;

/**
 * WizardPage to configure properties of "Last Customers" dashboard gadget.
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class DashboardGadgetLastCustomersConfigPage extends AbstractDashbardGadgetConfigPage<Object> {

	private I18nTextEditor gadgetTitle;
	
	private Spinner spinnerAmountOfCustomers;

	public DashboardGadgetLastCustomersConfigPage() {
		super(DashboardGadgetLastCustomersConfigPage.class.getName());
		setTitle("My Last Customers Gadget");
	}

	@Override
	public Control createPageContents(final Composite parent) {
		final XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		GridData gridData = new GridData();
		gridData.verticalIndent = 15;
		
		Label descriptionLabel = new Label(wrapper, SWT.NONE);
		descriptionLabel.setText("For the JFire \"My Last Customers\" gadget you can configure title and amount of\nlast customers to be shown");
		
		gadgetTitle = new I18nTextEditor(wrapper, "Select the title for this dashboard gadget.");
		gadgetTitle.setI18nText(!getLayoutEntry().getEntryName().isEmpty() ? getLayoutEntry().getEntryName() : createInitialName());
		
		Label spinnerLabel = new Label(wrapper, SWT.NONE);
		spinnerLabel.setText("Select the amount of last customers to be shown.");
		spinnerLabel.setLayoutData(gridData);
		
		int max = 50;
		spinnerAmountOfCustomers = new Spinner(wrapper, SWT.BORDER);
		spinnerAmountOfCustomers.setMinimum(0);
		spinnerAmountOfCustomers.setIncrement(1);
		spinnerAmountOfCustomers.setPageIncrement(5);
		spinnerAmountOfCustomers.setMaximum(max);	// just setting a fix value here
		
		int amount = 0;
		Object config = getLayoutEntry().getConfig();
		if (config instanceof DashboardGadgetLastCustomersConfig)
			amount = ((DashboardGadgetLastCustomersConfig) config).getAmountLastCustomers();
		
		spinnerAmountOfCustomers.setSelection(amount < max + 1 ? amount : max);
		
		return wrapper;
	}

	private I18nText createInitialName() {
		final I18nTextBuffer textBuffer = new I18nTextBuffer();
		DashboardLayoutConfigModuleInitialiser.initializeLastCustomersGadgetName(textBuffer);
		return textBuffer;
	}

	@Override
	public void configure(final DashboardGadgetLayoutEntry layoutEntry) {
		layoutEntry.getEntryName().copyFrom(gadgetTitle != null ? gadgetTitle.getI18nText() : createInitialName());
		
		// Create Config object for "Last customers" dashboard gadget and set it for the given layoutEntry.
		DashboardGadgetLastCustomersConfig config = new DashboardGadgetLastCustomersConfig(spinnerAmountOfCustomers.getSelection());
		layoutEntry.setConfig(config);
	}

	@Override
	public void setMessage(String message, int type) {
	}
}
