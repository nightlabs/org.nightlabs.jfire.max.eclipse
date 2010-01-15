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
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.timepattern.TimePatternSetComposite;
import org.nightlabs.base.ui.timepattern.builder.TimePatternSetBuilderWizard;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.timepattern.TimePatternSetImpl;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class CreateScheduledReportWizardPage extends WizardHopPage {

	private CreateScheduledReportWizard createReportWizard;
	
	private TimePatternSetComposite timePatternSetComposite;

	private I18nTextEditor nameEditor;
	
	/**
	 * @param pageName
	 */
	public CreateScheduledReportWizardPage(CreateScheduledReportWizard createReportWizard) {
		super(CreateScheduledReportWizardPage.class.getName(), "Schedule the execution of a report");
		setMessage("Define the time pattern for the execution");
		this.createReportWizard = createReportWizard;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		nameEditor = new I18nTextEditor(wrapper, "Scheduled report name");
		nameEditor.getI18nText().setText(Locale.getDefault(), createReportWizard.getReportLayout().getName().getText() + " (scheduled)");
		nameEditor.refresh();
		
		Label separator = new Label(wrapper, SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Hyperlink link = new Hyperlink(wrapper, SWT.NONE);
		link.setText("Build time pattern");
		link.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent arg0) {
				if (TimePatternSetBuilderWizard.open(timePatternSetComposite.getShell(), timePatternSetComposite.getTimePatternSet())) {
					timePatternSetComposite.refresh(true);
				}
			}
		});
		timePatternSetComposite = new TimePatternSetComposite(wrapper, SWT.NONE);
		timePatternSetComposite.setTimePatternSet(new TimePatternSetImpl());
		return wrapper;
	}

}
