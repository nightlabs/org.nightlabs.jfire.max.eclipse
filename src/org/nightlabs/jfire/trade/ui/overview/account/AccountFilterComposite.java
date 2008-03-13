package org.nightlabs.jfire.trade.ui.overview.account;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.JDOQueryComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AccountFilterComposite
	extends AbstractQueryFilterComposite<Account, AccountQuery>
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public AccountFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<Account, ? super AccountQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public AccountFilterComposite(Composite parent, int style,
		QueryProvider<Account, ? super AccountQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	@Override
	public Class<AccountQuery> getQueryClass() {
		return AccountQuery.class;
	}

	@Override
	protected List<JDOQueryComposite<Account, AccountQuery>> registerJDOQueryComposites()
	{
		List<JDOQueryComposite<Account, AccountQuery>> queryComps =
			new ArrayList<JDOQueryComposite<Account, AccountQuery>>();
		
		queryComps.add(accountSearchComposite);
		
		return queryComps;
	}

	private AccountSearchComposite accountSearchComposite;
	protected Composite createAccountComp()
	{
		accountSearchComposite = new AccountSearchComposite(this, SWT.NONE,
				LayoutMode.TOTAL_WRAPPER, LayoutDataMode.GRID_DATA);
		accountSearchComposite.setToolkit(getToolkit());
		return accountSearchComposite;
	}

	@Override
	protected void createContents()
	{
		createAccountComp();
	}

}
