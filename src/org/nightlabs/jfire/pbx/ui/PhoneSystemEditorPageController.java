package org.nightlabs.jfire.pbx.ui;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.pbx.PhoneSystem;
import org.nightlabs.jfire.pbx.dao.PhoneSystemDAO;
import org.nightlabs.jfire.pbx.id.PhoneSystemID;
import org.nightlabs.jfire.prop.StructField;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class PhoneSystemEditorPageController extends ActiveEntityEditorPageController<PhoneSystem>
{

	private static final String[] FETCH_GROUPS_PHONE_SYSTEM = new String[] {
		FetchPlan.DEFAULT,
		StructField.FETCH_GROUP_NAME,
		PhoneSystem.FETCH_GROUP_NAME,
		PhoneSystem.FETCH_GROUP_CALLABLE_STRUCT_FIELDS
	};

	private static final long serialVersionUID = -1651161683093714800L;

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(PhoneSystemEditorPageController.class);

	/**
	 * The asteriskServer id.
	 */
	private PhoneSystemID phoneSystemID;

	/**
	 * Create an instance of this controller for
	 * an {@link AsteriskServerEditor} and load the data.
	 */
	public PhoneSystemEditorPageController(EntityEditor editor)
	{
		super(editor);
		this.phoneSystemID = (PhoneSystemID) ((JDOObjectEditorInput<?>)editor.getEditorInput()).getJDOObjectID();
	}

	@Override
	protected PhoneSystem retrieveEntity(ProgressMonitor monitor) {
		monitor.beginTask("Begin", 1);
		try {
			if(phoneSystemID != null) {
				PhoneSystem phoneSystem = (PhoneSystem) PhoneSystemDAO.sharedInstance().getPhoneSystem(
						phoneSystemID, getEntityFetchGroups(),
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 1)
				);
				monitor.worked(1);
				return phoneSystem;
			}
			return null;
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			monitor.done();
		}
	}

	@Override
	protected PhoneSystem storeEntity(PhoneSystem controllerObject, ProgressMonitor monitor) {
		monitor.beginTask("Begin", 5);
		try	{
			monitor.worked(1);
			return (PhoneSystem) PhoneSystemDAO.sharedInstance().storePhoneSystem(
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
		return FETCH_GROUPS_PHONE_SYSTEM;
	}
}