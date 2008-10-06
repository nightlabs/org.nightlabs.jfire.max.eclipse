package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import java.util.Date;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.XComposite;
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


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class RecurringOfferHeaderComposite
extends HeaderComposite{

	private CurrentStateComposite currentStateComposite;
	private NextTransitionComposite nextTransitionComposite;
	XComposite infoStatuesContainerComp;

	private volatile RecurringOffer recurringOffer;

	public RecurringOfferHeaderComposite(ArticleContainerEditComposite articleContainerEditComposite,
			RecurringOffer recurringOffer) {
		super(articleContainerEditComposite, articleContainerEditComposite, recurringOffer);

		this.recurringOffer = recurringOffer;


		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL|GridData.HORIZONTAL_ALIGN_CENTER));


		infoStatuesContainerComp = new XComposite(this, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.NONE);
		infoStatuesContainerComp.setLayoutData(null);
		infoStatuesContainerComp.getGridLayout().numColumns = 3;
		currentStateComposite = new CurrentStateComposite(infoStatuesContainerComp , SWT.WRAP |SWT.NONE);
		currentStateComposite.setStatable(recurringOffer);
		//currentStateComposite.setLayoutData(null);

		nextTransitionComposite = new NextTransitionComposite(infoStatuesContainerComp ,SWT.WRAP | SWT.NONE);
		nextTransitionComposite.setStatable(recurringOffer);
		//	nextTransitionComposite.setLayoutData(new RowData(260, SWT.DEFAULT));
		nextTransitionComposite.addSignalListener(new SignalListener() {
			@Implement
			public void signal(SignalEvent event)
			{
				signalNextTransition(event);
			}
		});

		new Label(infoStatuesContainerComp, SWT.WRAP |SWT.NONE).setText("RecurredOffers:" + String.valueOf(recurringOffer.getRecurredOfferCount()));

		if(recurringOffer.getStatusKey() != null)
		{
			if(recurringOffer.getStatusKey().equals(RecurringOffer.STATUS_KEY_PRICES_NOT_EQUAL))
				new Label(infoStatuesContainerComp,SWT.WRAP |SWT.NONE).setText("Non equal Prices");

			if(recurringOffer.getStatusKey().equals(RecurringOffer.STATUS_KEY_SUSPENDED))
				new Label(infoStatuesContainerComp,SWT.WRAP |SWT.NONE).setText("Suspended");

			if(recurringOffer.getStatusKey().equals(RecurringOffer.STATUS_KEY_NONE))
				new Label(infoStatuesContainerComp,SWT.WRAP |SWT.NONE).setText("Active");
		}

		Date date  = recurringOffer.getRecurringOfferConfiguration().getCreatorTask().getLastExecDT();
		if(date != null)
			new Label(infoStatuesContainerComp,SWT.WRAP |SWT.NONE).setText("Last Task:" + DateFormatter.formatDate(date, DateFormatter.FLAGS_DATE_SHORT_TIME_HM));

		date  = recurringOffer.getRecurringOfferConfiguration().getCreatorTask().getNextExecDT();
		if(date != null)
			new Label(infoStatuesContainerComp, SWT.WRAP |SWT.NONE).setText("Next Task:" + DateFormatter.formatDate(date, DateFormatter.FLAGS_DATE_SHORT_TIME_HM));



		getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isDisposed())
					return;
				getShell().layout(true, true);

				getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (isDisposed())
							return;
						getShell().layout(true, true);
					}
				});
			}
		});








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






