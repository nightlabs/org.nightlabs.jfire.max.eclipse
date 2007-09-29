/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview;

import org.nightlabs.jfire.base.ui.overview.OverviewRegistry;


/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TradeOverviewRegistry extends OverviewRegistry {

	private static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.trade.ui.tradeOverview"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	public TradeOverviewRegistry() {
	}
	
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/**
	 * The private static shared member.
	 */
	private static TradeOverviewRegistry sharedInstance;

	/**
	 * Returns (and creates if neccesary) the static shared instance of TradeOverviewRegistry.
	 * @return the static shared instance of TradeOverviewRegistry.
	 */
	public static TradeOverviewRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (TradeOverviewRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new TradeOverviewRegistry();
				}
			}
		}
		return sharedInstance;
	}

}
