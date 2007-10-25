package org.nightlabs.jfire.trade.ui.account.transfer;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.accounting.ManualMoneyTransfer;
import org.nightlabs.jfire.accounting.MoneyTransfer;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.Transfer;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.jfire.transfer.id.TransferID;
import org.nightlabs.l10n.NumberFormatter;

/**
 * This composite lists all {@link MoneyTransfer}s of an account in a table.
 * @author Chairat Kongarayawetchakun - chairatk[at]nightlabs[dot]de
 */
public class MoneyTransferTable
extends AbstractTableComposite<MoneyTransfer>
{
	private TransferID transferID;

	/**
	 * The fetch groups of money transfer data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Transfer.FETCH_GROUP_THIS_TRANSFER,
		MoneyTransfer.FETCH_GROUP_CURRENCY,
		ManualMoneyTransfer.FETCH_GROUP_REASON}	;

	public MoneyTransferTable(Composite parent, int style)
	{
		super(parent, style);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.fromAccountTableColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(30));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.toAccountableColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(30));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.amountTableColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(20));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.reasonTableColumn.text")); //$NON-NLS-1$
		layout.addColumnData(new ColumnWeightData(20));

		table.setLayout(layout);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new MoneyTransferListLabelProvider());
	}

	public TransferID getTransferID()
	{
		return transferID;
	}

	private AnchorID currentTransferID;

	public void setMoneyTransfers(AnchorID currentTransferID, Collection<MoneyTransfer> moneyTransfers)
	{
		if (currentTransferID == null)
			throw new IllegalArgumentException("currentTransferID == null"); //$NON-NLS-1$

		this.currentTransferID = currentTransferID;
		super.setInput(moneyTransfers);
	}

	class MoneyTransferListLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) 
		{
			if (element instanceof ManualMoneyTransfer) {
				ManualMoneyTransfer manualMoneyTransfer = (ManualMoneyTransfer) element;
				switch (columnIndex) 
				{
				case(0):
					if (manualMoneyTransfer.getFrom().getAnchorID() != null)
						return manualMoneyTransfer.getFrom().getAnchorID();
				break;
				case(1):
					if (manualMoneyTransfer.getTo().getAnchorID() != null)
						return manualMoneyTransfer.getTo().getAnchorID(); 
				break;
				case(2):
					return NumberFormatter.formatCurrency(manualMoneyTransfer.getAmount(), manualMoneyTransfer.getCurrency(), true);
				case(3):
					if (manualMoneyTransfer.getReason() != null)
						return manualMoneyTransfer.getReason().getText();
				break;
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}

	public void setLoadingStatus()
	{
		this.currentTransferID = null;
		super.setInput(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.loadingDataPlaceholder")); //$NON-NLS-1$
	}

	@Override
	public void setInput(Object input)
	{
		throw new UnsupportedOperationException("Use setMoneyTransfers(...) or setLoadingStatus(...) instead!"); //$NON-NLS-1$
	}

}
