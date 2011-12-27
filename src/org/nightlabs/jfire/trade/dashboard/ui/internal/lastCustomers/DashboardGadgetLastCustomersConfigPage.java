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
 * WizardPage to configure properties of "My last customers" dashboard gadget.
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
		
		int max = DashboardGadgetLastCustomersConfig.maxAmountOfCustomersInDashboard;
		spinnerAmountOfCustomers = new Spinner(wrapper, SWT.BORDER);
		spinnerAmountOfCustomers.setMinimum(1);
		spinnerAmountOfCustomers.setMaximum(max);	// just set a fix value here
		spinnerAmountOfCustomers.setIncrement(5);
		spinnerAmountOfCustomers.setPageIncrement(5);
		
		int amount = DashboardGadgetLastCustomersConfig.initialAmountOfCustomersInDashboard;	// initial selection if no valid one can be read out from config
		Object config = getLayoutEntry().getConfig();
		if (config instanceof DashboardGadgetLastCustomersConfig) {
			DashboardGadgetLastCustomersConfig lcConfig = (DashboardGadgetLastCustomersConfig) config;
			if (lcConfig.getAmountLastCustomers() > 0)
				amount = lcConfig.getAmountLastCustomers();
		}
		
		spinnerAmountOfCustomers.setSelection(amount < max + 1 ? amount : max);
		
		return wrapper;
	}

	private I18nText createInitialName() {
		final I18nTextBuffer textBuffer = new I18nTextBuffer();
		TradeDashboardGadgetsConfigModuleInitialiser.initializeLastCustomersGadgetName(textBuffer);
		return textBuffer;
	}

	@Override
	public void initialize(DashboardGadgetLayoutEntry<?> layoutEntry) {
		super.initialize(layoutEntry);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void configure(final DashboardGadgetLayoutEntry layoutEntry) {
		layoutEntry.getEntryName().copyFrom(gadgetTitle != null ? gadgetTitle.getI18nText() : createInitialName());
		
		// Create config object for "My last customers" dashboard gadget and set it for the given layoutEntry.
		DashboardGadgetLastCustomersConfig config = new DashboardGadgetLastCustomersConfig();
		if (spinnerAmountOfCustomers != null && spinnerAmountOfCustomers.getSelection() > 0)
			config.setAmountLastCustomers(spinnerAmountOfCustomers.getSelection());
		
		layoutEntry.setConfig(config);
	}
}
