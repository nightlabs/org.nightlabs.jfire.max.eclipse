/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.overview.action;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EditIssueAction extends AbstractIssueAction {

	/**
	 * 
	 */
	public EditIssueAction() {
	}

	/**
	 * @param activePart
	 */
	public EditIssueAction(IWorkbenchPart activePart) {
		super(activePart);
	}

	/**
	 * @param text
	 */
	public EditIssueAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public EditIssueAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public EditIssueAction(String text, int style) {
		super(text, style);
	}

	@Override
	public boolean calculateEnabled() {
		return getSelectedIssueIDs().size() == 1;
	}
	
	@Override
	public void run() {
		try {
			RCPUtil.openEditor(new IssueEditorInput(getSelectedIssueIDs().iterator().next()), IssueEditor.EDITOR_ID);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}
}
