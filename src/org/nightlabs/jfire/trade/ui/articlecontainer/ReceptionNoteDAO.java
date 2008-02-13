package org.nightlabs.jfire.trade.ui.articlecontainer;

import java.util.Collection;
import java.util.Set;

import org.nightlabs.jfire.base.jdo.BaseJDOObjectDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.store.id.ReceptionNoteID;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ReceptionNoteDAO
extends BaseJDOObjectDAO<ReceptionNoteID, ReceptionNote>
{
	private static ReceptionNoteDAO sharedInstance;
	public static ReceptionNoteDAO sharedInstance() {
		if (sharedInstance == null) {
			synchronized (ReceptionNoteDAO.class) {
				if (sharedInstance == null)
					sharedInstance = new ReceptionNoteDAO();
			}
		}
		return sharedInstance;
	}
	protected ReceptionNoteDAO() {
		super();
	}

	@Override
	protected Collection<ReceptionNote> retrieveJDOObjects(Set<ReceptionNoteID> objectIDs, String[] fetchGroups,
			int maxFetchDepth, ProgressMonitor monitor)
	throws Exception
	{
		TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
		return tm.getReceptionNotes(objectIDs, fetchGroups, maxFetchDepth);
	}

	public Collection<ReceptionNote> getReceptionNotes(Set<ReceptionNoteID> objectIDs, String[] fetchGroups,
			int maxFetchDepth, ProgressMonitor monitor)
	throws Exception
	{
		return retrieveJDOObjects(objectIDs, fetchGroups, maxFetchDepth, monitor);
	}
}
