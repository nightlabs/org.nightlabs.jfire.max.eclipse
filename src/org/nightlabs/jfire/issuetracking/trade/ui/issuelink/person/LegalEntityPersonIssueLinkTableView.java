package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.NotificationAdapterSWTThreadAsync;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.login.ui.part.LSDViewPart;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issuetracking.ui.issuelink.person.AddNewCommentViewAction;
import org.nightlabs.jfire.issuetracking.ui.issuelink.person.CreateNewIssueViewAction;
import org.nightlabs.jfire.issuetracking.ui.issuelink.person.IPersonIssueLinkView;
import org.nightlabs.jfire.issuetracking.ui.issuelink.person.PersonIssueLinkTableComposite;
import org.nightlabs.jfire.issuetracking.ui.issuelink.person.RemovePersonIssueLinkViewAction;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.dao.PropertySetDAO;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractEditArticleContainerAction;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class LegalEntityPersonIssueLinkTableView extends LSDViewPart implements IPersonIssueLinkView
{
	public static final String VIEW_ID = LegalEntityPersonIssueLinkTableView.class.getName();

	private PersonIssueLinkTableComposite personIssueLinksComposite;
	private Person person;

	private CreateNewIssueViewAction createNewIssueViewAction = new CreateNewIssueViewAction();
	private AddNewCommentViewAction addNewCommentViewAction = new AddNewCommentViewAction();
	private RemovePersonIssueLinkViewAction removePersonIssueLinkViewAction = new RemovePersonIssueLinkViewAction();

	/**
	 * Listener setting the correct {@link LegalEntity}. This is used by the
	 * {@link AbstractEditArticleContainerAction} which opens an editor from another perspective.
	 */
	private NotificationListener notificationListenerPersonSelected = new NotificationAdapterJob("") {
		@Override
		public void notify(NotificationEvent event) {
			if (LegalEntityPersonIssueLinkTableView.this.equals(event.getSource()))
				return;

			if (getSite() != null && getSite().getShell() != null && !getSite().getShell().isDisposed() && getSite().getShell().getDisplay() != null) {
				Display display = getSite().getShell().getDisplay();
				if (event.getSubjects().isEmpty())
				{
					if (!display.isDisposed()) {
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								person = null;
								personIssueLinksComposite.setSelectedPersonID(null);
							}
						});
					}
				}
				else {
					PropertySetID tmpPersonID = null;
					if (event.getFirstSubject() instanceof AnchorID) {
						AnchorID legalEntityID = (AnchorID) event.getFirstSubject();
						LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(
								legalEntityID,
								new String[] {FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_PERSON},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								getProgressMonitor());
						tmpPersonID = (PropertySetID) JDOHelper.getObjectId(legalEntity.getPerson());
					}
					else if (event.getFirstSubject() instanceof PropertySetID) {
						tmpPersonID = (PropertySetID) event.getFirstSubject();
					}

					final PropertySetID personID = tmpPersonID;
					if (personID != null) {
						person = (Person) PropertySetDAO.sharedInstance().getPropertySet(personID,
								new String[] {FetchPlan.DEFAULT},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								getProgressMonitor());
					}
					if (personID != null && !display.isDisposed()) {
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								personIssueLinksComposite.setSelectedPersonID(personID);
							}
						});
					}
				}
			}
		}
	};

	private NotificationListener issueChangeListener = new NotificationAdapterSWTThreadAsync() {
		@Override
		public void notify(NotificationEvent notificationEvent)
		{
			if (getPerson() != null) {
				final PropertySetID personID = (PropertySetID) JDOHelper.getObjectId(getPerson());
				personIssueLinksComposite.setSelectedPersonID(personID);
			}
		}
	};

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent)
	{
		personIssueLinksComposite = new PersonIssueLinkTableComposite(parent, SWT.NONE);
		personIssueLinksComposite.setLayout(XComposite.getLayout(LayoutMode.TIGHT_WRAPPER));

		// register selection listener to listen for current selected legal entity
		SelectionManager.sharedInstance().addNotificationListener(TradePlugin.ZONE_SALE,
				LegalEntity.class, notificationListenerPersonSelected);
		SelectionManager.sharedInstance().addNotificationListener(TradePlugin.ZONE_SALE,
				Person.class, notificationListenerPersonSelected);

		// add notification listener for issue (link) changes
		JDOLifecycleManager.sharedInstance().addNotificationListener(Issue.class, issueChangeListener);

		// unregister listeners when disposed
		personIssueLinksComposite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				SelectionManager.sharedInstance().removeNotificationListener(TradePlugin.ZONE_SALE,
						LegalEntity.class, notificationListenerPersonSelected);
				SelectionManager.sharedInstance().removeNotificationListener(TradePlugin.ZONE_SALE,
						Person.class, notificationListenerPersonSelected);

				JDOLifecycleManager.sharedInstance().removeNotificationListener(Issue.class, issueChangeListener);
			}
		});

		// register view actions
		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
		if (toolBarManager.find(CreateNewIssueViewAction.ID) == null) {
			createNewIssueViewAction.init(this);
			toolBarManager.add(createNewIssueViewAction);
//			createNewIssueViewAction.setEnabled(false);
		}
		if (toolBarManager.find(RemovePersonIssueLinkViewAction.ID) == null) {
			removePersonIssueLinkViewAction.init(this);
			removePersonIssueLinkViewAction.setEnabled(false);
			toolBarManager.add(removePersonIssueLinkViewAction);
		}
		if (toolBarManager.find(AddNewCommentViewAction.ID) == null) {
			addNewCommentViewAction.init(this);
			addNewCommentViewAction.setEnabled(false);
			toolBarManager.add(addNewCommentViewAction);
		}
//		// register view actions
//		IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();
//		createNewIssueViewAction.init(this);
//		toolBarManager.add(createNewIssueViewAction);
////			createNewIssueViewAction.setEnabled(false);
//		removePersonIssueLinkViewAction.init(this);
//		removePersonIssueLinkViewAction.setEnabled(false);
//		toolBarManager.add(removePersonIssueLinkViewAction);
//		addNewCommentViewAction.init(this);
//		addNewCommentViewAction.setEnabled(false);
//		toolBarManager.add(addNewCommentViewAction);

		personIssueLinksComposite.getIssueTable().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				IssueLink issueLink = getSelectedIssueLink();
				boolean enabled = issueLink != null;
				addNewCommentViewAction.setEnabled(enabled);
				removePersonIssueLinkViewAction.setEnabled(enabled);
			}
		});
	}

	public PersonIssueLinkTableComposite getPersonIssueLinksComposite() {
		return personIssueLinksComposite;
	}

	@Override
	public Person getPerson() {
		return person;
	}

	@Override
	public IssueLink getSelectedIssueLink() {
		return personIssueLinksComposite.getSelectedIssueLink();
	}
}
