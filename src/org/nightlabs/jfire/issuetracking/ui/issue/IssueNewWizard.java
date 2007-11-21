package org.nightlabs.jfire.issuetracking.ui.issue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
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
			
			IssueCreateComposite ic = issueNewPage.getIssueCreateComposite();
			issue = new Issue(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Issue.class));

			issue.setIssueType(ic.getSelectedIssueType());
			issue.setSeverityType(ic.getSelectedIssueSeverityType());
			issue.setPriority(ic.getSelectedIssuePriority());
			issue.setReporter(ic.getSelectedReporter());
			issue.setAssigntoUser(ic.getSelectedAssigntoUser());
			
			if(ic.getSelectedAttachmentFileMap() != null){
				Map<String, InputStream> fileMap = ic.getSelectedAttachmentFileMap();
				for(String name : fileMap.keySet()){
					if (fileMap.get(name) != null) {
						try {
							IssueFileAttachment issueFileAttachment = new IssueFileAttachment(issue, IDGenerator.nextID(IssueFileAttachment.class));
							issueFileAttachment.loadStream(fileMap.get(name), name);
							issue.getFileList().add(issueFileAttachment);
						} catch (IOException e) {
							throw new RuntimeException(e);
						} finally {
							try {
								fileMap.get(name).close();
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
					}
				}
			}//if
			
			I18nText subject = issueNewPage.getIssueCreateComposite().getSubjectText().getI18nText();
			Set<String> languageIDs = subject.getLanguageIDs();
			for(String languageID : languageIDs){
				issue.getSubject().setText(languageID, subject.getText(languageID));
			}//for
			
			I18nText description = issueNewPage.getIssueCreateComposite().getDescriptionText().getI18nText();
			languageIDs = description.getLanguageIDs();
			for(String languageID : languageIDs){
				issue.getDescription().setText(languageID, description.getText(languageID));
			}//for
			
			issueDAO.storeIssueWithoutAttachedDocument(issue, true, new String[]{Issue.FETCH_GROUP_THIS}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
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
