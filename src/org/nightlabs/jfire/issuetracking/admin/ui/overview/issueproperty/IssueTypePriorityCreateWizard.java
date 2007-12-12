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
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueTypePriorityCreateWizard 
extends DynamicPathWizard {

	private IssuePriority issuePriority;
	private IssueTypePriorityCreateWizardPage priorityCreatePage;
	
	private boolean createPriority;
	private boolean storeOnServer;
	private String[] fetchGroups;
	
	public IssueTypePriorityCreateWizard(IssuePriority issuePriority, boolean storeOnServer, String[] fetchGroups) {
		super();		
		this.issuePriority = issuePriority;
		this.storeOnServer = storeOnServer || issuePriority == null;
		this.fetchGroups = fetchGroups;
		setWindowTitle("New Issue Type Wizard");
	}
	
	@Override
	public void addPages() 
	{
		priorityCreatePage = new IssueTypePriorityCreateWizardPage(issuePriority);
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
		return true;
	}

	public IssuePriority getIssuePriority() {
		return issuePriority;
	}
}
