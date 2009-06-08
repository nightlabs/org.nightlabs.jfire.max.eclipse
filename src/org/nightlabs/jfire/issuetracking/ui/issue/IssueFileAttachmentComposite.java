package org.nightlabs.jfire.issuetracking.ui.issue;

import java.io.File;
import java.io.IOException;

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
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
@Deprecated
public class IssueFileAttachmentComposite
extends XComposite
{
	private ListComposite<IssueFileAttachment> issueFileAttachmentListComposite;
	private Issue issue;

	public static enum IssueFileAttachmentCompositeStyle {
		withAddRemoveButton,
		withoutAddRemoveButton
	}

	private IssueFileAttachmentCompositeStyle style;

	/**
	 *
	 * @param parent
	 * @param compositeStyle
	 * @param layoutMode
	 * @param style
	 */
	public IssueFileAttachmentComposite(Composite parent, int compositeStyle, LayoutMode layoutMode, IssueFileAttachmentCompositeStyle style) {
		super(parent, compositeStyle, layoutMode);
		this.style = style;

		createContents();
	}

	private void createContents() {
		XComposite mainComposite = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		mainComposite.getGridLayout().numColumns = 2;

		issueFileAttachmentListComposite = new ListComposite<IssueFileAttachment>(mainComposite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		issueFileAttachmentListComposite.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IssueFileAttachment) {
					IssueFileAttachment issueFileAttachment = (IssueFileAttachment)element;
					return String.format("%s (%s)", issueFileAttachment.getFileName(), issueFileAttachment.getFileSize() + " bytes");
					// return ((IssueFileAttachment)element).getFileName();
				}

				return null;
			}
		});

		GridData gridData = new GridData(GridData.FILL_BOTH);
		mainComposite.setLayoutData(gridData);

		if (style == IssueFileAttachmentCompositeStyle.withAddRemoveButton) {
			XComposite buttonComposite = new  XComposite(mainComposite, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

			Button addButton = new Button(buttonComposite, SWT.PUSH);
			addButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueFileAttachmentComposite.button.addFile.text")); //$NON-NLS-1$
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
								IssueFileAttachment issueFileAttachment = new IssueFileAttachment(issue.getOrganisationID(), IDGenerator.nextID(IssueFileAttachment.class), issue);
								try {
									issueFileAttachment.loadFile(file);
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
								addIssueFileAttachment(issueFileAttachment);
							}
						});
					}
				}
			});

			Button removeButton = new Button(buttonComposite, SWT.PUSH);
			removeButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.IssueFileAttachmentComposite.button.removeFile.text")); //$NON-NLS-1$
			removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			removeButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					issue.removeIssueFileAttachment(issueFileAttachmentListComposite.getSelectedElement());
					issueFileAttachmentListComposite.removeSelected();
				}
			});

			buttonComposite.setLayoutData(new GridData());
		}
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessHorizontalSpace = true;
		mainComposite.setLayoutData(gridData);
	}

	/**
	 *
	 * @param issue
	 */
	public void setIssue(Issue issue) {
		this.issue = issue;
		issueFileAttachmentListComposite.setInput(issue.getIssueFileAttachments());
	}

	/**
	 *
	 * @return
	 */
	public IssueFileAttachment getSelectedIssueFileAttachment() {
		return issueFileAttachmentListComposite.getSelectedElement();
	}

	/**
	 *
	 * @param issueFileAttachment
	 */
	public void addIssueFileAttachment(IssueFileAttachment issueFileAttachment) {
		issueFileAttachmentListComposite.addElement(issueFileAttachment);
		issue.addIssueFileAttachment(issueFileAttachment);
	}
}
