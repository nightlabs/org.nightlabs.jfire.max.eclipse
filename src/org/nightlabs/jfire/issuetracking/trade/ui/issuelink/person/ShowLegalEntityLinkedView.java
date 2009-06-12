package org.nightlabs.jfire.issuetracking.trade.ui.issuelink.person;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * @author Fitas Amine - fitas at nightlabs dot de
 *
 */
public class ShowLegalEntityLinkedView  extends LSDViewPart{

	private ShowLegalEntityLinkedTreeComposite showLegalEntityLinkedTreeComposite;
	
	public static final String ID_VIEW = ShowLegalEntityLinkedView.class.getName();
	
	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
	}
	
	
	@Override
	public void createPartContents(Composite parent) {
		showLegalEntityLinkedTreeComposite = new ShowLegalEntityLinkedTreeComposite(parent, SWT.NONE);
		showLegalEntityLinkedTreeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));		
		
		SelectionManager.sharedInstance().addNotificationListener(
				TradePlugin.ZONE_SALE,
				LegalEntity.class, notificationListenerPersonSelected);
	
	}
	
	private NotificationListener notificationListenerPersonSelected = new NotificationAdapterJob("") { //$NON-NLS-1$
		public void notify(NotificationEvent event) {

			if (event.getSubjects().isEmpty())
				showLegalEntityLinkedTreeComposite.setPersonID(null);
			else
				showLegalEntityLinkedTreeComposite.setPersonID((AnchorID)event.getFirstSubject());
		}
	};
	
	
	
	
	
	
	
	
	
	
}
