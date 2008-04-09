package org.nightlabs.jfire.trade.ui.overview;

import org.nightlabs.jfire.base.ui.overview.OverviewRegistry;
import org.nightlabs.jfire.base.ui.overview.OverviewView;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TradeOverviewView extends OverviewView {

	public static final String VIEW_ID = TradeOverviewView.class.getName();

	/**
	 * 
	 */
	public TradeOverviewView() {
	}
	
	@Override
	protected OverviewRegistry getOverviewRegistry() {
		return TradeOverviewRegistry.sharedInstance();
	}

}
