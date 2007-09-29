package org.nightlabs.jfire.trade.overview.offer;

import java.util.Collection;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.OfferLocal;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.articlecontainer.OfferDAO;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.overview.ArticleContainerEntryViewer;
import org.nightlabs.jfire.trade.overview.offer.action.EditOfferAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class OfferEntryViewer 
extends ArticleContainerEntryViewer
{
	public static final String ID = OfferEntryViewer.class.getName();
	public static final String[] FETCH_GROUPS_OFFERS = new String[] {
		FetchPlan.DEFAULT, 
//		Offer.FETCH_GROUP_ARTICLES,
//		Offer.FETCH_GROUP_CREATE_USER,
//		Offer.FETCH_GROUP_PRICE,
//		Offer.FETCH_GROUP_STATES
		Offer.FETCH_GROUP_THIS_OFFER,
		Offer.FETCH_GROUP_OFFER_LOCAL,
		OfferLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON
	};
	
	public OfferEntryViewer(Entry entry) {
		super(entry);
	}

	@Override
	public AbstractTableComposite createListComposite(Composite parent) {
		final OfferListComposite list = new OfferListComposite(parent, SWT.NONE); 
		list.getTableViewer().addDoubleClickListener(new IDoubleClickListener(){
			public void doubleClick(DoubleClickEvent event) {
				EditOfferAction editAction = new EditOfferAction();
				editAction.setSelection(list.getTableViewer().getSelection());
				editAction.run();
			}
		});
		return list;
	}

	@Override
	public AbstractQueryFilterComposite createFilterComposite(Composite parent) {
		return new OfferFilterComposite(parent, SWT.NONE);
	}

	public String getID() {
		return ID;
	}
		
	@Override
	protected Object getQueryResult(Collection<JDOQuery> queries, ProgressMonitor monitor) 
	{
		try {
			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();		
			Set<OfferID> offerIDs = tradeManager.getOfferIDs(queries);
			return OfferDAO.sharedInstance().getOffers(offerIDs,
					FETCH_GROUPS_OFFERS, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					monitor);
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
