package org.nightlabs.jfire.trade.overview.receptionnote;

import java.util.Collection;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.store.id.ReceptionNoteID;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.articlecontainer.ReceptionNoteDAO;
import org.nightlabs.jfire.trade.overview.ArticleContainerEntryViewer;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ReceptionNoteEntryViewer 
extends ArticleContainerEntryViewer 
{
	
	public ReceptionNoteEntryViewer(Entry entry) {
		super(entry);
	}

	@Override
	public AbstractQueryFilterComposite createFilterComposite(Composite parent) {
		return new ReceptionNoteFilterComposite(parent, SWT.NONE);
	}

	public static final String ID = ReceptionNoteEntryViewer.class.getName();
	public String getID() {
		return ID;
	}

	// TODO: when edit action is available add doubleClickListener
	@Override
	public AbstractTableComposite createListComposite(Composite parent) {
		return new ReceptionNoteListComposite(parent, SWT.NONE);
	}	
	
	public static final String[] FETCH_GROUPS_RECEPTION_NOTES = new String[] {
		FetchPlan.DEFAULT,
		ReceptionNote.FETCH_GROUP_THIS_RECEPTION_NOTE
	};
	
	@Override
	protected Object getQueryResult(Collection<JDOQuery> queries, ProgressMonitor monitor) 
	{
		try {
			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();		
			Set<ReceptionNoteID> receptionNoteIDs = tradeManager.getOrderIDs(queries);
			return ReceptionNoteDAO.sharedInstance().getReceptionNotes(receptionNoteIDs,
					FETCH_GROUPS_RECEPTION_NOTES, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
					monitor);
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
