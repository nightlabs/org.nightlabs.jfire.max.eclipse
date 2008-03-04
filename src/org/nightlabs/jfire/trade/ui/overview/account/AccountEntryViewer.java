package org.nightlabs.jfire.trade.ui.overview.account;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryMap;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.progress.ProgressMonitor;

/**
 * implementation of a {@link JDOQuerySearchEntryViewer} for searching and
 * displaying {@link Account}s
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AccountEntryViewer
	extends JDOQuerySearchEntryViewer<Account, AccountQuery>
{
	public AccountEntryViewer(Entry entry) {
		super(entry);
	}

	@Override
	public AbstractTableComposite<Account> createListComposite(Composite parent) {
		return new AccountListComposite(parent, SWT.NONE);
	}
	
//	@Override
//	public AbstractQueryFilterComposite createFilterComposite(Composite parent) {
//		return new AccountFilterComposite(parent, SWT.NONE);
//	}

	public static final String[] FETCH_GROUPS_ACCOUNTS = new String[] {
//		Account.FETCH_GROUP_THIS_ACCOUNT, // we don't need the summaryAccounts that are included in this - better specify individually
		Account.FETCH_GROUP_NAME,
		Account.FETCH_GROUP_OWNER,
		Account.FETCH_GROUP_CURRENCY,
		Account.FETCH_GROUP_ACCOUNT_TYPE,
		AccountType.FETCH_GROUP_NAME,
		FetchPlan.DEFAULT,
		LegalEntity.FETCH_GROUP_PERSON
	};

	@Override
	protected Class<Account> getResultType()
	{
		return Account.class;
	}

	@Override
	protected Collection<Account> doSearch(QueryMap<Account, ? extends AccountQuery> queryMap,
		ProgressMonitor monitor)
	{
		return AccountDAO.sharedInstance().getAccountsForQueries(
			queryMap,
			FETCH_GROUPS_ACCOUNTS,
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
			monitor
			);
	}

}
