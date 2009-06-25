package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import java.util.Date;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.MessageComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.MessageComposite.MessageType;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalEvent;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalListener;
import org.nightlabs.jfire.timer.Task;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.recurring.RecurringOffer;
import org.nightlabs.jfire.trade.recurring.dao.RecurringOfferDAO;
import org.nightlabs.jfire.trade.recurring.jbpm.JbpmConstantsRecurringOffer;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.HeaderComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;


/**
 * @author Fitas Amine - fitas at nightlabs dot de
 */
public class RecurringOfferHeaderComposite
extends HeaderComposite{

	private CurrentStateComposite currentStateComposite;
	private NextTransitionComposite nextTransitionComposite;
	private XComposite infoStatuesContainerComp;
	private XComposite infodateContainerComp;
	private Label lastTaskDateLabel;
	private Label nextTaskDateLabel;
	private Label recurredOfferCount;
	private MessageComposite statusMsg;

	private volatile RecurringOffer recurringOffer;

	public RecurringOfferHeaderComposite(ArticleContainerEditComposite articleContainerEditComposite,
			RecurringOffer recurringOffer) {
		super(articleContainerEditComposite, articleContainerEditComposite, recurringOffer);

		this.recurringOffer = recurringOffer;


		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL|GridData.HORIZONTAL_ALIGN_CENTER));
		this.getGridLayout().numColumns = 1;

		infoStatuesContainerComp = new XComposite(this, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.NONE);
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = true;
		rowLayout.pack = false;
		rowLayout.marginLeft = 0;
		infoStatuesContainerComp.setLayout(rowLayout);

		currentStateComposite = new CurrentStateComposite(infoStatuesContainerComp , SWT.WRAP |SWT.NONE);
		currentStateComposite.setStatable(recurringOffer);
		currentStateComposite.setLayoutData(null);

		nextTransitionComposite = new NextTransitionComposite(infoStatuesContainerComp ,SWT.WRAP | SWT.NONE);
		nextTransitionComposite.setStatable(recurringOffer);
		nextTransitionComposite.setLayoutData(null);
		nextTransitionComposite.addSignalListener(new SignalListener() {
			@Override
			public void signal(SignalEvent event)
			{
				signalNextTransition(event);
			}
		});

		XComposite ordinaryWrapper = new XComposite(this, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER);

		recurredOfferCount = new Label(ordinaryWrapper, SWT.WRAP |SWT.NONE);
		recurredOfferCount.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferHeaderComposite.label.text.recurredOffers") + String.valueOf(recurringOffer.getRecurredOfferCount())); //$NON-NLS-1$

		statusMsg = new MessageComposite(this, SWT.NONE, "", MessageType.WARNING); //$NON-NLS-1$
		statusMsg.setLayoutData(new GridData());

		XComposite infodateContainerComp = new XComposite(ordinaryWrapper, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.NONE);
		rowLayout = new RowLayout();
		rowLayout.wrap = true;
		rowLayout.pack = false;
		rowLayout.marginLeft = 0;
		infodateContainerComp.setLayout(rowLayout);


		lastTaskDateLabel = new Label(infodateContainerComp,SWT.WRAP|SWT.NONE);
		lastTaskDateLabel.setLayoutData(new RowData());

		nextTaskDateLabel = new Label(infodateContainerComp,SWT.WRAP |SWT.NONE);
		nextTaskDateLabel.setLayoutData(new RowData());

		updateState();

		JDOLifecycleManager.sharedInstance().addNotificationListener(RecurringOffer.class, offerChangedListener);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				JDOLifecycleManager.sharedInstance().removeNotificationListener(RecurringOffer.class, offerChangedListener);
			}
		});

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

	private void updateState()
	{
		MessageComposite.MessageType iconType = MessageComposite.MessageType.INFO;

		if(recurringOffer.getStatusKey().equals(RecurringOffer.STATUS_KEY_NONE))
			setWidgetExcluded((GridData) statusMsg.getLayoutData(),true);
		else
		{
			setWidgetExcluded((GridData) statusMsg.getLayoutData(),false);

			String typeKey = RecurringOfferHeaderComposite.class.getName() + ".status.type." + recurringOffer.getStatusKey(); //$NON-NLS-1$
			if (Messages.RESOURCE_BUNDLE.containsKey(typeKey)) {
				String msgtype = Messages.getString(typeKey);
				if (msgtype != null && !"".equals(msgtype)) //$NON-NLS-1$
					iconType = MessageComposite.MessageType.valueOf(msgtype.toUpperCase());
			}
			statusMsg.setMessage(Messages.getString(RecurringOfferHeaderComposite.class.getName() + ".status.message." + recurringOffer.getStatusKey()), iconType); //$NON-NLS-1$
		}

		Date date  = recurringOffer.getRecurringOfferConfiguration().getCreatorTask().getLastExecDT();
		if(date != null)
		{
			lastTaskDateLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferHeaderComposite.label.text.lastTask") + DateFormatter.formatDate(date, DateFormatter.FLAGS_DATE_SHORT_TIME_HM)); //$NON-NLS-1$
			setWidgetExcluded((RowData) lastTaskDateLabel.getLayoutData(),false);

		}
		else
			setWidgetExcluded((RowData) lastTaskDateLabel.getLayoutData(),true);

		date  = recurringOffer.getRecurringOfferConfiguration().getCreatorTask().getNextExecDT();
		if(date != null)
		{
			nextTaskDateLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferHeaderComposite.label.text.nextTask") + DateFormatter.formatDate(date, DateFormatter.FLAGS_DATE_SHORT_TIME_HM)); //$NON-NLS-1$
			setWidgetExcluded((RowData) nextTaskDateLabel.getLayoutData(),false);

		}
		else
			setWidgetExcluded((RowData) nextTaskDateLabel.getLayoutData(),true);

		if(recurringOffer.getStatusKey().equals(RecurringOffer.STATUS_KEY_SUSPENDED))
		setWidgetExcluded((RowData) nextTaskDateLabel.getLayoutData(),true);

		recurredOfferCount.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferHeaderComposite.label.text.recurredOffers") + String.valueOf(recurringOffer.getRecurredOfferCount())); //$NON-NLS-1$

	}

	private void setWidgetExcluded(RowData data , boolean exclude)
	{
		data.exclude = exclude;
	}

	private void setWidgetExcluded(GridData data , boolean exclude)
	{
		data.exclude = exclude;
	}

	private void onOfferModified(final RecurringOffer recurringOffer, ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferHeaderComposite.job.task.name.updateUI"), 100); //$NON-NLS-1$
		try {
			this.recurringOffer = recurringOffer;
			currentStateComposite.setStatable(recurringOffer, new SubProgressMonitor(monitor, 1));
			nextTransitionComposite.setStatable(recurringOffer, new SubProgressMonitor(monitor, 1));
			getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;
					updateState();
					getShell().layout(true, true);
				}
			});
		} finally {
			monitor.done();
		}

	}

	private NotificationListener offerChangedListener = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.loadOfferJob.name")) { //$NON-NLS-1$
		public void notify(NotificationEvent notificationEvent)
		{
			ProgressMonitor monitor = getProgressMonitor();
			monitor.beginTask(
					Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.loadOfferMonitor.task.name"), //$NON-NLS-1$
					3
			);
			RecurringOffer _offer = RecurringOfferDAO.sharedInstance().getRecurringOffer(
					(OfferID) JDOHelper.getObjectId(recurringOffer),
					RecurringArticleContainerEditComposite.FETCH_GROUPS_RECURRING_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 1));

			onOfferModified(_offer, new SubProgressMonitor(monitor, 2));
			monitor.done();
		}
	};


	private void signalNextTransition(final SignalEvent event)
	{

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.performTransitionJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());

					if (JbpmConstantsRecurringOffer.Vendor.TRANSITION_NAME_START_RECURRENCE.equals(event.getTransition().getJbpmTransitionName()))
					{
						Task recurringTask = recurringOffer.getRecurringOfferConfiguration().getCreatorTask();
						if(!recurringTask.getTimePatternSet().getTimePatterns().isEmpty())
							tm.signalOffer((OfferID)JDOHelper.getObjectId(recurringOffer), event.getTransition().getJbpmTransitionName());
						else {
							nextTransitionComposite.getDisplay().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openError(getDisplay().getActiveShell(), Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferHeaderComposite.dialog.title"), Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.RecurringOfferHeaderComposite.dialog.message")); //$NON-NLS-1$ //$NON-NLS-2$
									nextTransitionComposite.setEnabled(true);
								}
							});
						}
					}
					else
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






