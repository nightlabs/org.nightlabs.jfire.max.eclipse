package org.nightlabs.jfire.trade.ui.overview.account;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class AccountFilterComposite
extends AbstractQueryFilterComposite
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public AccountFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public AccountFilterComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class getQueryClass() {
		return Account.class;
	}

	@Override
	protected List<JDOQueryComposite> registerJDOQueryComposites()
	{
		List<JDOQueryComposite> queryComps = new ArrayList<JDOQueryComposite>();
		queryComps.add(accountSearchComposite);
		return queryComps;
	}

	@Override
	protected void createContents(Composite parent) {
		createAccountComp(parent);
	}

	private AccountSearchComposite accountSearchComposite;
	protected Composite createAccountComp(Composite parent)
	{
		accountSearchComposite = new AccountSearchComposite(parent, SWT.NONE,
				LayoutMode.TOTAL_WRAPPER, LayoutDataMode.GRID_DATA);
		accountSearchComposite.setToolkit(getToolkit());
		return accountSearchComposite;
	}

}
