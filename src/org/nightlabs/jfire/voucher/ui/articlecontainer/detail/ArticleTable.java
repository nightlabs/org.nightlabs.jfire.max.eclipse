/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.voucher.ui.articlecontainer.detail;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticlePrice;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AllocationStatusImageUtil;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleChangeEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleChangeListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ClientArticleSegmentGroupSet;
import org.nightlabs.jfire.voucher.store.Voucher;
import org.nightlabs.jfire.voucher.store.VoucherKey;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.jfire.voucher.ui.detail.VoucherKeyDetailComposite;
import org.nightlabs.jfire.voucher.ui.resource.Messages;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.util.NLLocale;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ArticleTable
extends AbstractTableComposite<Article>
implements ISelectionProvider
{
	protected static class ArticleContentProvider
	implements IStructuredContentProvider
	{
		private ArticleEdit articleEdit;

		public ArticleContentProvider(ArticleEdit articleEdit)
		{
			this.articleEdit = articleEdit;
		}

		public Object[] getElements(Object inputElement)
		{
			return articleEdit.getArticles().toArray();
		}

		public void dispose()
		{
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{
		}
	}

	private static String getValidity(VoucherKey voucherKey)
	{
		return
				VoucherKeyDetailComposite.getValidity(voucherKey) +
				(VoucherKey.VALIDITY_REDEEMED_COMPLETELY != voucherKey.getValidity() && voucherKey.isRedemptionExisting() ? (' '+Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleTable.validitySuffix_partiallyRedeemed")) : ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected class ArticleLabelProvider
	extends LabelProvider
	implements ITableLabelProvider
	{
		public Image getColumnImage(Object element, int columnIndex)
		{
			if (! (element instanceof Article))
				return null;

			Article article = (Article) element;

			int ci = 0;
			if (ci == columnIndex)
				return null; // name

			if (++ci == columnIndex)
				return null; // voucherKey

			if (++ci == columnIndex)
				return null; // validity

			if (++ci == columnIndex) {
				return AllocationStatusImageUtil.getAllocationStatusImage(article);
			}

			if (!isInOrder() && !isInOffer()) {
				if (++ci == columnIndex)
					return TradePlugin.getDefault().getImageRegistry().get(TradePlugin.IMAGE_ORDER_16x16);
			}

			if (!isInOffer()) {
				if (++ci == columnIndex)
					return TradePlugin.getDefault().getImageRegistry().get(TradePlugin.IMAGE_OFFER_16x16);
			}

			if (!isInInvoice()) {
				if (++ci == columnIndex && article.getInvoiceID() != null)
					return TradePlugin.getDefault().getImageRegistry().get(TradePlugin.IMAGE_INVOICE_16x16);
			}

			if (!isInDeliveryNote()) {
				if (++ci == columnIndex && article.getDeliveryNoteID() != null)
					return TradePlugin.getDefault().getImageRegistry().get(TradePlugin.IMAGE_DELIVERY_NOTE_16x16);
			}

			return null;
		}

		public String getColumnText(Object element, int columnIndex)
		{
			if (! (element instanceof Article))
				return "Invalid element! Must be Article, but is " + (element == null ? "null" : element.getClass().getName()); //$NON-NLS-1$ //$NON-NLS-2$

			Article article = (Article) element;
			if (! (article.getProductType() instanceof VoucherType))
				return "article.productType is an instance of " + (article.getProductType() == null ? "null" : article.getProductType().getClass().getName()) + ", but must be a VoucherType!"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			VoucherType simpleProductType = (VoucherType) article.getProductType();
			Voucher voucher = (Voucher) article.getProduct();

			int ci = 0;
			if (ci == columnIndex)
				return simpleProductType.getName().getText(NLLocale.getDefault().getLanguage());

			if (++ci == columnIndex)
				return voucher.getVoucherKey() == null ? "" : voucher.getVoucherKey().getVoucherKey(); //$NON-NLS-1$

			if (++ci == columnIndex)
				return voucher.getVoucherKey() == null ? "" : getValidity(voucher.getVoucherKey()); //$NON-NLS-1$

			if (++ci == columnIndex) {
				return ""; // allocationStatus is displayed using images //$NON-NLS-1$
			}

			if (!isInOrder() && !isInOffer()) {
				if (++ci == columnIndex)
					return ""; // Long.toString(article.getOrderID().orderID); //$NON-NLS-1$
			}

			if (!isInOffer()) {
				if (++ci == columnIndex)
					return ""; // Long.toString(article.getOfferID().offerID); //$NON-NLS-1$
			}

			if (!isInInvoice()) {
				if (++ci == columnIndex && article.getInvoiceID() != null)
					return ""; // Long.toString(article.getInvoiceID().invoiceID); //$NON-NLS-1$
			}

			if (!isInDeliveryNote()) {
				if (++ci == columnIndex && article.getDeliveryNoteID() != null)
					return ""; // Long.toString(article.getDeliveryNoteID().deliveryNoteID); //$NON-NLS-1$
			}

			if (++ci == columnIndex) {
				ArticlePrice price = article.getPrice();
				return NumberFormatter.formatCurrency(price.getAmount(), price.getCurrency());
			}

			return ""; //$NON-NLS-1$
		}
	}

	private ArticleEdit articleEdit;
	private ArticleContentProvider articleContentProvider;
	private ArticleLabelProvider articleLabelProvider;
	private ClientArticleSegmentGroupSet clientArticleSegmentGroupSet;

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public ArticleTable(Composite parent, int style, ArticleEdit articleEdit)
	{
		super(parent, style, false);
		this.articleEdit = articleEdit;
		this.articleContentProvider = new ArticleContentProvider(articleEdit);
		this.articleLabelProvider = new ArticleLabelProvider();
		initTable();
		setHeaderVisible(true);
		clientArticleSegmentGroupSet = ((ClientArticleSegmentGroupSet)articleEdit.getSegmentEdit().getArticleSegmentGroup().getArticleSegmentGroups());
		clientArticleSegmentGroupSet.addArticleChangeListener(articleChangeListener);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				clientArticleSegmentGroupSet.removeArticleChangeListener(articleChangeListener);
			}
		});
	}

	private ArticleChangeListener articleChangeListener = new ArticleChangeListener() {
		public void articlesChanged(ArticleChangeEvent articleChangeEvent) {
			if (getTableViewer() != null && !getTable().isDisposed())
				refresh();
		}
	};

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleTable.nameTableColumn.text")); //$NON-NLS-1$
//		col.setToolTipText("Name of the voucher");

		col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleTable.keyTableColumn.text")); //$NON-NLS-1$
//		col.setToolTipText("Key (after delivery)");

		col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleTable.validityTableColumn.text")); //$NON-NLS-1$
//		col.setToolTipText("Validity");

		col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleTable.allocationStatusTableColumn.text"));		 //$NON-NLS-1$
//		col.setToolTipText("Status of the allocation");

		//////////// BEGIN Order, Offer, Invoice, DeliveryNote //////////
		if (!isInOrder() && !isInOffer()) {
			col = new TableColumn(table, SWT.LEFT);
			col.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleTable.orderTableColumn.text"));			 //$NON-NLS-1$
//			col.setToolTipText("Order");
		}

		if (!isInOffer()) {
			col = new TableColumn(table, SWT.LEFT);
			col.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleTable.offerTableColumn.text"));			 //$NON-NLS-1$
//			col.setToolTipText("Offer");
		}

		if (!isInInvoice()) {
			col = new TableColumn(table, SWT.LEFT);
			col.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleTable.invoiceTableColumn.text"));			 //$NON-NLS-1$
//			col.setToolTipText("Invoice");
		}

		if (!isInDeliveryNote()) {
			col = new TableColumn(table, SWT.LEFT);
			col.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleTable.deliveryNoteTableColumn.text")); //$NON-NLS-1$
//			col.setToolTipText("Delivery Note");
		}
		//////////// END Order, Offer, Invoice, DeliveryNote //////////

		TableColumn columnPrice = new TableColumn(table, SWT.RIGHT);
		columnPrice.setText(Messages.getString("org.nightlabs.jfire.voucher.ui.articlecontainer.detail.ArticleTable.priceTableColumn.text"));		 //$NON-NLS-1$

		if (isInOrder()) // name, allocationStatus, offer, invoice, deliveryNote, price
			table.setLayout(
					new WeightedTableLayout(
							new int[]{80, 40, 40, -1, -1, -1, -1, 30},
							new int[]{-1, -1, -1, 22, 22, 22, 22, -1}));
		else if (isInOffer())  // name, allocationStatus, invoice, deliveryNote, price
			table.setLayout(
					new WeightedTableLayout(
							new int[]{80, 40, 40, -1, -1, -1, 30},
							new int[]{-1, -1, -1, 22, 22, 22, -1}));
		else // Invoice || DeliveryNote: name, allocationStatus, order, offer, (invoice|deliveryNote), price
			table.setLayout(
					new WeightedTableLayout(
							new int[]{80, 40, 40, -1, -1, -1, -1, 30},
							new int[]{-1, -1, -1, 22, 22, 22, 22, -1}));
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(articleContentProvider);
		tableViewer.setLabelProvider(articleLabelProvider);
	}

	@Override
	public void setMenu(Menu menu)
	{
		super.setMenu(menu);
		Control[] children = getChildren();
		for (int i = 0; i < children.length; ++i) {
			children[i].setMenu(menu);
		}
	}
	
	private boolean isInOrder()
	{
		return articleEdit.getSegmentEdit().getArticleContainer() instanceof Order;
	}

	private boolean isInOffer()
	{
		return articleEdit.getSegmentEdit().getArticleContainer() instanceof Offer;
	}
	
	private boolean isInInvoice()
	{
		return articleEdit.getSegmentEdit().getArticleContainer() instanceof Invoice;
	}
	
	private boolean isInDeliveryNote()
	{
		return articleEdit.getSegmentEdit().getArticleContainer() instanceof DeliveryNote;
	}	
}
