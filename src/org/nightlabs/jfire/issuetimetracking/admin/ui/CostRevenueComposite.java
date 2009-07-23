package org.nightlabs.jfire.issuetimetracking.admin.ui;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.trade.ui.currency.CurrencyCombo;

/** 
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CostRevenueComposite 
extends XComposite 
{
	private CurrencyCombo currencyCombo;
	private Spinner costSpinner;
	private Spinner revenueSpinner;

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
		currencyCombo.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				costSpinner.setDigits(currencyCombo.getSelectedCurrency().getDecimalDigitCount());
				revenueSpinner.setDigits(currencyCombo.getSelectedCurrency().getDecimalDigitCount());
			}
		});

		Label monthlyCostLabel = new Label(this, SWT.NONE);
		monthlyCostLabel.setText("Hourly Cost");
		costSpinner = new Spinner(this, SWT.BORDER);
		costSpinner.addListener (SWT.Verify, new Listener () {
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

		costSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		costSpinner.setMinimum(0);
		costSpinner.setMaximum(Integer.MAX_VALUE);
		
		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		costSpinner.setLayoutData(gridData);

		Label monthlyRevenueLabel = new Label(this, SWT.NONE);
		monthlyRevenueLabel.setText("Hourly Revenue");
		revenueSpinner = new Spinner(this, SWT.BORDER);
		revenueSpinner.addListener (SWT.Verify, new Listener () {
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

		revenueSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		revenueSpinner.setMinimum(0);
		revenueSpinner.setMaximum(Integer.MAX_VALUE);
		
		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		revenueSpinner.setLayoutData(gridData);
	}

	public void setCurrency(Currency currency) {
		currencyCombo.setSelectedCurrency(currency);
	}

	public Currency getSelectedCurrency() {
		return currencyCombo.getSelectedCurrency();
	}
	
//	public void setProjectCost(ProjectCost projectCost) {
//		costSpinner.setText(Long.toString(projectCost.getDefaultCost().getAmount()));
//		revenueSpinner.setText(Long.toString(projectCost.getDefaultRevenue().getAmount()));
//	}
//	
//	public long getCost() {
//		return costSpinner.getText() == null || costSpinner.getText().isEmpty() ? 0 :Long.parseLong(costSpinner.getText());
//	}
//	
//	public long getRevenue() {
//		return revenueSpinner.getText() == null || revenueSpinner.getText().isEmpty() ? 0 :Long.parseLong(revenueSpinner.getText());
//	}
	
	public int getCost() {
		return costSpinner.getSelection();
	}
	
	public int getRevenue() {
		return revenueSpinner.getSelection();
	}
	
	public void setCost(int cost) {
		costSpinner.setSelection(cost);
	}
	
	public void setRevenue(int revenue) {
		revenueSpinner.setSelection(revenue);
	}
	
	public void addKeyListener(KeyListener listener) {
		costSpinner.addKeyListener(listener);
		revenueSpinner.addKeyListener(listener);
	}
}