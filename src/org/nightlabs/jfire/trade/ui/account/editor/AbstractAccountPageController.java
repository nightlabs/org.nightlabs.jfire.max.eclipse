/**
 * 
 */
package org.nightlabs.jfire.trade.ui.account.editor;

import javax.jdo.FetchPlan;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.SummaryAccount;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.Util;

/**
 * Abstract base class for creating account based Page Controllers
 * 
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 */
public abstract class AbstractAccountPageController
extends EntityEditorPageController
{
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Account.FETCH_GROUP_THIS_ACCOUNT,
		SummaryAccount.FETCH_GROUP_THIS_SUMMARY_ACCOUNT,
		AccountType.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON,
		PropertySet.FETCH_GROUP_FULL_DATA
	};
	
	private static final long serialVersionUID = -1651161683093714801L;

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AccountGeneralPageController.class);

	/**
	 * The user id.
	 */
	protected AnchorID anchorID;

	/**
	 * The user editor.
	 */
	protected EntityEditor editor;

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
		this.editor = editor;
		JDOLifecycleManager.sharedInstance().addNotificationListener(Account.class, accountChangedListener);
	}

	@Override
	public void dispose()
	{
		JDOLifecycleManager.sharedInstance().removeNotificationListener(Account.class, accountChangedListener);
		super.dispose();
	}
	
	private NotificationListener accountChangedListener = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AbstractAccountPageController.loadingChangedAccountJob.name")) //$NON-NLS-1$
	{
		public void notify(NotificationEvent notificationEvent) {
			doLoad(getProgressMonitor());
		}
	};
	
	/**
	 * Get the editor.
	 * @return the editor
	 */
	public EntityEditor getEditor() {
		return editor;
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
		return account;
	}

	/**
	 * Load the user data and user groups.
	 * @param monitor The progress monitor to use.
	 */
	public void doLoad(IProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AbstractAccountPageController.loadingAccountJob.name"), 4); //$NON-NLS-1$
		try {
			if (anchorID != null) {
				// load user with person data
				Account account = AccountDAO.sharedInstance().getAccount(
						anchorID, getFetchGroups(),
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(new ProgressMonitorWrapper(monitor), 3)
				);
				monitor.worked(1);
				// make a working copy to avoid changing the original
				this.account = Util.cloneSerializable(account);
			}
			monitor.done();
			fireModifyEvent(null, account);
		} catch(Exception e) {
			throw new RuntimeException(e);
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * may be overriden if other fetchGroups are needed than the default ones
	 * @return the fetchGroups to use, for obtaining the account
	 */
	protected String[] getFetchGroups() {
		return FETCH_GROUPS;
	}
}
