/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.overview;

import org.nightlabs.jfire.base.ui.overview.OverviewRegistry;
import org.nightlabs.jfire.base.ui.overview.OverviewView;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class IssueOverviewView extends OverviewView {

	public static final String VIEW_ID = IssueOverviewView.class.getName();

	/**
	 * 
	 */
	public IssueOverviewView() {
	}
	
	@Override
	protected OverviewRegistry getOverviewRegistry() {
		return IssueOverviewRegistry.sharedInstance();
	}

}
