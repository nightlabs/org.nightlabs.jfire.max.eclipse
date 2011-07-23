package org.nightlabs.jfire.trade.ui.account.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.base.login.ui.part.ICloseOnLogoutEditorPart;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditor;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

public class AccountEditor extends ActiveEntityEditor
implements ICloseOnLogoutEditorPart
{
	/**
	 * The editor id.
	 */
	public static final String EDITOR_ID = AccountEditor.class.getName();


	@Override
	protected String getEditorTitleFromEntity(Object entity) {
		return entity instanceof Account ? ((Account)entity).getName().getText() : null;
	}

	@Override
	protected Object retrieveEntityForEditorTitle(ProgressMonitor monitor) {
		AnchorID anchorID = ((AccountEditorInput)getEditorInput()).getJDOObjectID();
		assert anchorID != null;
		return AccountDAO.sharedInstance().getAccount(anchorID, new String[] { FetchPlan.DEFAULT, Account.FETCH_GROUP_NAME }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

//	/* (non-Javadoc)
//	 * @see org.nightlabs.base.ui.entityeditor.EntityEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
//	 */
//	@Override
//	public void init(IEditorSite site, IEditorInput input) throws PartInitException
//	{
//		super.init(site, input);
//		final AccountEditorInput accountEditorInput = (AccountEditorInput) input;
//		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AccountEditor.loadingAccountJob.name")) //$NON-NLS-1$
//		{
//			@Override
//			protected IStatus run(ProgressMonitor monitor)
//			throws Exception
//			{
//				final Account account = AccountDAO.sharedInstance().getAccount(
//						accountEditorInput.getJDOObjectID(),
//						new String[] { FetchPlan.DEFAULT, Account.FETCH_GROUP_NAME },
//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
//				Display.getDefault().asyncExec(new Runnable()
//				{
//					public void run()
//					{
//						setPartName(account.getName().getText());
//						setTitleToolTip(Anchor.getPrimaryKey(account.getOrganisationID(), account.getAnchorTypeID(), account.getAnchorID()));
//					}
//				});
//				return Status.OK_STATUS;
//			}
//		};
//		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
//		job.schedule();
//	}
}

