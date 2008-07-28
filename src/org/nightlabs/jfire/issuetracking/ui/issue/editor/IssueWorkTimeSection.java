package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.jfire.issue.Issue;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueWorkTimeSection 
extends AbstractIssueEditorGeneralSection 
{
//	private DownloadFileToolbarAction downloadFileToolbarAction;
	
	private Issue issue;
	
	private static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
	
	/**
	 * @param section
	 * @param managedForm
	 */
	public IssueWorkTimeSection(FormPage page, Composite parent, final IssueEditorPageController controller) {
		super(page, parent, controller);
		getClient().getGridLayout().numColumns = 3;
		getClient().getGridLayout().makeColumnsEqualWidth = false;
		getSection().setText("Work Time");
		
		new Label(getClient(), SWT.NONE).setText("State Time: ");
				
		Label startTimeLabel = new Label(getClient(), SWT.NONE);
		startTimeLabel.setText(dateTimeFormat.format(new Date()));
		
		Button startButton = new Button(getClient(), SWT.NONE);
		startButton.setText("Start");
		
		new Label(getClient(), SWT.NONE).setText("Finish Time: ");
		
		Label finishTimeLabel = new Label(getClient(), SWT.NONE);
		finishTimeLabel.setText(dateTimeFormat.format(new Date()));
		
		Button finishTimeButton = new Button(getClient(), SWT.NONE);
		finishTimeButton.setText("Finish");

		//		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
//		gridData.heightHint = 100;
//		issueFileAttachmentComposite.setLayoutData(gridData);
//		
//		downloadFileToolbarAction = new DownloadFileToolbarAction();
//		
//		getToolBarManager().add(downloadFileToolbarAction);
		
//		hookContextMenu();
		
//		updateToolBarManager();
	}

//	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				IssueFileAttachmentSection.this.fillContextMenu(manager);
//			}
//		});
//
////		Menu menu = menuMgr.createContextMenu(fileComposite.getFileListWidget());
////		fileComposite.getFileListWidget().setMenu(menu);
//	}
//	
//	private void fillContextMenu(IMenuManager manager) {
//		manager.add(downloadFileToolbarAction);
//	}
	
	@Override
	protected void doSetIssue(Issue newIssue) {
//		issueFileAttachmentComposite.setIssue(newIssue);
//		
//		if (issue != null && newIssue.getIssueFileAttachments().size() == nFile) {
//			return;
//		}
//		
//		issue = newIssue;
//		nFile = issue.getIssueFileAttachments().size();
	}
	
//	public class DownloadFileToolbarAction extends Action {		
//		public DownloadFileToolbarAction() {
//			super();
//			setId(DownloadFileToolbarAction.class.getName());
//			setImageDescriptor(SharedImages.getSharedImageDescriptor(
//					IssueTrackingPlugin.getDefault(), 
//					IssueFileAttachmentSection.class, 
//			"Download"));
//			setToolTipText("Download File(s)");
//			setText("Download");
//		}
//
//		@Override
//		public void run() {
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
//
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
//		}		
//	}
//	
//	public void saveFile(InputStream io, String fileName) throws IOException {
//		FileOutputStream fos = new FileOutputStream(fileName);
//		byte[] buf = new byte[256];
//		int read = 0;
//		while ((read = io.read(buf)) > 0) {
//			fos.write(buf, 0, read);
//		}
//	}
}
