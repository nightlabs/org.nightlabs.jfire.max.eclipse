package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.widgets.MessageBox;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.wizard.AbstractWizardPageProviderDelegate;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.issue.DuplicateIssueLinkException;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class AttachIssueToObjectWizardDelegate
extends AbstractWizardPageProviderDelegate
{
	protected AttachIssueToObjectWizard getAttachIssueToObjectWizard() {
		return (AttachIssueToObjectWizard) getWizard();
	}

	private AttachIssueSelectIssueLinkTypeWizardPage selectIssueLinkTypePage;
	private SelectAttachedIssueWizardPage selectIssueWizardPage;
	private List<IWizardPage> wizardPages;

	@Override
	public List<? extends IWizardPage> getPages()
	{
		if (wizardPages == null) {
			wizardPages = new ArrayList<IWizardPage>(2);
			selectIssueLinkTypePage = new AttachIssueSelectIssueLinkTypeWizardPage(getAttachIssueToObjectWizard().getAttachedObject());
			selectIssueWizardPage = new SelectAttachedIssueWizardPage(getAttachIssueToObjectWizard().getAttachedObject());
			wizardPages.add(selectIssueLinkTypePage);
			wizardPages.add(selectIssueWizardPage);
		}
		return wizardPages;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.IWizardDelegate#performFinish()
	 */
	@Override
	public boolean performFinish()
	{
		final boolean[] result = new boolean[] { true };
		try {
			getWizard().getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor _monitor) throws InvocationTargetException, InterruptedException
				{
					_monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard.task.createIssueLink"), 100); //$NON-NLS-1$
					//Issue Link Type
					IssueLinkType selectedIssueLinkType = selectIssueLinkTypePage.getSelectedIssueLinkType();

					Issue createdIssue = null;

					//Checking if the issue is new.
					Issue issue = selectIssueWizardPage.getIssue();

					if (JDOHelper.getObjectId(issue) == null) {
						try {
							User reporter = Login.getLogin().getUser(
									new String[] {User.FETCH_GROUP_NAME},
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
									new SubProgressMonitor(new ProgressMonitorWrapper(_monitor), 50)
							);
							issue.setReporter(reporter);

						} catch (LoginException e) {
							throw new RuntimeException(e);
						}

						createdIssue = IssueDAO.sharedInstance().storeIssue(issue, true, AttachIssueToObjectWizard.FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(new ProgressMonitorWrapper(_monitor), 50));
					}
					else {
						//Issue Link
						issue = IssueDAO.sharedInstance().getIssue((IssueID)JDOHelper.getObjectId(issue), AttachIssueToObjectWizard.FETCH_GROUP,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(new ProgressMonitorWrapper(_monitor), 50));
						IssueLink issueLink = issue.createIssueLink(selectedIssueLinkType, getAttachIssueToObjectWizard().getAttachedObject());
						if (issueLink == null) {
							MessageBox msg = new MessageBox(getShell());
							msg.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard.messageBox.hasLinkAlready.text")); //$NON-NLS-1$
							if (msg.open() == 1) {
								return;
							}
						}

						//Store Issue
						try {
							createdIssue = IssueDAO.sharedInstance().storeIssue(issue, true, AttachIssueToObjectWizard.FETCH_GROUP,
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(new ProgressMonitorWrapper(_monitor), 50));
						}
						catch (Exception e) {
							if (ExceptionUtils.indexOfThrowable(e, DuplicateIssueLinkException.class) >= 0) {
//								getContainer().getCurrentPage().getControl().setEnabled(true);
								// display message and close dialog
								MessageDialog.openError(
										getShell(),
										Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard.dialog.duplicateLink.title"), //$NON-NLS-1$
										Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard.dialog.duplicateLink.message")); //$NON-NLS-1$
								result[0] = true;
								return;
							}
							else {
								throw new RuntimeException(e);
							}
						}
					}

					// Open the editor <-- Do we immediately do this after establishing a link to an Issue?
					//                     Now that we've restricted ourselves to only open an Issue page in the Issue perspective, as apposed to previously
					//                     opening the Issue page in the same view as the current Order, it feels a bit strange to lost track of what was
					//                     done previously. See follow-up suggestion below. Kai
//					IssueEditorInput issueEditorInput = new IssueEditorInput((IssueID)JDOHelper.getObjectId(createdIssue));
//					try {
//						Editor2PerspectiveRegistry.sharedInstance().openEditor(issueEditorInput, IssueEditor.EDITOR_ID);
//					} catch (Exception e) {
//						throw new RuntimeException(e);
//					}

					// TODO [Follow-up suggestion]: Kai
					// --> Upon successfully establishing a link to an Issue (either a new Issue or an existing one), simply refresh the IssueTable
					//     displaying all the linked Issue to this particular Order.
					// --> Suppose one wishes to see the related Issue after creating the link, one simply double-clicks on corresponding item
					//     in the IssueTable.
					// --> Of course, not withstanding any (possible) complication(s), the newly linked Issue in the IssueTable shall immediately
					//     be highlighted/given focus/etc.
					//
					getAttachIssueToObjectWizard().setSelectedIssue(createdIssue);
					_monitor.done();
				}
			});
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}


		return result[0];
	}

}
