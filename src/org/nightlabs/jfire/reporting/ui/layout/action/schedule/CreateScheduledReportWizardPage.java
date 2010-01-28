/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.action.schedule;

import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.LanguageChooserCombo;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.language.LanguageChooserCombo.Mode;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.base.ui.timer.TaskDetailEditComposite;
import org.nightlabs.jfire.reporting.layout.render.RenderReportRequest;
import org.nightlabs.jfire.reporting.ui.config.BirtOutputCombo;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class CreateScheduledReportWizardPage extends WizardHopPage {

	private CreateScheduledReportWizard createReportWizard;

	private I18nTextEditor nameEditor;
	private TaskDetailEditComposite taskDetailComposite;

	private LanguageChooserCombo languageChooser;
	private BirtOutputCombo outputCombo;
	
	/**
	 * @param pageName
	 */
	public CreateScheduledReportWizardPage(CreateScheduledReportWizard createReportWizard) {
		super(CreateScheduledReportWizardPage.class.getName(), "Schedule the execution of a report");
		setMessage("Define the name and time pattern for the scheduled report");
		this.createReportWizard = createReportWizard;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		createReportWizard.getScheduledReport().getName().setText(
				Locale.getDefault(), 
				createReportWizard.getReportLayout().getName().getText() + " (scheduled)");
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		nameEditor = new I18nTextEditor(wrapper, "Scheduled report name");
		nameEditor.setI18nText(createReportWizard.getScheduledReport().getName(), EditMode.DIRECT);
		
		Label separator = new Label(wrapper, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		taskDetailComposite = new TaskDetailEditComposite(wrapper, SWT.NONE, "Enable render of scheduled report", null);
		taskDetailComposite.setTask(createReportWizard.getScheduledReport().getTask());
		
		XComposite secondPartWrapper = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL);
		secondPartWrapper.getGridLayout().numColumns = 2;
		
		Label languageLabel = new Label(secondPartWrapper, SWT.WRAP);
		languageLabel.setText("Language");
		Label outputFormatLabel = new Label(secondPartWrapper, SWT.WRAP);
		outputFormatLabel.setText("Output format");
		
		languageChooser = new LanguageChooserCombo(secondPartWrapper, Mode.iconAndText);

		outputCombo = new BirtOutputCombo(secondPartWrapper, SWT.NONE);
		GridData outputGD = new GridData();
		outputGD.widthHint = 100;
		outputCombo.setLayoutData(outputGD);
		
		return wrapper;
	}
	
	public void commitProperties() {
		taskDetailComposite.commitPropeties();
		RenderReportRequest renderReportRequest = createReportWizard.getScheduledReport().getRenderReportRequest();
		if (renderReportRequest == null) {
			renderReportRequest = new RenderReportRequest();
		}
		renderReportRequest.setOutputFormat(outputCombo.getSelectedElement());
		renderReportRequest.setLocale(new Locale(languageChooser.getLanguage().getLanguageID()));
		createReportWizard.getScheduledReport().setRenderReportRequest(renderReportRequest);
	}

}
