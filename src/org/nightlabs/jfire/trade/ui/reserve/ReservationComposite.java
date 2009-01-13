/**
 *
 */
package org.nightlabs.jfire.trade.ui.reserve;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.action.ISelectionAction;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleManagerRCP;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.dao.OfferDAO;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.overview.offer.action.EditOfferAction;
import org.nightlabs.jfire.trade.ui.overview.offer.action.PrintOfferAction;
import org.nightlabs.jfire.trade.ui.overview.offer.action.ShowOfferAction;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author daniel [AT] nightlabs [DOT] de
 */
public class ReservationComposite
extends XComposite
{
	class RefreshAction extends Action
	{
		private final String ID = RefreshAction.class.getName();

		public RefreshAction()
		{
			super();
			setId(ID);
			setText(Messages.getString("org.nightlabs.jfire.trade.ui.reserve.ReservationComposite.action.refresh.text")); //$NON-NLS-1$
			setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.reserve.ReservationComposite.action.refresh.tooltip")); //$NON-NLS-1$
			setImageDescriptor(SharedImages.getSharedImageDescriptor(TradePlugin.getDefault(), RefreshAction.class, "", ImageFormat.gif)); //$NON-NLS-1$
		}

		@Override
		public void run() {
			refresh();
		}
	}

	private ReservationTable reservationTable = null;
	private Form form;
	private EditOfferAction editAction;
	private ProductTypeID productTypeID;
	private Map<OfferID, Offer> offerIDToReservationOffer;

	/**
	 * @param parent
	 * @param style
	 */
	public ReservationComposite(Composite parent, int style) {
		super(parent, style);
		createComposite(this);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public ReservationComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	protected void createComposite(Composite parent)
	{
		offerIDToReservationOffer = new HashMap<OfferID, Offer>();

		form = getToolkit(true).createForm(parent);

		if (getToolkit() instanceof FormToolkit) {
			((FormToolkit)getToolkit()).decorateFormHeading(form);
		}
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
		form.getBody().setLayout(new GridLayout());
		reservationTable = new ReservationTable(form.getBody(), getBorderStyle(), true, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
		reservationTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		form.getToolBarManager().add(new RefreshAction());
		// add actions to context menu
		MenuManager menuManager = new MenuManager();
		final List<IAction> actions = createActions();
		for (IAction action : actions) {
			menuManager.add(action);
			form.getToolBarManager().add(action);
		}
		form.getToolBarManager().update(true);

		Menu contextMenu = menuManager.createContextMenu(reservationTable.getControl());
		reservationTable.getControl().setMenu(contextMenu);
		reservationTable.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateActions(actions, event.getSelection());
			}
		});

		// double click is edit
		reservationTable.addDoubleClickListener(new IDoubleClickListener(){
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (editAction != null)
					editAction.run();
			}
		});

		addNotficationListener();
		updateActions(actions, StructuredSelection.EMPTY);
	}

	protected List<IAction> createActions()
	{
		final List<IAction> actions = new ArrayList<IAction>();
		editAction = new EditOfferAction();
		actions.add(editAction);
		actions.add(new PrintOfferAction());
		actions.add(new ShowOfferAction());
		actions.add(new PayAndDeliverReservationAction());
		actions.add(new RejectReservationAction());
		return actions;
	}

	protected void updateActions(Collection<IAction> actions, ISelection selection)
	{
		for (IAction action : actions) {
			if (action instanceof ISelectionAction) {
				ISelectionAction selectionAction = (ISelectionAction) action;
				selectionAction.setSelection(selection);
				action.setEnabled(selectionAction.calculateEnabled());
			}
		}
	}

	protected void addNotficationListener()
	{
		JDOLifecycleManagerRCP.sharedInstance().addNotificationListener(Offer.class, offerListener);
		addDisposeListener(new DisposeListener(){
			@Override
			public void widgetDisposed(DisposeEvent e) {
				JDOLifecycleManagerRCP.sharedInstance().removeNotificationListener(Offer.class, offerListener);
			}
		});
	}

	public void setInput(ProductTypeID productTypeID, String title)
	{
		this.productTypeID = productTypeID;
		loadReservations();
		refreshGUI();
		form.setText(Messages.getString("org.nightlabs.jfire.trade.ui.reserve.ReservationComposite.form.text.reservationList")); //$NON-NLS-1$
		form.setMessage(title);
	}

	protected Map<OfferID, Offer> buildMap(Collection<Offer> offers)
	{
		Map<OfferID, Offer> offerIDToOffer = new HashMap<OfferID, Offer>();
		for (Offer offer : offers) {
			offerIDToOffer.put((OfferID) JDOHelper.getObjectId(offer), offer);
		}
		return offerIDToOffer;
	}

	protected void loadReservations()
	{
		if (productTypeID != null) {
			Collection<Offer> reservations = OfferDAO.sharedInstance().getReservations(productTypeID,
					ReservationTable.FETCH_GROUP_RESERVATIONS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			offerIDToReservationOffer = buildMap(reservations);
		}
	}

	protected void refreshGUI()
	{
		if (productTypeID != null && !reservationTable.isDisposed()) {
			reservationTable.setInput(offerIDToReservationOffer.values());
		}
	}

	public void refresh()
	{
		loadReservations();
		refreshGUI();
	}

	private NotificationListener offerListener = new NotificationAdapterJob(){
		@Override
		public void notify(NotificationEvent notificationEvent) {
			for (Iterator<DirtyObjectID> it = notificationEvent.getSubjects().iterator(); it.hasNext(); ) {
				DirtyObjectID dirtyObjectID = it.next();
				Set<OfferID> dirtyOfferIDs = new HashSet<OfferID>();
				if (offerIDToReservationOffer.containsKey(dirtyObjectID.getObjectID())) {
					switch (dirtyObjectID.getLifecycleState()) {
						case DIRTY:
							dirtyOfferIDs.add((OfferID)dirtyObjectID.getObjectID());
							break;
					}
				}
				if (!dirtyOfferIDs.isEmpty()) {
					List<Offer> dirtyOffers = OfferDAO.sharedInstance().getOffers(dirtyOfferIDs, ReservationTable.FETCH_GROUP_RESERVATIONS,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					for (Offer offer : dirtyOffers) {
						offerIDToReservationOffer.put((OfferID) JDOHelper.getObjectId(offer), offer);
					}
					Display display = getDisplay();
					if (!display.isDisposed()) {
						display.asyncExec(new Runnable(){
							@Override
							public void run() {
								refreshGUI();
							}
						});
					}
				}
			}
		}
	};
}
