package org.nightlabs.jfire.trade.ui.overview;

import org.nightlabs.jfire.base.ui.overview.OverviewView;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TradeOverviewView
	extends OverviewView
{
	public static final String VIEW_ID = TradeOverviewView.class.getName();
	public static final String SHELF_SCOPE = "TradeShelf";
	
	public TradeOverviewView()
	{
	}
	
	@Override
	protected String getScope()
	{
		return SHELF_SCOPE;
	}

}
