package org.nightlabs.jfire.trade.ui.repository.editor;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.store.dao.RepositoryDAO;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.Util;

class RepositoryGeneralPageController
extends EntityEditorPageController
{
	private Repository repository;

	public RepositoryGeneralPageController(EntityEditor editor)
	{
		super(editor, true);
		JDOLifecycleManager.sharedInstance().addNotificationListener(Repository.class, repositoryChangedListener);
	}
	@Override
	public void dispose()
	{
		JDOLifecycleManager.sharedInstance().removeNotificationListener(Repository.class, repositoryChangedListener);
		super.dispose();
	}

	private NotificationListener repositoryChangedListener = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.trade.ui.repository.editor.RepositoryGeneralPageController.loadingChangedRepositoryJob.name")) //$NON-NLS-1$
	{
		public void notify(NotificationEvent notificationEvent) {
			doLoad(getProgressMonitor());
		}
	};

	public Repository getRepository()
	{
		return repository;
	}

	@Override
	public void doLoad(ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.repository.editor.RepositoryGeneralPageController.loadingRepositoryJobMonitor.task.name"), 100); //$NON-NLS-1$
		AnchorID repositoryID = ((RepositoryEditorInput)getEntityEditor().getEditorInput()).getJDOObjectID();
		repository = Util.cloneSerializable(RepositoryDAO.sharedInstance().getRepository(
				repositoryID, new String[] { FetchPlan.DEFAULT, Repository.FETCH_GROUP_NAME },
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 100)));
		monitor.done();
		setLoaded(true); // must be done before fireModifyEvent!
		fireModifyEvent(null, repository);
	}

	@Override
	public boolean doSave(ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.repository.editor.RepositoryGeneralPageController.savingRepositoryJobMonitor.task.name"), 100); //$NON-NLS-1$
		RepositoryDAO.sharedInstance().storeRepository(repository, false, null, 1,
				new SubProgressMonitor(monitor, 100));
		monitor.done();
		return true;
	}
}
