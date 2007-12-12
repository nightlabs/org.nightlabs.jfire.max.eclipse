/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.IssuePriority;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueTypePriorityCreateWizard 
extends DynamicPathWizard {

	private IssuePriority issuePriority;
	
	public IssueTypePriorityCreateWizard(IssuePriority issuePriority) {
		super();
		this.issuePriority = issuePriority;
		setWindowTitle("New Issue Type Wizard");
	}
	
	@Override
	public void addPages() 
	{
		IssueTypePriorityCreateWizardPage createPage = new IssueTypePriorityCreateWizardPage(issuePriority);
		addPage(createPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	public IssuePriority getIssuePriority() {
		return issuePriority;
	}
}
