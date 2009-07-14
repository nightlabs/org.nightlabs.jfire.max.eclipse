package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueLinkDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.id.IssueLinkID;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.SimpleLifecycleListenerFilter;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * This is the related Section called from the {@link ShowLinkedIssuePage}, containing the {@link IssueTable} that displays
 * the {@link Issue}s linked to the controllerObject, which in this case refers to the {@link ArticleContainer}.
 *
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class ShowLinkedIssueSection
extends ToolBarSectionPart
{
	private ShowLinkedIssuePageController controller;
	private IFormPage page;
	private IssueTable issueTable;

	private AddIssueLinkAction addIssueLinkAction;
	private IssueLinkActionRemove issueLinkActionRemove;

	/**
	 * Creates a new instance of the ShowLinkedIssueSection.
	 */
	public ShowLinkedIssueSection(IFormPage page, Composite parent, final ShowLinkedIssuePageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.title")); //$NON-NLS-1$
		this.controller = controller;
		this.page = page;

		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;


		issueTable = new IssueTable(client, SWT.NONE);
		issueTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		issueTable.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				handleRemoveActionButton();
			}
		});


		// See notes in [Observation and strategy, 23.06.2009] in ShowLinkedIssuePageController.
		JDOLifecycleManager.sharedInstance().addLifecycleListener(issueLinksLifeCycleListener);
		JDOLifecycleManager.sharedInstance().addNotificationListener(Issue.class, issueChangeNotificationListener);
		issueTable.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				JDOLifecycleManager.sharedInstance().removeLifecycleListener(issueLinksLifeCycleListener);
				JDOLifecycleManager.sharedInstance().removeNotificationListener(Issue.class, issueChangeNotificationListener);
			}
		});


		// The Action buttons to perform the interface for adding and removing IssueLinks.
		addIssueLinkAction = new AddIssueLinkAction();
		getToolBarManager().add( addIssueLinkAction );

		issueLinkActionRemove = new IssueLinkActionRemove(issueTable, controller, page);
		getToolBarManager().add( issueLinkActionRemove );

		updateToolBarManager();
		getSection().setClient(client);
	}

	/**
	 * Makes the Action button enabled if and only if there is at least one selected item from the {@link IssueTable}.
	 */
	protected void handleRemoveActionButton() {
		assert issueLinkActionRemove != null;
		issueLinkActionRemove.setEnabled( issueTable.getSelectionIndex() >= 0 );
	}


	// -----------------------------------------------------------------------------------------------------------------------------------|
	// ---[ Lifecycle and Notification listeners: IssueLinks and Issues ] ----------------------------------------------------------------|
	// -----------------------------------------------------------------------------------------------------------------------------------|
	// We require the explicit listener here, to monitor any additions or removal of IssueLinks.
	// See notes in [Observation and strategy, 23.06.2009] in ShowLinkedIssuePageController.
	private JDOLifecycleListener issueLinksLifeCycleListener = new JDOLifecycleAdapterJob(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.job.name.checkingIssueLinks")) { //$NON-NLS-1$
		private IJDOLifecycleListenerFilter filter = new SimpleLifecycleListenerFilter(
				IssueLink.class, false, JDOLifecycleState.NEW, JDOLifecycleState.DELETED
		);

		@Override
		public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter() { return filter; }

		@Override
		public void notify(JDOLifecycleEvent event) {
			ProgressMonitor monitor = getProgressMonitor();
			monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.task.updatingIssueLinks"), 100); //$NON-NLS-1$
			try {
				// Perform the sifting here.
				for (DirtyObjectID dirtyObjectID : event.getDirtyObjectIDs()) {
					IssueLink issueLink = IssueLinkDAO.sharedInstance().getIssueLink(
							(IssueLinkID)dirtyObjectID.getObjectID(),
							new String[] {FetchPlan.DEFAULT, IssueLink.FETCH_GROUP_LINKED_OBJECT}, // <-- Seems enough without having to load IssueType.
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new SubProgressMonitor(monitor, 10)
					);

					// If any single one of the DirtyObjects within the carrier points to the <linkedObject> known by
					// the controller, then we stop the loop, and perform a single doLoad().
					JDOLifecycleState lState = dirtyObjectID.getLifecycleState();	// <-- Bad... how does one deal with deleted objects? I need to access the linkedObject from the deleted IssueLink. Check with jdoPreDelete()?
					if ( lState.equals(JDOLifecycleState.DELETED) || issueLink.getLinkedObjectID().equals( controller.getArticleContainerID() ) ) {
						controller.doLoad(new SubProgressMonitor(monitor, 70));
						break;
					}
				}

			} finally {
				monitor.done();
			}
		}
	};

	// And now, we require the implicit listener for that handles the Issue items in the IssueTable.
	private NotificationListener issueChangeNotificationListener = new NotificationAdapterJob() {
		@Override
		public void notify(final NotificationEvent event)
		{
			final ProgressMonitor monitor = getProgressMonitor();
			monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.task.updatingIssue"), 100); //$NON-NLS-1$
			try {
				if (!getSection().isDisposed()) {
					getSection().getDisplay().asyncExec(new Runnable(){
						@Override
						public void run() {
							// Check to see if any of the dirty Issues notified belong in our table.
							// If so, refresh the entry.
							Collection<Issue> issues = issueTable.getElements();
							if (issues.isEmpty())	return;

							Collection<IssueID> issueIDs = NLJDOHelper.getObjectIDList(issues);
							for (Object obj : event.getSubjects()) {
								if (obj == null)
									continue;

								DirtyObjectID dirtyObjectID = (DirtyObjectID) obj;
								if (dirtyObjectID.getLifecycleState().equals( JDOLifecycleState.DIRTY ) && issueIDs.contains(dirtyObjectID.getObjectID())) {
									controller.doLoad(new SubProgressMonitor(monitor, 90));
									break;
								}
							}
						}
					});
				}
			} finally {
				monitor.done();
			}

		}
	};



	// -----------------------------------------------------------------------------------------------------------------------------------|
	// ---[ Routines to handle the IssueTable contents ] ---------------------------------------------------------------------------------|
	// -----------------------------------------------------------------------------------------------------------------------------------|
	/**
	 * Sets the {@link Issue}s linked to the {@link ArticleContainer}, and updates the remove-Action-button.
	 */
	public void setLinkedIssues(Collection<Issue> issues) {
		issueTable.setInput(issues);
		handleRemoveActionButton();
	}

	/**
	 * Sets the {@link Issue}s linked to the {@link ArticleContainer}, and updates the remove-Action-button, and
	 * highlights the {@link Issue} identified by selectedIssue.
	 */
	public void setLinkedIssues(Collection<Issue> issues, Issue selectedIssue) {
		setLinkedIssues(issues);
		if (selectedIssue != null)
			highlightIssueEntry(selectedIssue);
	}

	/**
	 * @return the {@link IssueTable} from this Section.
	 */
	public IssueTable getIssueTable() {
		return issueTable;
	}

	/**
	 * Highlights the entry in the {@link IssueTable} matching the given {@link Issue}.
	 * Table will contain no highlight if no match is found.
	 * FIXME Something is still wrong here... Wont highlight the latest entry.
	 */
	protected void highlightIssueEntry(Issue issue) {
		int index = -1;
		issueTable.getTableViewer().getTable().setSelection(index);

		Collection<Issue> issues = issueTable.getElements();
		for(Issue issueElem : issues) {
			index++;
			if ( issueElem.equals(issue) ) {
				issueTable.getTableViewer().getTable().setSelection(index);
				break;
			}
		}
	}



	// -----------------------------------------------------------------------------------------------------------------------------------|
	/**
	 * Handles the action to add a new {@link IssueLink} to an {@link Issue}.
	 */
	private class AddIssueLinkAction extends Action {
		public AddIssueLinkAction() {
			setId(AddIssueLinkAction.class.getName());
			setImageDescriptor(SharedImages.ADD_16x16);
			setToolTipText(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.addIssueLinkAction.tooltip")); //$NON-NLS-1$
			setText(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.addIssueLinkAction.text")); //$NON-NLS-1$
		}

		@Override
		public void run() {
			Object linkedObject = controller.getArticleContainer();
			AttachIssueToObjectWizard attachIssueToObjectWizard = new AttachIssueToObjectWizard(linkedObject);
			DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(attachIssueToObjectWizard)
			{
				@Override
				protected Point getInitialSize()
				{
					return new Point(convertHorizontalDLUsToPixels(600), convertVerticalDLUsToPixels(450));
				}
			};
			dialog.open();

			// Update the table in the Section.
			if (dialog.getReturnCode() == Window.OK) { // != Window.CANCEL) {
				((ShowLinkedIssuePage)page).setHighlightIssueEntry( attachIssueToObjectWizard.getSelectedIssue() ); // <-- Attempt to highlight the latest entry in the table.
//				controller.doLoad(new NullProgressMonitor()); // <-- This shall be delegated to the issueLinksLifeCycleListener.
			}

		}
	}

}
