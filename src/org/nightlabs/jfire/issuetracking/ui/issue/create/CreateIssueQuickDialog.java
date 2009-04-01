package org.nightlabs.jfire.issuetracking.ui.issue.create;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.Editor2PerspectiveRegistry;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLocal;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.progress.NullProgressMonitor;

public class CreateIssueQuickDialog 
extends ResizableTitleAreaDialog
{
	public CreateIssueQuickDialog(Shell shell) {
		super(shell, Messages.RESOURCE_BUNDLE);
	}

	private QuickCreateIssueComposite quickCreateComposite;
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Create Issue");
		setMessage("Create an Issue");

		Composite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER);
		quickCreateComposite = new QuickCreateIssueComposite(wrapper, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		quickCreateComposite.setLayoutData(gridData);
		return wrapper;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Title");
	}
	
	private static String[] FETCH_GROUP_ISSUE = new String[]{
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_STATES,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		Issue.FETCH_GROUP_ISSUE_TYPE,
		IssueLocal.FETCH_GROUP_STATE,
		IssueLocal.FETCH_GROUP_STATES,
		Statable.FETCH_GROUP_STATE,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
	};
	
	@Override
	protected void okPressed() {
		try {
			Issue issue = IssueDAO.sharedInstance().storeIssue(quickCreateComposite.getCreatingIssue(), true, FETCH_GROUP_ISSUE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			IssueEditorInput editorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(issue));
			try {
				Editor2PerspectiveRegistry.sharedInstance().openEditor(editorInput, IssueEditor.EDITOR_ID);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		super.okPressed();
	}
}