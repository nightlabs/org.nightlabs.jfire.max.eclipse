/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.config;

import java.util.Arrays;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.RadioGroupComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.config.AbstractUserConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.reporting.trade.config.TradeDocumentsLocaleConfigModule.LocaleOption;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TradeDocumenstLocalePreferencePage extends
		AbstractUserConfigModulePreferencePage {

	private RadioGroupComposite<LocaleOption> localeOptions;
	
	public TradeDocumenstLocalePreferencePage() {
	}

	/**
	 * @param title
	 */
	public TradeDocumenstLocalePreferencePage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public TradeDocumenstLocalePreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#createConfigModuleController()
	 */
	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new TradeDocumentsLocaleConfigController(this);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#createPreferencePage(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createPreferencePage(Composite parent) {
		XComposite comp = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		Label l = new Label(comp, SWT.WRAP);
		l.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		l.setText(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.config.TradeDocumenstLocalePreferencePage.headingText")); //$NON-NLS-1$
		localeOptions = new RadioGroupComposite<LocaleOption>(comp, SWT.NONE);
		localeOptions.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return Messages.getString("org.nightlabs.jfire.reporting.trade.ui.config.TradeDocumenstLocalePreferencePage.localeOption." + element.toString()); //$NON-NLS-1$
			}
		});
		localeOptions.setInput(Arrays.asList(LocaleOption.values()));
		localeOptions.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				setConfigChanged(true);
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#updateConfigModule()
	 */
	@Override
	public void updateConfigModule() {
		getConfigModuleController().getConfigModule().setLocaleOption(localeOptions.getSelectedElement());
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#updatePreferencePage()
	 */
	@Override
	protected void updatePreferencePage() {
		localeOptions.setSelectedElement(getConfigModuleController().getConfigModule().getLocaleOption());
	}
	
	@Override
	public TradeDocumentsLocaleConfigController getConfigModuleController() {
		return (TradeDocumentsLocaleConfigController) super.getConfigModuleController();
	}

}
