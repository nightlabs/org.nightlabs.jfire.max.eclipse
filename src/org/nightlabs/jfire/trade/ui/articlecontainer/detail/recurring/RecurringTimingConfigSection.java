package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.forms.editor.FormPage;

import org.nightlabs.jfire.trade.recurring.RecurringOfferConfiguration;

public class RecurringTimingConfigSection extends AbstractRecurringConfigGeneralSection{


	private RecurringOfferConfigurationPageController controller;

	private Button buildTimePattern;




	public RecurringTimingConfigSection(FormPage page, Composite parent, final RecurringOfferConfigurationPageController controller) {

		super(page, parent, controller);
		getSection().setText("Timer");


//		buildTimePattern = new Button(above, SWT.PUSH);
//		buildTimePattern.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END
//		| GridData.GRAB_HORIZONTAL));
//		buildTimePattern.setText(AccountingPlugin.getResourceString("edit.task.accountingTaskDetailSection.buildTimePattern"));		
//		buildTimePattern.addSelectionListener(new SelectionListener() {
//		public void widgetDefaultSelected(SelectionEvent e) {
//		}
//		public void widgetSelected(SelectionEvent e) {
//		if
//		(TimePatternSetBuilderWizard.open(timePatternSetComposite.getTimePatternSet()))
//		{
//		timePatternSetComposite.refresh(true);
//		AccountingTaskDetailSection.this.markDirty();
//		}
//		}
//		});
//		timePatternSetComposite = new TimePatternSetComposite(timePatternWrapper,








	}



	@Override
	protected void updateConfigOffer(
			RecurringOfferConfiguration recurringOfferConfiguration) {
		// TODO Auto-generated method stub

	}


}
