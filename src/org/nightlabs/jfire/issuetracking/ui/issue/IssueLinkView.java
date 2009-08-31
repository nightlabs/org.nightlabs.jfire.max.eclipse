package org.nightlabs.jfire.issuetracking.ui.issue;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueLinkView
extends LSDViewPart
{
	public static final String VIEW_ID = IssueLinkView.class.getName();

	public IssueLinkView() {
		super();
	}

	private IMemento initMemento = null;
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.initMemento = memento;
	}

	private IssueLinkTable issueLinkTable;
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent)
	{
		issueLinkTable = new IssueLinkTable(parent, SWT.NONE);
		issueLinkTable.setLayoutData(new GridData(GridData.FILL_BOTH));
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

	private static String[] FETCH_GROUP = new String[] {
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_ISSUE_LINKS,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		IssueLink.FETCH_GROUP_ISSUE_LINK_TYPE,
		IssueLink.FETCH_GROUP_LINKED_OBJECT,
		IssueLink.FETCH_GROUP_LINKED_OBJECT_CLASS,
		IssueLinkType.FETCH_GROUP_NAME};

	private Issue issue;
	public void setIssue(Issue issue) {
		this.issue = IssueDAO.sharedInstance().getIssue(
				(IssueID)JDOHelper.getObjectId(issue),
				FETCH_GROUP,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());

		if (issueLinkTable != null)
			issueLinkTable.setIssue(this.issue);
	}
}