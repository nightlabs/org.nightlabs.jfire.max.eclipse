package org.nightlabs.jfire.trade.ui.overview.order;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.query.OrderQuery;
import org.nightlabs.jfire.trade.ui.articlecontainer.OrderDAO;
import org.nightlabs.jfire.trade.ui.overview.ArticleContainerEntryViewer;
import org.nightlabs.jfire.trade.ui.overview.order.action.EditOrderAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OrderEntryViewer
	extends ArticleContainerEntryViewer<Order, OrderQuery>
{
	public static final String ID = OrderEntryViewer.class.getName();
	public static final String[] FETCH_GROUPS_ORDERS = new String[] {
		FetchPlan.DEFAULT,
		Order.FETCH_GROUP_THIS_ORDER,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON
	};
	
	public OrderEntryViewer(Entry entry) {
		super(entry);
	}

	private OrderListComposite list;
	
	@Override
	public AbstractTableComposite<Order> createListComposite(Composite parent) {
		list = new OrderListComposite(parent, SWT.NONE);
		return list;
	}
	
	@Override
	protected void addResultTableListeners(AbstractTableComposite<Order> tableComposite) {
		super.addResultTableListeners(tableComposite);
		list.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				EditOrderAction editAction = new EditOrderAction();
				editAction.setSelection(list.getSelection());
				editAction.run();
			}
		});
	}
	
	public String getID() {
		return ID;
	}
		
	@Override
	protected Collection<Order> doSearch(
		QueryCollection<Order, ? extends OrderQuery> queryMap, ProgressMonitor monitor)
	{
		return OrderDAO.sharedInstance().getOrdersByQueries(
			queryMap,
			FETCH_GROUPS_ORDERS,
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
			monitor);
	}

	@Override
	protected Class<Order> getResultType()
	{
		return Order.class;
	}

}
