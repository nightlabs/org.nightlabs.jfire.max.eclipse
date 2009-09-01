package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.history.FetchGroupsIssueHistoryItem;
import org.nightlabs.jfire.issue.history.IssueHistoryItem;
import org.nightlabs.jfire.issue.history.IssueHistoryItemDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueHistoryTable;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueHistoryView
extends LSDViewPart
{
	public static final String VIEW_ID = IssueHistoryView.class.getName();

	private IMemento initMemento = null;
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.initMemento = memento;
	}

	private IssueHistoryTable issueHistoryTable;
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent)
	{
		issueHistoryTable = new IssueHistoryTable(parent, SWT.NONE);
		issueHistoryTable.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.login.part.LSDViewPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
	}

	private Issue issue;
	public void setIssue(Issue issue) {
		this.issue = issue;

		if (issueHistoryTable != null) {
			IssueID issueID = (IssueID)JDOHelper.getObjectId(issue);
			Collection<IssueHistoryItem> issueHistoryItems = IssueHistoryItemDAO.sharedInstance().getIssueHistoryItems(
					issueID,
					new String[]{FetchPlan.DEFAULT, FetchGroupsIssueHistoryItem.FETCH_GROUP_LIST},	// Since 28 May 2009.
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());

			issueHistoryTable.setInput(issueHistoryItems);
		}
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		if (issue != null)
			setIssue(issue);
	}
}