package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.util.Collection;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueType;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypePrioritySelectCreateWizard
extends DynamicPathWizard 
{
	private IssueType issueType;
	private Collection<IssuePriority> selectedIssuePriorities;
	
	private IssueTypePrioritySelectWizardPage prioritySelectionPage;
	
	/**
	 * @param issuePriority IssuePriority - the issue priority for wizard.
	 * @param storeOnserver boolean - send data to server if it's true.
	 * @param fetchGroups
	 */
	public IssueTypePrioritySelectCreateWizard(IssueType issueType) {
		super();		
		this.issueType = issueType;
		setWindowTitle("New Issue Priority Wizard");
	}
	
	@Override
	public void addPages() 
	{
		prioritySelectionPage = new IssueTypePrioritySelectWizardPage(issueType);
		addPage(prioritySelectionPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		selectedIssuePriorities = prioritySelectionPage.getSelectedIssuePriorities();
		return true;
	}

	public IssueType getIssueType() {
		return issueType;
	}
	
	public Collection<IssuePriority> getSelectedIssuePriorities() {
		return selectedIssuePriorities;
	}
}

