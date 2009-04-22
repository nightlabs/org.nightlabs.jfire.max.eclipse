package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.issuetimetracking.ProjectCost;
import org.nightlabs.jfire.trade.ui.currency.CurrencyCombo;

/** 
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CostRevenueComposite 
extends XComposite 
{
	private CurrencyCombo currencyCombo;
	private Text costText;
	private Text revenueText;

	public CostRevenueComposite(Composite parent, int style) {
		super(parent, style);
		getGridLayout().numColumns = 2;

		Label currencyLabel = new Label(this, SWT.NONE);
		currencyLabel.setText("Currency");
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		currencyLabel.setLayoutData(gridData);
		
		currencyCombo = new CurrencyCombo(this, SWT.NONE);
		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		currencyCombo.setLayoutData(gridData);

		Label monthlyCostLabel = new Label(this, SWT.NONE);
		monthlyCostLabel.setText("Monthly Cost");
		costText = new Text(this, SWT.SINGLE);
		costText.setTextLimit(20);
		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		costText.setLayoutData(gridData);

		Label monthlyRevenueLabel = new Label(this, SWT.NONE);
		monthlyRevenueLabel.setText("Monthly Revenue");
		revenueText = new Text(this, SWT.SINGLE);
		revenueText.setTextLimit(20);
		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		revenueText.setLayoutData(gridData);
	}

	public void setCurrency(Currency currency) {
		currencyCombo.setSelectedCurrency(currency);
	}

	public Currency getSelectedCurrency() {
		return currencyCombo.getSelectedCurrency();
	}
	
	public void setProjectCost(ProjectCost projectCost) {
		costText.setText(Long.toString(projectCost.getTotalCost()));
		revenueText.setText(Long.toString(projectCost.getTotalRevenue()));
	}
}
