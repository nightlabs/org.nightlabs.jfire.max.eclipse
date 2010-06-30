package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Collection;

import javax.jdo.FetchGroup;
import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.base.ui.security.UserSearchComposite;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.issuetracking.ui.issue.IssuePropertyDialog;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.jfire.issuetracking.ui.overview.action.DeleteIssueAction;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.security.User;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * The view containing an {@link IssueTable}, listing all {@link Issue}s matching a given {@link IssueQuery}.
 *
 * @author Chairat Kongarayawetchakun
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueEntryListViewer
extends JDOQuerySearchEntryViewer<Issue, IssueQuery>
{
	private QueryCollection<? extends IssueQuery> previousSavedQuery;

	public IssueEntryListViewer(Entry entry) {
		super(entry);
	}

	private IssueTable issueResultTable;

	@Override
	public AbstractTableComposite<Issue> createListComposite(Composite parent) {
		//		TODO we should pass the QueryMap obtained via this.getQueryMap() to the IssueTable so that it can filter new Issues agains it.
		issueResultTable = new IssueTable(parent, SWT.NONE);

		// [Observation; 02.07.2009]
		// It seems a good idea to have a NotificationListener here, to note whether an
		// Issue has been deleted, so that we can remove it from the list of table.
		// We dont have to make this 'completely' active, since its showing the results of the current search... right??
		// --> Let's give this a go first, and then decide later on. Kai.
		previousSavedQuery = null;
		JDOLifecycleManager.sharedInstance().addNotificationListener(Issue.class, issueChangeNotificationListener);

		issueResultTable.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				JDOLifecycleManager.sharedInstance().removeNotificationListener(Issue.class, issueChangeNotificationListener);
			}
		});

		issueResultTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Issue issue = issueResultTable.getFirstSelectedElement();

				//				IssueDescriptionView issuePropertyView = (IssueDescriptionView)RCPUtil.findView(IssueDescriptionView.VIEW_ID);
				//				IssueLinkView issueLinkView = (IssueLinkView)RCPUtil.findView(IssueLinkView.VIEW_ID);
				//				IssueHistoryView issueHistoryView = (IssueHistoryView)RCPUtil.findView(IssueHistoryView.VIEW_ID);
				//
				//				if (issuePropertyView != null) issuePropertyView.setIssue(issue);
				//				if (issueLinkView != null) issueLinkView.setIssue(issue);
				//				if (issueHistoryView != null) issueHistoryView.setIssue(issue);

				IssueID issueID = (IssueID)JDOHelper.getObjectId(issue);
				SelectionManager.sharedInstance().notify(
						new NotificationEvent(this, IssueTrackingPlugin.ZONE_PROPERTY, issueID, Issue.class)
				);
			}
		});

		return issueResultTable;
	}



	/**
	 * An implicit listener to handle the refreshing of the table's entry (or entries), in the case
	 * of a currently displayed Issue changed its fields or was deleted.
	 */
	private NotificationListener issueChangeNotificationListener = new NotificationAdapterJob() {
		@Override
		public void notify(final NotificationEvent event) {
			final ProgressMonitor monitor = getProgressMonitor();
			monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer.task.updatingIssues"), 100); //$NON-NLS-1$
			try {
				if (previousSavedQuery != null && !getComposite().isDisposed())
					getComposite().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							Collection<Issue> issues = issueResultTable.getElements();
							if (issues.isEmpty())	return;

							Collection<IssueID> issueIDs = NLJDOHelper.getObjectIDList(issues);
							for (Object obj : event.getSubjects()) {
								if (obj == null) continue;

								// In both DIRTY and DELETED cases, where the dirtyObjectID matches any of our table contents,
								// we want to refresh it again -- making sure that the refreshed contents adhere to the
								// constraints of the previous query. Kai.
								DirtyObjectID dirtyObjectID = (DirtyObjectID) obj;
								if ( issueIDs.contains(dirtyObjectID.getObjectID()) ) {
									issues = doSearch(previousSavedQuery, new SubProgressMonitor(monitor, 90));
									issueResultTable.setInput(issues);
									handleContextMenuItems();
									break;
								}
							}
						}
					});

			} finally {
				monitor.done();
			}

		}
	};



	@Override
	protected void addResultTableListeners(AbstractTableComposite<Issue> tableComposite) {
		super.addResultTableListeners(tableComposite);
	}

	public IssueTable getResultTable() {
		return issueResultTable;
	}

	@Override
	protected Collection<Issue> doSearch
	(QueryCollection<? extends IssueQuery> queryMap, ProgressMonitor monitor) {
		// We save the previous query; used later in the notification listener, in case we need
		// to refresh the table entries, given the query constraints.
		previousSavedQuery = queryMap;

		return IssueDAO.sharedInstance().getIssuesForQueries(
				queryMap,
				issueResultTable.getIssueTableFetchGroups(), // IssueTable.FETCH_GROUPS_ISSUE,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
	}

	@Override
	public Class<Issue> getTargetType()
	{
		return Issue.class;
	}

	/**
	 * The ID for the Quick search registry.
	 */
	public static final String QUICK_SEARCH_REGISTRY_ID = IssueEntryListViewer.class.getName();

	@Override
	protected String getQuickSearchRegistryID()
	{
		return QUICK_SEARCH_REGISTRY_ID;
	}



	// -----------------------------------------------------------------------------------------------------------------------------------|
	// ---[ Context-menu handling ]-------------------------------------------------------------------------------------------------------|
	// -----------------------------------------------------------------------------------------------------------------------------------|
	private MenuManager menuMgr;
	private IssueEditAction editIssueAction;
	private IssueDeleteAction deleteIssueAction;
	private IssueResolveAllAction resolveAllAction;
	private IssueCloseAllAction closeAllAction;
	private IssueAssignAllAction assignAllAction;
	private IssueSetPropertyAction setPropertyAction;
	/**
	 * Sets up the context menu to allow for the following operations:
	 *   1. Edit selected Issue(s) --> Opens the Issue pages.
	 *   2. Delete selected Issue(s) --> Invokes the delete dialogs.
	 */
	private void createContextMenu() {
		// Prepare the menu items.
		editIssueAction = new IssueEditAction();
		deleteIssueAction = new IssueDeleteAction();
		resolveAllAction = new IssueResolveAllAction();
		closeAllAction = new IssueCloseAllAction();
		assignAllAction = new IssueAssignAllAction();
		setPropertyAction = new IssueSetPropertyAction();

		// Relegate the menu-items to the menu-manager.
		menuMgr = getMenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager m) {
				menuMgr.add(editIssueAction);
				menuMgr.add(deleteIssueAction);
				menuMgr.add(resolveAllAction);
				menuMgr.add(closeAllAction);
				menuMgr.add(assignAllAction);
				menuMgr.add(setPropertyAction);
			}
		});

		// Create the menu and attach it to the IssueTable.
		Menu menu = menuMgr.createContextMenu(issueResultTable);
		issueResultTable.setMenu(menu); // <-- Hmm... this seems enough? Do we need to do this: 'getSite().registerContextMenu(menuMgr, issueTable);'

		// Define the behaviour of the menu items wrt the selections, or lack thereof, in the IssueTable.
		issueResultTable.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) { handleContextMenuItems(); }
		});
	}

	/**
	 * Depending on what item(s) is(are) selected from the {@link IssueTable}, the context-menu item
	 * should be correspondingly activated or deactivated.
	 */
	private void handleContextMenuItems() {
		assert editIssueAction != null && deleteIssueAction != null;

		// From the current behaviour, we can DELETE multiple Issues, but we can only EDIT one Issue.
		int ctr = issueResultTable.getSelectionCount();
		editIssueAction.setEnabled( ctr > 0 ); // OR should we also allow multiple Issues to be edited by opening multiple editors? Kai.
		deleteIssueAction.setEnabled( ctr > 0 );
	}

	@Override
	public Composite createComposite(Composite parent) {
		// Note: We can only setup the context-menu once the composite is created.
		//       And even then, we should check that the MenuManager in the super class is not null.
		Composite resultComposite = super.createComposite(parent);
		if (getMenuManager() != null) createContextMenu();
		return resultComposite;
	}


	// -----------------------------------------------------------------------------------------------------------------------------------|
	/**
	 * Handles the action to remove the selected Issue(s).
	 */
	private class IssueDeleteAction extends Action {
		public IssueDeleteAction() {
			setId(IssueDeleteAction.class.getName());
			setImageDescriptor(SharedImages.DELETE_16x16);
			setToolTipText("Delete selected Issue(s)");
			setText("Delete selected Issue(s)");
		}

		@Override
		public void run() {
			DeleteIssueAction deleteAction = new DeleteIssueAction();
			Collection<IssueID> issueIDs = NLJDOHelper.getObjectIDList(issueResultTable.getSelectedElements());
			deleteAction.setSelectedIssueIDs(issueIDs);
			deleteAction.run();
		}
	}

	// -----------------------------------------------------------------------------------------------------------------------------------|
	/**
	 * Handles the action to edit the selected Issue(s).
	 */
	private class IssueEditAction extends Action {
		public IssueEditAction() {
			setId(IssueEditAction.class.getName());
			setImageDescriptor(SharedImages.EDIT_16x16);
			setToolTipText("Edit selected Issue(s)");
			setText("Edit selected Issue(s)");
		}

		@Override
		public void run() {
			Collection<IssueID> issueIDs = NLJDOHelper.getObjectIDList(issueResultTable.getSelectedElements());
			for (IssueID issueID : issueIDs)
				try {
					RCPUtil.openEditor(new IssueEditorInput(issueID), IssueEditor.EDITOR_ID);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
		}
	}

	/**
	 * Handles the action to set resolved to the selected Issue(s).
	 */
	private class IssueResolveAllAction extends Action {
		public IssueResolveAllAction() {
			setId(IssueResolveAllAction.class.getName());
			setImageDescriptor(SharedImages.SAVE_16x16);
			setToolTipText("Resolve selected Issue(s)");
			setText("Resolve selected Issue(s)");
		}

		@Override
		public void run() {
			Collection<IssueID> issueIDs = NLJDOHelper.getObjectIDList(issueResultTable.getSelectedElements());
			for (IssueID issueID : issueIDs) {
				IssueDAO.sharedInstance().signalIssue(
						issueID,
						"resolve",
						false,
						new String[] {FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
				);
			}
		}
	}

	/**
	 * Handles the action to set closed to the selected Issue(s).
	 */
	private class IssueCloseAllAction extends Action {
		public IssueCloseAllAction() {
			setId(IssueCloseAllAction.class.getName());
			setImageDescriptor(SharedImages.DISCARD_16x16);
			setToolTipText("Close selected Issue(s)");
			setText("Close selected Issue(s)");
		}

		@Override
		public void run() {
			final Collection<IssueID> issueIDs = NLJDOHelper.getObjectIDList(issueResultTable.getSelectedElements());
			Job job = new Job("Signal Issues..............") {

				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception {
					for (IssueID issueID : issueIDs) {
						IssueDAO.sharedInstance().signalIssue(
								issueID,
								"close",
								false,
								new String[] {FetchPlan.DEFAULT},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
						);
					}
					return Status.OK_STATUS;
				}
			};
			job.setPriority(Job.SHORT);
			job.schedule();
		}
	}

	/**
	 * Handles the action to set assigned to the selected Issue(s).
	 */
	private class IssueAssignAllAction extends Action {
		public IssueAssignAllAction() {
			setId(IssueAssignAllAction.class.getName());
			setImageDescriptor(SharedImages.EDIT_16x16);
			setToolTipText("Assign selected Issue(s)");
			setText("Assign selected Issue(s)");
		}

		@Override
		public void run() {
			final Collection<Issue> issues = issueResultTable.getSelectedElements();
			UserSearchDialog userSearchDialog = new UserSearchDialog(getComposite().getShell(), null, UserSearchComposite.FLAG_TYPE_USER);
			int returnCode = userSearchDialog.open();
			if (returnCode == Dialog.OK) {
				final User assigneeUser = userSearchDialog.getSelectedUser();
				if (assigneeUser != null) {
					Job job = new Job("Signal Issues..............") {
						@Override
						protected IStatus run(ProgressMonitor monitor) throws Exception {
							for (Issue issue : issues) {
								issue.setAssignee(assigneeUser);
								IssueDAO.sharedInstance().storeIssue(issue, false, new String[] {FetchGroup.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
							}
							return Status.OK_STATUS;
						}
					};
					job.setPriority(Job.SHORT);
					job.schedule();
				}
			}
		}
	}

	/**
	 * Handles the action to change properties to the selected Issue(s).
	 */
	private class IssueSetPropertyAction extends Action {
		public IssueSetPropertyAction() {
			setId(IssueAssignAllAction.class.getName());
			setImageDescriptor(SharedImages.EDIT_16x16);
			setToolTipText("Edit properties");
			setText("Edit properties");
		}

		@Override
		public void run() {
			final Collection<Issue> selectedIssues = issueResultTable.getSelectedElements();
			final IssuePropertyDialog issuePropertyDialog = new IssuePropertyDialog(selectedIssues, getComposite().getShell());
			int returnType = issuePropertyDialog.open();
			if (returnType == Window.OK) {
				Job job = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer.task.updatingIssues")) {
					@Override
					protected IStatus run(ProgressMonitor monitor) throws Exception {
						for (Issue issue : selectedIssues) {
							issue.setIssueType(issuePropertyDialog.getSelectedIssueType());
							issue.setIssuePriority(issuePropertyDialog.getSelectedIssuePriority());
							issue.setIssueSeverityType(issuePropertyDialog.getSelectedIssueSeverityType());
							issue.setIssueResolution(issuePropertyDialog.getSelectedIssueResolution());

							IssueDAO.sharedInstance().storeIssue(issue, false, new String[] {FetchGroup.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
						}
						return Status.OK_STATUS;
					}
				};
				job.setPriority(Job.LONG);
				job.schedule();
			}
		}
	}

//	private static String[] FETCH_GROUP_FOR_ISSUE = new String[] {FetchGroup.DEFAULT,
//		Issue.FETCH_GROUP_ISSUE_TYPE,
//		Issue.FETCH_GROUP_ISSUE_PRIORITY,
//		Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
//		Issue.FETCH_GROUP_ISSUE_RESOLUTION};
}