/**
 *
 */
package org.nightlabs.jfire.trade.ui.reserve;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.AbstractJDOQuery;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.dao.OfferDAO;
import org.nightlabs.jfire.trade.query.OfferQuery;
import org.nightlabs.jfire.trade.ui.overview.action.AbstractArticleContainerAction;
import org.nightlabs.jfire.trade.ui.overview.offer.action.EditOfferAction;
import org.nightlabs.jfire.trade.ui.overview.offer.action.PrintOfferAction;
import org.nightlabs.jfire.trade.ui.overview.offer.action.ShowOfferAction;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author daniel
 *
 */
public class ReservationComposite
extends XComposite
{
	private ReservationTable reservationTable = null;
	private Form form;
//	private Button refreshButton;

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

	private void createComposite(Composite parent)
	{
		form = getToolkit(true).createForm(parent);
		if (getToolkit() instanceof FormToolkit) {
			((FormToolkit)getToolkit()).decorateFormHeading(form);
		}
		form.setLayoutData(new GridData(GridData.FILL_BOTH));
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
		form.getBody().setLayout(new GridLayout());
		reservationTable = new ReservationTable(form.getBody(), getBorderStyle());
		reservationTable.setLayoutData(new GridData(GridData.FILL_BOTH));

		MenuManager menuManager = new MenuManager();
		final List<AbstractArticleContainerAction> actions = new ArrayList<AbstractArticleContainerAction>();
		actions.add(new EditOfferAction());
		actions.add(new PrintOfferAction());
		actions.add(new ShowOfferAction());
		for (AbstractArticleContainerAction action : actions) {
			menuManager.add(action);
		}
		Menu contextMenu = menuManager.createContextMenu(reservationTable.getControl());
		reservationTable.getControl().setMenu(contextMenu);

		reservationTable.addSelectionChangedListener(new ISelectionChangedListener(){
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				for (AbstractArticleContainerAction action : actions) {
					action.setSelection(event.getSelection());
				}
			}
		});
	}

	private void loadReservations(ProductTypeID productTypeID)
	{
		QueryCollection<AbstractJDOQuery> queryCollection = new QueryCollection<AbstractJDOQuery>(Offer.class);
		OfferQuery offerQuery = new OfferQuery();
		offerQuery.setReserved(true);
		offerQuery.setProductTypeID(productTypeID);
		queryCollection.add(offerQuery);
		try {
			Collection<Offer> offers = OfferDAO.sharedInstance().getOffersByQuery(
					queryCollection, ReservationTable.FETCH_GROUP_RESERVATIONS,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
			reservationTable.setInput(offers);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public void setInput(ProductTypeID productTypeID, String title)
	{
		loadReservations(productTypeID);
		form.setText(Messages.getString("org.nightlabs.jfire.trade.ui.reserve.ReservationComposite.form.text.reservationList")); //$NON-NLS-1$
		form.setMessage(title);
	}
}
