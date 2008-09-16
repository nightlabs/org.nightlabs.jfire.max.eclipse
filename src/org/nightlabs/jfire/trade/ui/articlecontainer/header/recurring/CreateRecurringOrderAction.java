package org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring;


import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.config.TradeConfigModule;
import org.nightlabs.jfire.trade.recurring.RecurringOffer;
import org.nightlabs.jfire.trade.recurring.RecurringOrder;
import org.nightlabs.jfire.trade.recurring.RecurringTradeManager;
import org.nightlabs.jfire.trade.recurring.RecurringTradeManagerUtil;
import org.nightlabs.jfire.trade.recurring.dao.RecurringOfferDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring.ArticleContainerEditorInputRecurringOffer;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeComposite;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.eclipse.swt.widgets.Display;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.SegmentTypeID;
import org.nightlabs.jfire.trade.id.OrderID;

public class CreateRecurringOrderAction extends Action {

	private HeaderTreeComposite headerTreeComposite;

	// TODO should not be static and should be obtained by our new SharedImages registry
	private static final ImageDescriptor IMAGE_DESCRIPTOR = AbstractUIPlugin.imageDescriptorFromPlugin(TradePlugin.ID_PLUGIN, "icons/articleContainer/createOrder16.gif"); //$NON-NLS-1$

	public CreateRecurringOrderAction(HeaderTreeComposite headerTreeComposite)
	{
		super("Create Recurring Order", IMAGE_DESCRIPTOR); //$NON-NLS-1$
		//System.setProperty(JDOLifecycleManager.PROPERTY_KEY_JDO_LIFECYCLE_MANAGER, JDOLifecycleManager.class.getName());
		this.headerTreeComposite = headerTreeComposite;
	}


	@Override
	public void run()
	{ 
		Job createOrderJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.CreateOrderAction.job.creatingOrder")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.header.CreateOrderAction.task.creatingOrder"), 100); //$NON-NLS-1$
				try {

					RecurringTradeManager rtm = RecurringTradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

					TradeConfigModule tradeConfigModule = ConfigUtil.getUserCfMod(
							TradeConfigModule.class,
							new String[] {
								FetchPlan.DEFAULT,
								TradeConfigModule.FETCH_GROUP_CURRENCY,
							},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new SubProgressMonitor(monitor, 30));


					AnchorID customerID = (AnchorID) JDOHelper.getObjectId(headerTreeComposite.getPartner());


					RecurringOrder recurringOrder = rtm.createSaleRecurringOrder(customerID, null,
							tradeConfigModule.getCurrencyID(), new SegmentTypeID[] { null }, 
							new String[] { RecurringOrder.FETCH_GROUP_THIS_ORDER }, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

					OrderID orderID = (OrderID) JDOHelper.getObjectId(recurringOrder);

					final RecurringOffer recurringOffer = rtm.createRecurringOffer(
							orderID, null, 
							new String[] {FetchPlan.DEFAULT, RecurringOffer.FETCH_GROUP_RECURRING_OFFER_CONFIGURATION}, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

					recurringOffer.getRecurringOfferConfiguration().setCreateInvoice(true);

					RecurringOfferDAO.sharedInstance().storeRecurringOfferConfiguration(
							recurringOffer.getRecurringOfferConfiguration(), false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

					monitor.worked(85);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {			
							HeaderTreeComposite.openEditor(new ArticleContainerEditorInputRecurringOffer((OfferID)JDOHelper.getObjectId(recurringOffer)));
						}
					});
				} catch (Exception x) {
					throw new RuntimeException(x);
				} finally {
					monitor.done();
				}

				return Status.OK_STATUS;
			}
		};
		createOrderJob.schedule();
	}



}
