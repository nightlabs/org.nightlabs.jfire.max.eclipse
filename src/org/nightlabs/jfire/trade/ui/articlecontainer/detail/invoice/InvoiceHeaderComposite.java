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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice;

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
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.accounting.jbpm.JbpmConstantsInvoice;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalEvent;
import org.nightlabs.jfire.jbpm.ui.transition.next.SignalListener;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.InvoiceDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.HeaderComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.wizard.AbstractCombiTransferWizard;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class InvoiceHeaderComposite
extends HeaderComposite
{
	private Invoice invoice;

	private CurrentStateComposite currentStateComposite;
	private NextTransitionComposite nextTransitionComposite;

	public InvoiceHeaderComposite(ArticleContainerEditorComposite articleContainerEditorComposite, Invoice _invoice)
	{
		super(articleContainerEditorComposite, articleContainerEditorComposite, _invoice);
		this.invoice = _invoice;
		getGridLayout().numColumns = 2;

		currentStateComposite = new CurrentStateComposite(this, SWT.NONE);
		currentStateComposite.setStatable(invoice);

		nextTransitionComposite = new NextTransitionComposite(this, SWT.NONE);
		nextTransitionComposite.setStatable(invoice);
		nextTransitionComposite.addSignalListener(new SignalListener() {
			@Implement
			public void signal(SignalEvent event)
			{
				signalNextTransition(event);
			}
		});

		createArticleContainerContextMenu();

		JDOLifecycleManager.sharedInstance().addNotificationListener(Invoice.class, invoiceChangedListener);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				JDOLifecycleManager.sharedInstance().removeNotificationListener(Invoice.class, invoiceChangedListener);
			}
		});

//		new Label(this, SWT.NONE).setText("Invoice " + invoice.getInvoiceID());
//		setHeaderText("Invoice " + invoice.getInvoiceID());
	}

	private NotificationListener invoiceChangedListener = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoiceHeaderComposite.loadInvoicesJob.name")) { //$NON-NLS-1$
		public void notify(NotificationEvent notificationEvent)
		{
			ProgressMonitor monitor = getProgressMonitorWrapper();
			monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoiceHeaderComposite.loadInvoicesMonitor.task.name"), 3); //$NON-NLS-1$
			invoice = InvoiceDAO.sharedInstance().getInvoice(
					(InvoiceID) JDOHelper.getObjectId(invoice),
					ArticleContainerEditorComposite.FETCH_GROUPS_INVOICE_WITH_ARTICLES, // it's fine to use these fetch groups here, because we'll get it out of the cache - hence it's even better to load it with more fetch groups than only with the ones we need in this composite
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 1));
			currentStateComposite.setStatable(invoice, new SubProgressMonitor(monitor, 1));
			nextTransitionComposite.setStatable(invoice, new SubProgressMonitor(monitor, 1));
			monitor.done();
		}
	};

	private void signalNextTransition(final SignalEvent event)
	{
		// Filter the payment signal, since it must not be signalled directly, but only internally once the payment is complete.
		// Instead, we open the payment-wizard...
		if (JbpmConstantsInvoice.Both.TRANSITION_NAME_PAY.equals(event.getTransition().getJbpmTransitionName())) {
			((NextTransitionComposite)event.getSource()).setEnabled(true);
			ArticleContainerID articleContainerID = (ArticleContainerID) JDOHelper.getObjectId(invoice);
			CombiTransferArticleContainerWizard wizard = new CombiTransferArticleContainerWizard(
					articleContainerID,
					AbstractCombiTransferWizard.TRANSFER_MODE_PAYMENT);

			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
			dialog.open();
			return;
		}

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice.InvoiceHeaderComposite.performTransitionJob.name")) { //$NON-NLS-1$
			@Override
			@Implement
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					AccountingManager am = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					am.signalInvoice((InvoiceID)JDOHelper.getObjectId(invoice), event.getTransition().getJbpmTransitionName());
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
