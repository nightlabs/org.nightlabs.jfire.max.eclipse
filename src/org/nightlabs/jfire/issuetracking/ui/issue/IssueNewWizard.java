package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueNewWizard extends DynamicPathWizard{
	private IssueNewWizardPage issueNewPage;
	
	public IssueNewWizard(){
		setWindowTitle("Wizard Title");
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		issueNewPage = new IssueNewWizardPage();
		addPage(issueNewPage);
	}

	@Override
	public boolean performFinish() {
		Issue issue = null;
		try {
			IssueDAO issueDAO = IssueDAO.sharedInstance();
			issue = new Issue(issueNewPage.getIssueCreateComposite().getSelectedIssuePriority(), 
					issueNewPage.getIssueCreateComposite().getSelectedIssueSeverityType(), 
					issueNewPage.getIssueCreateComposite().getSelectedIssueStatus(), 
					issueNewPage.getIssueCreateComposite().getSelectedUser(),
					null);
			
			issue.setOrganisationID(Login.getLogin().getOrganisationID());
			issueDAO.createIssueWithoutAttachedDocument(issue, true, new String[]{Issue.FETCH_GROUP_THIS}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return issue == null? false:true;
	}
	
	@Override
	public boolean canFinish() {
		return true;
	}
}
