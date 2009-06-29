package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueLinkDAO;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class LegalEntityPersonIssueLinkTreeView  extends LSDViewPart{


	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(LegalEntityPersonIssueLinkTreeView .class);
	private PersonIssueLinkTreeComposite showLegalEntityLinkedTreeComposite;
	private CreateNewIssueViewAction createNewIssueViewAction = new CreateNewIssueViewAction();
	private AddNewCommentViewAction addNewCommentViewAction = new AddNewCommentViewAction();
	private IssueLink selectedIssueLink;


	protected void setSelectedIssueLink(IssueLink selectedIssueLink) {
		this.selectedIssueLink = selectedIssueLink;
	}

	public IssueLink getSelectedIssueLink() {
		return selectedIssueLink;
	}

	private LegalEntity partner = null;


	public static final String ID_VIEW = LegalEntityPersonIssueLinkTreeView.class.getName();

	private static final String[] FETCH_GROUPS_ISSUESLINK = new String[] {
		FetchPlan.DEFAULT,
		IssueLink.FETCH_GROUP_ISSUE,
		Issue.FETCH_GROUP_ISSUE_TYPE,
		Issue.FETCH_GROUP_SUBJECT,
		Issue.FETCH_GROUP_DESCRIPTION,
		Issue.FETCH_GROUP_ISSUE_COMMENTS,
		Issue.FETCH_GROUP_ISSUE_MARKERS,
		Issue.FETCH_GROUP_DESCRIPTION,
		IssueMarker.FETCH_GROUP_NAME,
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA,
		IssueComment.FETCH_GROUP_TEXT};

	@Override
	public void createPartContents(Composite parent) {
		showLegalEntityLinkedTreeComposite = new PersonIssueLinkTreeComposite(parent, SWT.NONE);
		showLegalEntityLinkedTreeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		SelectionManager.sharedInstance().addNotificationListener(
				TradePlugin.ZONE_SALE,
				LegalEntity.class, notificationListenerPersonSelected
		);

		showLegalEntityLinkedTreeComposite.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				SelectionManager.sharedInstance().removeNotificationListener(
						TradePlugin.ZONE_SALE,
						LegalEntity.class, notificationListenerPersonSelected
				);

				IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
				toolBarManager.removeAll();
			}
		});



		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		createNewIssueViewAction.init(this);
		toolBarManager.add(createNewIssueViewAction);
		addNewCommentViewAction.init(this);
		addNewCommentViewAction.setEnabled(false);
		toolBarManager.add(addNewCommentViewAction);
		createNewIssueViewAction.setEnabled(false);



		showLegalEntityLinkedTreeComposite.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			   public void selectionChanged(SelectionChangedEvent event) {
			       // if the selection is empty clear the label
			       if(event.getSelection().isEmpty()) {
			    	   addNewCommentViewAction.setEnabled(false);
			           return;
			       }
					StructuredSelection s = (StructuredSelection)event.getSelection();

					Object o = s.getFirstElement();
					if (o instanceof IssueLink)
					{
						setSelectedIssueLink((IssueLink)o);
						addNewCommentViewAction.setEnabled(true);
					}
					else
						addNewCommentViewAction.setEnabled(false);

			   }
			});

	}

	private NotificationListener notificationListenerPersonSelected = new NotificationAdapterJob("") { //$NON-NLS-1$
		public void notify(NotificationEvent event) {
			ProgressMonitor monitor = getProgressMonitor();
			monitor.beginTask("do sth.", 100);
			// some work


			monitor.worked(30);

			if (event.getSubjects().isEmpty())
				return;
			else
				legalEntityChanged((AnchorID)event.getFirstSubject(), new SubProgressMonitor(monitor, 70));
		}
	};

	// Some comments to this code (I already refactored it partially):
	//
	// 1) A method name *MUST* start with a small letter (never with a capital)! Read the sun code conventions!
	// 2) The method getChildNodes() of the TreeNode is called on the UI thread and therefore MUST NOT do
	//    expensive (= time consuming) code. Communicating to the server is considered to *always* be expensive!
	// 3) The method legalEntityChanged(...) must get the ProgressMonitor as parameter in order to indicate
	//    a) it's not executed on the UI thread (= it's OK to be expensive)
	//    b) to provide progress feed back.
	// 4) see notes in class IssueLinkTreeNode!
	// 5) This code creating IssueLinkTreeNodes shouldn't be here at all!!! This view is supposed to be a thin wrapper
	//    around the PersonIssueLinkTreeComposite. Hence all logic that is not essentially necessary here but could
	//    be implemented in PersonIssueLinkTreeComposite must be implemented there.
	// 6) And even more: Why the hell is the selection management done here now? It should be in PersonIssueLinkTreeComposite
	//    instead. If there are methods like getSelectedIssueLink() necessary here, they should simply delegate to PersonIssueLinkTreeComposite.
	//
	//
	// READ THESE DOCUMENTS:
	//  * https://www.jfire.org/modules/phpwiki/index.php/General%20Development%20Guidelines
	//  * https://www.jfire.org/modules/phpwiki/index.php/RCP%20Client%20Development%20Guidelines
	//
	// FIX THIS CODE!!! Move all into PersonIssueLinkTreeComposite that has nothing to do with the LegalEntity!
	//
	// Marco.

	private void legalEntityChanged(AnchorID partnerID, ProgressMonitor monitor)
	{
		monitor.beginTask("Loading legal entity", 100);
		try {
			LegalEntity partner = partnerID == null ? null : LegalEntityDAO.sharedInstance().getLegalEntity(
					partnerID,
					new String[] {
							FetchPlan.DEFAULT,
							LegalEntity.FETCH_GROUP_PERSON,
							PropertySet.FETCH_GROUP_FULL_DATA
					},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new SubProgressMonitor(monitor, 30)
			);
			this.partner = partner;

			final ObjectID personID = (ObjectID) JDOHelper.getObjectId(partner.getPerson());

			final Collection<IssueLink> links = IssueLinkDAO.sharedInstance().getIssueLinksByOrganisationIDAndLinkedObjectID(partner.getOrganisationID(),
					personID,
					FETCH_GROUPS_ISSUESLINK,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new SubProgressMonitor(monitor, 70)
			);

			final IssueLinkTreeNode rootlegalEntityIssuesLinkNode = new IssueLinkTreeNode("Issue List", null)
			{
				@Override
				public Object[] getChildNodes() {
					return links.toArray();
				}
				@Override
				public boolean hasChildren() {
					return !links.isEmpty();
				}
			};

			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if(showLegalEntityLinkedTreeComposite.isDisposed())
						return;

					showLegalEntityLinkedTreeComposite.setRootNode(rootlegalEntityIssuesLinkNode);
					showLegalEntityLinkedTreeComposite.getTreeViewer().expandToLevel(0);
					showLegalEntityLinkedTreeComposite.refresh();
					createNewIssueViewAction.setEnabled(true);
				}
			});
		} finally {
			monitor.done();
		}
	}

	public LegalEntity getPartner() {
		return partner;
	}

}


