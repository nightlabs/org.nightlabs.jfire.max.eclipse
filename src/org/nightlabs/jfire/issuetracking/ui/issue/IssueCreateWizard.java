package org.nightlabs.jfire.issuetracking.ui.issue;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
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
	private Issue newIssue;
	private ObjectID linkedObjectID;

	private IssueCreateWizardPage issueCreateWizardPage;
	
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
//		this.linkedObjectID = linkedObjectID;
//		
//		if (linkedObjectID != null) {
//			setWindowTitle("Create link to issue");
//		}
//		else {
//			setWindowTitle("Create new issue");	
//		}

		newIssue = new Issue(IDGenerator.getOrganisationID(), IDGenerator.nextID(Issue.class));
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		issueCreateWizardPage = new IssueCreateWizardPage(newIssue);
		addPage(issueCreateWizardPage);
	}

	@Override
	public boolean performFinish() {
		/************Create New Issue***************/
//		if (nextPage instanceof IssueCreateWizardPage) {
//			IssueCreateWizardPage issueCreateWizardPage = (IssueCreateWizardPage) nextPage;
		
//			IssueCreateComposite issueCreateComposite = issueCreateWizardPage.getIssueCreateComposite();
//			try {
//				newIssue = new Issue(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Issue.class));
//			} catch (LoginException e1) {
//				throw new RuntimeException(e1);
//			}

//			Set<IssueLinkTableItem> linkItems = issueCreateComposite.getIssueLinkAdderComposite().getIssueLinkTableItems();
//			for (IssueLinkTableItem linkItem : linkItems) {
//				IssueLinkHandler handler = issueCreateComposite.getIssueLinkAdderComposite().getIssueLinkTable().getIssueLinkHandler(linkItem.getLinkedObjectID());
//				newIssue.createIssueLink(linkItem.getIssueLinkType(), linkedObject);
////				IssueLink issueLink = new IssueLink(newIssue.getOrganisationID(), 
////						IDGenerator.nextID(IssueLink.class), 
////						newIssue, 
////						linkItem.getIssueLinkType(),
////						handler.getLinkedObject(linkItem.getLinkedObjectID(), new NullProgressMonitor()));
//
////				newIssue.getIssueLinks().add(issueLink);
//			}

			// TODO this should be done in the IssueCreateWizardPage
//			newIssue.setIssueType(issueCreateComposite.getSelectedIssueType());
//			newIssue.setIssueSeverityType(issueCreateComposite.getSelectedIssueSeverityType());
//			newIssue.setIssuePriority(issueCreateComposite.getSelectedIssuePriority());
//			newIssue.setReporter(issueCreateComposite.getSelectedReporter());
//			newIssue.setAssignee(issueCreateComposite.getSelectedAssignToUser());
//
//			if(issueCreateComposite.getSelectedAttachmentFileMap() != null){
//				Map<String, InputStream> fileMap = issueCreateComposite.getSelectedAttachmentFileMap();
//				for(String name : fileMap.keySet()){
//					if (fileMap.get(name) != null) {
//						try {
//							IssueFileAttachment issueFileAttachment = new IssueFileAttachment(newIssue, IDGenerator.nextID(IssueFileAttachment.class));
//							issueFileAttachment.loadStream(fileMap.get(name), name);
//							newIssue.getFileList().add(issueFileAttachment);
//						} catch (IOException e) {
//							throw new RuntimeException(e);
//						} finally {
//							try {
//								fileMap.get(name).close();
//							} catch (IOException e) {
//								throw new RuntimeException(e);
//							}
//						}
//					}
//				}
//			}//if
//			
//			I18nText subject = issueCreateWizardPage.getIssueCreateComposite().getSubjectText().getI18nText();
//			Set<String> languageIDs = subject.getLanguageIDs();
//			for(String languageID : languageIDs){
//				newIssue.getSubject().setText(languageID, subject.getText(languageID));
//			}//for
//
//			I18nText description = issueCreateWizardPage.getIssueCreateComposite().getDescriptionText().getI18nText();
//			languageIDs = description.getLanguageIDs();
//			for(String languageID : languageIDs){
//				newIssue.getDescription().setText(languageID, description.getText(languageID));
//			}//for
//		}
		
//		/**********Attach Objects to an issue**********/
//		if (nextPage instanceof IssueLinkAttachWizardPage) {
//			IssueLinkAttachWizardPage issueLinkCreateWizardPage = (IssueLinkAttachWizardPage)nextPage;
//			
//			
//		}
		
		/*****Open the created issue*****/
		// TODO this should be done on a worker thread! Use the Wizard.getContainer().run(...) method!
		Issue issue = IssueDAO.sharedInstance().storeIssue(newIssue, true, IssueTable.FETCH_GROUPS_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		IssueEditorInput editorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(issue));
		try {
			Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, IssueEditor.EDITOR_ID);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return true;
	}

//	@Override
//	public boolean canFinish() {
//		return nextPage.canBeLastPage();
//	}
}