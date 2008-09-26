package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import java.util.Date;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalEvent;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalListener;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.recurring.RecurringOffer;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.HeaderComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class RecurringOfferHeaderComposite
extends HeaderComposite{

	private CurrentStateComposite currentStateComposite;
	private NextTransitionComposite nextTransitionComposite;
	private XComposite infoDateComp;
	private Label nextExecutionstampTask;
	private Label lastExecutionstampTask;

	private volatile RecurringOffer recurringOffer;

	public RecurringOfferHeaderComposite(ArticleContainerEditComposite articleContainerEditComposite,
			RecurringOffer recurringOffer) {
		super(articleContainerEditComposite, articleContainerEditComposite, recurringOffer);

		this.recurringOffer = recurringOffer;

		this.setLayout(new RowLayout());

		currentStateComposite = new CurrentStateComposite(this, SWT.NONE);
		currentStateComposite.setStatable(recurringOffer);
		currentStateComposite.setLayoutData(null);

		nextTransitionComposite = new NextTransitionComposite(this, SWT.NONE);
		nextTransitionComposite.setStatable(recurringOffer);
		nextTransitionComposite.setLayoutData(new RowData(260, SWT.DEFAULT));
		nextTransitionComposite.addSignalListener(new SignalListener() {
			@Implement
			public void signal(SignalEvent event)
			{
				signalNextTransition(event);
			}
		});

		XComposite infoStatuesContainerComp = new XComposite(this, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.NONE);

		XComposite infoStatuesComp = new XComposite(infoStatuesContainerComp, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		infoStatuesComp.setLayoutData(null);
		infoStatuesComp.getGridLayout().numColumns = 1;


		new Label(infoStatuesComp, SWT.NONE).setText("RecurredOffers:");
		new Label(infoStatuesComp, SWT.NONE).setText(String.valueOf(recurringOffer.getRecurredOfferCount()));

		Label statuesLabel  = new Label(infoStatuesComp, SWT.NONE);


		if(recurringOffer.getStatusKey().equals(RecurringOffer.STATUS_KEY_PRICES_NOT_EQUAL)) 
			statuesLabel.setText("Non equal Prices");

		if(recurringOffer.getStatusKey().equals(RecurringOffer.STATUS_KEY_SUSPENDED)) 
			statuesLabel.setText("Suspended");

		if(recurringOffer.getStatusKey().equals(RecurringOffer.STATUS_KEY_NONE)) 
			statuesLabel.setText("Active");


		XComposite infoDateContainerComp = new XComposite(this, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.NONE);

		infoDateComp = new XComposite(infoDateContainerComp, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
		infoDateComp.setLayoutData(null);
		infoDateComp.getGridLayout().numColumns = 3;
		new Label(infoDateComp, SWT.NONE).setText("Last Task:");


		lastExecutionstampTask = new Label(infoDateComp, SWT.NONE);
		Date date  = recurringOffer.getRecurringOfferConfiguration().getCreatorTask().getLastExecDT();
		if(date != null)
			lastExecutionstampTask.setText(DateFormatter.formatDate(date, DateFormatter.FLAGS_DATE_SHORT_TIME_HM));

		new Label(infoDateComp, SWT.NONE).setText("Next Task:");
		nextExecutionstampTask = new Label(infoDateComp, SWT.NONE);
		date  = recurringOffer.getRecurringOfferConfiguration().getCreatorTask().getNextExecDT();
		if(date != null)
			nextExecutionstampTask.setText(DateFormatter.formatDate(date, DateFormatter.FLAGS_DATE_SHORT_TIME_HM));


















	}

	private void signalNextTransition(final SignalEvent event)
	{
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.performTransitionJob.name")) { //$NON-NLS-1$
			@Override
			@Implement
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					tm.signalOffer((OfferID)JDOHelper.getObjectId(recurringOffer), event.getTransition().getJbpmTransitionName());
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.setUser(true);
		job.schedule();
	}


}






