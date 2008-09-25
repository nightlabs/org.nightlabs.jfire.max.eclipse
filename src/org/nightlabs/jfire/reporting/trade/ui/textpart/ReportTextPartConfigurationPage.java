/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.textpart;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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

	private ReportRegistryItemsSection reportRegistryItemsSection;
	private ReportTextPartConfigurationSection reportTextPartConfigurationSection;
	
	/**
	 * @param editor
	 */
	public ReportTextPartConfigurationPage(FormEditor editor) {
		super(editor, ReportTextPartConfigurationPage.class.getName(), "Repor text parts");
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
		final ReportTextPartConfigurationPageController controller = (ReportTextPartConfigurationPageController) getPageController();
		
		reportRegistryItemsSection = new ReportRegistryItemsSection(this, parent, controller);
		getManagedForm().addPart(reportRegistryItemsSection);

		reportTextPartConfigurationSection = new ReportTextPartConfigurationSection(this, parent, controller);
		getManagedForm().addPart(reportTextPartConfigurationSection);

		reportRegistryItemsSection.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateConfigurationSection();
			}
		});
		if (controller.isLoaded()) {
			reportRegistryItemsSection.updateReportRegistryItems();
		}
	}

	@Override
	protected void handleControllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
		getManagedForm().getForm().getDisplay().asyncExec(new Runnable() {
			public void run() {
				reportRegistryItemsSection.updateReportRegistryItems();
				updateConfigurationSection();
			}
		});		
	}

	private void updateConfigurationSection() {
		ReportRegistryItem selection = reportRegistryItemsSection.getSelectedReportRegistryItem();
		if (selection != null)
			reportTextPartConfigurationSection.loadReportTextPartConfiguration(selection);
	}
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return "Report text parts";
	}

}
