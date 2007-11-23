 package org.nightlabs.jfire.issuetracking.ui.issue;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.history.IssueHistory;
import org.nightlabs.progress.NullProgressMonitor;

public class IssueViewDialog extends CenteredDialog{
	private static final Logger logger = Logger.getLogger(IssueViewDialog.class);

	private Issue issue;  

	public IssueViewDialog(Shell parentShell, Issue issue) 
	{
		super(parentShell);
		this.issue = issue;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	protected Control createDialogArea(Composite parent) 
	{
		getShell().setText("Title");
//		parent.setLayout(new GridLayout(1, false));
		
		XComposite buttonComposite = new XComposite(parent, SWT.NONE);
		buttonComposite.getGridLayout().numColumns = 7;
		buttonComposite.getGridData().grabExcessVerticalSpace = false;
		
		Button editButton = new Button(buttonComposite, SWT.PUSH);
		editButton.setText("Edit");
		editButton.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseUp(MouseEvent e) {
				try {
					IssueEditDialog editDialog = new IssueEditDialog(getShell(), issue);
					if(editDialog.open() == Dialog.OK){
						boolean confirm = MessageDialog.openConfirm(getShell(), "Confirm", "Are you sure to update this issue?");
						if(confirm){
							//-----------------------------------
							try {
								IssueHistory issueHistory = new IssueHistory(issue);
								IssueDAO issueDAO = IssueDAO.sharedInstance();
								
								issueHistory = issueDAO.createIssueHistory(issueHistory, true, new String[]{IssueHistory.FETCH_GROUP_THIS}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
								
								IssueEditComposite ie = editDialog.getIssueEditComposite();
								issue = new Issue(Login.sharedInstance().getOrganisationID(), IDGenerator.nextID(Issue.class));

								issueDAO.storeIssue(issue, true, new String[]{Issue.FETCH_GROUP_THIS}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
								
//								if(ie.getSelectedAttachmentFile() != null){
//									InputStream in = new FileInputStream(ie.getSelectedAttachmentFile());
//
//									if (in != null) {
//										try {
//											issue.loadStream(in, ie.getSelectedAttachmentFile().getName());
//										} catch (IOException e) {
//											throw new RuntimeException(e);
//										} finally {
//											try {
//												in.close();
//											} catch (IOException e) {
//												throw new RuntimeException(e);
//											}
//										}
//									}
//								}//if
//								
//								I18nText i18nText = issueNewPage.getIssueCreateComposite().getSubjectText().getI18nText();
//								Set<String> languageIDs = i18nText.getLanguageIDs();
//								for(String languageID : languageIDs){
//									issue.getSubject().setText(languageID, i18nText.getText(languageID));
//								}//for
//								
//								i18nText = issueNewPage.getIssueCreateComposite().getDescriptionText().getI18nText();
//								languageIDs = i18nText.getLanguageIDs();
//								for(String languageID : languageIDs){
//									issue.getDescription().setText(languageID, i18nText.getText(languageID));
//								}//for
							} catch (Exception ex) {
								throw new RuntimeException(ex);
							}
							//-----------------------------------
						}
					}//if
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		
		Button replyButton = new Button(buttonComposite, SWT.PUSH);
		replyButton.setText("Reply");
		
		Button notifyButton = new Button(buttonComposite, SWT.PUSH);
		notifyButton.setText("Notify");
		
		Button deleteButton = new Button(buttonComposite, SWT.PUSH);
		deleteButton.setText("Delete");
		
		Button reassignButton = new Button(buttonComposite, SWT.PUSH);
		reassignButton.setText("Reassign");
		
		Button resolveButton = new Button(buttonComposite, SWT.PUSH);
		resolveButton.setText("Resolve");
		
		Button commentButton = new Button(buttonComposite, SWT.PUSH);
		commentButton.setText("Comment");
		
		IssueViewComposite issueViewComposite = new IssueViewComposite(issue, parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		issueViewComposite.setLayoutData(gridData);
		
		IssueHistoryTable issueHistoryTable = new IssueHistoryTable(parent, SWT.NONE);
		
		return parent;
	}  

	@Override
	protected Control createContents(Composite parent) {
		Control ctrl = super.createContents(parent);
		getButton(Dialog.OK).setEnabled(true);
		return ctrl;
	}
}