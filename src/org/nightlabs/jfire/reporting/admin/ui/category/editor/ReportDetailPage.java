/**
 *
 */
package org.nightlabs.jfire.reporting.admin.ui.category.editor;

import javax.jdo.FetchPlan;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;

/**
 * Page that shows and edits name and description of a {@link ReportRegistryItem}.
 *
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportDetailPage extends EntityEditorPageWithProgress {

	public static final String PAGE_ID = ReportDetailPage.class.getName();

	public static class Factory implements IEntityEditorPageFactory {

		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ReportDetailPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ReportRegistryItemPageController(editor, new String[] {
					FetchPlan.DEFAULT,
					ReportRegistryItem.FETCH_GROUP_NAME,
					ReportRegistryItem.FETCH_GROUP_DESCRIPTION
			});
		}
	}

	private ReportDetailSection detailSection;

	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public ReportDetailPage(FormEditor editor) {
		super(editor, PAGE_ID, "Name && Description");
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
		detailSection = new ReportDetailSection(this, parent);
		getManagedForm().addPart(detailSection);

		if (getPageController().isLoaded()) {
			detailSection.setReportRegistryItem(getPageController().getControllerObject());
		}
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		if (detailSection != null && !detailSection.getSection().isDisposed()) {
			detailSection.getSection().getDisplay().asyncExec(new Runnable() {
				public void run() {
					detailSection.setReportRegistryItem(getPageController().getControllerObject());
				}
			});
		}
	}

	@Override
	public ReportRegistryItemPageController getPageController() {
		return (ReportRegistryItemPageController) super.getPageController();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return "Name & description";
	}

}
