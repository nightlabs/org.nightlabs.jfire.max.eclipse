package org.nightlabs.jfire.issuetracking.ui.issue;

import java.io.File;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;

public class IssueFileAttachmentComposite 
extends XComposite 
{
	private ListComposite<File> issueFileAttachmentFileListComposite;
	private Issue issue;
	
	public IssueFileAttachmentComposite(Composite parent, int compositeStyle, LayoutMode layoutMode, Issue issue) {
		super(parent, compositeStyle, layoutMode);
		this.issue = issue;
		createContents();
	}

	private void createContents() {
		XComposite fileListComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		fileListComposite.getGridLayout().numColumns = 2;

		issueFileAttachmentFileListComposite = new ListComposite<File>(fileListComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		issueFileAttachmentFileListComposite.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((File)element).getName();
			}
		});

		GridData gridData = new GridData(GridData.FILL_BOTH);
		fileListComposite.setLayoutData(gridData);

		XComposite buttonComposite = new  XComposite(fileListComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				FileDialog fileDialog = new FileDialog(RCPUtil.getActiveShell(), SWT.OPEN);
				String selectedFile = fileDialog.open();
				if (selectedFile != null) {
					File file = new File(selectedFile);
					IssueFileAttachment issueFileAttachment = new IssueFileAttachment(issue, IDGenerator.nextID(IssueFileAttachment.class));
//					issueFileAttachment.
//					issue.addIssueFileAttachment(issueFileAttachment);
					try {
						try {
//							fis = new FileInputStream(file);
//							FileDescriptor fd = fis.getFD();
//							issue.addIssueFileAttachmentDescriptor(fd, file.getName());
//							
//							IssueFileAttachmentItem issueFileAttachmentItem = new IssueFileAttachmentItem(file.getName(), fd);
//							issueFileAttachmentItemListComposite.addElement(issueFileAttachmentItem);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					} finally {
						try {
//							fis.close();
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		});

		Button removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
//				issue.removeIssueFileAttachmentDescriptor(issueFileAttachmentItemListComposite.getSelectedElement().getFileDescriptor());
//				issueFileAttachmentItemListComposite.removeAllSelected();
			}
		});
		buttonComposite.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		fileListComposite.setLayoutData(gridData);
	}

	// there is no need to manage IssueFileAttachmentItem instead of IssueFileAttachment - remove this class and directly manage IssueFileAttachment.
	// Additionally, note that you NEVER keep files (or other resources) open any longer as you really really really need. A java.io.File is used
	// as an address of a file (without opening it!) while a FileDescriptor references an OPEN file.
//	class IssueFileAttachmentItem {
//		private String fileName;
//		private FileDescriptor fileDescriptor;
//		private File file;
//
//		public IssueFileAttachmentItem(String fileName, FileDescriptor fileDescritor) {
//			this.fileName = fileName;
//			this.fileDescriptor = fileDescritor;
//		}
//
//		public FileDescriptor getFileDescriptor() {
//			return fileDescriptor;
//		}
//
//		public String getFileName() {
//			return fileName;
//		}
//	}
}
