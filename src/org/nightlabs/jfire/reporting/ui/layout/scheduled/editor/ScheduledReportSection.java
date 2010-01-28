/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled.editor;

import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.language.LanguageChangeEvent;
import org.nightlabs.base.ui.language.LanguageChangeListener;
import org.nightlabs.base.ui.language.LanguageChooserCombo;
import org.nightlabs.base.ui.language.LanguageChooserCombo.Mode;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.ui.config.BirtOutputCombo;
import org.nightlabs.jfire.reporting.ui.layout.ReportLayoutSelectionComposite;
import org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizard;
import org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizard.WizardResult;
import org.nightlabs.util.Util;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ScheduledReportSection extends ToolBarSectionPart {

	private ScheduledReport scheduledReport;
	private ReportLayoutSelectionComposite reportLayoutComposite;
	private LanguageChooserCombo languageChooser;
	private Text parameterText;
	
	private Map<String, Object> reportParameters = null;
	private ReportRegistryItemID reportLayoutID = null;
	private BirtOutputCombo outputCombo;
	
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
				if (!Util.equals(reportLayoutID, reportLayoutComposite.getReportLayoutID())) {
					reportParameters = null;
					updateParameterText();
					markDirty();
				}
			}
		});
		
		XComposite secondPartWrapper = new XComposite(client, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		secondPartWrapper.getGridLayout().numColumns = 3;
		
		Label languageLabel = new Label(secondPartWrapper, SWT.WRAP);
		languageLabel.setText("Language");
		Label outputFormatLabel = new Label(secondPartWrapper, SWT.WRAP);
		outputFormatLabel.setText("Output format");
		Label parameterLabel = new Label(secondPartWrapper, SWT.WRAP);
		parameterLabel.setText("Parameters");
		
		languageChooser = new LanguageChooserCombo(secondPartWrapper, Mode.iconAndText);
		languageChooser.addLanguageChangeListener(new LanguageChangeListener() {
			@Override
			public void languageChanged(LanguageChangeEvent event) {
				markDirty();
			}
		});

		outputCombo = new BirtOutputCombo(secondPartWrapper, SWT.NONE);
		GridData outputGD = new GridData();
		outputGD.widthHint = 100;
		outputCombo.setLayoutData(outputGD);
		outputCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				markDirty();
			}
		});
		
		XComposite parameterWrapper = new XComposite(secondPartWrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		parameterWrapper.getGridLayout().numColumns = 3;
		parameterWrapper.getGridLayout().makeColumnsEqualWidth = false;
		parameterText = new Text(parameterWrapper, parameterWrapper.getBorderStyle());
		parameterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		parameterText.setEditable(false);
		Button defineParameters = new Button(parameterWrapper, SWT.PUSH);
		defineParameters.setText("...");
		defineParameters.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ReportRegistryItemID layoutID = reportLayoutComposite.getReportLayoutID();
				if (layoutID != null) {
					WizardResult wizardResult = ReportParameterWizard.openResult(parameterText.getShell(), layoutID, true);
					if (wizardResult != null && wizardResult.isAcquisitionFinished()) {
						reportParameters = wizardResult.getParameters();
						updateParameterText();
						markDirty();
					}
				}
			}
		});
		Button resetParameters = new Button(parameterWrapper, SWT.PUSH);
		resetParameters.setText("X");
		resetParameters.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				reportParameters = null;
				updateParameterText();
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
	
	private void updateParameterText() {
		if (reportParameters != null && reportParameters.size() > 0) {
			parameterText.setText(String.format("%s parameter(s) defined", reportParameters.size()));
		} else {
			parameterText.setText("No parameters defined");
		}
	}
	
	@Override
	public void refresh() {
		super.refresh();
		if (scheduledReport != null) {
			reportLayoutComposite.setReportLayoutID(scheduledReport.getReportLayoutID());
			reportLayoutID = scheduledReport.getReportLayoutID();
			RenderReportRequest renderReportRequest = scheduledReport.getRenderReportRequest();
			if (renderReportRequest != null) {
				languageChooser.select(scheduledReport.getRenderReportRequest().getLocale());
				reportParameters = renderReportRequest.getParameters();
				outputCombo.setSelection(renderReportRequest.getOutputFormat());
			} else {
				reportParameters = null;
			}
			updateParameterText();
		}
	}
	
	@Override
	public void commit(boolean onSave) {
		super.commit(onSave);
		scheduledReport.setReportLayoutID(reportLayoutComposite.getReportLayoutID());
		RenderReportRequest renderReportRequest = new RenderReportRequest();
		renderReportRequest.setReportRegistryItemID(reportLayoutComposite.getReportLayoutID());
		renderReportRequest.setLocale(new Locale(languageChooser.getLanguage().getLanguageID()));
		renderReportRequest.setParameters(reportParameters);
		renderReportRequest.setOutputFormat(outputCombo.getSelectedElement());
		scheduledReport.setRenderReportRequest(renderReportRequest);
	}
}
