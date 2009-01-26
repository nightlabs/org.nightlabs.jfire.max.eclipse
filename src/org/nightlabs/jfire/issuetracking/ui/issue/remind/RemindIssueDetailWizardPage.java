package org.nightlabs.jfire.issuetracking.ui.issue.remind;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
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
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class RemindIssueDetailWizardPage 
extends WizardHopPage
{
	//GUI
	private Label optionLbl;
	private Button emailButton;
	private Button messageButton;
	private Button smsButton;

	//Used objects
	private Issue selectedIssue;

	public RemindIssueDetailWizardPage(Issue issue) {
		super(RemindIssueDetailWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.remind.RemindIssueDetailWizardPage.title"), SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), RemindIssueWizard.class)); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.remind.RemindIssueDetailWizardPage.description")); //$NON-NLS-1$
		this.selectedIssue = issue;
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 2;

		optionLbl = new Label(mainComposite, SWT.NONE);
		optionLbl.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.remind.RemindIssueDetailWizardPage.label.option.notifyvia.text")); //$NON-NLS-1$
		GridData gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		optionLbl.setLayoutData(gd);

		//Check Buttons
		XComposite buttonComposite = new XComposite(mainComposite, SWT.NONE);
		buttonComposite.getGridLayout().numColumns = 1;
		buttonComposite.getGridData().grabExcessHorizontalSpace = true;

		emailButton = new Button(buttonComposite, SWT.CHECK);
		emailButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.remind.RemindIssueDetailWizardPage.button.email.text")); //$NON-NLS-1$

		messageButton = new Button(buttonComposite, SWT.CHECK);
		messageButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.remind.RemindIssueDetailWizardPage.button.message.text")); //$NON-NLS-1$

		smsButton = new Button(buttonComposite, SWT.CHECK);
		smsButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.remind.RemindIssueDetailWizardPage.button.sms.text")); //$NON-NLS-1$
		
		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		return getErrorMessage() == null;
	}
}

//class SMTPAuthenticator extends javax.mail.Authenticator {
//
//	public PasswordAuthentication getPasswordAuthentication() {
//		return new PasswordAuthentication(u_email, u_pass);
//	}
//}