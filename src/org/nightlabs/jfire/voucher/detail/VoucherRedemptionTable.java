package org.nightlabs.jfire.voucher.detail;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.voucher.accounting.VoucherRedemption;
import org.nightlabs.jfire.voucher.resource.Messages;
import org.nightlabs.jfire.voucher.store.VoucherKey;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.l10n.NumberFormatter;

public class VoucherRedemptionTable
extends AbstractTableComposite<VoucherRedemption>
{
	private static class LabelProvider extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof VoucherRedemption) {
				VoucherRedemption voucherRedemption = (VoucherRedemption) element;

				switch (columnIndex) {
					case 0:
						return DateFormatter.formatDateShortTimeHMS(voucherRedemption.getPayment().getEndDT(), true);
					case 1:
						return NumberFormatter.formatCurrency(voucherRedemption.getPayment().getAmount(), voucherRedemption.getPayment().getCurrency());
					default:
						return ""; //$NON-NLS-1$
				}
			}

			if (columnIndex == 0)
				return String.valueOf(element);

			return ""; //$NON-NLS-1$
		}
	}

	public VoucherRedemptionTable(Composite parent)
	{
		super(parent, SWT.NONE);
	}

	@Implement
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn col;

		col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.voucher.detail.VoucherRedemptionTable.timestampTableColumn.text")); //$NON-NLS-1$

		col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.voucher.detail.VoucherRedemptionTable.amountTableColumn.text")); //$NON-NLS-1$

		table.setLayout(new WeightedTableLayout(new int[] { 1, 1 }));
	}

	@Implement
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

	private VoucherKey voucherKey;

	protected void setVoucherKey(VoucherKey voucherKey)
	{
		this.voucherKey = voucherKey;
		setInput(voucherKey == null ? null : voucherKey.getRedemptions());
	}
}
