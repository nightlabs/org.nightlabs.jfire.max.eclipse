/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.textpart;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportTextPartConfigurationPage extends EntityEditorPageWithProgress {
	
	public static class Factory implements IEntityEditorPageFactory {

		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ReportTextPartConfigurationPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ReportTextPartConfigurationPageController(editor);
		}
		
	}

	private ReportTextPartConfigurationSection reportTextPartConfigurationSection;
	
	/**
	 * @param editor
	 */
	public ReportTextPartConfigurationPage(FormEditor editor) {
		super(editor, ReportTextPartConfigurationPage.class.getName(), Messages.getString("org.nightlabs.jfire.reporting.trade.ui.textpart.ReportTextPartConfigurationPage.name")); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
		final ReportTextPartConfigurationPageController controller = (ReportTextPartConfigurationPageController) getPageController();
		
		reportTextPartConfigurationSection = new ReportTextPartConfigurationSection(this, parent, controller);
		reportTextPartConfigurationSection.getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getManagedForm().addPart(reportTextPartConfigurationSection);

		if (controller.isLoaded()) {
			reportTextPartConfigurationSection.updateReportRegistryItems();
		}
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		getManagedForm().getForm().getDisplay().asyncExec(new Runnable() {
			public void run() {
				reportTextPartConfigurationSection.updateReportRegistryItems();
			}
		});
		switchToContent();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return Messages.getString("org.nightlabs.jfire.reporting.trade.ui.textpart.ReportTextPartConfigurationPage.pageFormTitle"); //$NON-NLS-1$
	}

}
