package org.nightlabs.jfire.trade.ui.overview.invoice;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.trade.query.InvoiceQuery;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

public class InvoiceFilterComposite
	extends AbstractArticleContainerFilterComposite<InvoiceQuery>
{
	private Button yesButton; 
	private Button noButton;	
	
	public InvoiceFilterComposite(Composite parent, int style, LayoutMode layoutMode,
		LayoutDataMode layoutDataMode,
		QueryProvider<? super InvoiceQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	public InvoiceFilterComposite(Composite parent, int style,
		QueryProvider<InvoiceQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}
	
	
	@Override
	protected void createQueryUserOptionsRow(XComposite wrapper)
	{
		getFieldNames().add(InvoiceQuery.FieldName.outstanding);
		GridLayout layout = new GridLayout(5, false);
		layout.makeColumnsEqualWidth = true;
		wrapper.setLayout(layout);
		final Group customerGroup = new Group(wrapper, SWT.NONE);
		customerGroup.setText("Outstanding"); 
		customerGroup.setLayout( new GridLayout(2, false));
		customerGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		Button userActiveButton = new Button(customerGroup, SWT.CHECK);
		userActiveButton.setText("Active"); 
		final GridData userLabelData = new GridData(GridData.FILL_HORIZONTAL);
		userLabelData.horizontalSpan = 2;
		userActiveButton.setLayoutData(userLabelData);
		userActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(final boolean active)
			{
				getQuery().setFieldEnabled(InvoiceQuery.FieldName.outstanding, active);
				((InvoiceQuery)getQuery()).setOutstanding(active);
			}
		});

		SelectionListener outstandingSelectionListener = new SelectionAdapter()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				((InvoiceQuery)getQuery()).setOutstanding(yesButton.getSelection());		
			}
		};
		
		yesButton = new Button(customerGroup, SWT.RADIO);
		yesButton.setText("Yes");
		yesButton.setEnabled(false);
		yesButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		yesButton.addSelectionListener(outstandingSelectionListener);
		yesButton.setSelection(true);
		
		noButton = new Button(customerGroup, SWT.RADIO);
		noButton.setText("No");
		noButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		noButton.setEnabled(false);
		noButton.addSelectionListener(outstandingSelectionListener);
		
		super.createQueryUserOptionsRow(wrapper);
	}
	
	@Override
	public Class<InvoiceQuery> getQueryClass() {
		return InvoiceQuery.class;
	}
	
	@Override
	protected void updateUI(final QueryEvent event, final List<FieldChangeCarrier> changedFields)
	{
		super.updateUI(event, changedFields);
		for (final FieldChangeCarrier changedField : changedFields)
		{
			final String propertyName = changedField.getPropertyName();
			if (getEnableFieldName(InvoiceQuery.FieldName.outstanding).equals(propertyName))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				noButton.setEnabled(active);
				yesButton.setEnabled(active);	
			}
		}
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
