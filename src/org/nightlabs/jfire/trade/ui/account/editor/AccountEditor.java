package org.nightlabs.jfire.trade.ui.account.editor;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.Anchor;
import org.nightlabs.progress.ProgressMonitor;

public class AccountEditor extends EntityEditor
{
	/**
	 * The editor id.
	 */
	public static final String EDITOR_ID = AccountEditor.class.getName();

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.entityeditor.EntityEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		final AccountEditorInput accountEditorInput = (AccountEditorInput) input;
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.account.editor.AccountEditor.loadingAccountJob.name")) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				final Account account = AccountDAO.sharedInstance().getAccount(
						accountEditorInput.getJDOObjectID(),
						new String[] { FetchPlan.DEFAULT, Account.FETCH_GROUP_NAME },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						setPartName(account.getName().getText());
						setTitleToolTip(Anchor.getPrimaryKey(account.getOrganisationID(), account.getAnchorTypeID(), account.getAnchorID()));
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}
}

