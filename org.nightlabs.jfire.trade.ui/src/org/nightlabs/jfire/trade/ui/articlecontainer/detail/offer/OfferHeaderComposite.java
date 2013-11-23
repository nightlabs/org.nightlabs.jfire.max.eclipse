/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer;

import java.util.Date;

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalEvent;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalListener;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.dao.OfferDAO;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.HeaderComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.HeaderVendorCustomerComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.IDateFormatter;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class OfferHeaderComposite
extends HeaderComposite
{
	private volatile Offer offer;

	private final CurrentStateComposite currentStateComposite;
	private final NextTransitionComposite nextTransitionComposite;

	private XComposite expiryTimestampContainerComp;
	private XComposite expiryTimestampUnfinalizedComp;
	private DateTimeControl expiryTimestampUnfinalized;
	private Button expiryTimestampUnfinalizedAutoManaged;

	private XComposite expiryTimestampFinalizedComp;
	private DateTimeControl expiryTimestampFinalized;
	private Button expiryTimestampFinalizedAutoManaged;

	private final HeaderVendorCustomerComposite headerVendorCustomerComposite;

	protected OfferID getOfferID()
	{
		return (OfferID) JDOHelper.getObjectId(offer);
	}

	private final RowData headerVendorCustomerCompositeRowData;

	private int getHeaderVendorCustomerCompositeRowDataWidth()
	{
		final RowLayout rowLayout = (RowLayout)getLayout();
		return getClientArea().width - rowLayout.marginLeft - rowLayout.marginRight - rowLayout.marginWidth;
	}

	public OfferHeaderComposite(final ArticleContainerEditComposite articleContainerEditComposite, final Offer offer)
	{
		super(articleContainerEditComposite, articleContainerEditComposite, offer);
		this.offer = offer;

		this.setLayout(new RowLayout());

		headerVendorCustomerComposite = new HeaderVendorCustomerComposite(this);
		headerVendorCustomerCompositeRowData = new RowData(getHeaderVendorCustomerCompositeRowDataWidth(), headerVendorCustomerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		headerVendorCustomerComposite.setLayoutData(headerVendorCustomerCompositeRowData);
		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				headerVendorCustomerCompositeRowData.width = getHeaderVendorCustomerCompositeRowDataWidth();
			}
		});

		currentStateComposite = new CurrentStateComposite(this, SWT.NONE);
		currentStateComposite.setStatable(offer);
		currentStateComposite.setLayoutData(null);

		nextTransitionComposite = new NextTransitionComposite(this, SWT.NONE);
		nextTransitionComposite.setStatable(offer);
		nextTransitionComposite.setLayoutData(new RowData(260, SWT.DEFAULT));
		nextTransitionComposite.addSignalListener(new SignalListener() {
			@Override
			public void signal(final SignalEvent event)
			{
				signalNextTransition(event);
			}
		});

		if (!offer.isFinalized()) {
			if (expiryTimestampContainerComp == null)
				expiryTimestampContainerComp = new XComposite(this, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.NONE);

			expiryTimestampUnfinalizedComp = new XComposite(expiryTimestampContainerComp, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
			expiryTimestampUnfinalizedComp.setLayoutData(null);
			expiryTimestampUnfinalizedComp.getGridLayout().numColumns = 2;
			new Label(expiryTimestampUnfinalizedComp, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.label.expiry.unfinalized")); //$NON-NLS-1$
			expiryTimestampUnfinalized = new DateTimeControl(expiryTimestampUnfinalizedComp, SWT.NONE, IDateFormatter.FLAGS_DATE_SHORT_TIME_HM);
			new Label(expiryTimestampUnfinalizedComp, SWT.NONE);
			expiryTimestampUnfinalizedAutoManaged = new Button(expiryTimestampUnfinalizedComp, SWT.CHECK);
			expiryTimestampUnfinalizedAutoManaged.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.button.text.manageAutomatically")); //$NON-NLS-1$
			expiryTimestampUnfinalizedAutoManaged.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.button.tooltip.manageAutomatically")); //$NON-NLS-1$

			expiryTimestampUnfinalized.setDate(offer.getExpiryTimestampUnfinalized());
			expiryTimestampUnfinalizedAutoManaged.setSelection(offer.isExpiryTimestampUnfinalizedAutoManaged());

			expiryTimestampUnfinalizedAutoManaged.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					saveExpiryTimestamp();
				}
			});

			expiryTimestampUnfinalized.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					expiryTimestampUnfinalizedAutoManaged.setSelection(false);
					saveExpiryTimestamp();
				}
			});

			expiryTimestampUnfinalized.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent event) {
					expiryTimestampModifiedNeedsSaveOnFocusLost = true;
					expiryTimestampUnfinalizedAutoManaged.setSelection(false);
				}
			});

			expiryTimestampUnfinalized.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(final FocusEvent e) {
					if (expiryTimestampModifiedNeedsSaveOnFocusLost) {
						expiryTimestampModifiedNeedsSaveOnFocusLost = false;
						saveExpiryTimestamp();
					}
				}
			});
		}

		if (!offer.getOfferLocal().isAccepted()) {
			if (expiryTimestampContainerComp == null)
				expiryTimestampContainerComp = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);

			expiryTimestampFinalizedComp = new XComposite(expiryTimestampContainerComp, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.NONE);
			expiryTimestampFinalizedComp.setLayoutData(null);
			expiryTimestampFinalizedComp.getGridLayout().numColumns = 2;
			new Label(expiryTimestampFinalizedComp, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.label.expiry.finalized")); //$NON-NLS-1$
			expiryTimestampFinalized = new DateTimeControl(expiryTimestampFinalizedComp, SWT.NONE, IDateFormatter.FLAGS_DATE_SHORT_TIME_HM);
			new Label(expiryTimestampFinalizedComp, SWT.NONE);
			expiryTimestampFinalizedAutoManaged = new Button(expiryTimestampFinalizedComp, SWT.CHECK);
			expiryTimestampFinalizedAutoManaged.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.button.text.manageAutomatically")); //$NON-NLS-1$
			expiryTimestampFinalizedAutoManaged.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.button.tooltip.manageAutomatically")); //$NON-NLS-1$

			expiryTimestampFinalized.setDate(offer.getExpiryTimestampFinalized());
			expiryTimestampFinalizedAutoManaged.setSelection(offer.isExpiryTimestampFinalizedAutoManaged());

			expiryTimestampFinalizedAutoManaged.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					saveExpiryTimestamp();
				}
			});

			expiryTimestampFinalized.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					expiryTimestampFinalizedAutoManaged.setSelection(false);
					saveExpiryTimestamp();
				}
			});

			expiryTimestampFinalized.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(final ModifyEvent event) {
					expiryTimestampModifiedNeedsSaveOnFocusLost = true;
					expiryTimestampFinalizedAutoManaged.setSelection(false);
				}
			});

			expiryTimestampFinalized.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(final FocusEvent e) {
					if (expiryTimestampModifiedNeedsSaveOnFocusLost) {
						expiryTimestampModifiedNeedsSaveOnFocusLost = false;
						saveExpiryTimestamp();
					}
				}
			});

			if (offer.isFinalized())
				expiryTimestampFinalizedComp.setEnabled(false);
		}

		if (expiryTimestampContainerComp != null)
			expiryTimestampContainerComp.getGridLayout().numColumns = 2;

		createArticleContainerContextMenu();

		JDOLifecycleManager.sharedInstance().addNotificationListener(Offer.class, offerChangedListener);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(final DisposeEvent e)
			{
				JDOLifecycleManager.sharedInstance().removeNotificationListener(Offer.class, offerChangedListener);
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

	private boolean expiryTimestampModifiedNeedsSaveOnFocusLost = false;

	private void saveExpiryTimestamp()
	{
		final Date _expiryTimestampUnfinalized = expiryTimestampUnfinalized == null ? offer.getExpiryTimestampUnfinalized() : expiryTimestampUnfinalized.getDate();
		final boolean _expiryTimestampUnfinalizedAutoManaged = expiryTimestampUnfinalizedAutoManaged == null ? offer.isExpiryTimestampUnfinalizedAutoManaged() : expiryTimestampUnfinalizedAutoManaged.getSelection();
		final Date _expiryTimestampFinalized = expiryTimestampFinalized == null ? offer.getExpiryTimestampFinalized() : expiryTimestampFinalized.getDate();
		final boolean _expiryTimestampFinalizedAutoManaged = expiryTimestampFinalizedAutoManaged == null ? offer.isExpiryTimestampFinalizedAutoManaged() : expiryTimestampFinalizedAutoManaged.getSelection();

		final org.nightlabs.base.ui.job.Job job = new org.nightlabs.base.ui.job.Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.job.name.savingExpiryTimeStamp")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final ProgressMonitor monitor) throws Exception {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.job.name.savingExpiryTimeStamp"), 2); //$NON-NLS-1$
				try {
					final Offer _offer = OfferDAO.sharedInstance().setOfferExpiry(
							getOfferID(),
							_expiryTimestampUnfinalized, _expiryTimestampUnfinalizedAutoManaged,
							_expiryTimestampFinalized, _expiryTimestampFinalizedAutoManaged,
							true,
							ArticleContainerEditComposite.FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 1));

					onOfferModified(_offer, new SubProgressMonitor(monitor, 1));
				} finally {
					monitor.done();
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.INTERACTIVE);
		job.setUser(true);
		job.schedule();
	}

	private void onOfferModified(final Offer offer, final ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.job.task.name.updateUI"), 100); //$NON-NLS-1$
		try {
			this.offer = offer;
			currentStateComposite.setStatable(offer, new SubProgressMonitor(monitor, 1));
			nextTransitionComposite.setStatable(offer, new SubProgressMonitor(monitor, 1));

			getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (expiryTimestampUnfinalizedComp != null && offer.isFinalized()) {
						expiryTimestampUnfinalizedComp.dispose();
						expiryTimestampUnfinalizedComp = null;
						expiryTimestampUnfinalized = null;
						expiryTimestampUnfinalizedAutoManaged = null;
					}

					if (expiryTimestampFinalizedComp != null && (offer.getOfferLocal().isAccepted() || offer.getOfferLocal().isRejected())) {
						expiryTimestampFinalizedComp.dispose();
						expiryTimestampFinalizedComp = null;
						expiryTimestampFinalized = null;
						expiryTimestampFinalizedAutoManaged = null;
					}

					if (expiryTimestampUnfinalizedComp == null && expiryTimestampFinalizedComp == null && expiryTimestampContainerComp != null) {
						expiryTimestampContainerComp.dispose();
						expiryTimestampContainerComp = null;
					}

					if (expiryTimestampUnfinalized != null) {
						expiryTimestampUnfinalized.setDate(offer.getExpiryTimestampUnfinalized());
						expiryTimestampUnfinalizedAutoManaged.setSelection(offer.isExpiryTimestampUnfinalizedAutoManaged());
					}

					if (expiryTimestampFinalized != null) {
						expiryTimestampFinalized.setDate(offer.getExpiryTimestampFinalized());
						expiryTimestampFinalizedAutoManaged.setSelection(offer.isExpiryTimestampFinalizedAutoManaged());

						if (offer.isFinalized())
							expiryTimestampFinalizedComp.setEnabled(false);
					}

					getShell().layout(true, true);
				}
			});
		} finally {
			monitor.done();
		}
	}

	private final NotificationListener offerChangedListener = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.loadOfferJob.name")) { //$NON-NLS-1$
		public void notify(final NotificationEvent notificationEvent)
		{
			final ProgressMonitor monitor = getProgressMonitor();
			monitor.beginTask(
					Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.loadOfferMonitor.task.name"), //$NON-NLS-1$
					3
			);
			final Offer _offer = OfferDAO.sharedInstance().getOffer(
					(OfferID) JDOHelper.getObjectId(offer),
					//					ArticleContainerEditComposite.FETCH_GROUPS_OFFER_WITH_ARTICLES, // it's fine to use these fetch groups here, because we'll get it out of the cache - hence it's even better to load it with more fetch groups than only with the ones we need in this composite
					// After a change, it does NOT load the ArticleContainer *with* articles, but *WITHOUT*!
					// Therefore, it's a bad idea to load them with articles! Marco.
					ArticleContainerEditComposite.FETCH_GROUPS_ARTICLE_CONTAINER_WITHOUT_ARTICLES,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 1));

			onOfferModified(_offer, new SubProgressMonitor(monitor, 2));
			monitor.done();
		}
	};

	private void signalNextTransition(final SignalEvent event)
	{
		final Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.performTransitionJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final ProgressMonitor monitor)
			{
				try {
					final TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
					tm.signalOffer((OfferID)JDOHelper.getObjectId(offer), event.getTransition().getJbpmTransitionName());
				} catch (final Exception x) {
					throw new RuntimeException(x);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.setUser(true);
		job.schedule();
	}

	@Override
	public void refresh() {
		headerVendorCustomerComposite.setArticleContainer(getArticleContainer());
	}
}
