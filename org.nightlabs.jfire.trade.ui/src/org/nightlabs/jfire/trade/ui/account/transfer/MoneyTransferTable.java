package org.nightlabs.jfire.trade.ui.account.transfer;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.ManualMoneyTransfer;
import org.nightlabs.jfire.accounting.MoneyTransfer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.Anchor;
import org.nightlabs.jfire.transfer.Transfer;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.jfire.transfer.id.TransferID;
import org.nightlabs.l10n.DateFormatter;
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
		Transfer.FETCH_GROUP_FROM,
		Transfer.FETCH_GROUP_TO,
		MoneyTransfer.FETCH_GROUP_CURRENCY,
		ManualMoneyTransfer.FETCH_GROUP_DESCRIPTION,
		ManualMoneyTransfer.FETCH_GROUP_REASON,
		Account.FETCH_GROUP_NAME,
		Account.FETCH_GROUP_ACCOUNT_TYPE,
		AccountType.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON};

	public MoneyTransferTable(Composite parent, int style)
	{
		super(parent, style);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.timestampTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.directionTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.otherAccountTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.otherAccountTypeTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.amountTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.reasonTableColumn.text")); //$NON-NLS-1$

		WeightedTableLayout layout = new WeightedTableLayout(new int[]{30, 10, 30, 30, 30, 50});
		table.setLayout(layout);

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

	private AnchorID currentAnchorID;

	public void setMoneyTransfers(AnchorID currentAnchorID, Collection<MoneyTransfer> moneyTransfers)
	{
		if (currentAnchorID == null)
			throw new IllegalArgumentException("currentAnchorID == null"); //$NON-NLS-1$

		this.currentAnchorID = currentAnchorID;
		super.setInput(moneyTransfers);
	}

	private Anchor getOtherAnchor(MoneyTransfer transfer) {
		if (JDOHelper.getObjectId(transfer.getFrom()).equals(currentAnchorID)) {
			return transfer.getTo();
		} else
			return transfer.getFrom();
	}

	class MoneyTransferListLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (element instanceof MoneyTransfer) {
				MoneyTransfer moneyTransfer = (MoneyTransfer) element;
				Anchor other = getOtherAnchor(moneyTransfer);
				switch (columnIndex) {
					case(0): return DateFormatter.formatDateShortTimeHM(moneyTransfer.getTimestamp(), false);	//Timestamp
					case(1):	//Direction
						if (JDOHelper.getObjectId(moneyTransfer.getFrom()).equals(currentAnchorID)) {
							return "->"; //$NON-NLS-1$
						}
					return "<-"; //$NON-NLS-1$
					case(2):	//Other
						if (other instanceof Account)
							return ((Account)other).getName().getText();
						else
							return ((LegalEntity)other).getPerson().getDisplayName();
					case(3):	//Other type
						if (other instanceof Account)
							return ((Account)other).getAccountType().getName().getText();
						else
							return Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.column.legalEntity"); //$NON-NLS-1$
					case(4): return NumberFormatter.formatCurrency(moneyTransfer.getAmount(), moneyTransfer.getCurrency(), true);	//Amount
					case(5): return moneyTransfer.getDescription();
				}
				return null;
			}
			return null;
		}
	}

	public void setLoadingStatus()
	{
		this.currentAnchorID = null;
		super.setInput(Messages.getString("org.nightlabs.jfire.trade.ui.repository.transfer.ProductTransferTable.loadingDataPlaceholder")); //$NON-NLS-1$
	}

	@Override
	public void setInput(Object input)
	{
		throw new UnsupportedOperationException("Use setMoneyTransfers(...) or setLoadingStatus(...) instead!"); //$NON-NLS-1$
	}

}
