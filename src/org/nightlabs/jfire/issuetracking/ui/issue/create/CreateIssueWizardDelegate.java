package org.nightlabs.jfire.issuetracking.ui.issue.create;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.base.ui.wizard.AbstractWizardDelegate;
import org.nightlabs.base.ui.wizard.IPageProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLocal;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel Mazurek
 *
 */
public class CreateIssueWizardDelegate
extends AbstractWizardDelegate
{
	private static String[] FETCH_GROUP = new String[]{
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_STATES,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		Issue.FETCH_GROUP_ISSUE_TYPE,
		IssueLocal.FETCH_GROUP_STATE,
		IssueLocal.FETCH_GROUP_STATES,
		Statable.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_ISSUE_LINKS,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
	};

	protected Issue newIssue;

	public CreateIssueWizardDelegate()
	{
		newIssue = new Issue(IDGenerator.getOrganisationID(), IDGenerator.nextID(Issue.class));
		newIssue.setReporter(Login.sharedInstance().getUser(new String[]{User.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()));
	}

	@Override
	protected IPageProvider createPageProvider() {
		return new CreateIssueWizardPageProvider(newIssue);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.wizard.IWizardActionHandler#performFinish(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean performFinish()
	{
		Issue issue = IssueDAO.sharedInstance().storeIssue(newIssue, true, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		IssueEditorInput editorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(issue));
		try {
			Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, IssueEditor.EDITOR_ID);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

//	@Override
//	public void prepare(IProgressMonitor monitor)
//	{
//		newIssue = new Issue(IDGenerator.getOrganisationID(), IDGenerator.nextID(Issue.class));
//		newIssue.setReporter(Login.sharedInstance().getUser(new String[]{User.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
//	}

}
