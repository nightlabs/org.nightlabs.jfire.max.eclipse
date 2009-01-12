package org.nightlabs.jfire.trade.ui.overview.offer;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.OfferLocal;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.l10n.NumberFormatter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public class OfferListComposite
extends AbstractArticleContainerListComposite<Offer>
{
	/**
	 * The fetch-groups this list composite needs to display offers.
	 */
	public static final String[] FETCH_GROUPS_OFFER = {
		FetchPlan.DEFAULT,
		Offer.FETCH_GROUP_CREATE_USER,
		Offer.FETCH_GROUP_CUSTOMER,
		Offer.FETCH_GROUP_VENDOR,
		Offer.FETCH_GROUP_CURRENCY,
		Offer.FETCH_GROUP_PRICE,
		Offer.FETCH_GROUP_FINALIZE_USER,
		Offer.FETCH_GROUP_OFFER_LOCAL,
		Offer.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON,
		OfferLocal.FETCH_GROUP_THIS_OFFER_LOCAL
	};

	public OfferListComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return Offer.class;
	}

//	@Override
//	protected void createArticleContainerIDPrefixTableColumn(
//			TableViewer tableViewer, Table table)
//	{
//		TableColumn tc = new TableColumn(table, SWT.LEFT);
//		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.offerIDPrefixTableColumn.text")); //$NON-NLS-1$
////		tableLayout.setColumnData(tc, new ColumnWeightData(10));
//		addWeightedColumn(10);
//	}
//
//	@Override
//	protected void createArticleContainerIDTableColumn(TableViewer tableViewer,
//			Table table)
//	{
//		TableColumn tc = new TableColumn(table, SWT.RIGHT);
//		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.offerIDTableColumn.text")); //$NON-NLS-1$
////		tableLayout.setColumnData(tc, new ColumnWeightData(10));
//		addWeightedColumn(10);
//	}

	@Implement
	@Override
	protected void createAdditionalTableColumns(TableViewer tableViewer,
			Table table)
	{
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.finalizeDateTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.finalizeUserTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.priceTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);
	}

	@Implement
	@Override
	protected String getAdditionalColumnText(Object element,
			int additionalColumnIndex, int firstAdditionalColumnIndex, int columnIndex)
	{
		if (!(element instanceof Offer))
			return ""; //$NON-NLS-1$

		Offer offer = (Offer) element;
		switch (additionalColumnIndex) {
			case 0:
				if (offer.getFinalizeDT() != null)
					return DateFormatter.formatDateShort(offer.getFinalizeDT(), false);
			break;
			case 1:
				if (offer.getFinalizeUser() != null)
					return offer.getFinalizeUser().getName();
			break;
			case 2:
				if (offer.getPrice() != null && offer.getCurrency() != null)
					return NumberFormatter.formatCurrency(offer.getPrice().getAmount(), offer.getCurrency());
			break;
		}

		return ""; //$NON-NLS-1$
	}
}
