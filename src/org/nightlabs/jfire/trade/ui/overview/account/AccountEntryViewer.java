package org.nightlabs.jfire.trade.ui.overview.account;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.progress.ProgressMonitor;

/**
 * implementation of a {@link JDOQuerySearchEntryViewer} for searching and 
 * displaying {@link Account}s
 * 
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class AccountEntryViewer  
extends JDOQuerySearchEntryViewer
{	
	public AccountEntryViewer(Entry entry) {
		super(entry);
	}

	public AbstractTableComposite<Account> createListComposite(Composite parent) {
		return new AccountListComposite(parent, SWT.NONE);
	}
	
	public AbstractQueryFilterComposite createFilterComposite(Composite parent) {
		return new AccountFilterComposite(parent, SWT.NONE);
	}

	public static final String[] FETCH_GROUPS_ACCOUNTS = new String[] {
		Account.FETCH_GROUP_THIS_ACCOUNT, 
		FetchPlan.DEFAULT,
		LegalEntity.FETCH_GROUP_PERSON
	};

	@Override
	protected Object getQueryResult(Collection<JDOQuery> queries, ProgressMonitor monitor) 
	{
		try {
			return AccountDAO.sharedInstance().getAccountsForQueries(
					queries,
					FETCH_GROUPS_ACCOUNTS, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					monitor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
