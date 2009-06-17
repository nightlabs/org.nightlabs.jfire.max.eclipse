package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
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
import org.nightlabs.progress.NullProgressMonitor;

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
	private IssueLinkTreeNode mainIssuesListLinkNode;	
	@Override
	public void createPartContents(Composite parent) {
		showLegalEntityLinkedTreeComposite = new PersonIssueLinkTreeComposite(parent, SWT.NONE);
		showLegalEntityLinkedTreeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		// sets the Empty root node
		IssueLinkTreeNode rootlegalEntityIssuesLinkNode= new IssueLinkTreeNode("Root",null,true){
			@Override
			public Object[]  getChildNodes() {
				setHasChildNodes(true);
				return new Object[] {mainIssuesListLinkNode};				
			}	
		};

		this.mainIssuesListLinkNode = new IssueLinkTreeNode("Issue List",null,false);
		showLegalEntityLinkedTreeComposite.setRootNode(rootlegalEntityIssuesLinkNode);

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
			}
		});


	}

	private NotificationListener notificationListenerPersonSelected = new NotificationAdapterJob("") { //$NON-NLS-1$
		public void notify(NotificationEvent event) {
			if (event.getSubjects().isEmpty())
				return;
			else
				LegalEntityChanged((AnchorID)event.getFirstSubject());
		}
	};


	private void LegalEntityChanged(AnchorID partnerID)
	{
		final LegalEntity partner = partnerID == null ? null : LegalEntityDAO.sharedInstance().getLegalEntity(
				partnerID,
				new String[] {
						FetchPlan.DEFAULT,
						LegalEntity.FETCH_GROUP_PERSON,
						PropertySet.FETCH_GROUP_FULL_DATA
				},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor());

		final ObjectID personID = (ObjectID) JDOHelper.getObjectId(partner.getPerson());			
		this.mainIssuesListLinkNode= new IssueLinkTreeNode("Issue List",null,true){
			@Override
			public Object[] getChildNodes() {	
				Object[] links = IssueLinkDAO.sharedInstance().getIssueLinksByOrganisationIDAndLinkedObjectID(partner.getOrganisationID(), 
						personID, 
						FETCH_GROUPS_ISSUESLINK, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new NullProgressMonitor()).toArray();
				setHasChildNodes(links.length>0);
				return links;
			}	
		};		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				showLegalEntityLinkedTreeComposite.refresh();
			}
		});
	}









}
