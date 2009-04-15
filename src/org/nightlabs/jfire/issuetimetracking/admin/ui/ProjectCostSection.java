package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.jfire.trade.ui.currency.CurrencyCombo;

public class ProjectCostSection extends ToolBarSectionPart {

	private XComposite client;
	
	/**
	 * @param page
	 * @param parent
	 * @param style
	 * @param title
	 */
	public ProjectCostSection(FormPage page, Composite parent) {
		super(
				page, parent, 
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
				"Project Cost");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());
		
		client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1; 
		
		XComposite costComposite = new XComposite(client, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		costComposite.getGridLayout().numColumns = 2;
		
		new Label(costComposite, SWT.NONE).setText("Currency");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		CurrencyCombo currencyCombo = new CurrencyCombo(costComposite, SWT.NONE);
		currencyCombo.setLayoutData(gridData);
		
		new Label(costComposite, SWT.NONE).setText("Monthly Cost");
		Text costText = new Text(costComposite, SWT.SINGLE);
		costText.setLayoutData(gridData);
		
		new Label(costComposite, SWT.NONE).setText("Monthly Revenue");
		Text revenueText = new Text(costComposite, SWT.SINGLE);
		revenueText.setLayoutData(gridData);

		getSection().setClient(client);
	}
	
	public XComposite getClient() {
		return client;
	}
}
