package org.nightlabs.jfire.issuetracking.ui.issue;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.login.ui.part.LSDViewPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueLinkView
extends LSDViewPart
{
	public static final String VIEW_ID = IssueLinkView.class.getName();

	private IMemento initMemento = null;
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.initMemento = memento;
	}

	private IssueLinkTable issueLinkTable;
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent)
	{
		issueLinkTable = new IssueLinkTable(parent, SWT.NONE);
		issueLinkTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		SelectionManager.sharedInstance().addNotificationListener(IssueTrackingPlugin.ZONE_PROPERTY, Issue.class, issueSelectionListener);

//		if (initMemento != null)
//			descriptionDetailComposite.init(initMemento);

		issueLinkTable.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				SelectionManager.sharedInstance().removeNotificationListener(IssueTrackingPlugin.ZONE_PROPERTY, Issue.class, issueSelectionListener);
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.login.part.LSDViewPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
	}

	private static String[] FETCH_GROUP = new String[] {
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_ISSUE_LINKS,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		IssueLink.FETCH_GROUP_ISSUE_LINK_TYPE,
		IssueLink.FETCH_GROUP_LINKED_OBJECT,
		IssueLink.FETCH_GROUP_LINKED_OBJECT_CLASS,
		IssueLinkType.FETCH_GROUP_NAME};

	private NotificationListener issueSelectionListener = new NotificationAdapterCallerThread(){
		public void notify(NotificationEvent notificationEvent) {
			Object firstSelection = notificationEvent.getFirstSubject();
			if (firstSelection instanceof IssueID) {
				IssueID issueID = (IssueID) firstSelection;
				if (issueLinkTable != null && !issueLinkTable.isDisposed()) {
					Issue issue = IssueDAO.sharedInstance().getIssue(issueID, FETCH_GROUP, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					issueLinkTable.setIssue(issue);

//					issueLinkTable.setInput(issue.getIssueLinks());
				}
			}
		}
	};
}