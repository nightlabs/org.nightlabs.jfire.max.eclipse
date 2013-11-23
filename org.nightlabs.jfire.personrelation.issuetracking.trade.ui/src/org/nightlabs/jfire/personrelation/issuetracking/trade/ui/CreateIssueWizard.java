package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.dao.IssueLinkTypeDAO;
import org.nightlabs.jfire.issuetracking.ui.issue.create.CreateIssueDetailWizardPage;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.personrelation.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class CreateIssueWizard extends DynamicPathWizard
{
	private Person person;
	private Issue issue;
	private CreateIssueDetailWizardPage createIssueDetailWizardPage;

	public CreateIssueWizard(PropertySetID personID, ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.personrelation.issuetracking.trade.ui.CreateIssueWizard.task.initalizingWizard"), 60); //$NON-NLS-1$
		try {
			person = (Person) PropertySetDAO.sharedInstance().getPropertySet(personID, null, 1, new SubProgressMonitor(monitor, 20));

			issue = new Issue(false);
			issue.setReporter(
					Login.sharedInstance().getUser(
							new String[] { FetchPlan.DEFAULT, User.FETCH_GROUP_NAME },
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 20)
					)
			);

			IssueLinkType issueLinkType = IssueLinkTypeDAO.sharedInstance().getIssueLinkType(
					IssueLinkType.ISSUE_LINK_TYPE_ID_RELATED,
					new String[] { FetchPlan.DEFAULT, IssueLinkType.FETCH_GROUP_NAME },
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 20)
			);

			issue.createIssueLink(issueLinkType, person);
		} finally {
			monitor.done();
		}
	}

	@Override
	public void addPages() {
		createIssueDetailWizardPage = new CreateIssueDetailWizardPage(issue);
		addPage(createIssueDetailWizardPage);
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor _monitor) throws InvocationTargetException, InterruptedException
				{
					ProgressMonitor monitor = new ProgressMonitorWrapper(_monitor);
					IssueDAO.sharedInstance().storeIssue(issue, false, null, 1, monitor);
				}
			});
		} catch (Exception x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
			return false;
		}

		return true;
	}

}
