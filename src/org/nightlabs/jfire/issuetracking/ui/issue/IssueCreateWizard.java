package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;

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

//		I18nText subject = issueNewPage.getIssueCreateComposite().getSubjectText().getI18nText();
//		Set<String> languageIDs = subject.getLanguageIDs();
//		for(String languageID : languageIDs){
//			issue.getSubject().setText(languageID, subject.getText(languageID));
//		}//for
//
//		I18nText description = issueNewPage.getIssueCreateComposite().getDescriptionText().getI18nText();
//		languageIDs = description.getLanguageIDs();
//		for(String languageID : languageIDs){
//			issue.getDescription().setText(languageID, description.getText(languageID));
//		}//for
//
//		Issue newIssue = issueDAO.storeIssue(issue, true, IssueTable.FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//		IssueEditorInput editorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(newIssue));
//		try {
//			Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, IssueEditor.EDITOR_ID);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}

		return true;
	}
	
	@Override
	public boolean canFinish() {
		return true;
	}
}
