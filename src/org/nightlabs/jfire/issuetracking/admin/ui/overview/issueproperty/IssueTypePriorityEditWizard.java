/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.dao.IssuePriorityDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypePriorityEditWizard 
extends DynamicPathWizard 
{
	private IssuePriority issuePriority;
	private IssueTypePriorityGeneralWizardPage priorityCreatePage;
	
	private boolean storeOnServer;
	private String[] fetchGroups;
	
	/**
	 * @param issuePriority IssuePriority - the issue priority for wizard.
	 * @param storeOnserver boolean - send data to server if it's true.
	 * @param fetchGroups
	 */
	public IssueTypePriorityEditWizard(IssuePriority issuePriority, boolean storeOnServer, String[] fetchGroups) {
		super();		
		this.issuePriority = issuePriority;
		this.storeOnServer = storeOnServer || issuePriority == null;
		this.fetchGroups = fetchGroups;
		setWindowTitle("New Issue Type Wizard");
	}
	
	@Override
	public void addPages() {
		priorityCreatePage = new IssueTypePriorityGeneralWizardPage(issuePriority);
		addPage(priorityCreatePage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		issuePriority = priorityCreatePage.getPriorityComposite().getIssuePriority();
		if (storeOnServer) {
			issuePriority = IssuePriorityDAO.sharedInstance().storeIssuePriority(issuePriority, true, fetchGroups, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		}
		return issuePriority != null;
	}

	public IssuePriority getIssuePriority() {
		return issuePriority;
	}
}
