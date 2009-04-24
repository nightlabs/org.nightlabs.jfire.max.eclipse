package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
		monthlyCostLabel.setText("Hourly Cost");
		costText = new Text(this, SWT.SINGLE);
		costText.addListener (SWT.Verify, new Listener () {
			public void handleEvent (Event e) {
				String string = e.text;
				char [] chars = new char [string.length ()];
				string.getChars (0, chars.length, chars, 0);
				for (int i=0; i<chars.length; i++) {
					if (!('0' <= chars [i] && chars [i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});

		costText.setTextLimit(20);
		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		costText.setLayoutData(gridData);

		Label monthlyRevenueLabel = new Label(this, SWT.NONE);
		monthlyRevenueLabel.setText("Hourly Revenue");
		revenueText = new Text(this, SWT.SINGLE);
		revenueText.addListener (SWT.Verify, new Listener () {
			public void handleEvent (Event e) {
				String string = e.text;
				char [] chars = new char [string.length ()];
				string.getChars (0, chars.length, chars, 0);
				for (int i=0; i<chars.length; i++) {
					if (!('0' <= chars [i] && chars [i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});

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
		costText.setText(Long.toString(projectCost.getDefaultCost().getAmount()));
		revenueText.setText(Long.toString(projectCost.getDefaultRevenue().getAmount()));
	}
	
	public long getCost() {
		return costText.getText() == null || costText.getText().isEmpty() ? 0 :Long.parseLong(costText.getText());
	}
	
	public long getRevenue() {
		return revenueText.getText() == null || revenueText.getText().isEmpty() ? 0 :Long.parseLong(revenueText.getText());
	}
	
	public void addKeyListener(KeyListener listener) {
		costText.addKeyListener(listener);
		revenueText.addKeyListener(listener);
	}
}