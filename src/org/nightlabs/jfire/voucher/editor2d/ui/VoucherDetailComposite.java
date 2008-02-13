package org.nightlabs.jfire.voucher.editor2d.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.voucher.editor2d.ui.resource.Messages;
import org.nightlabs.jfire.voucher.scripting.PreviewParameterValuesResult;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class VoucherDetailComposite
extends XComposite
{
	public VoucherDetailComposite(Composite parent, int style)
	{
		super(parent, style);
		createComposite(this);
	}
	
	private PreviewParameterValuesResult ppvr = null;
	public void setPreviewParameterValuesResult(PreviewParameterValuesResult ppvr)
	{
		this.ppvr = ppvr;
		if (ppvr != null) {
			populateCurrencies(ppvr);
		}
	}

	public Currency getSelectedCurrency() {
		return selectedCurrency;
	}

	private Combo currencyCombo = null;
	private Currency selectedCurrency = null;
	private SelectionListener currencyListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
		public void widgetSelected(SelectionEvent e) {
			currencySelected();
		}
	};
	
	private void currencySelected() {
		selectedCurrency = currencies.get(currencyCombo.getSelectionIndex());
	}
	
//	private NightlabsFormsToolkit toolkit = null;
	protected void createComposite(Composite parent)
	{
//		toolkit = new NightlabsFormsToolkit(Display.getCurrent());
//		Composite comp = toolkit.createComposite(parent, SWT.NONE);
		Composite comp = new XComposite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		Label customerGroupLabel = toolkit.createLabel(comp, Messages.getString("org.nightlabs.jfire.voucher.editor2d.ui.VoucherDetailComposite.label.currency")); //$NON-NLS-1$
		Label customerGroupLabel = new Label(comp, SWT.NONE);
		customerGroupLabel.setText(Messages.getString("org.nightlabs.jfire.voucher.editor2d.ui.VoucherDetailComposite.label.currency")); //$NON-NLS-1$
		GridData labelData = new GridData();
		labelData.widthHint = 100;
		customerGroupLabel.setLayoutData(labelData);
		int comboStyle = SWT.BORDER | SWT.READ_ONLY;
		currencyCombo = new Combo(comp, comboStyle);
		currencyCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		currencyCombo.addSelectionListener(currencyListener);
	}
	
	private List<Currency> currencies = null;
	protected void populateCurrencies(PreviewParameterValuesResult ppvr)
	{
		currencyCombo.removeAll();
		currencies = new ArrayList<Currency>(ppvr.getCurrencies());
		for (Iterator<Currency> it = currencies.iterator(); it.hasNext(); ) {
			Currency c = it.next();
			currencyCombo.add(c.getCurrencySymbol());
		}
		currencyCombo.select(0);
		currencySelected();
	}
}
