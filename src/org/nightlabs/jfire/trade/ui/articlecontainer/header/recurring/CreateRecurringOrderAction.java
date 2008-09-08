package org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring;


import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeComposite;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.eclipse.swt.widgets.Display;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.progress.ProgressMonitor;


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
					CreateRecurringOrder();

					monitor.worked(85);
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
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

	public void CreateRecurringOrder()
	{
		// create recurring Order and Offer


	}


}
