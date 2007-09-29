package org.nightlabs.jfire.trade.repository.editor;

import java.util.Iterator;

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
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.store.dao.RepositoryDAO;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;

public class RepositoryEditor
extends EntityEditor
{
	public static final String EDITOR_ID = RepositoryEditor.class.getName();

	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException
	{
		super.init(site, input);
		loadName();

		JDOLifecycleManager.sharedInstance().addNotificationListener(Repository.class, repositoryChangedListener);	
	}

	@Override
	public void dispose()
	{
		JDOLifecycleManager.sharedInstance().removeNotificationListener(Repository.class, repositoryChangedListener);
		super.dispose();
	}

	private NotificationListener repositoryChangedListener = new NotificationAdapterCallerThread()
	{
		public void notify(NotificationEvent notificationEvent) {
			AnchorID repositoryID = ((RepositoryEditorInput) getEditorInput()).getJDOObjectID();
			boolean ignore = true;
			for (Iterator<?> it = notificationEvent.getSubjects().iterator(); it.hasNext(); ) {
				DirtyObjectID dirtyObjectID = (DirtyObjectID) it.next();
				if (repositoryID.equals(dirtyObjectID.getObjectID())) {
					ignore = false;
					break;
				}
			}

			if (!ignore)
				loadName();
		}
	};

	private Job loadNameJob = null;
	private synchronized void loadName()
	{
		final RepositoryEditorInput repositoryEditorInput = (RepositoryEditorInput) getEditorInput();
		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.repository.editor.RepositoryEditor.loadingRepositoryJob.name")) //$NON-NLS-1$
		{
			@Override
			protected IStatus run(ProgressMonitor monitor)
			throws Exception
			{
				final Repository repository = RepositoryDAO.sharedInstance().getRepository(
						repositoryEditorInput.getJDOObjectID(),
						new String[] { FetchPlan.DEFAULT, Repository.FETCH_GROUP_NAME },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				final Job thisJob = this;
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						if (loadNameJob != thisJob)
							return;

						setPartName(repository.getName().getText());
						setTitleToolTip(Repository.getPrimaryKey(repository.getOrganisationID(), repository.getAnchorTypeID(), repository.getAnchorID()));
					}
				});
				return Status.OK_STATUS;
			}
		};
		loadNameJob = job;
		job.setPriority(Job.SHORT);
		job.schedule();	
	}
}
