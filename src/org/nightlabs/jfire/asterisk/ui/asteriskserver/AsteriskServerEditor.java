package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.base.ui.login.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.pbx.dao.PhoneSystemDAO;
import org.nightlabs.jfire.pbx.id.PhoneSystemID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class AsteriskServerEditor
extends ActiveEntityEditor
implements ICloseOnLogoutEditorPart
{
	public static final String EDITOR_ID = AsteriskServerEditor.class.getName();

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		AsteriskServer.FETCH_GROUP_NAME
	};

	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		return entity instanceof AsteriskServer ? ((AsteriskServer)entity).getName().getText() : null;
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		PhoneSystemID asteriskServerID = ((AsteriskServerEditorInput)getEditorInput()).getJDOObjectID();
		assert asteriskServerID != null;
		return PhoneSystemDAO.sharedInstance().getPhoneSystem(asteriskServerID, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	@Override
	public void dispose() {
		super.dispose();
	}
}