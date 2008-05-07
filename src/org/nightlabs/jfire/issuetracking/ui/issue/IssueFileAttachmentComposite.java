package org.nightlabs.jfire.issuetracking.ui.issue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issue.dao.IssueFileAttachmentDAO;
import org.nightlabs.jfire.issue.id.IssueFileAttachmentID;
import org.nightlabs.progress.NullProgressMonitor;

public class IssueFileAttachmentComposite 
extends XComposite 
{
	private ListComposite<IssueFileAttachment> fileListComposite;
	private Issue issue;

	public IssueFileAttachmentComposite(Composite parent, int compositeStyle, LayoutMode layoutMode, Issue issue) {
		super(parent, compositeStyle, layoutMode);
		this.issue = issue;
		createContents();
	}

	private void createContents() {
		XComposite mainComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		mainComposite.getGridLayout().numColumns = 2;

		fileListComposite = new ListComposite<IssueFileAttachment>(mainComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		fileListComposite.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IssueFileAttachment)element).getFileName();
			}
		});

		fileListComposite.setInput(issue.getIssueFileAttachments());

		GridData gridData = new GridData(GridData.FILL_BOTH);
		mainComposite.setLayoutData(gridData);

		XComposite buttonComposite = new  XComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				FileDialog fileDialog = new FileDialog(RCPUtil.getActiveShell(), SWT.OPEN);
				final String selectedFile = fileDialog.open();

				if (selectedFile != null) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							File file = new File(selectedFile);
							IssueFileAttachment issueFileAttachment = new IssueFileAttachment(issue, IDGenerator.nextID(IssueFileAttachment.class));
							try {
								issueFileAttachment.loadFile(file);
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
							fileListComposite.addElement(issueFileAttachment);
							issue.addIssueFileAttachment(issueFileAttachment);		
						}
					});
				}
			}
		});

		Button removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				issue.removeIssueFileAttachment(fileListComposite.getSelectedElement());
				fileListComposite.removeSelected();
			}
		});

		Button downloadButton = new Button(buttonComposite, SWT.PUSH);
		downloadButton.setText("Download");
		downloadButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		downloadButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				final IssueFileAttachment issueFileAttachment = fileListComposite.getSelectedElement();
				
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						IssueFileAttachment ia = IssueFileAttachmentDAO.sharedInstance().getIssueFileAttachment((IssueFileAttachmentID)JDOHelper.getObjectId(issueFileAttachment), new String[]{FetchPlan.DEFAULT, IssueFileAttachment.FETCH_GROUP_DATA}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
						InputStream is = ia.createFileAttachmentInputStream();
						if (is != null) {
							try {
								FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell(), SWT.SAVE);
								String selectedFile = fileDialog.open();
								if (selectedFile != null) {
									saveFile(is, selectedFile);
								}
							} catch (Exception ex) {
								throw new RuntimeException(ex);
							}
						}		
					}
				});
				
			}
		});

		buttonComposite.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		mainComposite.setLayoutData(gridData);
	}

	public void saveFile(InputStream io, String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		byte[] buf = new byte[256];
		int read = 0;
		while ((read = io.read(buf)) > 0) {
			fos.write(buf, 0, read);
		}
	}
}
