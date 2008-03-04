package org.nightlabs.jfire.trade.ui.overview.receptionnote;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryMap;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.query.ReceptionNoteQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.articlecontainer.ReceptionNoteDAO;
import org.nightlabs.jfire.trade.ui.overview.ArticleContainerEntryViewer;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ReceptionNoteEntryViewer
	extends ArticleContainerEntryViewer<ReceptionNote, ReceptionNoteQuickSearchQuery>
{
	
	public ReceptionNoteEntryViewer(Entry entry) {
		super(entry);
	}

//	@Override
//	public AbstractQueryFilterComposite createFilterComposite(Composite parent) {
//		return new ReceptionNoteFilterComposite(parent, SWT.NONE);
//	}

	public static final String ID = ReceptionNoteEntryViewer.class.getName();
	public String getID() {
		return ID;
	}

	// TODO: when edit action is available add doubleClickListener
	@Override
	public AbstractTableComposite<ReceptionNote> createListComposite(Composite parent) {
		return new ReceptionNoteListComposite(parent, SWT.NONE);
	}
	
	public static final String[] FETCH_GROUPS_RECEPTION_NOTES = new String[] {
		FetchPlan.DEFAULT,
		ReceptionNote.FETCH_GROUP_THIS_RECEPTION_NOTE
	};
	
//	@Override
//	protected Object getQueryResult(Collection<? extends AbstractJDOQuery> queries, ProgressMonitor monitor)
//	{
//		try {
//			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//			Set<ReceptionNoteID> receptionNoteIDs = tradeManager.getOrderIDs(queries);
//			return ReceptionNoteDAO.sharedInstance().getReceptionNotes(receptionNoteIDs,
//					FETCH_GROUPS_RECEPTION_NOTES,
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//					monitor);
//		}
//		catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}

	@Override
	protected Collection<ReceptionNote> doSearch(
		QueryMap<ReceptionNote, ? extends ReceptionNoteQuickSearchQuery> queryMap, ProgressMonitor monitor)
	{
		return ReceptionNoteDAO.sharedInstance().getReceptionNotesByQueries(
			queryMap,
			FETCH_GROUPS_RECEPTION_NOTES,
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
			monitor);
	}

	@Override
	protected Class<ReceptionNote> getResultType()
	{
		return ReceptionNote.class;
	}
}
