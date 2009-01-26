package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.util.Collection;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypeSeverityTypeSelectCreateWizard
extends DynamicPathWizard 
{
	private IssueType issueType;
	private Collection<IssueSeverityType> selectedIssueSeverityTypes;
	
	private IssueTypeSeverityTypeSelectWizardPage severityTypeSelectionPage;
	
	/**
	 * @param issueSeverityType IssueSeverityType - the issue priority for wizard.
	 * @param storeOnserver boolean - send data to server if it's true.
	 * @param fetchGroups
	 */
	public IssueTypeSeverityTypeSelectCreateWizard(IssueType issueType) {
		super();		
		this.issueType = issueType;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeSeverityTypeSelectCreateWizard.title")); //$NON-NLS-1$
	}
	
	@Override
	public void addPages() 
	{
		severityTypeSelectionPage = new IssueTypeSeverityTypeSelectWizardPage(issueType);
		addPage(severityTypeSelectionPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		selectedIssueSeverityTypes = severityTypeSelectionPage.getSelectedIssueSeverityTypes();
		return true;
	}

	public IssueType getIssueType() {
		return issueType;
	}
	
	public Collection<IssueSeverityType> getSelectedIssueSeverities() {
		return selectedIssueSeverityTypes;
	}
}

