package org.nightlabs.jfire.issuetracking.ui.issue;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

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

public class IssueFileAttachmentComposite 
extends XComposite 
{
	private ListComposite<IssueFileAttachmentItem> issueFileAttachmentItemListComposite;

	public IssueFileAttachmentComposite(Composite parent, int compositeStyle, LayoutMode layoutMode) {
		super(parent, compositeStyle, layoutMode);
		createContents();
	}

	private void createContents() {
		XComposite fileListComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		fileListComposite.getGridLayout().numColumns = 2;

		issueFileAttachmentItemListComposite = new ListComposite<IssueFileAttachmentItem>(fileListComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		issueFileAttachmentItemListComposite.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IssueFileAttachmentItem)element).getFileName();
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
					FileInputStream fis = null;
					try {
						try {
							fis = new FileInputStream(file);
							FileDescriptor fd = fis.getFD();
							IssueFileAttachmentItem issueFileAttachmentItem = new IssueFileAttachmentItem(file.getName(), fd);
							issueFileAttachmentItemListComposite.addElement(issueFileAttachmentItem);
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					} finally {
						try {
							fis.close();
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
				issueFileAttachmentItemListComposite.removeAllSelected();
			}
		});
		buttonComposite.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		fileListComposite.setLayoutData(gridData);
	}


	class IssueFileAttachmentItem {
		private String fileName;
		private FileDescriptor fd;

		public IssueFileAttachmentItem(String fileName, FileDescriptor fd) {
			this.fileName = fileName;
			this.fd = fd;
		}

		public FileDescriptor getFd() {
			return fd;
		}

		public String getFileName() {
			return fileName;
		}
	}
}
