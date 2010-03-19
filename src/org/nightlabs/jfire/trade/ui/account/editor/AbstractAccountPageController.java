package org.nightlabs.jfire.trade.ui.account.editor;

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Abstract base class for creating account based Page Controllers
 *
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 */
public abstract class AbstractAccountPageController
extends ActiveEntityEditorPageController<Account>
{
	/**
	 * The user id.
	 */
	protected AnchorID anchorID;

	/**
	 * The editor model
	 */
	protected Account account;

	/**
	 * Create an instance of this controller for
	 * an {@link UserEditor} and load the data.
	 */
	public AbstractAccountPageController(EntityEditor editor)
	{
		super(editor);
		this.anchorID = (AnchorID)((JDOObjectEditorInput)editor.getEditorInput()).getJDOObjectID();
	}

	/**
	 * Get the anchorID.
	 * @return the anchorID
	 */
	public AnchorID getAnchorID() {
		return anchorID;
	}

	/**
	 * Returns the user associated with this controller
	 */
	public Account getAccount() {
//		return account;
		return getControllerObject();
	}

	@Override
	protected Account retrieveEntity(ProgressMonitor monitor) {
		return AccountDAO.sharedInstance().getAccount(
				anchorID, getEntityFetchGroups(),
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}

	@Override
	protected Account storeEntity(Account account, ProgressMonitor monitor) {
		return AccountDAO.sharedInstance().storeAccount(
				account, true, getEntityFetchGroups(),
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}
		
//	/**
//	 * Load the user data and user groups.
//	 * @param monitor The progress monitor to use.
//	 */
//	public void doLoad(ProgressMonitor monitor)
//	{
//		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AbstractAccountPageController.loadingAccountJob.name"), 4); //$NON-NLS-1$
//		try {
//			if (anchorID != null) {
//				// load user with person data
//				Account account = AccountDAO.sharedInstance().getAccount(
//						anchorID, getFetchGroups(),
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//						new SubProgressMonitor(monitor, 3)
//				);
//				monitor.worked(1);
//				// make a working copy to avoid changing the original
//				this.account = Util.cloneSerializable(account);
//			}
//			monitor.done();
//			fireModifyEvent(null, account);
//		} catch(Exception e) {
//			throw new RuntimeException(e);
//		} finally {
//			monitor.done();
//		}
//	}

//	/**
//	 * may be overriden if other fetchGroups are needed than the default ones
//	 * @return the fetchGroups to use, for obtaining the account
//	 */
//	protected String[] getFetchGroups() {
//		return FETCH_GROUPS;
//	}
	
}
