/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issue.dao.IssueFileAttachmentDAO;
import org.nightlabs.jfire.issue.id.IssueFileAttachmentID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueFileAttachmentComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueFileAttachmentComposite.IssueFileAttachmentCompositeStyle;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFileAttachmentSection extends AbstractIssueEditorGeneralSection {

	private IssueFileAttachmentComposite issueFileAttachmentComposite;
	
	private DownloadFileToolbarAction downloadFileToolbarAction;
	private AddFileToolbarAction addFileToolbarAction;
	private RemoveFileToolbarAction removeFileToolbarAction;
	
	private Issue issue;
	
	private int nFile;
	/**
	 * @param section
	 * @param managedForm
	 */
	public IssueFileAttachmentSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getClient().getGridLayout().numColumns = 2;
		getClient().getGridLayout().makeColumnsEqualWidth = false;
		getSection().setText("Attachments");
		
		Label fileLabel = new Label(getClient(), SWT.NONE);
		fileLabel.setText("Files: ");
		
		issueFileAttachmentComposite = 
			new IssueFileAttachmentComposite(getClient(), SWT.NONE, LayoutMode.TIGHT_WRAPPER, IssueFileAttachmentCompositeStyle.withoutAddRemoveButton);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 100;
		issueFileAttachmentComposite.setLayoutData(gridData);
		
		downloadFileToolbarAction = new DownloadFileToolbarAction();
		addFileToolbarAction = new AddFileToolbarAction();
		removeFileToolbarAction = new RemoveFileToolbarAction();
		
		getToolBarManager().add(downloadFileToolbarAction);
		getToolBarManager().add(addFileToolbarAction);
		getToolBarManager().add(removeFileToolbarAction);
		
		hookContextMenu();
		
		updateToolBarManager();
	}

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
	
	@Override
	protected void doSetIssue(Issue newIssue) {
		issueFileAttachmentComposite.setIssue(newIssue);
		
		if (issue != null && newIssue.getIssueFileAttachments().size() == nFile) {
			return;
		}
		
		issue = newIssue;
		nFile = issue.getIssueFileAttachments().size();
	}
	
	public class DownloadFileToolbarAction extends Action {		
		public DownloadFileToolbarAction() {
			super();
			setId(DownloadFileToolbarAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueFileAttachmentSection.class, 
			"Download"));
			setToolTipText("Download File(s)");
			setText("Download");
		}

		@Override
		public void run() {
			final IssueFileAttachment issueFileAttachment = issueFileAttachmentComposite.getSelectedIssueFileAttachment();

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
	
	public void saveFile(InputStream io, String fileName) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		byte[] buf = new byte[256];
		int read = 0;
		while ((read = io.read(buf)) > 0) {
			fos.write(buf, 0, read);
		}
	}
	
	public class AddFileToolbarAction extends Action {		
		public AddFileToolbarAction() {
			super();
			setId(AddFileToolbarAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueFileAttachmentSection.class, 
			"Add"));
			setToolTipText("Add File(s)");
			setText("Add");
		}

		@Override
		public void run() {
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
						issueFileAttachmentComposite.addIssueFileAttachment(issueFileAttachment);
					}
				});
			}
			
			markDirty();
		}		
	}
	
	public class RemoveFileToolbarAction extends Action {		
		public RemoveFileToolbarAction() {
			super();
			setId(RemoveFileToolbarAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueFileAttachmentSection.class, 
			"Remove"));
			setToolTipText("Remove File(s)");
			setText("Remove");
		}

		@Override
		public void run() {
//			List<IssueFileAttachment> fileList = issue.getIssueFileAttachments();
//			fileComposite.removeFiles(fileComposite.getFileListWidget().getSelection());
//			for (int i = 0; i < fileComposite.getFileListWidget().getSelection().length; i++) {
//				String fileName = fileComposite.getFileListWidget().getSelection()[i];
//				for (IssueFileAttachment ia : fileList) {
//					if (ia.getFileName().equals(fileName)) {
//						fileList.remove(ia);
//					}
//				}
//			}
			
//			issue.getIssueFileAttachments().clear();
//			issue.getIssueFileAttachments().addAll(fileList);
			
			markDirty();
		}		
	}
}
