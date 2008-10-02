package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.nightlabs.base.ui.timepattern.TimePatternSetComposite;
import org.nightlabs.base.ui.timepattern.builder.TimePatternSetBuilderWizard;

import org.nightlabs.jfire.trade.recurring.RecurringOfferConfiguration;



/**
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 *
 */
public class RecurringTimingConfigSection extends AbstractRecurringConfigGeneralSection{


	private RecurringOfferConfigurationPageController controller;
	private Button taskTimePattern;
	private TimePatternSetComposite timePatternSetComposite;
	

	public RecurringTimingConfigSection(FormPage page, Composite parent, final RecurringOfferConfigurationPageController controller) {

		super(page, parent, controller);
		getSection().setText("Recurring Timer");
		getClient().getGridLayout().numColumns = 3;
		getClient().getGridLayout().makeColumnsEqualWidth = false;


		taskTimePattern = new Button(getClient(), SWT.PUSH);
		taskTimePattern.setText("Task Timer...");		
		taskTimePattern.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			public void widgetSelected(SelectionEvent e) {
				if
				(TimePatternSetBuilderWizard.open(timePatternSetComposite.getTimePatternSet()))
				{
					timePatternSetComposite.refresh(true);
					RecurringTimingConfigSection.this.markDirty();
				}
			}
		});

		timePatternSetComposite = new TimePatternSetComposite(getClient(),
				SWT.NONE);

	}



	@Override
	protected void updateConfigOffer(
			RecurringOfferConfiguration recurringOfferConfiguration) {
		// TODO Auto-generated method stub

	}


}
