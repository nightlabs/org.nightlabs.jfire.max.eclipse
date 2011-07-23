package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import javax.jdo.FetchPlan;

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
import org.nightlabs.jfire.base.login.ui.part.LSDViewPart;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issuelink.person.AddNewCommentViewAction;
import org.nightlabs.jfire.issuetracking.ui.issuelink.person.CreateNewIssueViewAction;
import org.nightlabs.jfire.issuetracking.ui.issuelink.person.IPersonIssueLinkView;
import org.nightlabs.jfire.issuetracking.ui.issuelink.person.PersonIssueLinkTreeComposite;
import org.nightlabs.jfire.issuetracking.ui.issuelink.person.RemovePersonIssueLinkViewAction;
import org.nightlabs.jfire.person.Person;
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
public class LegalEntityPersonIssueLinkTreeView extends LSDViewPart implements IPersonIssueLinkView
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(LegalEntityPersonIssueLinkTreeView .class);
	public static final String ID_VIEW = LegalEntityPersonIssueLinkTreeView.class.getName();

	private PersonIssueLinkTreeComposite showLegalEntityLinkedTreeComposite;
	private CreateNewIssueViewAction createNewIssueViewAction = new CreateNewIssueViewAction();
	private AddNewCommentViewAction addNewCommentViewAction = new AddNewCommentViewAction();
	private RemovePersonIssueLinkViewAction removePersonIssueLinkViewAction = new RemovePersonIssueLinkViewAction();
	private LegalEntity partner = null;

	public Object getSelectedNode() {
		if (showLegalEntityLinkedTreeComposite.isDisposed())
			return null;
		else
			return showLegalEntityLinkedTreeComposite.getSelectedNode();
	}

	@Override
	public void createPartContents(Composite parent) {
		showLegalEntityLinkedTreeComposite = new PersonIssueLinkTreeComposite(parent, SWT.NONE, getSite());
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
		removePersonIssueLinkViewAction.init(this);
		removePersonIssueLinkViewAction.setEnabled(false);
		toolBarManager.add(removePersonIssueLinkViewAction);
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
				{	addNewCommentViewAction.setEnabled(true);
					removePersonIssueLinkViewAction.setEnabled(true);
				}
				else
				{
					addNewCommentViewAction.setEnabled(false);
					removePersonIssueLinkViewAction.setEnabled(false);
				}
			}
		});
	}

	private NotificationListener notificationListenerPersonSelected = new NotificationAdapterJob("") { //$NON-NLS-1$
		public void notify(NotificationEvent event) {
			ProgressMonitor monitor = getProgressMonitor();
			monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.LegalEntityPersonIssueLinkTreeView.job.doSomething"), 100); //$NON-NLS-1$
			// some work
			monitor.worked(30);
			if (event.getSubjects().isEmpty())
				return;
			else
				legalEntityChanged((AnchorID)event.getFirstSubject(), new SubProgressMonitor(monitor, 70));
		}
	};

	private void legalEntityChanged(AnchorID partnerID, ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person.LegalEntityPersonIssueLinkTreeView.task.loadingLegalEntity"), 100); //$NON-NLS-1$
		try {
			LegalEntity partner = partnerID == null ? null : LegalEntityDAO.sharedInstance().getLegalEntity(
					partnerID,
					new String[] {
							FetchPlan.DEFAULT,
							LegalEntity.FETCH_GROUP_PERSON,
//							PropertySet.FETCH_GROUP_FULL_DATA
					},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new SubProgressMonitor(monitor, 30)
			);
			this.partner = partner;

			showLegalEntityLinkedTreeComposite.setRootNode(partner.getPerson(), new SubProgressMonitor(monitor, 70));
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					createNewIssueViewAction.setEnabled(true);
				}
			});
		} finally {
			monitor.done();
		}
	}

	public LegalEntity getLegalEntity() {
		return partner;
	}

	@Override
	public Person getPerson() {
		if (partner != null) {
			return partner.getPerson();
		}
		return null;
	}

	@Override
	public IssueLink getSelectedIssueLink() {
		return showLegalEntityLinkedTreeComposite.getSelectedIssueLink();
	}
}


