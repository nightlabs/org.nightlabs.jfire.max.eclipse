package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.lang.reflect.InvocationTargetException;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.id.IssueTypeID;

/**
 * @author Daniel Mazurek
 *
 */
public class CreateIssueTypeWizard extends DynamicPathWizard 
{
	private CreateIssueTypeWizardPage namePage;
	
	public CreateIssueTypeWizard() {
		super();
		setWindowTitle("Create Issue Type");
		setForcePreviousAndNextButtons(false);
	}

	@Override
	public void addPages() 
	{
		namePage = new CreateIssueTypeWizardPage();
		addPage(namePage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() 
	{
		final I18nText name = namePage.getIssueTypeName();
		try {
			final String organisationID = Login.getLogin().getOrganisationID();
			getContainer().run(true, false, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException 
				{
					IssueType issueType = new IssueType(organisationID, IDGenerator.nextIDString(IssueType.class));
					issueType.getName().copyFrom(name);					
					issueType = IssueTypeDAO.sharedInstance().storeIssueTypes(issueType, 
							new String[] {FetchPlan.DEFAULT, IssueType.FETCH_GROUP_NAME}, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
							new ProgressMonitorWrapper(monitor));
					final IssueTypeID issueTypeID = (IssueTypeID) JDOHelper.getObjectId(issueType);
					if (!getShell().isDisposed()) {
						getShell().getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								try {
									RCPUtil.openEditor(new IssueTypeEditorInput(issueTypeID), IssueTypeEditor.EDITOR_ID);
								} catch (PartInitException e) {
									throw new RuntimeException(e);
								}
							}
						});						
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

}
