package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class ShowLinkedIssuePage 
extends EntityEditorPageWithProgress 
{
	public static class Factory implements IEntityEditorPageFactory {
		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ShowLinkedIssuePage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ShowLinkedIssuePageController(editor);
		}
	}
	
	private ShowLinkedIssueSection showIssueLinkSection;
	
	/**
	 * @param editor
	 */
	public ShowLinkedIssuePage(FormEditor editor) {
		super(editor, ShowLinkedIssuePage.class.getName(), "Issue Links");
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
		final ShowLinkedIssuePageController controller = (ShowLinkedIssuePageController) getPageController();
		
		showIssueLinkSection = new ShowLinkedIssueSection(this, parent, controller);
		showIssueLinkSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getManagedForm().addPart(showIssueLinkSection);

		if (controller.isLoaded()) {
			showIssueLinkSection.setIssueLinkTableItems(controller.getIssueLinkTableItems());
		}
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		switchToContent();
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (showIssueLinkSection != null && !showIssueLinkSection.getSection().isDisposed())
					showIssueLinkSection.setIssueLinkTableItems(getController().getIssueLinkTableItems());
			}
		});
	}
	
	@Override
	protected String getPageFormTitle() {
		return "Issue Links";
	}
	
	protected ShowLinkedIssuePageController getController() {
		return (ShowLinkedIssuePageController)getPageController();
	}
}