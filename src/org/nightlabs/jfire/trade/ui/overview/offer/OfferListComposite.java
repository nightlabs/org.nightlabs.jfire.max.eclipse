package org.nightlabs.jfire.trade.ui.overview.offer;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.l10n.NumberFormatter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class OfferListComposite
extends AbstractArticleContainerListComposite
{
	public OfferListComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return Offer.class;
	}

	@Implement
	@Override
	protected void createArticleContainerIDPrefixTableColumn(
			TableViewer tableViewer, Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.offerIDPrefixTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));
	}

	@Implement
	@Override
	protected void createArticleContainerIDTableColumn(TableViewer tableViewer,
			Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.offerIDTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));
	}

	@Implement
	@Override
	protected void createAdditionalTableColumns(TableViewer tableViewer,
			Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.finalizeDateTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));
		
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.finalizeUserTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite.priceTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));
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
