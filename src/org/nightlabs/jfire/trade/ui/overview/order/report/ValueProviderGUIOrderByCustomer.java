/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.order.report;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.reporting.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.query.ArticleContainerQuery;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.OrderDAO;
import org.nightlabs.jfire.trade.ui.overview.order.OrderListComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class ValueProviderGUIOrderByCustomer 
extends AbstractValueProviderGUI 
{
	public static final String[] FETCH_GROUPS_ORDERS = new String[] {
		FetchPlan.DEFAULT,
		Order.FETCH_GROUP_THIS_ORDER,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON
	};
	
	private OrderListComposite orderListComposite = null;
	
	/**
	 * @param valueProviderConfig
	 */
	public ValueProviderGUIOrderByCustomer(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	public Control createGUI(Composite wrapper) {
		orderListComposite = new OrderListComposite(wrapper, SWT.NONE);
		orderListComposite.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent arg0) {
				notifyOutputChanged();
			}
		});
		return orderListComposite;
	}

	public Object getOutputValue() {
		if (orderListComposite.getSelectedElements().size() >= 1)
			return JDOHelper.getObjectId(orderListComposite.getSelectedElements().iterator().next());
		return null;
	}

	public boolean isAcquisitionComplete() {
		return getOutputValue() != null;
	}

	public void setInputParameterValue(String parameterID, final Object value) {
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.overview.order.report.ValueProviderGUIOrderByCustomer.loadOrdersJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				ArticleContainerQuery query = new ArticleContainerQuery(Order.class);
				query.setCustomerID((AnchorID) value);
				Collection<ArticleContainerQuery> qs = new HashSet<ArticleContainerQuery>();
				qs.add(query);
				Set<OrderID> orderIDs = null;
				try {
					orderIDs = TradePlugin.getDefault().getTradeManager().getArticleContainerIDs(qs);
				} catch (RemoteException e) {
					throw new RuntimeException(e);
				}
				final Collection<Order> orders = (Collection<Order>) OrderDAO.
					sharedInstance().getOrders(orderIDs, FETCH_GROUPS_ORDERS, 
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						orderListComposite.setInput(orders);
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadJob.schedule();
	}

}
