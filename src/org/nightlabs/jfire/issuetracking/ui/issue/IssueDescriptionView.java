package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.issue.Issue;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueDescriptionView
extends LSDViewPart
{
	public static final String VIEW_ID = IssueDescriptionView.class.getName();

	public IssueDescriptionView() {
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

	private Issue issue;
	public void setIssue(Issue issue) {
		this.issue = issue;

		if (descriptionDetailComposite != null)
			descriptionDetailComposite.setIssueDescription(issue.getDescription());
	}
}