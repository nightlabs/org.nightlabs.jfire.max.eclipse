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

package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import java.util.Locale;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
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
import org.nightlabs.jfire.dynamictrade.store.DynamicProduct;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticlePrice;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AllocationStatusImageUtil;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleChangeEvent;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleChangeListener;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ClientArticleSegmentGroups;
import org.nightlabs.l10n.NumberFormatter;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ArticleTable
extends AbstractTableComposite
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
				return null; // ProductType.name

			if (++ci == columnIndex)
				return null; // Product.name

			if (++ci == columnIndex)
				return null; // qty
			
			if (++ci == columnIndex)
				return null; // unit
			
			if (++ci == columnIndex) {
				return AllocationStatusImageUtil.getAllocationStatusImage(article);
//				if (article.isAllocationAbandoned())
//					return SharedImages.getSharedImage(DynamictradePlugin.getDefault(), ArticleTable.class, "allocationAbandoned");
//				else if (article.isAllocationPending())
//					return SharedImages.getSharedImage(DynamictradePlugin.getDefault(), ArticleTable.class, "allocationPending");
//				else if (article.isReleaseAbandoned())
//					return SharedImages.getSharedImage(DynamictradePlugin.getDefault(), ArticleTable.class, "releaseAbandoned");
//				else if (article.isReleasePending())
//					return SharedImages.getSharedImage(DynamictradePlugin.getDefault(), ArticleTable.class, "releasePending");
//				else if (article.isAllocated())
//					return SharedImages.getSharedImage(DynamictradePlugin.getDefault(), ArticleTable.class, "allocated");
//				else
//					return SharedImages.getSharedImage(DynamictradePlugin.getDefault(), ArticleTable.class, "notAllocated");
			}

			if (!articleEdit.isInOrder() && !articleEdit.isInOffer()) {
				if (++ci == columnIndex)
					return TradePlugin.getDefault().getImageRegistry().get(TradePlugin.IMAGE_ORDER_16x16);
			}

			if (!articleEdit.isInOffer()) {
				if (++ci == columnIndex)
					return TradePlugin.getDefault().getImageRegistry().get(TradePlugin.IMAGE_OFFER_16x16);
			}

			if (!articleEdit.isInInvoice()) {
				if (++ci == columnIndex && article.getInvoiceID() != null)
					return TradePlugin.getDefault().getImageRegistry().get(TradePlugin.IMAGE_INVOICE_16x16);
			}

			if (!articleEdit.isInDeliveryNote()) {
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
			if (! (article.getProductType() instanceof DynamicProductType))
				return "article.productType is an instance of " + (article.getProductType() == null ? "null" : article.getProductType().getClass().getName()) + ", but must be a DynamicProductType!"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			DynamicProduct dynamicProduct = (DynamicProduct) article.getProduct();
			DynamicProductType dynamicProductType = (DynamicProductType) article.getProductType();

			int ci = 0;
			if (ci == columnIndex)
				return dynamicProductType.getName().getText(Locale.getDefault().getLanguage());

			if (++ci == columnIndex)
				return dynamicProduct.getName().getText(Locale.getDefault().getLanguage());

			if (++ci == columnIndex)
				return NumberFormatter.formatFloat(dynamicProduct.getQuantity(), 2);

			if (++ci == columnIndex)
				return dynamicProduct.getUnit().getSymbol().getText();

			if (++ci == columnIndex) {
				return ""; // allocationStatus is displayed using images //$NON-NLS-1$
			}

			if (!articleEdit.isInOrder() && !articleEdit.isInOffer()) {
				if (++ci == columnIndex)
					return ""; // Long.toString(article.getOrderID().orderID); //$NON-NLS-1$
			}

			if (!articleEdit.isInOffer()) {
				if (++ci == columnIndex)
					return ""; // Long.toString(article.getOfferID().offerID); //$NON-NLS-1$
			}

			if (!articleEdit.isInInvoice()) {
				if (++ci == columnIndex && article.getInvoiceID() != null)
					return ""; // Long.toString(article.getInvoiceID().invoiceID); //$NON-NLS-1$
			}

			if (!articleEdit.isInDeliveryNote()) {
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
	private ClientArticleSegmentGroups clientArticleSegmentGroups;

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
		getTable().setHeaderVisible(true);
		clientArticleSegmentGroups = ((ClientArticleSegmentGroups)articleEdit.getSegmentEdit().getArticleSegmentGroup().getArticleSegmentGroups());
		clientArticleSegmentGroups.addArticleChangeListener(articleChangeListener);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				clientArticleSegmentGroups.removeArticleChangeListener(articleChangeListener);
			}
		});
	}

	private ArticleChangeListener articleChangeListener = new ArticleChangeListener() {
		public void articlesChanged(ArticleChangeEvent articleChangeEvent) {
			if (!tableViewer.getTable().isDisposed())
				tableViewer.refresh();
//			for (Iterator it = articleChangeEvent.getArticles().iterator(); it.hasNext();) {
//				Article article = (Article) it.next();
//				
//			}
		}
	};

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{		
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleTable.productTypeNameTableColumn.text")); //$NON-NLS-1$
//		col.setToolTipText("ProductTypeName");

		col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleTable.productNameTableColumn.text")); //$NON-NLS-1$
//		col.setToolTipText("ProductName");
		
		col = new TableColumn(table, SWT.RIGHT);
		col.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleTable.quantityTableColumn.text")); //$NON-NLS-1$

		col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleTable.unitTableColumn.text")); //$NON-NLS-1$

		col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleTable.statusTableColumn.text"));		 //$NON-NLS-1$
//		col.setToolTipText("Status");
		
		//////////// BEGIN Order, Offer, Invoice, DeliveryNote //////////
		if (!articleEdit.isInOrder() && !articleEdit.isInOffer()) {
			col = new TableColumn(table, SWT.LEFT);
			col.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleTable.orderTableColumn.text"));			 //$NON-NLS-1$
//			col.setToolTipText("Order");
		}

		if (!articleEdit.isInOffer()) {
			col = new TableColumn(table, SWT.LEFT);
			col.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleTable.offerTableColumn.text"));			 //$NON-NLS-1$
//			col.setToolTipText("Offer");
		}

		if (!articleEdit.isInInvoice()) {
			col = new TableColumn(table, SWT.LEFT);
			col.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleTable.invoiceTableColumn.text"));			 //$NON-NLS-1$
//			col.setToolTipText("Invoice");			
		}

		if (!articleEdit.isInDeliveryNote()) {
			col = new TableColumn(table, SWT.LEFT);
			col.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleTable.deliveryNoteTableColumn.text")); //$NON-NLS-1$
//			col.setToolTipText("DeliveryNote");
		}
		//////////// END Order, Offer, Invoice, DeliveryNote //////////

		col = new TableColumn(table, SWT.RIGHT);
		col.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleTable.articlePriceTableColumn.text"));		 //$NON-NLS-1$

		if (articleEdit.isInOrder()) // name, allocationStatus, offer, invoice, deliveryNote, price
			table.setLayout(
					new WeightedTableLayout(
							new int[]{50, 70, 20, 20, -1, -1, -1, -1, 30},
							new int[]{-1, -1, -1, -1, 22, 22, 22, 22, -1}));
		else if (articleEdit.isInOffer())  // name, allocationStatus, invoice, deliveryNote, price
			table.setLayout(
					new WeightedTableLayout(
							new int[]{50, 70, 20, 20, -1, -1, -1, 30},
							new int[]{-1, -1, -1, -1, 22, 22, 22, -1}));
		else // Invoice || DeliveryNote: name, allocationStatus, order, offer, (invoice|deliveryNote), price
			table.setLayout(
					new WeightedTableLayout(
							new int[]{50, 70, 20, 20, -1, -1, -1, -1, 30},
							new int[]{-1, -1, -1, -1, 22, 22, 22, 22, -1}));
	}

	/**
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setContentProvider(articleContentProvider);
		tableViewer.setLabelProvider(articleLabelProvider);
	}

//	private LinkedList selectionChangedListeners = new LinkedList();
	
	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		tableViewer.addSelectionChangedListener(listener);
//		selectionChangedListeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection()
	{
		return tableViewer.getSelection();
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		tableViewer.removeSelectionChangedListener(listener);
//		selectionChangedListeners.remove(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection)
	{
		tableViewer.setSelection(selection);
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
}
