package org.nightlabs.jfire.issuetracking.ui.issue.remind;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class RemindIssueDetailWizardPage 
extends WizardHopPage
{
	//GUI
	private Label optionLbl;
	
	//Used objects
	private Issue selectedIssue;

	public RemindIssueDetailWizardPage(Issue issue) {
		super(RemindIssueDetailWizardPage.class.getName(), "Remind Issue", SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), RemindIssueWizard.class));
		setDescription("Enter detail");
		this.selectedIssue = issue;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 2;

		optionLbl = new Label(mainComposite, SWT.NONE);
		optionLbl.setText("Notify via: ");
		
		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		return getErrorMessage() == null;
	}
}