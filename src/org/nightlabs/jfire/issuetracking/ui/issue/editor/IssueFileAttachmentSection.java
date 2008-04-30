/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueFileAttachmentComposite;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFileAttachmentSection extends AbstractIssueEditorGeneralSection {

	private IssueFileAttachmentComposite fileComposite;
	
	private DownloadFileAction downloadFileAction;
	private AddFileAction addFileAction;
	private RemoveFileAction removeFileAction;
	
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
		
		fileComposite = 
			new IssueFileAttachmentComposite(getClient(), SWT.NONE, LayoutMode.TIGHT_WRAPPER, controller.getIssue());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 100;
		fileComposite.setLayoutData(gridData);
		
		downloadFileAction = new DownloadFileAction();
		addFileAction = new AddFileAction();
		removeFileAction = new RemoveFileAction();
		
		getToolBarManager().add(downloadFileAction);
		getToolBarManager().add(addFileAction);
		getToolBarManager().add(removeFileAction);
		
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
		manager.add(downloadFileAction);
		manager.add(addFileAction);
		manager.add(removeFileAction);
	}
	
	@Override
	protected void doSetIssue(Issue newIssue) {
		if (issue != null && newIssue.getIssueFileAttachments().size() == nFile) {
			return;
		}
		
		issue = newIssue;
		nFile = issue.getIssueFileAttachments().size();
		
//		List<IssueFileAttachment> fileAttachments = newIssue.getIssueFileAttachments();
//		List<File> fileList = new ArrayList<File>();
//		for(IssueFileAttachment isa : fileAttachments) {
//			fileComposite.addFile(isa.getFileName(), isa.createFileAttachmentInputStream());
//		}
	}
	
	public class DownloadFileAction extends Action {		
		public DownloadFileAction() {
			super();
			setId(DownloadFileAction.class.getName());
			setImageDescriptor(SharedImages.getSharedImageDescriptor(
					IssueTrackingPlugin.getDefault(), 
					IssueFileAttachmentSection.class, 
			"Download"));
			setToolTipText("Download File(s)");
			setText("Download");
		}

		@Override
		public void run() {
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
	
	public class AddFileAction extends Action {		
		public AddFileAction() {
			super();
			setId(AddFileAction.class.getName());
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
			String selectedFile = fileDialog.open();
			if (selectedFile != null) {
				File file = new File(selectedFile);
				try {
//					fileComposite.addFile(file.getName(), new FileInputStream(file));
					
					IssueFileAttachment fileAttachment = new IssueFileAttachment(issue, IDGenerator.nextID(IssueFileAttachment.class));
					fileAttachment.loadFile(file);
//					fileAttachment.loadFile((fileComposite.getInputStreamMap().get(selectedFile), selectedFile);
					issue.getIssueFileAttachments().add(fileAttachment);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
			markDirty();
		}		
	}
	
	public class RemoveFileAction extends Action {		
		public RemoveFileAction() {
			super();
			setId(RemoveFileAction.class.getName());
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
