package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

public class IssueTypeNewEditorPage extends EntityEditorPageWithProgress {
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = IssueTypeNewEditorPage.class.getName();

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link IssueTypeNewEditorPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new IssueTypeNewEditorPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new IssueTypeNewPageController(editor);
		}
	}

	/**
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 * 
	 * @param editor The editor for which to create this
	 * 		form page. 
	 */
	public IssueTypeNewEditorPage(FormEditor editor)
	{
		super(editor, ID_PAGE, "New Issue Type");
	}

	@Override
	protected void addSections(Composite parent) {
		final IssueTypeNewPageController controller = (IssueTypeNewPageController)getPageController();
		
		IssueTypeCreateComposite ic = new IssueTypeCreateComposite(parent, SWT.NONE);
	}

	@Override
	protected void asyncCallback() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleControllerObjectModified(
			EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent(); // multiple calls don't hurt
	}
	
	@Override
	protected String getPageFormTitle() {
		return "Create a new Issue Type";
	}

}
