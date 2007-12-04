package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

public class IssueTypeEditorPage extends EntityEditorPageWithProgress {
	/**
	 * The id of this page.
	 */
	public static final String ID_PAGE = IssueTypeEditorPage.class.getName();

	/**
	 * The Factory is registered to the extension-point and creates
	 * new instances of {@link IssueTypeEditorPage}. 
	 */
	public static class Factory implements IEntityEditorPageFactory {

		public IFormPage createPage(FormEditor formEditor) {
			return new IssueTypeEditorPage(formEditor);
		}
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new IssueTypeEditorPageController(editor);
		}
	}

	private IssueTypeNameSection issueTypeNameSection;
	private IssueTypePrioritySection issueTypePrioritySection;
	private IssueTypeSeverityTypeSection issueTypeSeverityTypeSection;
	/**
	 * <p>
	 * This constructor is used by the entity editor
	 * page extension system.
	 * 
	 * @param editor The editor for which to create this
	 * 		form page. 
	 */
	public IssueTypeEditorPage(FormEditor editor)
	{
		super(editor, ID_PAGE, "New Issue Type");
	}

	@Override
	protected void addSections(Composite parent) {
		IssueTypeEditorPageController controller = (IssueTypeEditorPageController)getPageController();
		
		issueTypeNameSection = new IssueTypeNameSection(this, parent, controller);
		getManagedForm().addPart(issueTypeNameSection);
		
		issueTypePrioritySection = new IssueTypePrioritySection(this, parent, controller);
		issueTypePrioritySection.setIssueType(controller.getIssueType());
		getManagedForm().addPart(issueTypePrioritySection);
		
		issueTypeSeverityTypeSection = new IssueTypeSeverityTypeSection(this, parent, controller);
		issueTypeSeverityTypeSection.setIssueType(controller.getIssueType());
		getManagedForm().addPart(issueTypeSeverityTypeSection);
	}

	@Override
	protected void asyncCallback() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void handleControllerObjectModified(
			EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent();		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IssueTypeEditorPageController controller = (IssueTypeEditorPageController)getPageController();
				
				if(issueTypeSeverityTypeSection != null)
					issueTypeSeverityTypeSection.setIssueType(controller.getIssueType());
				if(issueTypePrioritySection != null)
					issueTypePrioritySection.setIssueType(controller.getIssueType());
				if(issueTypeNameSection != null)
					issueTypeNameSection.setIssueType(controller.getIssueType());
			}
		});
		
	}
	
	@Override
	protected String getPageFormTitle() {
		return "Create a new Issue Type";
	}

}
