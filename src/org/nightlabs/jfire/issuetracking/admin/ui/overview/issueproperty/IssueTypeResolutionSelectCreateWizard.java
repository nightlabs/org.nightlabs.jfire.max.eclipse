package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import java.util.Collection;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueType;

/**
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 *
 */
public class IssueTypeResolutionSelectCreateWizard
extends DynamicPathWizard 
{
	private IssueType issueType;
	private Collection<IssueResolution> selectedIssueResolutions;
	
	private IssueTypeResolutionSelectWizardPage resolutionSelectionPage;
	
	/**
	 * @param issueSeverityType IssueSeverityType - the issue priority for wizard.
	 * @param storeOnserver boolean - send data to server if it's true.
	 * @param fetchGroups
	 */
	public IssueTypeResolutionSelectCreateWizard(IssueType issueType) {
		super();		
		this.issueType = issueType;
		setWindowTitle("New Issue Resolution Wizard");
	}
	
	@Override
	public void addPages() 
	{
		resolutionSelectionPage = new IssueTypeResolutionSelectWizardPage(issueType);
		addPage(resolutionSelectionPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		selectedIssueResolutions = resolutionSelectionPage.getSelectedIssueResolutions();
		return true;
	}

	public IssueType getIssueType() {
		return issueType;
	}
	
	public Collection<IssueResolution> getSelectedIssueResolutions() {
		return selectedIssueResolutions;
	}
}

