package org.nightlabs.jfire.trade.ui.overview.offer;

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
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.articlecontainer.OfferDAO;
import org.nightlabs.jfire.trade.ui.overview.ArticleContainerEntryViewer;
import org.nightlabs.jfire.trade.ui.overview.offer.action.EditOfferAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class OfferEntryViewer
	extends ArticleContainerEntryViewer<Offer, OfferQuery>
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
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON
	};
	
	public OfferEntryViewer(Entry entry) {
		super(entry);
	}

	private OfferListComposite list;
	
	@Override
	public AbstractTableComposite<Offer> createListComposite(Composite parent) {
		list = new OfferListComposite(parent, SWT.NONE);
		return list;
	}

	@Override
	protected void addResultTableListeners(AbstractTableComposite<Offer> tableComposite) {
		super.addResultTableListeners(tableComposite);
		list.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				EditOfferAction editAction = new EditOfferAction();
				editAction.setSelection(list.getSelection());
				editAction.run();
			}
		});
	}
	
	public String getID() {
		return ID;
	}
		
	@Override
	protected Collection<Offer> doSearch(
		QueryCollection<Offer, ? extends OfferQuery> queryMap, ProgressMonitor monitor)
	{
		return OfferDAO.sharedInstance().getOffersByQuery(
			queryMap,
			FETCH_GROUPS_OFFERS,
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
			monitor);
	}

	@Override
	protected Class<Offer> getResultType()
	{
		return Offer.class;
	}

}
