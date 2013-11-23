package org.nightlabs.jfire.trade.ui.overview.invoice;

import java.util.Comparator;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.PriceFragment;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.util.BaseComparator;

public class InvoiceListComposite
extends AbstractArticleContainerListComposite<Invoice>
{
	public static final Comparator<Invoice> INVOICE_FINALIZE_DT_COMPARATOR = new Comparator<Invoice>(){
		@Override
		public int compare(Invoice o1, Invoice o2)
		{
			int result = BaseComparator.comparatorNullCheck(o1, o2);
			if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
				int result2 = BaseComparator.comparatorNullCheck(o1.getFinalizeDT(), o2.getFinalizeDT());
				if (result2== BaseComparator.COMPARE_RESULT_NOT_NULL) {
					return o1.getFinalizeDT().compareTo(o2.getFinalizeDT());
				}
				return result2;
			}
			return result;
		}
	};

	public static final Comparator<Invoice> INVOICE_PRICE_COMPARATOR = new Comparator<Invoice>() {
		@Override
		public int compare(Invoice o1, Invoice o2)
		{
			int result = BaseComparator.comparatorNullCheck(o1, o2);
			if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
				int result2 = BaseComparator.comparatorNullCheck(o1.getPrice(), o2.getPrice());
				if (result2 == BaseComparator.COMPARE_RESULT_NOT_NULL) {
					return PRICE_COMPARATOR.compare(o1.getPrice(), o2.getPrice());
				}
				return result2;
			}
			return result;
		}
	};

	public static final Comparator<Invoice> INVOICE_AMOUNT_TO_PAY_COMPARATOR = new Comparator<Invoice>() {
		@Override
		public int compare(Invoice o1, Invoice o2)
		{
			int result = BaseComparator.comparatorNullCheck(o1, o2);
			if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
				int result2 = BaseComparator.comparatorNullCheck(o1.getInvoiceLocal(), o2.getInvoiceLocal());
				if (result2 == BaseComparator.COMPARE_RESULT_NOT_NULL) {
					return (int) (o1.getInvoiceLocal().getAmountToPay() - o2.getInvoiceLocal().getAmountToPay());
				}
				return result2;
			}
			return result;
		}
	};

	/**
	 * The fetch-groups this list composite needs to display invoices.
	 */
	public static final String[] FETCH_GROUPS_INVOICES = new String[] {
		FetchPlan.DEFAULT,
		Invoice.FETCH_GROUP_THIS_INVOICE,
		Invoice.FETCH_GROUP_INVOICE_LOCAL,
		Invoice.FETCH_GROUP_PRICE,
		Price.FETCH_GROUP_CURRENCY,
		Price.FETCH_GROUP_FRAGMENTS,
		PriceFragmentType.FETCH_GROUP_NAME,
		PriceFragment.FETCH_GROUP_PRICE_FRAGMENT_TYPE,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON
	};

	public InvoiceListComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createAdditionalTableColumns(TableViewer tableViewer,
			Table table)
	{
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.invoice.InvoiceListComposite.finalizeDateTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.invoice.InvoiceListComposite.finalizeUserTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);

		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.invoice.InvoiceListComposite.priceTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);

		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.invoice.InvoiceListComposite.amountToPayTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);
	}

	@Override
	protected String getAdditionalColumnText(Object element,
			int additionalColumnIndex, int firstAdditionalColumnIndex, int columnIndex)
	{
		if (!(element instanceof Invoice))
			return ""; //$NON-NLS-1$

		Invoice invoice = (Invoice) element;
		switch (additionalColumnIndex) {
			case 0:
				if (invoice.getFinalizeDT() != null)
					return formatDate(invoice.getFinalizeDT());
			break;
			case 1:
				if (invoice.getFinalizeUser() != null)
					return invoice.getFinalizeUser().getName();
			break;
			case 2:
				if (invoice.getPrice() != null && invoice.getCurrency() != null)
					return NumberFormatter.formatCurrency(invoice.getPrice().getAmount(), invoice.getCurrency());
			break;
			case 3:
				if (invoice.getPrice() != null && invoice.getCurrency() != null)
					return NumberFormatter.formatCurrency(invoice.getInvoiceLocal().getAmountToPay(), invoice.getCurrency());
			break;
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return Invoice.class;
	}

	@Override
	protected Comparator<?> getAdditionalColumnComparator(Object element,
			int additionalColumnIndex, int firstAdditionalColumnIndex,
			int columnIndex) {
		switch (additionalColumnIndex) {
			case 0: return INVOICE_FINALIZE_DT_COMPARATOR;
			case 2: return INVOICE_PRICE_COMPARATOR;
			case 3: return INVOICE_AMOUNT_TO_PAY_COMPARATOR;
			default: return null;
		}
	}
}
