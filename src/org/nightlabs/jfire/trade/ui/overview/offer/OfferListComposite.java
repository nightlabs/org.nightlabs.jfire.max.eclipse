package org.nightlabs.jfire.trade.ui.overview.offer;

import java.util.Comparator;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.OfferLocal;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.util.BaseComparator;

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

	public static final Comparator<Offer> OFFER_FINALIZE_DT_COMPARATOR = new Comparator<Offer>(){
		@Override
		public int compare(Offer o1, Offer o2)
		{
			int result = BaseComparator.comparatorNullCheck(o1, o2);
			if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
				int result2 = BaseComparator.comparatorNullCheck(o1.getFinalizeDT(), o2.getFinalizeDT());
				if (result2== BaseComparator.COMPARE_RESULT_NOT_NULL) {
					return o1.getFinalizeDT().compareTo(o2.getFinalizeDT());
				}
				return result2;
			}
			return result;
		}
	};

	public static final Comparator<Offer> OFFER_PRICE_COMPARATOR = new Comparator<Offer>() {
		@Override
		public int compare(Offer o1, Offer o2)
		{
			int result = BaseComparator.comparatorNullCheck(o1, o2);
			if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
				int result2 = BaseComparator.comparatorNullCheck(o1.getPrice(), o2.getPrice());
				if (result2 == BaseComparator.COMPARE_RESULT_NOT_NULL) {
					return PRICE_COMPARATOR.compare(o1.getPrice(), o2.getPrice());
				}
				return result2;
			}
			return result;
		}
	};

	public OfferListComposite(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 * @param viewerStyle
	 */
	public OfferListComposite(Composite parent, int style, boolean initTable,
			int viewerStyle) {
		super(parent, style, initTable, viewerStyle);
	}

	@Override
	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return Offer.class;
	}

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

		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.priceTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);
	}

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
					return formatDate(offer.getFinalizeDT());
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

	@Override
	protected Comparator<?> getColumnComparator(Object element, int columnIndex)
	{
		if (columnIndex == 8) {
			return OFFER_FINALIZE_DT_COMPARATOR;
		}
		else if (columnIndex == 10) {
			return OFFER_PRICE_COMPARATOR;
		}
		return super.getColumnComparator(element, columnIndex);
	}
}
