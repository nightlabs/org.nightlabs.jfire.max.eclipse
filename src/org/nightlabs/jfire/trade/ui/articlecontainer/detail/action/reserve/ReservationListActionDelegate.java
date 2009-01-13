/**
 *
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reserve;

import java.util.Collection;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.LoginStateChangeEvent;
import org.nightlabs.jfire.base.ui.login.action.LSDWorkbenchWindowActionDelegate;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.reserve.ReservationEditor;
import org.nightlabs.jfire.trade.ui.reserve.ReservationEditorInput;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * @author daniel
 *
 */
public class ReservationListActionDelegate extends LSDWorkbenchWindowActionDelegate {

	private InternalReservationListActionDelegate delegate = null;

	/**
	 * Internal delegate that is required, because SeatHistoryActionDelegate is loaded already
	 * before we are logged-in. Therefore, it is impossible to declare e.g. the field <code>ProductType eventID</code>
	 * which leads to deferred <code>NoClassDefFoundError</code>s.
	 */
	private class InternalReservationListActionDelegate
	{
		private ProductTypeID productTypeID = null;
		private IAction action = null;

		public void run(IAction action)
		{
			if (productTypeID != null)
			{
				try {
//					Editor2PerspectiveRegistry.sharedInstance().openEditor(
//							new ReservationEditorInput(productTypeID),
//							ReservationEditor.EDITOR_ID);
					RCPUtil.openEditor(new ReservationEditorInput(productTypeID),
							ReservationEditor.EDITOR_ID);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		private final NotificationListener selectionListener = new NotificationAdapterCallerThread(){
			@SuppressWarnings("unchecked") //$NON-NLS-1$
			public void notify(NotificationEvent notificationEvent) {
				Set<Object> subjects = notificationEvent.getSubjects();
				setSelection(new StructuredSelection(subjects));
			}
		};

		@SuppressWarnings("unchecked") //$NON-NLS-1$
		public void setSelection(ISelection selection) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				if (structuredSelection.size() == 1) {
					Object firstElement = structuredSelection.getFirstElement();
					if (firstElement instanceof ProductTypeID) {
						productTypeID = (ProductTypeID) firstElement;
					}
					else if (firstElement instanceof Collection) {
						Collection collection = (Collection) firstElement;
						if (!collection.isEmpty()) {
							Object firstEntry = collection.iterator().next();
							if (firstEntry instanceof ProductTypeID) {
								productTypeID = (ProductTypeID) firstEntry;
							}
						}
					}
				}
			}
			if (action != null) {
				if (productTypeID != null) {
					Class<?> clazz = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(productTypeID);
					if (ProductType.class.isAssignableFrom(clazz)) {
						action.setEnabled(true);
						return;
					}
				}
				action.setEnabled(false);
			}
		}

		public void selectionChanged(IAction action, ISelection selection) {
			this.action = action;
			setSelection(selection);
		}

		public void loginStateChanged(LoginStateChangeEvent event)
		{
			if (event.getNewLoginState().equals(LoginState.LOGGED_OUT)) {
				productTypeID = null;
				SelectionManager.sharedInstance().removeNotificationListener(TradePlugin.ZONE_SALE,
						ProductTypeID.class, selectionListener);
			}
			if (event.getNewLoginState().equals(LoginState.LOGGED_IN)) {
				// To listen for changes from outside (e.g. ProductTypeQuickListView)
				SelectionManager.sharedInstance().addNotificationListener(TradePlugin.ZONE_SALE,
						ProductTypeID.class, selectionListener);
			}
		}

		public void dispose() {
			SelectionManager.sharedInstance().removeNotificationListener(TradePlugin.ZONE_SALE,
					ProductTypeID.class, selectionListener);
		}
	}

	private InternalReservationListActionDelegate getDelegate() {
		if (delegate == null && Login.isLoggedIn())
			delegate = new InternalReservationListActionDelegate();

		return delegate;
	}

	@Override
	public void run(IAction action) {
		if (getDelegate() != null)
			getDelegate().run(action);
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		super.selectionChanged(action, selection);
		if (getDelegate() != null)
			getDelegate().selectionChanged(action, selection);
	}

	@Override
	public void loginStateChanged(LoginStateChangeEvent event)
	{
		super.loginStateChanged(event);
		if (getDelegate() != null)
			getDelegate().loginStateChanged(event);
	}

	@Override
	public void init(IWorkbenchWindow window) {
		super.init(window);
//		// To listen for changes from outside (e.g. ProductTypeQuickListView)
//		SelectionManager.sharedInstance().addNotificationListener(TradePlugin.ZONE_SALE,
//				ProductTypeID.class, selectionListener);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (getDelegate() != null)
			getDelegate().dispose();
	}


}
