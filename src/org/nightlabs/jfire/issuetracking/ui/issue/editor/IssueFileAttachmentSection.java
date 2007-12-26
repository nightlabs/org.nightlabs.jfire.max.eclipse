/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.FileListSelectionComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueFileAttachment;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFileAttachmentSection extends AbstractIssueEditorGeneralSection {

	private FileListSelectionComposite fileComposite;
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
		fileComposite.setLayoutData(gridData);
	}

	@Override
	protected void doSetIssue(Issue issue) {
		List<IssueFileAttachment> fileAttachments = issue.getFileList();
		List<File> fileList = new ArrayList<File>();
		for(IssueFileAttachment isa : fileAttachments) {
			fileComposite.addFile(isa.getFileName(), isa.createFileAttachmentInputStream());
//			try {
//				FileOutputStream fos = new FileOutputStream(f);
//				byte[] buf = new byte[256];
//				int read = 0;
//				while ((read = isa.createFileAttachmentInputStream().read(buf)) > 0) {
//					fos.write(buf, 0, read);
//				}
//			} catch(IOException ex) {
//				throw new RuntimeException(ex);
//			}
		}
//		);
	}
}
