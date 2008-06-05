package org.nightlabs.jfire.issuetracking.ui.issue.create;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItem;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class CreateIssueWizardPage 
extends WizardHopPage
{
	private Issue issue;
	private CreateIssueComposite issueCreateComposite;
	
	public CreateIssueWizardPage(Issue issue){
		super(CreateIssueWizardPage.class.getName(), "New Issue");
		setDescription("Create a new issue.");
		this.issue = issue;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		issueCreateComposite = new CreateIssueComposite(this, mainComposite, SWT.NONE, issue);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		issueCreateComposite.setLayoutData(gridData);

		if (issueLinkTableItem != null) {
			issueCreateComposite.getIssueLinkAdderComposite().getIssueLinkTable().addIssueLinkTableItem(issue, issueLinkTableItem);
		}
		
		return mainComposite;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			updatePageComplete();
		}
		super.setVisible(visible);
	}

	public void updatePageComplete() {
		if(issueCreateComposite.getSubjectText().getEditText().equals("")){
			setErrorMessage("The subject should not be empty.");
			getContainer().updateButtons();
			return;
		}//if
		
		if(issueCreateComposite.getSelectedReporter() == null){
			setErrorMessage("The reporter should not be null");
			getContainer().updateButtons();
			return;
		}
		else{
			setErrorMessage(null);
			getContainer().updateButtons();
		}
	}

	@Override
	public boolean isPageComplete() {
		return getErrorMessage() == null;
	}
	
	public CreateIssueComposite getIssueCreateComposite(){
		return issueCreateComposite;
	}
	
	private IssueLinkTableItem issueLinkTableItem;
	public void setIssueLinkTableItem(IssueLinkTableItem issueLinkTableItem) {
		this.issueLinkTableItem = issueLinkTableItem;
	}
}
