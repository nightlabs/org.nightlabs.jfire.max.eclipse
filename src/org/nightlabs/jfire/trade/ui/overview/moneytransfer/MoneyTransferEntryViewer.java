package org.nightlabs.jfire.trade.ui.overview.moneytransfer;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.MoneyTransfer;
import org.nightlabs.jfire.accounting.dao.MoneyTransferDAO;
import org.nightlabs.jfire.accounting.query.MoneyTransferQuery;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.Anchor;
import org.nightlabs.jfire.transfer.Transfer;
import org.nightlabs.jfire.transfer.id.TransferID;
import org.nightlabs.l10n.GlobalDateFormatter;
import org.nightlabs.l10n.GlobalNumberFormatter;
import org.nightlabs.l10n.IDateFormatter;
import org.nightlabs.progress.ProgressMonitor;

public class MoneyTransferEntryViewer
extends JDOQuerySearchEntryViewer<MoneyTransfer, MoneyTransferQuery>
{
	final String[] FETCH_GROUPS = new String[] {
			FetchPlan.DEFAULT,
			Transfer.FETCH_GROUP_FROM,
			Transfer.FETCH_GROUP_TO,
			MoneyTransfer.FETCH_GROUP_INITIATOR,
			MoneyTransfer.FETCH_GROUP_CURRENCY,
			MoneyTransfer.FETCH_GROUP_DESCRIPTION,
			Account.FETCH_GROUP_NAME,
			Account.FETCH_GROUP_ACCOUNT_TYPE,
			AccountType.FETCH_GROUP_NAME,
			LegalEntity.FETCH_GROUP_PERSON};

	
	public MoneyTransferEntryViewer(Entry entry) {
		super(entry);
	}

	@Override
	public AbstractTableComposite<MoneyTransfer> createListComposite(Composite parent) {
		return new MoneyTransferTable(parent, SWT.NONE);
	}

	@Override
	public Class<MoneyTransfer> getTargetType()
	{
		return MoneyTransfer.class;
	}

	@Override
	protected Collection<MoneyTransfer> doSearch(QueryCollection<? extends MoneyTransferQuery> queryMap,
		ProgressMonitor monitor)
	{
		return MoneyTransferDAO.sharedInstance().getMoneyTransfersForQueries(
			queryMap,
			FETCH_GROUPS,
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
			monitor
			);
	}

	/**
	 * The ID for the Quick search registry.
	 */
	public static final String QUICK_SEARCH_REGISTRY_ID = MoneyTransferEntryViewer.class.getName();

	@Override
	protected String getQuickSearchRegistryID()
	{
		return QUICK_SEARCH_REGISTRY_ID;
	}
	
	private class MoneyTransferTable
	extends AbstractTableComposite<MoneyTransfer>
	{
		private TransferID transferID;

		/**
		 * The fetch groups of money transfer data.
		 */
		
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
			tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.fromTableColumn.text")); //$NON-NLS-1$

			tc = new TableColumn(table, SWT.LEFT);
			tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.fromTypeTableColumn.text")); //$NON-NLS-1$

			tc = new TableColumn(table, SWT.LEFT);
			tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.toTableColumn.text")); //$NON-NLS-1$

			tc = new TableColumn(table, SWT.LEFT);
			tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.amountTableColumn.text")); //$NON-NLS-1$

			tc = new TableColumn(table, SWT.LEFT);
			tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.reasonTableColumn.text")); //$NON-NLS-1$

			tc = new TableColumn(table, SWT.LEFT);
			tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.MoneyTransferTable.initiatorTableColumn.text")); //$NON-NLS-1$
			
			WeightedTableLayout layout = new WeightedTableLayout(new int[]{30, 30, 20, 30, 30, 50, 30});
			table.setLayout(layout);

			table.setLayout(layout);
		}

		@Override
		protected void setTableProvider(TableViewer tableViewer)
		{
			tableViewer.setContentProvider(new ArrayContentProvider());
			tableViewer.setLabelProvider(new MoneyTransferListLabelProvider());
		}

		class MoneyTransferListLabelProvider
		extends TableLabelProvider
		{
			public String getColumnText(Object element, int columnIndex)
			{
				if (element instanceof MoneyTransfer) {
					MoneyTransfer moneyTransfer = (MoneyTransfer) element;
					switch (columnIndex) {
						case(0): return GlobalDateFormatter.sharedInstance().formatDate(moneyTransfer.getTimestamp(), IDateFormatter.FLAGS_DATE_SHORT_TIME_HM);	//Timestamp
						case(1):	//from Account/LegalEntity
							return getName(moneyTransfer.getFrom());
						case(2):	//from type
							return moneyTransfer.getFrom().getAnchorTypeID();
						case(3):	//to Account/LegalEntity
							return getName(moneyTransfer.getTo());
						case(4):	//Amount 
							return GlobalNumberFormatter.sharedInstance().formatCurrency(moneyTransfer.getAmount(), moneyTransfer.getCurrency(), true);
						case(5):	//Description/Reason 
							return moneyTransfer.getDescription();
						case(6):	//Initiator 
							return moneyTransfer.getInitiator().getName();
					}
					return null;
				}
				return null;
			}
		}

		public String getName(Anchor anchor) {
			if (anchor instanceof Account)
				return ((Account)anchor).getName().getText();
			else
				return ((LegalEntity)anchor).getPerson().getDisplayName();
		}
	}
}