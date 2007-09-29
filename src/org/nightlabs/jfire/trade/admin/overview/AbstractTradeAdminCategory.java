package org.nightlabs.jfire.trade.admin.overview;

import org.eclipse.swt.widgets.Composite;

public abstract class AbstractTradeAdminCategory
implements TradeAdminCategory
{
	private TradeAdminCategoryFactory tradeAdminCategoryFactory;

	public AbstractTradeAdminCategory(TradeAdminCategoryFactory tradeAdminCategoryFactory)
	{
		this.tradeAdminCategoryFactory = tradeAdminCategoryFactory;
	}

	public TradeAdminCategoryFactory getTradeAdminCategoryFactory()
	{
		return tradeAdminCategoryFactory;
	}

	private Composite composite;

	/**
	 * When extending <code>AbstractTradeAdminCategory</code> you should <b>not</b>
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
