package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class IssueEntryViewer  
extends ArticleContainerEntryViewer
{
	public static final String ID = IssueEntryViewer.class.getName();
//	public static String[] FETCH_GROUPS_DELIVERY_NOTES = new String[] {
//	FetchPlan.DEFAULT,
//	DeliveryNote.FETCH_GROUP_THIS_DELIVERY_NOTE,
//	DeliveryNote.FETCH_GROUP_DELIVERY_NOTE_LOCAL,
//	DeliveryNoteLocal.FETCH_GROUP_STATE,
//	State.FETCH_GROUP_STATE_DEFINITION,
//	StateDefinition.FETCH_GROUP_NAME,
//	LegalEntity.FETCH_GROUP_PERSON
//	};

	public IssueEntryViewer(Entry entry) {
		super(entry);
	}

	@Override
	public AbstractTableComposite createListComposite(Composite parent)
	{
//		final AbstractArticleContainerListComposite list = new DeliveryNoteListComposite(parent, SWT.NONE);
//		list.getTableViewer().addDoubleClickListener(new IDoubleClickListener(){
//		public void doubleClick(DoubleClickEvent event) {
//		EditDeliveryNoteAction editAction = new EditDeliveryNoteAction();
//		editAction.setSelection(list.getTableViewer().getSelection());
//		editAction.run();
//		}
//		});
		return null;
	}

	@Override
	public AbstractQueryFilterComposite createFilterComposite(Composite parent) {
		return null;//new DeliveryNoteFilterComposite(parent, SWT.NONE);
	}

	public String getID() {
		return ID;
	}	

	@Override
	protected Object getQueryResult(Collection<JDOQuery> queries, ProgressMonitor monitor) 
	{
//		try {
//		TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//		Set<DeliveryNoteID> deliveryNoteIDs = tradeManager.getDeliveryNoteIDs(queries);			
//		return DeliveryNoteDAO.sharedInstance().getDeliveryNotes(deliveryNoteIDs,
//		FETCH_GROUPS_DELIVERY_NOTES, 
//		NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
//		monitor);
//		return ArticleContainerDAO.sharedInstance().getArticleContainersForQueries(queries, 
//		FETCH_GROUPS_DELIVERY_NOTES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//		} catch (Exception e) {
//		throw new RuntimeException(e);
//		}
//		}
		return null;
	}
}
