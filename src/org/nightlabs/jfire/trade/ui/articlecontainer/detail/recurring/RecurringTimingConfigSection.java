package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.timepattern.TimePatternSetComposite;
import org.nightlabs.base.ui.timepattern.TimePatternSetModifyEvent;
import org.nightlabs.base.ui.timepattern.TimePatternSetModifyListener;
import org.nightlabs.base.ui.timepattern.builder.TimePatternSetBuilderWizard;
import org.nightlabs.jfire.trade.recurring.RecurringOfferConfiguration;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.timepattern.TimePattern;
import org.nightlabs.util.Util;



/**
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
 *
 */
public class RecurringTimingConfigSection extends AbstractRecurringConfigGeneralSection {


	private TimePatternSetComposite timePatternSetComposite;
	private DateTimeControl stopDateControl;
	private Button enableEndCheck;

	
	public RecurringTimingConfigSection(FormPage page, Composite parent, final RecurringOfferConfigurationPageController controller) {
		super(page, parent, controller);
		getSection().setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringTimingConfigSection.text")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringTimingConfigSection.description")); //$NON-NLS-1$

		timePatternSetComposite = new TimePatternSetComposite(getContainer(), SWT.NONE);
		timePatternSetComposite.addTimePatternSetModifyListener(new TimePatternSetModifyListener(){
			@Override			
			public void timePatternSetModified(TimePatternSetModifyEvent event)
			{
				saveTimePattern();
			}

		});

		XComposite enableDateContainer = new XComposite(getContainer(), SWT.NONE, LayoutMode.LEFT_RIGHT_WRAPPER);
		enableDateContainer.getGridLayout().numColumns = 2;

		enableEndCheck = new Button(enableDateContainer, SWT.CHECK);
		enableEndCheck.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringTimingConfigSection.button.text.activateEndDate")); //$NON-NLS-1$
		enableEndCheck.setToolTipText(""); //$NON-NLS-1$
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

		stopDateControl = new DateTimeControl(enableDateContainer,false,SWT.NONE, DateFormatter.FLAGS_DATE_SHORT_TIME_HM);
		stopDateControl.setEnabled(false);
		stopDateControl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(stopDateControl.getDate()!=null)
					getController().getControllerObject().setSuspendDate(stopDateControl.getDate());
				markDirty();

			}
		});


		DefineRecurringTimePatternAction defineRecurringTimePatternAction = new DefineRecurringTimePatternAction();
		getToolBarManager().add(defineRecurringTimePatternAction);

		AddRecurringTimePatternAction addRecurringTimePatternAction = new AddRecurringTimePatternAction();
		getToolBarManager().add(addRecurringTimePatternAction);

		RemoveRecurringTimePatternAction removeRecurringTimePatternAction = new RemoveRecurringTimePatternAction();
		getToolBarManager().add(removeRecurringTimePatternAction);

		updateToolBarManager();
	}
	
	@Override
	protected void updateConfigOffer(
			RecurringOfferConfiguration recurringOfferConfiguration) {

		timePatternSetComposite.setTimePatternSet(Util.cloneSerializable(recurringOfferConfiguration.getCreatorTask().getTimePatternSet()));
	}


	class AddRecurringTimePatternAction
	extends Action
	{
		public AddRecurringTimePatternAction() {
			super();
			setId(AddRecurringTimePatternAction.class.getName());
			setText("+"); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringTimingConfigSection.addAction.tooltip")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			createRecurringTimePatternClicked();
		}
	}

	class RemoveRecurringTimePatternAction
	extends Action
	{
		public RemoveRecurringTimePatternAction() {
			super();		
			setId(RemoveRecurringTimePatternAction.class.getName());
			setText("-"); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringTimingConfigSection.removeAction.tooltip")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			removeRecurringTimePatternClicked();
		}
	}


	class DefineRecurringTimePatternAction
	extends Action
	{
		public DefineRecurringTimePatternAction() {
			super();
			setId(DefineRecurringTimePatternAction.class.getName());
			setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringTimingConfigSection.defineAction.text")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringTimingConfigSection.defineAction.tooltip")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			defineRecurringTimePatternClicked();
		}
	}


	protected void createRecurringTimePatternClicked()
	{
		timePatternSetComposite.createTimePattern();

	}
	protected void removeRecurringTimePatternClicked()
	{

		timePatternSetComposite.removeSelectedTimePatterns();

	}

	protected void defineRecurringTimePatternClicked()
	{	
		if(TimePatternSetBuilderWizard.open(timePatternSetComposite.getTimePatternSet()))
		{
			saveTimePattern();
		}
	}


	protected void saveTimePattern()
	{
		timePatternSetComposite.refresh(true);

		getController().getControllerObject().getCreatorTask().getTimePatternSet().clearTimePatterns();

		Set<TimePattern> patterns = timePatternSetComposite.getTimePatternSet().getTimePatterns();
		for (TimePattern p : patterns) {
			getController().getControllerObject().getCreatorTask().getTimePatternSet().addTimePattern(p);
		}
		markDirty();	
	}
}
