package org.nightlabs.jfire.issuetimetracking.admin.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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

	public static final String PROPERTY_KEY_CURRENCY = "currency";
	public static final String PROPERTY_KEY_COST = "cost";
	public static final String PROPERTY_KEY_REVENUE = "revenue";

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public CostRevenueComposite(Composite parent, int style, boolean hasCurrencyCombo) {
		super(parent, style);
		getGridLayout().numColumns = 2;

		GridData gridData = new GridData();
		if (hasCurrencyCombo) {
			Label currencyLabel = new Label(this, SWT.NONE);
			currencyLabel.setText("Currency");
			gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
			currencyLabel.setLayoutData(gridData);

			currencyCombo = new CurrencyCombo(this, SWT.NONE);
			gridData = new GridData();
			gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
			currencyCombo.setLayoutData(gridData);
			currencyCombo.addSelectionChangedListener(new ISelectionChangedListener(){
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					Currency oldCurrency = currency;
					currency = currencyCombo.getSelectedCurrency();
					updateSpinnerDigits();
					propertyChangeSupport.firePropertyChange(PROPERTY_KEY_CURRENCY, oldCurrency, currency);
				}
			});
			currencyCombo.addPropertyChangeListener(CurrencyCombo.PROPERTY_KEY_LOAD_JOB_FINISHED, new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					currency = currencyCombo.getSelectedCurrency();
					updateSpinnerDigits();
				}
			});
		}

		Label monthlyCostLabel = new Label(this, SWT.NONE);
		monthlyCostLabel.setText("Hourly Cost");
		costSpinner = new Spinner(this, SWT.BORDER);

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

		revenueSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		revenueSpinner.setMinimum(0);
		revenueSpinner.setMaximum(Integer.MAX_VALUE);

		gridData = new GridData();
		gridData.verticalAlignment = GridData.VERTICAL_ALIGN_CENTER;
		gridData.widthHint = 150;
		gridData.verticalIndent = 5;
		revenueSpinner.setLayoutData(gridData);

		costSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				long oldCost = cost;
				cost = costSpinner.getSelection();
				propertyChangeSupport.firePropertyChange(PROPERTY_KEY_COST, oldCost, cost);
			}
		});
		revenueSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				long oldRevenue = revenue;
				revenue = revenueSpinner.getSelection();
				propertyChangeSupport.firePropertyChange(PROPERTY_KEY_REVENUE, oldRevenue, revenue);
			}
		});
	}

	private void updateSpinnerDigits()
	{
		Currency currency = this.currency;
		if (currency == null)
			return;

		costSpinner.setDigits(currency.getDecimalDigitCount());
		revenueSpinner.setDigits(currency.getDecimalDigitCount());
	}

	private Currency currency;
	public void setCurrency(Currency currency) {
		this.currency = currency;
		if (currencyCombo != null)
			currencyCombo.setSelectedCurrency(currency);

		updateSpinnerDigits();
	}

	public Currency getSelectedCurrency() {
		return currencyCombo.getSelectedCurrency();
	}

	private long cost;
	private long revenue;

	public long getCost() {
		return cost;
	}

	public long getRevenue() {
		return revenue;
	}

	public void setCost(long cost) {
		this.cost = cost;
		costSpinner.setSelection((int)cost);
	}

	public void setRevenue(long revenue) {
		this.revenue = revenue;
		revenueSpinner.setSelection((int)revenue);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyKey, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyKey, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyKey, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyKey, listener);
	}
}