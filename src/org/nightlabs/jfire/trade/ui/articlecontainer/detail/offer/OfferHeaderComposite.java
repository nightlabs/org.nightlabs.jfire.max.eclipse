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

import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.jbpm.state.CurrentStateComposite;
import org.nightlabs.jfire.jbpm.transition.next.NextTransitionComposite;
import org.nightlabs.jfire.jbpm.transition.next.SignalEvent;
import org.nightlabs.jfire.jbpm.transition.next.SignalListener;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.articlecontainer.OfferDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditorComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.HeaderComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
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
	private Offer offer;
//	private Button finalizedCheckBox;
//	private Button acceptedCheckBox;
//	private Button rejectedCheckBox;
//	private Button confirmedCheckBox;

	private CurrentStateComposite currentStateComposite;
	private NextTransitionComposite nextTransitionComposite;

	protected OfferID getOfferID()
	{
		return (OfferID) JDOHelper.getObjectId(offer);
	}

	public OfferHeaderComposite(GeneralEditorComposite generalEditorComposite, Offer offer)
	{
		super(generalEditorComposite, generalEditorComposite, offer);
		this.offer = offer;

		this.getGridLayout().numColumns = 2;

		currentStateComposite = new CurrentStateComposite(this, SWT.NONE);
		currentStateComposite.setStatable(offer);

		nextTransitionComposite = new NextTransitionComposite(this, SWT.NONE);
		nextTransitionComposite.setStatable(offer);
		nextTransitionComposite.addSignalListener(new SignalListener() {
			@Implement
			public void signal(SignalEvent event)
			{
				signalNextTransition(event);
			}
		});

//		XComposite c1 = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//		c1.getGridLayout().numColumns = 4;
//		finalizedCheckBox = new Button(c1, SWT.CHECK);
//		finalizedCheckBox.setText("finalized");
//		finalizedCheckBox.setToolTipText("An offer must be finalized before further action. After it is finalized, it cannot be changed anymore.");
//		finalizedCheckBox.setSelection(offer.isFinalized());
//		finalizedCheckBox.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				finalizedCheckBoxSelectionChanged();
//			}
//		});
//
//		acceptedCheckBox = new Button(c1, SWT.CHECK);
//		acceptedCheckBox.setText("accepted");
//		acceptedCheckBox.setToolTipText("Did the customer accept the offer?");
//		acceptedCheckBox.setSelection(offer.getOfferLocal().isAccepted());
//		acceptedCheckBox.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				acceptedCheckBoxSelectionChanged();
//			}
//		});
//
//		rejectedCheckBox = new Button(c1, SWT.CHECK);
//		rejectedCheckBox.setText("rejected");
//		rejectedCheckBox.setToolTipText("Did the customer reject the offer?");
//		rejectedCheckBox.setSelection(offer.getOfferLocal().isRejected());
//		rejectedCheckBox.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				rejectedCheckBoxSelectionChanged();
//			}
//		});
//
//		confirmedCheckBox = new Button(c1, SWT.CHECK);
//		confirmedCheckBox.setText("confirmed");
//		confirmedCheckBox.setToolTipText("Have we already sent a confirmation to the customer?");
//		confirmedCheckBox.setSelection(offer.getOfferLocal().isConfirmed());
//		confirmedCheckBox.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				confirmedCheckBoxSelectionChanged();
//			}
//		});

//		setStatusCheckBoxesEnabled();
		createArticleContainerContextMenu();

		JDOLifecycleManager.sharedInstance().addNotificationListener(Offer.class, offerChangedListener);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				JDOLifecycleManager.sharedInstance().removeNotificationListener(Offer.class, offerChangedListener);
			}
		});
	}

	private NotificationListener offerChangedListener = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.loadOfferJob.name")) { //$NON-NLS-1$
		public void notify(NotificationEvent notificationEvent)
		{
			ProgressMonitor monitor = getProgressMonitorWrapper();
			monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.loadOfferMonitor.task.name"), 3); //$NON-NLS-1$
			offer = OfferDAO.sharedInstance().getOffer(
					(OfferID) JDOHelper.getObjectId(offer),
					GeneralEditorComposite.FETCH_GROUPS_OFFER, // it's fine to use these fetch groups here, because we'll get it out of the cache - hence it's even better to load it with more fetch groups than only with the ones we need in this composite
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 1));
			currentStateComposite.setStatable(offer, new SubProgressMonitor(monitor, 1));
			nextTransitionComposite.setStatable(offer, new SubProgressMonitor(monitor, 1));
			monitor.done();
		}
	};

	private void signalNextTransition(final SignalEvent event)
	{
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferHeaderComposite.performTransitionJob.name")) { //$NON-NLS-1$
			@Implement
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					tm.signalOffer((OfferID)JDOHelper.getObjectId(offer), event.getTransition().getJbpmTransitionName());
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

//	private void setStatusCheckBoxesEnabled()
//	{
//		if (offer.getOfferLocal().isRejected())
//			acceptedCheckBox.setEnabled(false);
//
//		if (offer.getOfferLocal().isAccepted())
//			rejectedCheckBox.setEnabled(false);
//	}
//
//	private void finalizedCheckBoxSelectionChanged() {
//		try {
//			if (offer.isFinalized()) {
//				MessageDialog.openError(Display.getDefault().getActiveShell(), "Cannot undo finalization!", "This offer is finalized. A finalization cannot be taken back.");
//				finalizedCheckBox.setSelection(true);
//			}
//			else {
//				TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//				tradeManager.finalizeOffer(getOfferID(), false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//			}
//		} catch (Exception x) {
//			throw new RuntimeException(x);
//		}
//
//		setStatusCheckBoxesEnabled();
//	}
//
//	private void acceptedCheckBoxSelectionChanged() {
//		try {
//			if (offer.getOfferLocal().isAccepted()) {
//				MessageDialog.openError(Display.getDefault().getActiveShell(), "Cannot undo acceptance!", "This offer is accepted. An acceptance cannot be taken back.");
//				acceptedCheckBox.setSelection(true);
//			}
//			else {
//				TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//				tradeManager.acceptOffer(getOfferID(), false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//			}
//		} catch (Exception x) {
//			throw new RuntimeException(x);
//		}
//
//		setStatusCheckBoxesEnabled();
//	}
//
//	private void rejectedCheckBoxSelectionChanged() {
//		try {
//			if (offer.getOfferLocal().isRejected()) {
//				MessageDialog.openError(Display.getDefault().getActiveShell(), "Cannot undo rejection!", "This offer is rejected. A rejection cannot be taken back.");
//				rejectedCheckBox.setSelection(true);
//			}
//			else {
//				TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//				tradeManager.rejectOffer(getOfferID(), false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//			}
//		} catch (Exception x) {
//			throw new RuntimeException(x);
//		}
//
//		setStatusCheckBoxesEnabled();
//	}
//
//	private void confirmedCheckBoxSelectionChanged() {
//		try {
//			if (offer.getOfferLocal().isConfirmed()) {
//				MessageDialog.openError(Display.getDefault().getActiveShell(), "Cannot undo confirmation!", "This offer is confirmed. A confirmation cannot be taken back.");
//				confirmedCheckBox.setSelection(true);
//			}
//			else {
//				TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//				tradeManager.confirmOffer(getOfferID(), false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//			}
//		} catch (Exception x) {
//			throw new RuntimeException(x);
//		}
//
//		setStatusCheckBoxesEnabled();
//	}

}
