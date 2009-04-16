package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.ui.currency.CurrencyCombo;

/** 
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CostRevenueComposite 
extends XComposite 
{
	public CostRevenueComposite(Composite parent, int style) {
		super(parent, style);
		getGridLayout().numColumns = 2;
		
		new Label(this, SWT.NONE).setText("Currency");
		GridData gridData = new GridData();
		CurrencyCombo currencyCombo = new CurrencyCombo(this, SWT.NONE);
		currencyCombo.getGridData().horizontalIndent = 0;
		gridData.grabExcessHorizontalSpace = true;
		currencyCombo.setLayoutData(gridData);
		
		new Label(this, SWT.NONE).setText("Monthly Cost");
		Text costText = new Text(this, SWT.SINGLE);
		costText.setTextLimit(20);
		gridData = new GridData();
		gridData.verticalIndent = 5;
		gridData.widthHint = 150;
		costText.setLayoutData(gridData);

		new Label(this, SWT.NONE).setText("Monthly Revenue");
		Text revenueText = new Text(this, SWT.SINGLE);
		revenueText.setTextLimit(20);
		gridData = new GridData();
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		revenueText.setLayoutData(gridData);
	}

}
