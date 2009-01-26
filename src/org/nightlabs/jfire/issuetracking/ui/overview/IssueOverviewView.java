package org.nightlabs.jfire.issuetracking.ui.overview;

import org.nightlabs.jfire.base.ui.overview.OverviewView;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class IssueOverviewView
	extends OverviewView
{
	public static final String VIEW_ID = IssueOverviewView.class.getName();
	public static final String SHELF_SCOPE = "IssueShelf";  //$NON-NLS-1$

	public IssueOverviewView()
	{
	}
	
	@Override
	protected String getScope()
	{
		return SHELF_SCOPE;
	}

}
