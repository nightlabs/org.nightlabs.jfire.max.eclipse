package org.nightlabs.jfire.trade.dashboard.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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


public class DashboardGadgetLastCustomersConfigPage extends AbstractDashbardGadgetConfigPage<Object> {

	private I18nTextEditor gadgetTitle;
	
	private Spinner spinnerAmountOfCustomers;

	public DashboardGadgetLastCustomersConfigPage() {
		super(DashboardGadgetLastCustomersConfigPage.class.getName());
//		setTitle("My Last Customers Gadget");
	}

	@Override
	public Control createPageContents(final Composite parent) {
		final XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		DashboardGadgetLastCustomersConfig config = (DashboardGadgetLastCustomersConfig) getLayoutEntry().getConfig();
		int amount = config.getAmountLastCustomers();
		
		gadgetTitle = new I18nTextEditor(wrapper, "My Last Customers");
		gadgetTitle.setI18nText(!getLayoutEntry().getEntryName().isEmpty() ? getLayoutEntry().getEntryName() : createInitialName());

		spinnerAmountOfCustomers = new Spinner(wrapper, SWT.BORDER);
		spinnerAmountOfCustomers.setMinimum(0);
		spinnerAmountOfCustomers.setIncrement(1);
		spinnerAmountOfCustomers.setPageIncrement(5);
		spinnerAmountOfCustomers.setMaximum(50);	// just setting a fix value here
		spinnerAmountOfCustomers.setSelection(amount < spinnerAmountOfCustomers.getMaximum() + 1 ? amount : spinnerAmountOfCustomers.getMaximum());
		
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
		
		// Create Config object for "Last customers" widget and set it for the given layoutEntry.
		DashboardGadgetLastCustomersConfig config = new DashboardGadgetLastCustomersConfig(spinnerAmountOfCustomers.getSelection());
		layoutEntry.setConfig(config);
	}

	@Override
	public void setMessage(String message, int type) {
		// TODO Auto-generated method stub
		
	}
}
