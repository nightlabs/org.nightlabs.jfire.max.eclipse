/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.editor;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageFactory;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportEditorPage extends EntityEditorPageWithProgress {

	public static class Factory implements IEntityEditorPageFactory {

		@Override
		public IFormPage createPage(FormEditor formEditor) {
			return new ScheduledReportEditorPage(formEditor);
		}

		@Override
		public IEntityEditorPageController createPageController(EntityEditor editor) {
			return new ScheduledReportEditorPageController(editor);
		}
		
	}
	
	private ScheduledReportNameSection nameSection;
	private ScheduledReportTaskSection taskSection;
	private ScheduledReportSection reportSection;
	
	/**
	 * @param editor
	 * @param id
	 * @param name
	 */
	public ScheduledReportEditorPage(FormEditor editor) {
		super(editor, ScheduledReportEditor.class.getName(), "Scheduled report");
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#addSections(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void addSections(Composite parent) {
		nameSection = new ScheduledReportNameSection(this, parent);
		getManagedForm().addPart(nameSection);
		
		taskSection = new ScheduledReportTaskSection(this, parent);
		getManagedForm().addPart(taskSection);
		
		reportSection = new ScheduledReportSection(this, parent);
		getManagedForm().addPart(reportSection);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.editor.EntityEditorPageWithProgress#getPageFormTitle()
	 */
	@Override
	protected String getPageFormTitle() {
		return "Scheduled Report";
	}
}
