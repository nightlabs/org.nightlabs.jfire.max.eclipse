/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.dao.IssueSeverityTypeDAO;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypeSeverityTypeEditWizard 
extends DynamicPathWizard 
{
	private IssueSeverityType issueSeverityType;
	private IssueTypeSeverityTypeGeneralWizardPage severityTypeCreatePage;
	
	private boolean storeOnServer;
	private String[] fetchGroups;
	
	/**
	 * @param issueSeverityType IssuePriority - the issue priority for wizard.
	 * @param storeOnserver boolean - send data to server if it's true.
	 * @param fetchGroups
	 */
	public IssueTypeSeverityTypeEditWizard(IssueSeverityType issueSeverityType, boolean storeOnServer, String[] fetchGroups) {
		super();		
		this.issueSeverityType = issueSeverityType;
		this.storeOnServer = storeOnServer || issueSeverityType == null;
		this.fetchGroups = fetchGroups;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeSeverityTypeEditWizard.title")); //$NON-NLS-1$
	}
	
	@Override
	public void addPages() {
		severityTypeCreatePage = new IssueTypeSeverityTypeGeneralWizardPage(issueSeverityType);
		addPage(severityTypeCreatePage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		issueSeverityType = severityTypeCreatePage.getSeverityTypeComposite().getIssueSeverityType();
		if (storeOnServer) {
			issueSeverityType = IssueSeverityTypeDAO.sharedInstance().storeIssueSeverityType(issueSeverityType, true, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		}
		return issueSeverityType != null;
	}

	public IssueSeverityType getIssueSeverityType() {
		return issueSeverityType;
	}
}
