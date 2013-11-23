package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.base.ui.entity.tree.ActiveJDOEntityTreeCategory;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.pbx.dao.PhoneSystemDAO;
import org.nightlabs.jfire.pbx.id.PhoneSystemID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class EntityTreeCategoryAsteriskServer
extends ActiveJDOEntityTreeCategory<PhoneSystemID, AsteriskServer> {

	public static String[] FETCH_GROUPS_ASTERISK_SERVER = new String[] {
		FetchPlan.DEFAULT, AsteriskServer.FETCH_GROUP_NAME
	};

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.tree.ActiveJDOEntityTreeCategory#getJDOObjectClass()
	 */
	@Override
	protected Class<AsteriskServer> getJDOObjectClass() {
		return AsteriskServer.class;
	}
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.tree.ActiveJDOEntityTreeCategory#retrieveJDOObjects(java.util.Set, org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	protected Collection<AsteriskServer> retrieveJDOObjects(Set<PhoneSystemID> objectIDs, ProgressMonitor monitor) {
		List<PhoneSystem> phoneSystems = PhoneSystemDAO.sharedInstance().getPhoneSystems(
				objectIDs,
				FETCH_GROUPS_ASTERISK_SERVER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
		);
		Collection<AsteriskServer> result = new ArrayList<AsteriskServer>(phoneSystems.size());
		for (PhoneSystem phoneSystem : phoneSystems) {
			if (phoneSystem instanceof AsteriskServer)
				result.add((AsteriskServer) phoneSystem);
		}
		return result;
	}

	@Override
	protected Collection<AsteriskServer> retrieveJDOObjects(ProgressMonitor monitor) {
		Collection<AsteriskServer> result = PhoneSystemDAO.sharedInstance().getPhoneSystems(AsteriskServer.class, true,
				FETCH_GROUPS_ASTERISK_SERVER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor
		);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.entity.tree.ActiveJDOEntityTreeCategory#sortJDOObjects(java.util.List)
	 */
	@Override
	protected void sortJDOObjects(List<AsteriskServer> objects) {
	}
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.tree.IEntityTreeCategory#createEditorInput(java.lang.Object)
	 */
	@Override
	public IEditorInput createEditorInput(Object o) {
		AsteriskServer asteriskServer = (AsteriskServer)o;
		PhoneSystemID asteriskServerID = PhoneSystemID.create(asteriskServer.getOrganisationID(), asteriskServer.getPhoneSystemID());
		return new AsteriskServerEditorInput(asteriskServerID);
	}
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.entity.tree.IEntityTreeCategory#createLabelProvider()
	 */
	@Override
	public ITableLabelProvider createLabelProvider() {
		return new TableLabelProvider() {
			@Override
			public String getColumnText(Object element, int columnIndex) {
				switch (columnIndex) {
					case 0:
						if (element instanceof AsteriskServer)
							return ((AsteriskServer) element).getName().getText();
						else
							return String.valueOf(element);

					default:
							return ""; //$NON-NLS-1$
				}
			}
			@Override
			public String getText(Object element) {
				if (element instanceof AsteriskServer)
					return ((AsteriskServer) element).getName().getText();
				else
					return String.valueOf(element);
			}
		};
	}
}
