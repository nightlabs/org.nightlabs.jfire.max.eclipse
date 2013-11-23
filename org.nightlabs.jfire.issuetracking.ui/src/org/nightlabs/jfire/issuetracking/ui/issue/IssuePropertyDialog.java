package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.eclipse.ui.dialog.ResizableTitleAreaDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 *
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 *
 */
public class IssuePropertyDialog
extends ResizableTitleAreaDialog
{

	private IssuePropertyComposite issuePropertyComposite;
	private Collection<Issue> issues;

	public IssuePropertyDialog(Collection<Issue> issues, Shell shell) {
		super(shell, null);
		this.issues = issues;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssuePropertyDialog.title")); //$NON-NLS-1$
		setMessage(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssuePropertyDialog.message")); //$NON-NLS-1$
		Composite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER) {
			@Override
			public boolean setFocus() {
				return getButton(OK).forceFocus();
			}
		};
		issuePropertyComposite = new IssuePropertyComposite(issues, wrapper, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		issuePropertyComposite.setLayoutData(gridData);
		return wrapper;
	}

	public IssueType getSelectedIssueType() {
		return issuePropertyComposite.getSelectedIssueType();
	}

	public IssuePriority getSelectedIssuePriority() {
		return issuePropertyComposite.getSelectedIssuePriority();
	}

	public IssueSeverityType getSelectedIssueSeverityType() {
		return issuePropertyComposite.getSelectedIssueSeverityType();
	}

	public IssueResolution getSelectedIssueResolution() {
		return issuePropertyComposite.getSelectedIssueResolution();
	}
}