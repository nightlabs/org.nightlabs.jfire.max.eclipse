package org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers;

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
import org.nightlabs.jfire.trade.dashboard.TradeDashboardGadgetsConfigModuleInitialiser;
import org.nightlabs.jfire.trade.dashboard.ui.resource.Messages;

/**
 * WizardPage to configure properties of "My Last Customers" dashboard gadget.
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class DashboardGadgetLastCustomersConfigPage extends AbstractDashbardGadgetConfigPage<Object> {

	private I18nTextEditor gadgetTitle;
	
	private Spinner spinnerAmountOfCustomers;

	public DashboardGadgetLastCustomersConfigPage() {
		super(DashboardGadgetLastCustomersConfigPage.class.getName());
		setTitle(Messages.getString(
			"org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.DashboardGadgetLastCustomersConfigPage.title")); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(final Composite parent) {
		final XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, 2);
		
		Label descriptionLabel = new Label(wrapper, SWT.WRAP);
		descriptionLabel.setText(Messages.getString(
			"org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.DashboardGadgetLastCustomersConfigPage.descriptionLabel.text")); //$NON-NLS-1$
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		descriptionLabel.setLayoutData(gd);
		
		Label titleLabel = new Label(wrapper, SWT.NONE);
		titleLabel.setText(Messages.getString("org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.DashboardGadgetLastCustomersConfigPage.gadgetTitle.caption")); //$NON-NLS-1$
		gadgetTitle = new I18nTextEditor(wrapper);
		gadgetTitle.setI18nText(!getLayoutEntry().getEntryName().isEmpty() ? getLayoutEntry().getEntryName() : createInitialName());
		gadgetTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		Label spinnerLabel = new Label(wrapper, SWT.NONE);
		spinnerLabel.setText(Messages.getString(
			"org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.DashboardGadgetLastCustomersConfigPage.spinnerLabel.text")); //$NON-NLS-1$
		spinnerLabel.setLayoutData(new GridData());
		
		int max = 50;
		spinnerAmountOfCustomers = new Spinner(wrapper, SWT.BORDER);
		spinnerAmountOfCustomers.setMinimum(1);
		spinnerAmountOfCustomers.setIncrement(5);
		spinnerAmountOfCustomers.setPageIncrement(5);
		spinnerAmountOfCustomers.setMaximum(100);	// just set a fix value here
		
		int amount = 5;	// initial selection if none could be read out
		Object config = getLayoutEntry().getConfig();
		if (config instanceof DashboardGadgetLastCustomersConfig)
			amount = ((DashboardGadgetLastCustomersConfig) config).getAmountLastCustomers();
		
		spinnerAmountOfCustomers.setSelection(amount < max + 1 ? amount : max);
		
		return wrapper;
	}

	private I18nText createInitialName() {
		final I18nTextBuffer textBuffer = new I18nTextBuffer();
		TradeDashboardGadgetsConfigModuleInitialiser.initializeLastCustomersGadgetName(textBuffer);
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
