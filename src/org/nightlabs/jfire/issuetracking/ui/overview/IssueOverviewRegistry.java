package org.nightlabs.jfire.issuetracking.ui.overview;

import org.nightlabs.jfire.base.ui.overview.OverviewRegistry;

public class IssueOverviewRegistry 
extends OverviewRegistry
{
private static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.issuetracking.ui.issueOverview"; //$NON-NLS-1$
	
	/**
	 * 
	 */
	public IssueOverviewRegistry() {
	}
	
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/**
	 * The private static shared member.
	 */
	private static IssueOverviewRegistry sharedInstance;

	/**
	 * Returns (and creates if neccesary) the static shared instance of TradeOverviewRegistry.
	 * @return the static shared instance of TradeOverviewRegistry.
	 */
	public static IssueOverviewRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (IssueOverviewRegistry.class) {
				if (sharedInstance == null) {
					sharedInstance = new IssueOverviewRegistry();
				}
			}
		}
		return sharedInstance;
	}

}
