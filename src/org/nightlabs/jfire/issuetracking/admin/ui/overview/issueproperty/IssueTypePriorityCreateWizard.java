/**
 * 
 */
package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueTypePriorityCreateWizard 
extends DynamicPathWizard {

	public IssueTypePriorityCreateWizard() {
		super();
		setWindowTitle("New Issue Type Wizard");
	}
	
	@Override
	public void addPages() 
	{
		IssueTypePriorityCreateWizardPage createPage = new IssueTypePriorityCreateWizardPage();
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

}
