package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.jface.action.Action;
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
import org.nightlabs.jfire.issue.id.IssueLinkID;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.jdo.notification.SimpleLifecycleListenerFilter;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class ShowLinkedIssueSection
extends ToolBarSectionPart
{
	private ShowLinkedIssuePageController controller;
	private IFormPage page;
	private IssueTable issueTable;

	private AddIssueLinkAction addIssueLinkAction;

	/**
	 * @param page
	 * @param parent
	 * @param controller
	 */
	public ShowLinkedIssueSection(IFormPage page, Composite parent, final ShowLinkedIssuePageController controller) {
		super(page, parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR, Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssueSection.title")); //$NON-NLS-1$
		this.controller = controller;
		this.page = page;

		getSection().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		getSection().setLayout(new GridLayout());

		XComposite client = new XComposite(getSection(), SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		client.getGridLayout().numColumns = 1;


		addIssueLinkAction = new AddIssueLinkAction();
		getToolBarManager().add(addIssueLinkAction);

		getSection().setClient(client);
		updateToolBarManager();

		issueTable = new IssueTable(client, SWT.NONE);
		issueTable.setLayoutData(new GridData(GridData.FILL_BOTH));


		// --- 8< --- KaiExperiments: since 23.06.2009 ------------------
		// See notes in [Observation and strategy, 23.06.2009] in ShowLinkedIssuePageController.
		JDOLifecycleManager.sharedInstance().addLifecycleListener(issueLinksLifeCycleListener);
		JDOLifecycleManager.sharedInstance().addNotificationListener(Issue.class, issueChangeNotificationListener);
		issueTable.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				JDOLifecycleManager.sharedInstance().removeLifecycleListener(issueLinksLifeCycleListener);
				JDOLifecycleManager.sharedInstance().removeNotificationListener(Issue.class, issueChangeNotificationListener);
			}
		});
		// ------ KaiExperiments ----- >8 -------------------------------
	}

	// --- 8< --- KaiExperiments: since 23.06.2009 ------------------
	// We require the explicit listener here, to monitor any additions or removal of IssueLinks.
	private JDOLifecycleListener issueLinksLifeCycleListener = new JDOLifecycleAdapterJob("Checking IssueLinks...") {
		private IJDOLifecycleListenerFilter filter = new SimpleLifecycleListenerFilter(
				IssueLink.class, false, JDOLifecycleState.NEW, JDOLifecycleState.DELETED
		);

		@Override
		public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter() { return filter; }

		@Override
		public void notify(JDOLifecycleEvent event) {
			// Perform the sifting here.
			for (DirtyObjectID dirtyObjectID : event.getDirtyObjectIDs()) {
				IssueLink issueLink = IssueLinkDAO.sharedInstance().getIssueLink(
						(IssueLinkID)dirtyObjectID.getObjectID(),
						new String[] {FetchPlan.DEFAULT, IssueLink.FETCH_GROUP_LINKED_OBJECT}, // <-- Seems enough without having to load IssueType.
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()
				);

				// If any single one of the DirtyObjects within the carrier points to the <linkedObject> known by
				// the controller, then we stop the loop, and perform a single doLoad().
				JDOLifecycleState lState = dirtyObjectID.getLifecycleState();	// <-- Bad... how does one deal with deleted objects? I need to access the linkedObject from the deleted IssueLink. Check with jdoPreDelete()?
				if ( lState.equals(JDOLifecycleState.DELETED) || issueLink.getLinkedObjectID().equals( controller.getArticleContainerID() ) ) {
					controller.doLoad(new NullProgressMonitor());
					break;
				}
			}
		}
	};

	// And now, we require the implicit listener for that handles the Issue items in the IssueTable.
	private NotificationListener issueChangeNotificationListener = new NotificationAdapterJob() {
		@Override
		public void notify(NotificationEvent event) {
			// Check to see if any of the dirty Issues notified belong in our table.
			// If so, refresh the entry.

			// TODO Finish this properly. Pleazzzzz....
			controller.doLoad(new NullProgressMonitor());

//			for (Object dirtyObjectID : event.getSubjects()) {
//
//			}
//
//
//			event.getSource();
//			System.out.println();
		}
	};
	// ------ KaiExperiments ----- >8 -------------------------------



	public void setLinkedIssues(Collection<Issue> issues) {
		issueTable.setInput(issues);
	}

	public void setLinkedIssues(Collection<Issue> issues, Issue selectedIssue) {
		issueTable.setInput(issues);
		if (selectedIssue != null)
			highlightIssueEntry(selectedIssue);
	}

	public IssueTable getIssueTable() {
		return issueTable;
	}

	/**
	 * Highlights the entry in the {@link IssueTable} matching the given {@link Issue}.
	 * Table will contain no highlight if no match is found.
	 */
	public void highlightIssueEntry(Issue issue) {
		int index = -1;
		issueTable.getTableViewer().getTable().setSelection(index);

		Collection<Issue> issues = issueTable.getElements(); // Sometimes, something is not right here. Cache elements dont seem updated?? Kai.
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
			setToolTipText("Add a link to an Issue");
			setText("Add a link to an Issue");
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
