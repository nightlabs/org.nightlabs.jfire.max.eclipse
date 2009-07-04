package org.nightlabs.jfire.personrelation.issuetracking.trade.ui;

import java.util.Collection;
import java.util.Collections;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.selection.SelectionProviderProxy;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.personrelation.ui.PersonRelationTree;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationListener;

public class PersonRelationIssueTreeView
extends LSDViewPart
{
	private PersonRelationTree personRelationTree;
	private SelectionProviderProxy selectionProviderProxy = new SelectionProviderProxy();

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		getSite().setSelectionProvider(selectionProviderProxy);
	}

	@Override
	public void createPartContents(Composite parent) {
		personRelationTree = new PersonRelationTree(parent);

		SelectionManager.sharedInstance().addNotificationListener(
				TradePlugin.ZONE_SALE,
				LegalEntity.class, notificationListenerLegalEntitySelected
		);

		personRelationTree.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent event) {
				SelectionManager.sharedInstance().removeNotificationListener(
						TradePlugin.ZONE_SALE,
						LegalEntity.class, notificationListenerLegalEntitySelected
				);
			}
		});

		selectionProviderProxy.addRealSelectionProvider(personRelationTree);
	}

	private NotificationListener notificationListenerLegalEntitySelected = new NotificationAdapterJob("Selecting legal entity")
	{
		public void notify(org.nightlabs.notification.NotificationEvent notificationEvent) {
			AnchorID legalEntityID = (AnchorID) notificationEvent.getFirstSubject();
			LegalEntity legalEntity = null;
			if (legalEntityID != null) {
				legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(
						legalEntityID,
						new String[] { FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_PERSON },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						getProgressMonitor()
				);
			}

			final PropertySetID personID = (PropertySetID) (legalEntity == null ? null : JDOHelper.getObjectId(legalEntity.getPerson()));

			personRelationTree.getDisplay().asyncExec(new Runnable() {
				public void run() {
					if (personRelationTree.isDisposed())
						return;

					@SuppressWarnings("unchecked")
					Collection<PropertySetID> personIDs = (Collection<PropertySetID>) (personID != null ? Collections.singleton(personID) : Collections.emptyList());
					personRelationTree.setInputPersonIDs(personIDs);
				}
			});
		}
	};

	public PersonRelationTree getPersonRelationTree() {
		return personRelationTree;
	}
}
