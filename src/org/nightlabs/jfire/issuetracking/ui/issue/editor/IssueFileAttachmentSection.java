/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.FileListSelectionComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFileAttachmentSection extends AbstractIssueEditorGeneralSection {

	private FileListSelectionComposite fileComposite;
	
	private AddFileAction addFileAction;
	private RemoveFileAction removeFileAction;
	
	private Issue issue;
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
			new FileListSelectionComposite(getClient(), SWT.NONE, LayoutMode.TIGHT_WRAPPER, FileListSelectionComposite.LIST);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 100;
		fileComposite.setLayoutData(gridData);
		
		addFileAction = new AddFileAction();
		removeFileAction = new RemoveFileAction();
		
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

		Menu menu = menuMgr.createContextMenu(fileComposite.getFileListWidget());
		fileComposite.getFileListWidget().setMenu(menu);
	}
	
	private void fillContextMenu(IMenuManager manager) {
		manager.add(addFileAction);
		manager.add(removeFileAction);
	}
	
	@Override
	protected void doSetIssue(Issue issue) {
		this.issue = issue;
		
		List<IssueFileAttachment> fileAttachments = issue.getFileList();
		List<File> fileList = new ArrayList<File>();
		for(IssueFileAttachment isa : fileAttachments) {
			fileComposite.addFile(isa.getFileName(), isa.createFileAttachmentInputStream());
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
			FileDialog fileDialog = new FileDialog(RCPUtil.getActiveWorkbenchShell(), SWT.OPEN);
			String selectedFile = fileDialog.open();
			if (selectedFile != null) {
				File file = new File(selectedFile);
				try {
					fileComposite.addFile(file.getName(), new FileInputStream(file));
					
					IssueFileAttachment fileAttachment = new IssueFileAttachment(issue, IDGenerator.nextID(IssueFileAttachment.class));
					fileAttachment.loadFile(file);
//					fileAttachment.loadFile((fileComposite.getInputStreamMap().get(selectedFile), selectedFile);
					issue.getFileList().add(fileAttachment);
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
			List<IssueFileAttachment> fileList = issue.getFileList();
			fileComposite.removeFile(fileComposite.getFileListWidget().getSelection());
			for (int i = 0; i < fileComposite.getFileListWidget().getSelection().length; i++) {
				String fileName = fileComposite.getFileListWidget().getSelection()[i];
				for (IssueFileAttachment ia : fileList) {
					if (ia.getFileName().equals(fileName)) {
						fileList.remove(ia);
					}
				}
			}
			
			issue.getFileList().clear();
			issue.getFileList().addAll(fileList);
			
			markDirty();
		}		
	}
}
