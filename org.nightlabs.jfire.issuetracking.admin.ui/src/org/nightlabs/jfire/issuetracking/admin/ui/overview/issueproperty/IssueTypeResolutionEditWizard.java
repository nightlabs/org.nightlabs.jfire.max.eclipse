/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypeResolutionEditWizard 
extends DynamicPathWizard 
{
	private IssueResolution issueResolution;
	private IssueTypeResolutionGeneralWizardPage resolutionCreatePage;
	
	private boolean storeOnServer;
	private String[] fetchGroups;
	
	/**
	 * @param issueResolution IssueResolution - the issue resolution for wizard.
	 * @param storeOnserver boolean - send data to server if it's true.
	 * @param fetchGroups
	 */
	public IssueTypeResolutionEditWizard(IssueResolution issueResolution, boolean storeOnServer, String[] fetchGroups) {
		super();		
		this.issueResolution = issueResolution;
		this.storeOnServer = storeOnServer || issueResolution == null;
		this.fetchGroups = fetchGroups;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeResolutionEditWizard.title")); //$NON-NLS-1$
	}
	
	@Override
	public void addPages() {
		resolutionCreatePage = new IssueTypeResolutionGeneralWizardPage(issueResolution);
		addPage(resolutionCreatePage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		issueResolution = resolutionCreatePage.getResolutionComposite().getIssueResolution();
		if (storeOnServer) {
//			issueResolution = IssueResolutionDAO.sharedInstance().storeIssueResolution(issueResolution, true, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		}
		return issueResolution != null;
	}

	public IssueResolution getIssueSeverityType() {
		return issueResolution;
	}
}
