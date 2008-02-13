package org.nightlabs.jfire.trade.ui.overview.invoice;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

public class InvoiceFilterComposite
//extends XComposite
extends AbstractArticleContainerFilterComposite
{
	public InvoiceFilterComposite(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	protected Class getQueryClass() {
		return Invoice.class;
	}
	
//	private Combo booked;
//	private DateTimeEdit bookDTMin;
//	private DateTimeEdit bookDTMax;
//
//	private CComboComposite<Currency> currency;
//	private CurrencyEdit amountToPayMin;
//	private CurrencyEdit amountToPayMax;
//	private CurrencyEdit amountPaidMin;
//	private CurrencyEdit amountPaidMax;
//
//	public InvoiceFilterComposite(Composite parent, int style)
//	{
//		super(parent, style);
//		setLayout(new RowLayout());
//
//		try {
//			Login.getLogin();
//		} catch (LoginException e1) {
//			// ignore - will get a class not found exception later
//			// TODO use a LSDViewPart ;-)
//		}
//
//		if (wildcardAllCurrencies == null)
//			wildcardAllCurrencies = new Currency("_all_", "[all]", 2);
//		{
//			Group comp = new Group(this, SWT.NONE);
//			comp.setText("Book Detail");
//			comp.setLayout(new GridLayout(2, false));
//
//			booked = new Combo(comp, SWT.BORDER | SWT.READ_ONLY);
//
//			booked.add("Booked + Not Booked");
//			booked.add("Only Booked");
//			booked.add("Only Not Booked");
//			booked.select(0);
//
//			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//			gd.horizontalSpan = 2;
//			booked.setLayoutData(gd);
//			booked.addSelectionListener(new SelectionAdapter() {
//				@Override
//				public void widgetSelected(SelectionEvent e)
//				{
//					bookedSelected();
//				}
//			});
//
//			bookDTMin = new DateTimeEdit(
//					comp,
//					DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
//					"Booked After");
//			bookDTMin.setDate(new Date(0));
//
//			bookDTMax = new DateTimeEdit(
//					comp,
//					DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
//					"Booked Before");
//			Calendar cal = Calendar.getInstance();
//			cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
//			cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
//			cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
//			cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));
//			bookDTMax.setDate(cal.getTime());
//
//			bookedSelected();
//		}
//
//		{
//			Group comp = new Group(this, SWT.NONE);
//			comp.setText("Pay Detail");
//			comp.setLayout(new GridLayout(4, false));
//
//			// TODO load the currencies ASYNCHRONOUSLY
//			List<Currency> currencies = new CurrencyDAO().getCurrencies(new NullProgressMonitor());
//			currencies.add(0, wildcardAllCurrencies);
//			currency = new CComboComposite<Currency>(currencies, new LabelProvider() {
//				@Override
//				public String getText(Object element)
//				{
//					if (element instanceof org.nightlabs.l10n.Currency)
//						return ((org.nightlabs.l10n.Currency)element).getCurrencySymbol();
//
//					return super.getText(element);
//				}
//			}, comp, SWT.BORDER);
//
//			currency.addSelectionListener(new SelectionAdapter() {
//				@Override
//				public void widgetSelected(SelectionEvent e)
//				{
//					currencySelected();
//				}
//			});
//
//			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//			gd.horizontalSpan = 4;
//			currency.setLayoutData(gd);
//			currency.select(0); // select the wildcard
//
//			amountPaidMin = new CurrencyEdit(comp, currency.getSelectedElement(), CurrencyEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX, "Min Amount Paid");
//			amountPaidMax = new CurrencyEdit(comp, currency.getSelectedElement(), CurrencyEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX, "Max Amount Paid");
//
//			amountToPayMin = new CurrencyEdit(comp, currency.getSelectedElement(), CurrencyEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX, "Min Amount To Pay");
//			amountToPayMax = new CurrencyEdit(comp, currency.getSelectedElement(), CurrencyEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX, "Max Amount To Pay");
//		}
//	}
//
//	private static Currency wildcardAllCurrencies;
//
//	private void bookedSelected()
//	{
//		bookDTMin.setEnabled(booked.getSelectionIndex() == 1);
//		bookDTMax.setEnabled(booked.getSelectionIndex() == 1);
//	}
//
//	private void currencySelected()
//	{
//		boolean enabled = !wildcardAllCurrencies.equals(currency.getSelectedElement());
//		amountPaidMin.setEnabled(enabled);
//		amountPaidMax.setEnabled(enabled);
//		amountToPayMin.setEnabled(enabled);
//		amountToPayMax.setEnabled(enabled);
//		if (enabled) {
//			Currency c = currency.getSelectedElement();
//			amountPaidMin.setCurrency(c);
//			amountPaidMax.setCurrency(c);
//			amountToPayMin.setCurrency(c);
//			amountToPayMax.setCurrency(c);
//		}
//	}
//
//	public Collection<InvoiceQuery> getInvoiceQueries()
//	{
//		// TODO we should manage (with an extension point) multiple query pages (tabbed)
//		// that provide multiple queries.
//		List<InvoiceQuery> invoiceQueries = new ArrayList<InvoiceQuery>(1);
//
//		InvoiceQuery query = new InvoiceQuery();
//		invoiceQueries.add(query);
//
//		switch (booked.getSelectionIndex()) {
//			case 1:
//				query.setBooked(Boolean.TRUE);
//				break;
//			case 2:
//				query.setBooked(Boolean.FALSE);
//				break;
//		}
//
//		if (bookDTMin.isActive())
//			query.setBookDTMin(bookDTMin.getDate());
//
//		if (bookDTMax.isActive())
//			query.setBookDTMax(bookDTMax.getDate());
//
//
//		if (wildcardAllCurrencies != currency.getSelectedElement())
//			query.setCurrencyID((CurrencyID) JDOHelper.getObjectId(currency.getSelectedElement()));
//
//		if (amountPaidMin.isActive())
//			query.setAmountPaidMin(amountPaidMin.getValue());
//
//		if (amountPaidMax.isActive())
//			query.setAmountPaidMax(amountPaidMax.getValue());
//
//		if (amountToPayMin.isActive())
//			query.setAmountToPayMin(amountToPayMin.getValue());
//
//		if (amountToPayMax.isActive())
//			query.setAmountToPayMax(amountToPayMax.getValue());
//
//		return invoiceQueries;
//	}
		
}
