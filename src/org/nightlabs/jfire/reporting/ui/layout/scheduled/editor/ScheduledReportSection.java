/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.editor;

import java.util.Locale;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.language.LanguageChooserCombo;
import org.nightlabs.base.ui.language.LanguageChooserCombo.Mode;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.ui.layout.ReportLayoutSelectionComposite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportSection extends ToolBarSectionPart {

	private ScheduledReport scheduledReport;
	private ReportLayoutSelectionComposite reportLayoutComposite;
	private LanguageChooserCombo languageChooser;
	
	public ScheduledReportSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR, "Report && parameters");
		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;

		getSection().setClient(client);
		
		reportLayoutComposite = new ReportLayoutSelectionComposite(client, SWT.NONE, "Report layout");
		reportLayoutComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				markDirty();
			}
		});
		
		languageChooser = new LanguageChooserCombo(client, Mode.iconAndText);
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
			reportLayoutComposite.setReportLayoutID(scheduledReport.getReportLayoutID());
			RenderReportRequest renderReportRequest = scheduledReport.getRenderReportRequest();
			if (renderReportRequest != null) {
				languageChooser.select(scheduledReport.getRenderReportRequest().getLocale());
			}
		}
	}
	
	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		scheduledReport.setReportLayoutID(reportLayoutComposite.getReportLayoutID());
		RenderReportRequest renderReportRequest = new RenderReportRequest();
		renderReportRequest.setReportRegistryItemID(reportLayoutComposite.getReportLayoutID());
		renderReportRequest.setLocale(new Locale(languageChooser.getLanguage().getLanguageID()));
		scheduledReport.setRenderReportRequest(renderReportRequest);
	}
}
