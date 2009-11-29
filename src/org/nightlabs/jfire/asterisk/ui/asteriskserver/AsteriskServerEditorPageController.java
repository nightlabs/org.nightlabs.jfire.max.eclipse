package org.nightlabs.jfire.asterisk.ui.asteriskserver;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.asterisk.AsteriskServer;
import org.nightlabs.jfire.asterisk.ui.resource.Messages;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.pbx.dao.PhoneSystemDAO;
import org.nightlabs.jfire.pbx.id.PhoneSystemID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * A controller that loads a asteriskServer
 *
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class AsteriskServerEditorPageController extends ActiveEntityEditorPageController<AsteriskServer>
{

	private static final String[] FETCH_GROUPS_ASTERISK_SERVER = new String[] {
		FetchPlan.DEFAULT,
		AsteriskServer.FETCH_GROUP_NAME,
		AsteriskServer.FETCH_GROUP_CALL_FILE_PROPERTIES}
	;

	private static final long serialVersionUID = -1651161683093714800L;

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AsteriskServerEditorPageController.class);

	/**
	 * The asteriskServer id.
	 */
	private PhoneSystemID asteriskServerID;

	/**
	 * Create an instance of this controller for
	 * an {@link AsteriskServerEditor} and load the data.
	 */
	public AsteriskServerEditorPageController(EntityEditor editor)
	{
		super(editor);
		this.asteriskServerID = (PhoneSystemID) ((JDOObjectEditorInput<?>)editor.getEditorInput()).getJDOObjectID();
	}

	@Override
	protected AsteriskServer retrieveEntity(ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.AsteriskServerEditorPageController.retrieveEntity.monitor.task.name"), 1); //$NON-NLS-1$
		try {
			if(asteriskServerID != null) {
				// load asteriskServer
				AsteriskServer asteriskServer = (AsteriskServer) PhoneSystemDAO.sharedInstance().getPhoneSystem(
						asteriskServerID, getEntityFetchGroups(),
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 1)
				);
				monitor.worked(1);
				return asteriskServer;
			}
			return null;
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			monitor.done();
		}
	}

	@Override
	protected AsteriskServer storeEntity(AsteriskServer controllerObject, ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.asterisk.ui.asteriskserver.AsteriskServerEditorPageController.storeEntity.monitor.task.name"), 5); //$NON-NLS-1$
		try	{
			monitor.worked(1);
			return (AsteriskServer) PhoneSystemDAO.sharedInstance().storePhoneSystem(
					controllerObject, true, getEntityFetchGroups(),
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 5)
			);
		} catch(Exception e) {
			monitor.setCanceled(true);
			throw new RuntimeException(e);
		} finally {
			monitor.done();
		}
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS_ASTERISK_SERVER;
	}
}