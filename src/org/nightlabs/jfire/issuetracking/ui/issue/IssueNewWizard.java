package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Set;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.i18n.I18nText;
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
			issue = new Issue(issueNewPage.getIssueCreateComposite().getSelectedReporter().getOrganisationID(),
					issueNewPage.getIssueCreateComposite().getSelectedIssuePriority(), 
					issueNewPage.getIssueCreateComposite().getSelectedIssueSeverityType(), 
					issueNewPage.getIssueCreateComposite().getSelectedState(), 
					issueNewPage.getIssueCreateComposite().getSelectedReporter(),
					issueNewPage.getIssueCreateComposite().getSelectedAssigntoUser(),
					null);
			
			I18nText i18nText = issueNewPage.getIssueCreateComposite().getSubjectText().getI18nText();
			Set<String> languageIDs = i18nText.getLanguageIDs();
			for(String languageID : languageIDs){
				issue.getSubject().setText(languageID, i18nText.getText(languageID));
			}//for
			
			i18nText = issueNewPage.getIssueCreateComposite().getDescriptionText().getI18nText();
			languageIDs = i18nText.getLanguageIDs();
			for(String languageID : languageIDs){
				issue.getDescription().setText(languageID, i18nText.getText(languageID));
			}//for
			
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
