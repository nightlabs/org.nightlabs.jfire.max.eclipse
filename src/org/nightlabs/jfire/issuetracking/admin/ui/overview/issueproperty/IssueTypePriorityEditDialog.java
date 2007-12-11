// package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;
//
//import org.apache.log4j.Logger;
//import org.eclipse.jface.dialogs.Dialog;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.Shell;
//import org.nightlabs.base.ui.dialog.CenteredDialog;
//import org.nightlabs.jfire.issue.Issue;
//
//public class IssueTypePriorityEditDialog extends CenteredDialog{
//	private static final Logger logger = Logger.getLogger(IssueTypePriorityEditDialog.class);
//
//	private Issue issue;  
//	private IssueTypePriorityEditComposite issueEditComposite;
//	
//	public IssueTypePriorityEditDialog(Shell parentShell, Issue issue) 
//	{
//		super(parentShell);
//		this.issue = issue;
//		setShellStyle(getShellStyle() | SWT.RESIZE);
//	}
//	
//	protected Control createDialogArea(Composite parent) 
//	{
//		getShell().setText("Title");
//		
//		issueEditComposite = new IssueTypePriorityEditComposite(issue, parent, SWT.NONE);
//		GridData gridData = new GridData(GridData.FILL_BOTH);
//		issueEditComposite.setLayoutData(gridData);
//		
//		return parent;
//	}  
//
//	@Override
//	protected Control createContents(Composite parent) {
//		Control ctrl = super.createContents(parent);
//		getButton(Dialog.OK).setEnabled(true);
//		return ctrl;
//	}
//	
//	public IssueTypePriorityEditComposite getIssueEditComposite(){
//		return issueEditComposite;
//	}
//}