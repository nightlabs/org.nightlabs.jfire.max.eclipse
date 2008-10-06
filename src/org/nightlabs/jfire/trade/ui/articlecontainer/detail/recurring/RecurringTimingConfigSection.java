package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.timepattern.TimePatternSetComposite;
import org.nightlabs.base.ui.timepattern.builder.TimePatternSetBuilderWizard;
import org.nightlabs.jfire.trade.recurring.RecurringOfferConfiguration;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.util.Util;



/**
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 *
 */
public class RecurringTimingConfigSection extends AbstractRecurringConfigGeneralSection{


	private RecurringOfferConfigurationPageController controller;
	private Button taskTimePattern;
	private TimePatternSetComposite timePatternSetComposite;
	private DateTimeControl stopDateControl;
	private Button enableEndCheck;



	public RecurringTimingConfigSection(FormPage page, Composite parent, final RecurringOfferConfigurationPageController controller) {

		super(page, parent, controller);
		getSection().setText("Recurring Timer");
		getClient().getGridLayout().numColumns = 2;
		getClient().getGridLayout().makeColumnsEqualWidth = false;


		taskTimePattern = new Button(getClient(), SWT.PUSH);
		taskTimePattern.setText("Recurring Date...");
		taskTimePattern.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if(TimePatternSetBuilderWizard.open(timePatternSetComposite.getTimePatternSet()))
				{
					timePatternSetComposite.refresh(true);
					Set<TimePattern> patterns = timePatternSetComposite.getTimePatternSet().getTimePatterns();
					for (TimePattern p : patterns) {
						getController().getControllerObject().getCreatorTask().getTimePatternSet().addTimePattern(p);
					}
					markDirty();
				}
			}
		});

		timePatternSetComposite = new TimePatternSetComposite(getClient(),SWT.NONE);
		timePatternSetComposite.setLayoutData(new GridData(550,75));

		enableEndCheck = new Button(getClient(), SWT.CHECK);
		enableEndCheck.setText("Activate End Date");
		enableEndCheck.setToolTipText("");
		enableEndCheck.setSelection(false);
		enableEndCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stopDateControl.setEnabled(enableEndCheck.getSelection());

				if(!enableEndCheck.getSelection())
				{
					getController().getControllerObject().setSuspendDate(null);
					markDirty();
				}

			}
		});

		stopDateControl = new DateTimeControl(getClient(), SWT.NONE, DateFormatter.FLAGS_DATE_SHORT_TIME_HM);
		stopDateControl.setEnabled(false);
		stopDateControl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(stopDateControl.getDate()!=null)
					getController().getControllerObject().setSuspendDate(stopDateControl.getDate());
				markDirty();

			}
		});

	}
	@Override
	protected void updateConfigOffer(
			RecurringOfferConfiguration recurringOfferConfiguration) {

		timePatternSetComposite.setTimePatternSet(Util.cloneSerializable(recurringOfferConfiguration.getCreatorTask().getTimePatternSet()));
		getClient().pack();

	}


}
