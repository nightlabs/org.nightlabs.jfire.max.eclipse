/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportNameSection extends ToolBarSectionPart {

	private ScheduledReport scheduledReport;
	private I18nTextEditor nameEditor;
	
	public ScheduledReportNameSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR, "Scheduled report name");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;

		getSection().setClient(client);
		
		nameEditor = new I18nTextEditor(client, "Name");
		nameEditor.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				markDirty();
			}
		});
	}
	
	@Override
	public boolean setFormInput(Object input) {
		if (input instanceof ScheduledReport) {
			this.scheduledReport = (ScheduledReport) input;
		}
		return super.setFormInput(input);
	}
	
	@Override
	public void refresh() {
		super.refresh();
		if (scheduledReport != null) {
			nameEditor.setI18nText(scheduledReport.getName());
		}
	}
	
	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		nameEditor.copyToOriginal();
	}
}
