package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractIssueCategory
implements IssueCategory
{
	private IssueCategoryFactory issueCategoryFactory;

	public AbstractIssueCategory(IssueCategoryFactory tradeAdminCategoryFactory)
	{
		this.issueCategoryFactory = tradeAdminCategoryFactory;
	}

	public IssueCategoryFactory getTradeAdminCategoryFactory()
	{
		return issueCategoryFactory;
	}

	private Composite composite;

	/**
	 * When extending <code>AbstractIssueCategory</code> you should <b>not</b>
	 * override this method, but instead implement {@link #_createComposite(Composite)}.
	 *
	 * {@inheritDoc}
	 */
	public Composite createComposite(Composite parent)
	{
		composite = _createComposite(parent);
		return composite;
	}

	protected abstract Composite _createComposite(Composite parent);

	public Composite getComposite()
	{
		return composite;
	}
}
