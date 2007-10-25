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

package org.nightlabs.jfire.trade.ui.articlecontainer.header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.articlecontainer.OfferDAO;
import org.nightlabs.jfire.trade.ui.articlecontainer.OrderDAO;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class OrderTreeNode extends HeaderTreeNode
{
	public static final String[] FETCH_GROUPS_ORDER = new String[] {
		FetchPlan.DEFAULT,
		Order.FETCH_GROUP_OFFERS
	};
	public static final String[] FETCH_GROUPS_OFFER = new String[] {
		FetchPlan.DEFAULT,
		Offer.FETCH_GROUP_ORDER
//		Offer.FETCH_GROUP_CUSTOMER_ID,
//		Offer.FETCH_GROUP_VENDOR_ID
	};

	private Order order;

	@Override
	public void clear()
	{
		super.clear();
		synchronized (offerIDsLoaded) {
			offerIDsLoaded.clear();
		}
	}

	/**
	 * @param parent
	 */
	public OrderTreeNode(HeaderTreeNode parent, byte position, Order order)
	{
		super(parent, position);
		this.order = order;
		init();
	}

	@Override
	public Image getColumnImage(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return getHeaderTreeComposite().imageOrderTreeNode;
			default:
				return null;
		}
	}

	@Override
	@Implement
	public String getColumnText(int columnIndex)
	{
		switch (columnIndex) {
			case 0: return order.getOrderIDPrefix() + '/' + ObjectIDUtil.longObjectIDFieldToString(order.getOrderID());
			default:
				return null;
		}
	}

	@Override
	@Implement
	protected List loadChildData(ProgressMonitor monitor)
	{
		try {
//			TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();

			OrderID orderID = (OrderID) JDOHelper.getObjectId(order);
			Order o = OrderDAO.sharedInstance().getOrder(orderID, FETCH_GROUPS_ORDER, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//			Order o = tm.getOrder(orderID, FETCH_GROUPS_ORDER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			ArrayList<Offer> res = new ArrayList<Offer>(o.getOffers());

			Collections.sort(res, new Comparator<Offer>() {
				public int compare(Offer o0, Offer o1)
				{
					long id0 = o0.getOfferID();
					long id1 = o1.getOfferID();

					if (id0 == id1)
						return 0;

					if (id0 > id1)
						return 1;
					else
						return -1;
				}
			});

			return res;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@Implement
	protected List<HeaderTreeNode> createChildNodes(List childData)
	{
		ArrayList<HeaderTreeNode> res = new ArrayList<HeaderTreeNode>();
		for (Iterator it = childData.iterator(); it.hasNext(); ) {
			Offer offer = (Offer) it.next();
			OfferID offerID = (OfferID) JDOHelper.getObjectId(offer);
			synchronized (offerIDsLoaded) {
				if (!offerIDsLoaded.contains(offerID)) {
					offerIDsLoaded.add(offerID);
					res.add(new OfferTreeNode(this, POSITION_LAST_CHILD, offer));
				}
			}
		}
		return res;
	}

	private Set<OfferID> offerIDsLoaded = new HashSet<OfferID>();

	/**
	 * @return Returns the order.
	 */
	public Order getOrder()
	{
		return order;
	}

	@Override
	public Collection<DirtyObjectID> onNewElementsCreated(Collection<DirtyObjectID> dirtyObjectIDs, ProgressMonitor monitor)
	{
		if (children != null) {
			Map<Object, DirtyObjectID> objectID2DirtyObjectIDMap = new HashMap<Object, DirtyObjectID>(dirtyObjectIDs.size());
	
			Set<OfferID> offerIDsToLoad = new HashSet<OfferID>();
			for (Iterator<DirtyObjectID> itD = dirtyObjectIDs.iterator(); itD.hasNext(); ) {
				DirtyObjectID dirtyObjectID = itD.next();
				objectID2DirtyObjectIDMap.put(dirtyObjectID.getObjectID(), dirtyObjectID);
				if (dirtyObjectID.getObjectID() instanceof OfferID) {
					OfferID offerID = (OfferID) dirtyObjectID.getObjectID();
					itD.remove();
					synchronized (offerIDsLoaded) {
						if (!offerIDsLoaded.contains(offerID))
							offerIDsToLoad.add(offerID);
					}
				}
			}
	
			if (!offerIDsToLoad.isEmpty()) {
				final List<Offer> offers = new OfferDAO().getOffers(offerIDsToLoad, FETCH_GROUPS_OFFER, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	
				OrderID orderID = (OrderID) JDOHelper.getObjectId(getOrder());
	
				for (Iterator<Offer> it = offers.iterator(); it.hasNext(); ) {
					Offer offer = it.next();
					if (!orderID.equals(JDOHelper.getObjectId(offer.getOrder()))) {
						it.remove();
						dirtyObjectIDs.add(objectID2DirtyObjectIDMap.get(JDOHelper.getObjectId(offer)));
					}
				}
	
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						if (children == null)
							return;
	
						for (Offer offer : offers) {
							OfferID offerID = (OfferID) JDOHelper.getObjectId(offer);
	
							synchronized (offerIDsLoaded) {
								if (!offerIDsLoaded.contains(offerID)) {
									offerIDsLoaded.add(offerID);
									new OfferTreeNode(OrderTreeNode.this, POSITION_FIRST_CHILD, offer);
								}
							}
						}
					}
				});
			}
		} // if (children != null) {
		return super.onNewElementsCreated(dirtyObjectIDs, monitor);
	}
}
