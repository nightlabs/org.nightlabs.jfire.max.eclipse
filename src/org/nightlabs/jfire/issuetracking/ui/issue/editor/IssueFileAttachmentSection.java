/**
 *
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issue.dao.IssueFileAttachmentDAO;
import org.nightlabs.jfire.issue.id.IssueFileAttachmentID;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * A section for the {@link IssueEditorGeneralPage} to handle the interface mechanisms for the
 * {@link IssueFileAttachment}s.
 *
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueFileAttachmentSection extends AbstractIssueEditorGeneralSection {
//	private IssueFileAttachmentComposite issueFileAttachmentComposite; // <-- Marked for removal.
	private IssueFileAttachmentTable issueFileAttachmentTable;

	private DownloadFileToolbarAction downloadFileToolbarAction;
	private AddFileToolbarAction addFileToolbarAction;
	private RemoveFileToolbarAction removeFileToolbarAction;

	/**
	 * @param section
	 * @param managedForm
	 */
	public IssueFileAttachmentSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getClient().getGridLayout().numColumns = 2;
		getClient().getGridLayout().makeColumnsEqualWidth = false;
		getSection().setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentSection.section.text")); //$NON-NLS-1$

		Label fileLabel = new Label(getClient(), SWT.NONE);
		fileLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentSection.label.file.text")); //$NON-NLS-1$


		// Top set of Action buttons.
		downloadFileToolbarAction = new DownloadFileToolbarAction();
		getToolBarManager().add(downloadFileToolbarAction);

		addFileToolbarAction = new AddFileToolbarAction();
		getToolBarManager().add(addFileToolbarAction);

		removeFileToolbarAction = new RemoveFileToolbarAction();
		getToolBarManager().add(removeFileToolbarAction);



		// Create the TableComposite. Since 05.06.2009.
		issueFileAttachmentTable = new IssueFileAttachmentTable(getClient());
		new IssueFileAttachmentTableComposite(getClient(), SWT.NONE);

		// Attach funcitonal listener(s) to the CompositeTable.
		issueFileAttachmentTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) { handleSectionButtons(); }
		});


		hookContextMenu();	// <-- FIXME This is not functioning. Kai
		updateToolBarManager();
	}


	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.issuetracking.ui.issue.editor.AbstractIssueEditorGeneralSection#doSetIssue(org.nightlabs.jfire.issue.Issue)
	 */
	@Override
	protected void doSetIssue(Issue issue) {
		Collection<IssueFileAttachment> issueFileAttachments = issue.getIssueFileAttachments();
		boolean isFileAttachmentExists = issueFileAttachments != null && !issueFileAttachments.isEmpty();
		if (isFileAttachmentExists)
			issueFileAttachmentTable.setInput( issueFileAttachments );

		getSection().setExpanded( isFileAttachmentExists );
		handleSectionButtons();
	}

	/**
	 * Based on the contents of the filenames in the list, control the 'enabled' states of the
	 * toolbar buttons accordingly.
	 */
	protected void handleSectionButtons() {
		assert downloadFileToolbarAction != null && removeFileToolbarAction != null;

		boolean isItemSelected = issueFileAttachmentTable.getSelectionIndex() >= 0;
		downloadFileToolbarAction.setEnabled( isItemSelected );
		removeFileToolbarAction.setEnabled( isItemSelected );
	}

	/**
	 * Saves a file indicated by fileName to the given {@link InputStream}.
	 */
	public void saveFile(InputStream io, String fileName) throws IOException {
		// Should this method be here?
		FileOutputStream fos = new FileOutputStream(fileName);
		byte[] buf = new byte[256];
		int read = 0;
		while ((read = io.read(buf)) > 0) {
			fos.write(buf, 0, read);
		}
	}



	// -----------------------------------------------------------------------------------------------------------------------------------|
	// --->> FIXME On context menu: [These doesnt seem to be functioning? Kai]
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				IssueFileAttachmentSection.this.fillContextMenu(manager);
			}
		});

//		Menu menu = menuMgr.createContextMenu(fileComposite.getFileListWidget());
//		fileComposite.getFileListWidget().setMenu(menu);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(downloadFileToolbarAction);
		manager.add(addFileToolbarAction);
		manager.add(removeFileToolbarAction);
	}
	// -----------------------------------------------------------------------------------------------------------------------------------|




	// -----------------------------------------------------------------------------------------------------------------------------------|
	/**
	 *  Setup more control for the TableComposite in this Section.
	 *  This seems enough, and we dont have to have anything more elaborate(?)
	 */
	private class IssueFileAttachmentTableComposite extends XComposite {
		// This solution is a proposed improvement over the IssueFileAttachmentComposite, which previously,
		// in addition to the composite, encapsulates the ListComposite<IssueFileAttachment>.
		// Changes:
		//   (1) From ListComposite<IssueFileAttachment> to AbstractTableComposite<IssueFileAttachment>
		//       -- So that we can have multiple columns (at least 2 to display the fileName and the fileSize).
		//
		//   (2) Once the UI is set up, we only need to use the reference to the TableComposite for displaying contents.
		//       -- All 'actions' should be relegated to the corresponding 'Action' classes instead.
		public IssueFileAttachmentTableComposite(Composite parent, int style) {
			super(parent, style, LayoutMode.TIGHT_WRAPPER);
			getGridLayout().numColumns = 2;
			getGridLayout().makeColumnsEqualWidth = false;
			getGridData().grabExcessHorizontalSpace = true;

			GridData gridData = new GridData(GridData.FILL_BOTH);
			gridData.heightHint = 200;

			assert issueFileAttachmentTable != null;
			issueFileAttachmentTable.setLayoutData(gridData);
		}
	}


	// -----------------------------------------------------------------------------------------------------------------------------------|
	/**
	 * Handles the action to save (or download to a local copy) of an {@link IssueFileAttachment}.
	 */
	public class DownloadFileToolbarAction extends Action {
		public DownloadFileToolbarAction() {
			setId(DownloadFileToolbarAction.class.getName());
			setImageDescriptor(SharedImages.SAVE_16x16);
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentSection.DownloadFileToolbarAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentSection.DownloadFileToolbarAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			// Note: We should suggest the same filename as is listed in the CompositeTable for the download filename.
			Collection<IssueFileAttachment> items = issueFileAttachmentTable.getSelectedElements();
			if (items == null || items.isEmpty()) return;

			// For now, we shall assume NO multiple selection from the CompositeTable; and that only ONE IssueFileAttachment
			// is available for download, per selection-action.
			final IssueFileAttachment issueFileAttachment = items.iterator().next();
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					IssueFileAttachment iFA = IssueFileAttachmentDAO.sharedInstance().getIssueFileAttachment(
							(IssueFileAttachmentID)JDOHelper.getObjectId(issueFileAttachment),
							new String[] {FetchPlan.DEFAULT, IssueFileAttachment.FETCH_GROUP_DATA},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new NullProgressMonitor() );

					InputStream inputStream = iFA.createFileAttachmentInputStream();
					if (inputStream != null) {
						try {
							FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell(), SWT.SAVE);
							fileDialog.setFileName( issueFileAttachment.getFileName() );
							String selectedFile = fileDialog.open();
							if (selectedFile != null) {
								saveFile(inputStream, selectedFile);
							}
						} catch (Exception ex) {
							throw new RuntimeException(ex);
						}
					}
				}
			});


//			final IssueFileAttachment issueFileAttachment = issueFileAttachmentComposite.getSelectedIssueFileAttachment();
//			if (issueFileAttachment == null)
//				return; // Do nothing if nothing selected.
//			Display.getDefault().asyncExec(new Runnable() {
//				@Override
//				public void run() {
//					IssueFileAttachment ia = IssueFileAttachmentDAO.sharedInstance().getIssueFileAttachment((IssueFileAttachmentID)JDOHelper.getObjectId(issueFileAttachment), new String[]{FetchPlan.DEFAULT, IssueFileAttachment.FETCH_GROUP_DATA}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
//					InputStream is = ia.createFileAttachmentInputStream();
//					if (is != null) {
//						try {
//							FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell(), SWT.SAVE);
//							String selectedFile = fileDialog.open();
//							if (selectedFile != null) {
//								saveFile(is, selectedFile);
//							}
//						} catch (Exception ex) {
//							throw new RuntimeException(ex);
//						}
//					}
//				}
//			});

//			if (fileComposite.getFileListWidget().getSelectionIndex() != -1) {
//				InputStream is = fileComposite.getInputStreamMap().get(fileComposite.getFileListWidget().getItem(fileComposite.getFileListWidget().getSelectionIndex()));
//				if (is != null) {
//					try {
//						FileDialog fileDialog = new FileDialog(RCPUtil.getActiveShell(), SWT.SAVE);
//						String selectedFile = fileDialog.open();
//						if (selectedFile != null) {
//							fileComposite.saveFile(is, selectedFile);
//						}
//					} catch (Exception ex) {
//						throw new RuntimeException(ex);
//					}
//				}
//			}
		}
	}


	// -----------------------------------------------------------------------------------------------------------------------------------|
	/**
	 * Handles the action to add a file to the {@link IssueFileAttachment}.
	 * TODO Check to see if the newly selected file already exists in the CompositeTable.
	 */
	public class AddFileToolbarAction extends Action {
		private Issue issue;
		public AddFileToolbarAction() {
			setId(AddFileToolbarAction.class.getName());
			setImageDescriptor(SharedImages.ADD_16x16);
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentSection.AddFileToolbarAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentSection.AddFileToolbarAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			issue = getIssue();
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

						// Update the current Issue with the newly instantiated IssueFileAttachment.
						issue.addIssueFileAttachment(issueFileAttachment);

						// Note: We will be adding the newly instantiated IssueFileAttachment to the collection in the current Issue,
						// and so, we should expand the section if it was previously not expanded.
						if (issueFileAttachmentTable.getItemCount() == 0) { //(issue.getIssueFileAttachments().size() == 0) {
							issueFileAttachmentTable.setInput(issue.getIssueFileAttachments());	// <-- Only on first populate.
							getSection().setExpanded(true);
						}

						// Update the UI.
						issueFileAttachmentTable.refresh(true);
						markDirty();
					}
				});
			}

		}
	}


	// -----------------------------------------------------------------------------------------------------------------------------------|
	/**
	 * Handles the action to remove a 'selected' file from the {@link IssueFileAttachment}.
	 */
	public class RemoveFileToolbarAction extends Action {
		public RemoveFileToolbarAction() {
			setId(RemoveFileToolbarAction.class.getName());
			setImageDescriptor(SharedImages.DELETE_16x16);
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentSection.RemoveFileToolbarAction.toolTipText")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueFileAttachmentSection.RemoveFileToolbarAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			Collection<IssueFileAttachment> items = issueFileAttachmentTable.getSelectedElements();
			if (items == null || items.isEmpty()) return;

			Issue issue = getIssue();
			for(IssueFileAttachment issueFileAttachment : items) {
				issueFileAttachmentTable.removeElement(issueFileAttachment);
				issue.removeIssueFileAttachment(issueFileAttachment);
			}

			// Done!
			markDirty();
		}
	}
}
