package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueManager;
import org.nightlabs.jfire.issue.IssueManagerHome;
import org.nightlabs.jfire.issue.IssueManagerUtil;

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
			IssueManagerHome issueManagerHome = (IssueManagerHome) IssueManagerUtil.getHome(Login.getLogin().getInitialContextProperties());
			IssueManager issueManager = issueManagerHome.create();
			
//			issue = issueManager....
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
