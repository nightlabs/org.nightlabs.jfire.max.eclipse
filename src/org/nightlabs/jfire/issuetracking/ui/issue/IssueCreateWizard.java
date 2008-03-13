package org.nightlabs.jfire.issuetracking.ui.issue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class IssueCreateWizard 
extends DynamicPathWizard
{
	private Issue issue;
	private WizardHopPage nextPage;
	private ObjectID linkedObjectID;
	
	public IssueCreateWizard()
	{
		this(null);
	}
	
	/**
	 * Launch the wizard with a linkedObject for which to immediately create a new {@link IssueLink}.
	 *
	 * @param linkedObjectID
	 */
	public IssueCreateWizard(ObjectID linkedObjectID)
	{
		this.linkedObjectID = linkedObjectID;
		
		if (linkedObjectID != null) {
			setWindowTitle("Create link to issue");
		}
		else {
			setWindowTitle("Create new issue");	
		}
		
		issue = new Issue(IDGenerator.getOrganisationID(), IDGenerator.nextID(Issue.class));
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		if (linkedObjectID != null)
			nextPage = new IssueLinkCreateWizardPage(linkedObjectID);
		else
			nextPage = new IssueCreateWizardPage(issue);
		
		addPage(nextPage);
	}

	@Override
	public boolean performFinish() {
		IssueDAO issueDAO = IssueDAO.sharedInstance();

		if (nextPage instanceof IssueCreateWizardPage) {
			IssueCreateWizardPage issueCreateWizardPage = (IssueCreateWizardPage) nextPage;
		
			IssueCreateComposite ic = issueCreateWizardPage.getIssueCreateComposite();
			try {
				issue = new Issue(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Issue.class));
			} catch (LoginException e1) {
				throw new RuntimeException(e1);
			}

			Set<IssueLink> issueLinks = ic.getIssueLinks();
			if(issueLinks != null)
				for (IssueLink	issueLink : issueLinks) {
					issue.getIssueLinks().add(issueLink);
				}

			issue.setIssueType(ic.getSelectedIssueType());
			issue.setIssueSeverityType(ic.getSelectedIssueSeverityType());
			issue.setIssuePriority(ic.getSelectedIssuePriority());
			issue.setReporter(ic.getSelectedReporter());
			issue.setAssignee(ic.getSelectedAssigntoUser());
			
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
			
			I18nText subject = issueCreateWizardPage.getIssueCreateComposite().getSubjectText().getI18nText();
			Set<String> languageIDs = subject.getLanguageIDs();
			for(String languageID : languageIDs){
				issue.getSubject().setText(languageID, subject.getText(languageID));
			}//for

			I18nText description = issueCreateWizardPage.getIssueCreateComposite().getDescriptionText().getI18nText();
			languageIDs = description.getLanguageIDs();
			for(String languageID : languageIDs){
				issue.getDescription().setText(languageID, description.getText(languageID));
			}//for

			Issue newIssue = issueDAO.storeIssue(issue, true, IssueTable.FETCH_GROUPS_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			IssueEditorInput editorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(newIssue));
			try {
				Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, IssueEditor.EDITOR_ID);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		if (nextPage instanceof IssueLinkCreateWizardPage) {
			IssueLinkCreateWizardPage issueLinkCreateWizardPage = (IssueLinkCreateWizardPage)nextPage;
		}

		return true;
	}
	
	@Override
	public boolean canFinish() {
		return true;
	}
}