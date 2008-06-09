package org.nightlabs.jfire.issuetracking.ui.issue.create;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateIssueWizard 
extends DynamicPathWizard
{
	private Issue newIssue;
	private ObjectID linkedObjectID;

	private CreateIssueDetailWizardPage issueCreateGeneralWizardPage;
	private CreateIssueOptionalWizardPage issueCreateDetailWizardPage;

	/**
	 * Launch the wizard with a linkedObject for which to immediately create a new {@link IssueLink}.
	 *
	 * @param linkedObjectID
	 */
	public CreateIssueWizard()
	{
		setWindowTitle("Create new issue");	
		newIssue = new Issue(IDGenerator.getOrganisationID(), IDGenerator.nextID(Issue.class));
		newIssue.setReporter(Login.sharedInstance().getUser(new String[]{User.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new org.eclipse.core.runtime.NullProgressMonitor()));
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		issueCreateGeneralWizardPage = new CreateIssueDetailWizardPage(newIssue);
		addPage(issueCreateGeneralWizardPage);

		issueCreateDetailWizardPage = new CreateIssueOptionalWizardPage(newIssue);
		addPage(issueCreateDetailWizardPage);
	}

	private static String[] FETCH_GROUP = new String[]{
		FetchPlan.DEFAULT
	};

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor _monitor) throws InvocationTargetException, InterruptedException {
					Issue issue = IssueDAO.sharedInstance().storeIssue(newIssue, true, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					IssueEditorInput editorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(issue));
					try {
						Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, IssueEditor.EDITOR_ID);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	@Override
	public boolean canFinish() {
		return issueCreateGeneralWizardPage.isPageComplete();
	}
}