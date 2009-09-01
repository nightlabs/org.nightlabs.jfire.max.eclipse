package org.nightlabs.jfire.issuetracking.ui.issue;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueDescriptionView
extends LSDViewPart
{
	public static final String VIEW_ID = IssueDescriptionView.class.getName();

	private IMemento initMemento = null;
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.initMemento = memento;
	}

	private IssueDescriptionDetailComposite descriptionDetailComposite;
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent)
	{
		descriptionDetailComposite = new IssueDescriptionDetailComposite(parent, SWT.NONE);
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
		Issue.FETCH_GROUP_DESCRIPTION};

	private Issue issue;
	public void setIssue(Issue issue) {
		this.issue = IssueDAO.sharedInstance().getIssue(
				(IssueID)JDOHelper.getObjectId(issue),
				FETCH_GROUP,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());

		if (descriptionDetailComposite != null)
			descriptionDetailComposite.setIssueDescription(this.issue.getDescription());
	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		if (issue != null)
			setIssue(issue);
	}
}