package org.nightlabs.jfire.issuetracking.ui.issue.create;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.nightlabs.base.ui.wizard.AbstractPageProvider;
import org.nightlabs.jfire.issue.Issue;

/**
 * @author Daniel Mazurek
 *
 */
public class CreateIssueWizardPageProvider
extends AbstractPageProvider
{
	private Issue issue;

	public CreateIssueWizardPageProvider(Issue issue) {
		this.issue = issue;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.wizard.IPageProvider#getPages()
	 */
	@Override
	public List<? extends IWizardPage> createPages()
	{
		return Collections.singletonList(new CreateIssueDetailWizardPage(issue));
	}

}
