package org.nightlabs.jfire.trade.dashboard.ui.internal.clientScripts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashbardGadgetConfigPage;
import org.nightlabs.jfire.dashboard.DashboardGadgetLayoutEntry;

public class DashboardGadgetClientScriptsConfigPage extends AbstractDashbardGadgetConfigPage<Object> {

	private I18nTextEditor gadgetTitle;
	
	private Button buttonConfirmProcessing;
	
	public DashboardGadgetClientScriptsConfigPage() {
		super(DashboardGadgetClientScriptsConfigPage.class.getName());
		setTitle("Client scripts");
	}

	@Override
	public Control createPageContents(Composite parent) {
		final XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, 2);
		
		Label labelDescription1 = new Label(wrapper, SWT.WRAP);
		labelDescription1.setText("This gadget will show you all stored client scripts.");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		labelDescription1.setLayoutData(gd);
		
		Label labelTitle = new Label(wrapper, SWT.NONE);
		labelTitle.setText("Select the title for this gadget:");
		labelTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		gadgetTitle = new I18nTextEditor(wrapper);
		gadgetTitle.setI18nText(!getLayoutEntry().getEntryName().isEmpty() ? getLayoutEntry().getEntryName() : createInitialName());
		gadgetTitle.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label labelConfirmProcessing = new Label(wrapper, SWT.NONE);
		labelConfirmProcessing.setText("Confirm processing");
		labelConfirmProcessing.setLayoutData(new GridData());
//		
		buttonConfirmProcessing = new Button(wrapper, SWT.CHECK);
		
		Label labelDescription2 = new Label(wrapper, SWT.WRAP);
		labelDescription2.setText("Description2");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		labelDescription2.setLayoutData(gd);
		
		
		
		
		
		
		
		return wrapper;
	}
	
	private I18nText createInitialName() {
		final I18nTextBuffer textBuffer = new I18nTextBuffer();
//		TradeDashboardGadgetsConfigModuleInitialiser.initializeClientScriptsGadgetName(textBuffer);
		return textBuffer;
	}

	@Override
	public void configure(DashboardGadgetLayoutEntry<?> layoutEntry) {
	}
	
}
