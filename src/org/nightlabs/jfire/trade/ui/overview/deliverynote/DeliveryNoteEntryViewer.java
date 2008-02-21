package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;
import org.nightlabs.jfire.trade.ui.overview.ArticleContainerEntryViewer;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.action.EditDeliveryNoteAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DeliveryNoteEntryViewer
extends ArticleContainerEntryViewer
{
	public static final String ID = DeliveryNoteEntryViewer.class.getName();
	public static String[] FETCH_GROUPS_DELIVERY_NOTES = new String[] {
		FetchPlan.DEFAULT,
		DeliveryNote.FETCH_GROUP_THIS_DELIVERY_NOTE,
		DeliveryNote.FETCH_GROUP_DELIVERY_NOTE_LOCAL,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON
	};
	
	public DeliveryNoteEntryViewer(Entry entry) {
		super(entry);
	}

	private AbstractArticleContainerListComposite list;
	
	@Override
	public AbstractTableComposite createListComposite(Composite parent)
	{
		list = new DeliveryNoteListComposite(parent, SWT.NONE);
		return list;
	}

	@Override
	protected void addResultTableListeners(AbstractTableComposite tableComposite) {
		super.addResultTableListeners(tableComposite);
		list.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				EditDeliveryNoteAction editAction = new EditDeliveryNoteAction();
				editAction.setSelection(list.getTableViewer().getSelection());
				editAction.run();
			}
		});
	}
	
	@Override
	public AbstractQueryFilterComposite createFilterComposite(Composite parent) {
		return new DeliveryNoteFilterComposite(parent, SWT.NONE);
	}

	public String getID() {
		return ID;
	}
		
	@Override
	protected Object getQueryResult(Collection<? extends JDOQuery> queries, ProgressMonitor monitor)
	{
		try {
//			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//			Set<DeliveryNoteID> deliveryNoteIDs = tradeManager.getDeliveryNoteIDs(queries);
//			return DeliveryNoteDAO.sharedInstance().getDeliveryNotes(deliveryNoteIDs,
//					FETCH_GROUPS_DELIVERY_NOTES,
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//					monitor);
			return ArticleContainerDAO.sharedInstance().getArticleContainersForQueries(queries,
					FETCH_GROUPS_DELIVERY_NOTES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
