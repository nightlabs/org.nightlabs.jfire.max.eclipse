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

package org.nightlabs.jfire.trade.ui.transfer.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.dao.InvoiceDAO;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.dao.DeliveryNoteDAO;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.TransferUtil;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CombiTransferArticleContainerWizard
extends AbstractCombiTransferWizard
{
	private ArticleContainer articleContainer = null;
	private Offer offer = null;
	private Order order = null;
	private Invoice invoice = null;
	private DeliveryNote deliveryNote = null;

	private ArticleContainerID articleContainerID = null;
	private OfferID offerID = null;
	private OrderID orderID = null;
	private InvoiceID invoiceID = null;
	private DeliveryNoteID deliveryNoteID = null;

	private List<Article> articlesToTransfer = new ArrayList<Article>();
	
	/**
	 * This is set after in perform finish to indicate 
	 * whether the transfers where successful.
	 * A specialized dialog is shown then, but the wizard will close.
	 */
	private boolean transfersSuccessful;

	/**
	 * @param articleContainerID Either an instance of {@link OrderID} or of {@link OfferID} or
	 *		of {@link InvoiceID} or of {@link DeliveryNoteID} specifying the <code>ArticleContainer</code>
	 *		that should be paid/delivered. Only {@link Article}s that are not yet in an {@link Invoice} will be
	 *		paid, if the referenced <code>ArticleContainer</code> is not an {@link Invoice}.
	 *		An <code>Invoice</code> will implicitely be created. Only <code>Article</code>s
	 *		that are not yet in a {@link DeliveryNote} will be delivered, if the referenced
	 *		<code>ArticleContainer</code> is not a {@link DeliveryNote}. A <code>DeliveryNote</code>
	 *		is implicitely created. If the <code>transferMode</code> is
	 *		{@link AbstractCombiTransferWizard#TRANSFER_MODE_BOTH}, then
	 *		only those <code>Article</code>s are processed, that are neither in an <code>Invoice</code>
	 *		nor a <code>DeliveryNote</code> (with the exception that if the referenced <code>ArticleContainer</code>
	 *		is an <code>Invoice</code> or a <code>DeliveryNote</code>, the <code>Articles</code> belonging to them
	 *		will of course be processed).
	 * @param transferMode One of {@link AbstractCombiTransferWizard#TRANSFER_MODE_DELIVERY},
	 *		{@link AbstractCombiTransferWizard#TRANSFER_MODE_PAYMENT} or {@link AbstractCombiTransferWizard#TRANSFER_MODE_BOTH}.
	 */
	public CombiTransferArticleContainerWizard(ArticleContainerID articleContainerID, byte transferMode)
	{
		super(transferMode);

		this.articleContainerID = articleContainerID;
		if (articleContainerID instanceof OrderID) {
			this.orderID = (OrderID) articleContainerID;
		} else if (articleContainerID instanceof OfferID) {
			this.offerID = (OfferID) articleContainerID;
		} else if (articleContainerID instanceof InvoiceID) {
			this.invoiceID = (InvoiceID) articleContainerID;
		} else if (articleContainerID instanceof DeliveryNoteID) {
			this.deliveryNoteID = (DeliveryNoteID) articleContainerID;
		} else
			throw new IllegalStateException("articleContainerID is of unknown type: " + articleContainerID.getClass().getName()); //$NON-NLS-1$

		loadData();
	}

//	/**
//	 * @param offerID The id of the {@link Offer} that should be paid/delivered. Only
//	 *		{@link Article}s that are not yet in an {@link Invoice} will be
//	 *		paid. An <code>Invoice</code> will implicitely be created. Only <code>Article</code>s
//	 *		that are not yet in a {@link DeliveryNote} will be delivered. A <code>DeliveryNote</code>
//	 *		is implicitely created. If the
//	 *		<code>transferMode</code> is {@link AbstractCombiTransferWizard#TRANSFER_MODE_BOTH}, then
//	 *		only those <code>Article</code>s are processed, that are neither in an <code>Invoice</code>
//	 *		nor a <code>DeliveryNote</code>.
//	 * @param transferMode One of {@link AbstractCombiTransferWizard#TRANSFER_MODE_DELIVERY},
//	 *		{@link AbstractCombiTransferWizard#TRANSFER_MODE_PAYMENT} or {@link AbstractCombiTransferWizard#TRANSFER_MODE_BOTH}.
//	 */
//	public CombiTransferArticleContainerWizard(OfferID offerID, byte transferMode)
//	{
//		super(transferMode);
//		this.offerID = offerID;
//		articleContainerID = offerID;
//		loadData();
//	}
//
//	/**
//	 * @param orderID The id of the {@link Order} that should be paid/delivered. Only
//	 *		{@link Article}s that are not yet in an {@link Invoice} will be
//	 *		paid. An <code>Invoice</code> will implicitely be created. Only <code>Article</code>s
//	 *		that are not yet in a {@link DeliveryNote} will be delivered. A <code>DeliveryNote</code>
//	 *		is implicitely created. If the
//	 *		<code>transferMode</code> is {@link AbstractCombiTransferWizard#TRANSFER_MODE_BOTH}, then
//	 *		only those <code>Article</code>s are processed, that are neither in an <code>Invoice</code>
//	 *		nor a <code>DeliveryNote</code>.
//	 * @param transferMode One of {@link AbstractCombiTransferWizard#TRANSFER_MODE_DELIVERY},
//	 *		{@link AbstractCombiTransferWizard#TRANSFER_MODE_PAYMENT} or {@link AbstractCombiTransferWizard#TRANSFER_MODE_BOTH}.
//	 */
//	public CombiTransferArticleContainerWizard(OrderID orderID, byte transferMode)
//	{
//		super(transferMode);
//		this.orderID = orderID;
//		articleContainerID = orderID;
//		loadData();
//	}
//
//	/**
//	 * @param deliveryNoteID The id of the {@link DeliveryNote} that should be paid/delivered. Only
//	 *		{@link Article}s that are not yet in an {@link Invoice} will be
//	 *		paid. An <code>Invoice</code> will implicitely be created.
//	 * @param transferMode One of {@link AbstractCombiTransferWizard#TRANSFER_MODE_DELIVERY},
//	 *		{@link AbstractCombiTransferWizard#TRANSFER_MODE_PAYMENT} or {@link AbstractCombiTransferWizard#TRANSFER_MODE_BOTH}.
//	 */
//	public CombiTransferArticleContainerWizard(DeliveryNoteID deliveryNoteID, byte transferMode)
//	{
//		super(transferMode);
//		this.deliveryNoteID = deliveryNoteID;
//		articleContainerID = deliveryNoteID;
//		loadData();
//	}
//
//	/**
//	 * @param invoiceID The id of the {@link Invoice} that should be paid/delivered. Only <code>Article</code>s
//	 *		that are not yet in a {@link DeliveryNote} will be delivered. A <code>DeliveryNote</code>
//	 *		is implicitely created.
//	 * @param transferMode One of {@link AbstractCombiTransferWizard#TRANSFER_MODE_DELIVERY},
//	 *		{@link AbstractCombiTransferWizard#TRANSFER_MODE_PAYMENT} or {@link AbstractCombiTransferWizard#TRANSFER_MODE_BOTH}.
//	 */
//	public CombiTransferArticleContainerWizard(InvoiceID invoiceID, byte transferMode)
//	{
//		super(transferMode);
//		this.invoiceID = invoiceID;
//		articleContainerID = invoiceID;
//		loadData();
//	}

	protected static final String[] FETCH_GROUPS_OFFER = new String[] {
		Offer.FETCH_GROUP_ORDER,
		Offer.FETCH_GROUP_CURRENCY,
		Order.FETCH_GROUP_CUSTOMER,
		Order.FETCH_GROUP_CUSTOMER_GROUP,
		Offer.FETCH_GROUP_ARTICLES,
		Article.FETCH_GROUP_PRODUCT_TYPE, // for delivery
		ProductType.FETCH_GROUP_NAME, // for delivery
		Article.FETCH_GROUP_PRODUCT, // for delivery
		Article.FETCH_GROUP_DELIVERY_NOTE_ID,
		Article.FETCH_GROUP_INVOICE_ID,
		Article.FETCH_GROUP_REVERSED_ARTICLE_ID,
//		Article.FETCH_GROUP_DELIVERY_NOTE, // for delivery
//		Article.FETCH_GROUP_INVOICE, // for payment
		Article.FETCH_GROUP_PRICE, // for payment
		Price.FETCH_GROUP_CURRENCY, // for payment
		FetchPlan.DEFAULT
	};

	protected static final String[] FETCH_GROUPS_ORDER = new String[]{
		Order.FETCH_GROUP_CURRENCY,
		Order.FETCH_GROUP_CUSTOMER,
		Order.FETCH_GROUP_CUSTOMER_GROUP,
		Order.FETCH_GROUP_ARTICLES,
		Article.FETCH_GROUP_PRODUCT_TYPE, // for delivery
		ProductType.FETCH_GROUP_NAME, // for delivery
		Article.FETCH_GROUP_PRODUCT, // for delivery
		Article.FETCH_GROUP_DELIVERY_NOTE_ID,
		Article.FETCH_GROUP_INVOICE_ID,
		Article.FETCH_GROUP_REVERSED_ARTICLE_ID,
//		Article.FETCH_GROUP_DELIVERY_NOTE, // for delivery
//		Article.FETCH_GROUP_INVOICE, // for payment
		Article.FETCH_GROUP_PRICE, // for payment
		Price.FETCH_GROUP_CURRENCY, // for payment
		FetchPlan.DEFAULT
	};

	protected static final String[] FETCH_GROUPS_INVOICE = new String[]{
		Invoice.FETCH_GROUP_CUSTOMER,
		Invoice.FETCH_GROUP_CURRENCY,
		Article.FETCH_GROUP_ORDER,
		Order.FETCH_GROUP_CUSTOMER_GROUP,
		Invoice.FETCH_GROUP_ARTICLES,
		Article.FETCH_GROUP_PRODUCT_TYPE, // for delivery
		ProductType.FETCH_GROUP_NAME, // for delivery
		Article.FETCH_GROUP_PRODUCT, // for delivery
		Article.FETCH_GROUP_DELIVERY_NOTE_ID,
		Article.FETCH_GROUP_INVOICE_ID,
		Article.FETCH_GROUP_REVERSED_ARTICLE_ID,
//		Article.FETCH_GROUP_DELIVERY_NOTE, // for delivery
//		Article.FETCH_GROUP_INVOICE, // for payment
		Article.FETCH_GROUP_PRICE, // for payment
//		ArticlePrice.FETCH_GROUP_CURRENCY, // for payment

		Invoice.FETCH_GROUP_INVOICE_LOCAL, // for finding out the amountToPay
		Invoice.FETCH_GROUP_PRICE, // for finding out the amountToPay

		FetchPlan.DEFAULT
	};

	protected static final String[] FETCH_GROUPS_DELIVERY_NOTE = new String[]{
		DeliveryNote.FETCH_GROUP_CUSTOMER,
		Article.FETCH_GROUP_ORDER,
		Order.FETCH_GROUP_CUSTOMER_GROUP,
		DeliveryNote.FETCH_GROUP_ARTICLES,
		Article.FETCH_GROUP_PRODUCT_TYPE, // for delivery
		ProductType.FETCH_GROUP_NAME, // for delivery
		Article.FETCH_GROUP_PRODUCT, // for delivery
		Article.FETCH_GROUP_DELIVERY_NOTE_ID,
		Article.FETCH_GROUP_INVOICE_ID,
		Article.FETCH_GROUP_REVERSED_ARTICLE_ID,
//		Article.FETCH_GROUP_DELIVERY_NOTE, // for delivery
//		Article.FETCH_GROUP_INVOICE, // for payment
		Article.FETCH_GROUP_PRICE, // for payment
		Price.FETCH_GROUP_CURRENCY, // for payment
		FetchPlan.DEFAULT
	};

	protected void loadData()
	{
		try {
			articlesToTransfer.clear();

			if (offerID != null) {
				TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
				offer = tradeManager.getOffer(offerID, FETCH_GROUPS_OFFER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				articleContainer = offer;

				this.setCustomerID(
						(AnchorID) JDOHelper.getObjectId(offer.getOrder().getCustomer()));

				this.addCustomerGroupID(
						(CustomerGroupID) JDOHelper.getObjectId(offer.getOrder().getCustomerGroup()));

				this.setCurrency(offer.getCurrency());

				for (Article article : articleContainer.getArticles()) {
					// If payment is enabled, we transfer only payable articles
					// If delivery is enabled, we transfer only deliverable articles
//					if ((!isPaymentEnabled() || article.getInvoiceID() == null) &&
//							(!isDeliveryEnabled() || article.getDeliveryNoteID() == null))
					if ((!isPaymentEnabled() || TransferUtil.canAddToInvoice(article)) &&
							(!isDeliveryEnabled() || TransferUtil.canAddToDeliveryNote(article)))
						articlesToTransfer.add(article);
				}
			}
			else if (orderID != null) {
				TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
				order = tradeManager.getOrder(orderID, FETCH_GROUPS_ORDER, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				articleContainer = order;

				this.setCustomerID(
						(AnchorID) JDOHelper.getObjectId(order.getCustomer()));

				this.addCustomerGroupID(
						(CustomerGroupID) JDOHelper.getObjectId(order.getCustomerGroup()));

				this.setCurrency(order.getCurrency());

				for (Article article : articleContainer.getArticles()) {
					// If payment is enabled, we transfer only the articles without invoice
					// If delivery is enabled, we transfer only the articles without delivery note
//					if ((!isPaymentEnabled() || article.getInvoiceID() == null) &&
//							(!isDeliveryEnabled() || article.getDeliveryNoteID() == null))
					if ((!isPaymentEnabled() || TransferUtil.canAddToInvoice(article)) &&
							(!isDeliveryEnabled() || TransferUtil.canAddToDeliveryNote(article)))
						articlesToTransfer.add(article);
				}
			}
			else if (invoiceID != null) {
				invoice = InvoiceDAO.sharedInstance().getInvoice(invoiceID, FETCH_GROUPS_INVOICE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()); // TODO need real progress monitor!
				articleContainer = invoice;

				this.setCustomerID(
						(AnchorID) JDOHelper.getObjectId(invoice.getCustomer()));

				this.setCurrency(invoice.getCurrency());

				for (Article article : articleContainer.getArticles()) {
					// If delivery is enabled, we transfer only articles without delivery note
					// otherwise, we transfer all articles
//					if (!isDeliveryEnabled() || article.getDeliveryNoteID() == null) {
					if (!isDeliveryEnabled() || TransferUtil.canAddToDeliveryNote(article)) {
						this.addCustomerGroupID(
								(CustomerGroupID) JDOHelper.getObjectId(article.getOrder().getCustomerGroup()));
						articlesToTransfer.add(article);
					}
				}

				this.setTotalAmount(invoice.getInvoiceLocal().getAmountToPay());
			}
			else if (deliveryNoteID != null) {
				deliveryNote = DeliveryNoteDAO.sharedInstance().getDeliveryNote(
						deliveryNoteID, FETCH_GROUPS_DELIVERY_NOTE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()); // TODO need real progress monitor!
				articleContainer = deliveryNote;

				this.setCustomerID(
						(AnchorID) JDOHelper.getObjectId(deliveryNote.getCustomer()));

				Currency currency = null;
				for (Article article : articleContainer.getArticles()) {
					// If payment is enabled, we transfer only articles without invoice
					// otherwise, we transfer all articles
//					if (!isPaymentEnabled() || article.getInvoiceID() == null) {
					if (!isPaymentEnabled() || TransferUtil.canAddToInvoice(article)) {
						this.addCustomerGroupID(
								(CustomerGroupID) JDOHelper.getObjectId(article.getOrder().getCustomerGroup()));
						articlesToTransfer.add(article);

						if (currency == null)
							currency = article.getPrice().getCurrency();
						else if (!currency.getCurrencyID().equals(article.getPrice().getCurrency().getCurrencyID()))
							throw new IllegalArgumentException("The DeliveryNote \"" + deliveryNoteID + "\" has articles with differing currencies! Cannot pay!"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}

				this.setCurrency(currency);
			}
			else
				throw new IllegalStateException("None of orderID, offerID, invoiceID or deliveryNoteID has been defined (all null)!"); //$NON-NLS-1$


			if (invoiceID == null) {
				long amountToPay = 0;
				for (Article article : articlesToTransfer) {
					amountToPay += article.getPrice().getAmount();
				}
				this.setTotalAmount(amountToPay);
			}

			// The LegalEntityID of the local organisation
			AnchorID mandatorID = AnchorID.create(
					SecurityReflector.getUserDescriptor().getOrganisationID(),
					OrganisationLegalEntity.ANCHOR_TYPE_ID_LEGAL_ENTITY, OrganisationLegalEntity.class.getName());

			if (mandatorID.equals(getCustomerID()))
				setSide(Side.Customer);
			else
				setSide(Side.Vendor);

		} catch (RuntimeException x) {
			throw x;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	@Override
	public boolean performFinish()
	{
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						transfersSuccessful = false;
						monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticleContainerWizard.performFinish.monitor.task.name"),3); //$NON-NLS-1$
						monitor.worked(1);
						if (invoiceIDs == null) {
							if ((getTransferMode() & TRANSFER_MODE_PAYMENT) != 0) {
								AccountingManager accountingManager = TransferWizardUtil.getAccountingManager();
								invoiceIDs = new ArrayList<InvoiceID>(1);
								if (invoiceID != null)
									invoiceIDs.add(invoiceID);
								else {
//								 FIXME IDPREFIX (next line) should be asked from user if necessary!
									Invoice invoice = accountingManager.createInvoice(
											articleContainerID, null,
											true, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
									InvoiceID invoiceID = (InvoiceID) JDOHelper.getObjectId(invoice);
									invoiceIDs.add(invoiceID);
								}
							}
						}

						if ((getTransferMode() & TRANSFER_MODE_DELIVERY) != 0) {
							// find out which articles still miss a DeliveryNote
							List<Article> articlesWithoutDeliveryNote = null;
							for (Article article : articleContainer.getArticles()) {
								if (article.getDeliveryNoteID() == null) {
									if (articlesWithoutDeliveryNote == null)
										articlesWithoutDeliveryNote = new LinkedList<Article>();

									articlesWithoutDeliveryNote.add(article);
								}
							}

							if (articlesWithoutDeliveryNote != null) {
								StoreManager storeManager = TransferWizardUtil.getStoreManager();
//							 FIXME IDPREFIX (next line) should be asked from user if necessary!
								storeManager.createDeliveryNote(articleContainerID, null, true, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
							}
						}
						monitor.worked(1);
//						if (deliveryNoteIDs == null) {
//							if ((getTransferMode() & TRANSFER_MODE_DELIVERY) != 0) {
//								StoreManager storeManager = TransferWizardUtil.getStoreManager();
//								deliveryNoteIDs = new ArrayList(1);
//								if (deliveryNoteID != null)
//									deliveryNoteIDs.add(deliveryNoteID);
//								else {
//									DeliveryNote deliveryNote = storeManager.createDeliveryNote(articleContainerID, true, null);
//									DeliveryNoteID deliveryNoteID = (DeliveryNoteID) JDOHelper.getObjectId(deliveryNote);
//									deliveryNoteIDs.add(deliveryNoteID);
//								}
//							}
//						}

						if (TransferWizardUtil.payAndDeliver(getShell(), CombiTransferArticleContainerWizard.this)) {
							// set the successful flag only if the transfers could be created
							transfersSuccessful = true;
						}
						monitor.worked(1);
					} catch (RuntimeException x) {
						throw x;
					} catch (Exception x) {
						throw new RuntimeException(x);
					}
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

//	/**
//	 * @return Returns the offer.
//	 */
//	public Offer getOffer()
//	{
//		return offer;
//	}

	public Collection<ProductTypeID> getProductTypeIDs()
	{
		return TransferWizardUtil.getProductTypeIDs(articlesToTransfer);
	}

	public Map<ProductTypeID, ProductType> getProductTypeByIDMap()
	{
		return TransferWizardUtil.getProductTypeByIDMap(articlesToTransfer);
	}

	private Collection<InvoiceID> invoiceIDs = null;

	public Collection<InvoiceID> getInvoiceIDs()
	{
		return invoiceIDs;
	}

//	private Collection deliveryNoteIDs = null;
//
//	public Collection getDeliveryNoteIDs()
//	{
//		return deliveryNoteIDs;
//	}

	public List<Article> getArticles(Set<? extends ProductTypeID> productTypeIDs, boolean reversing)
	{
		return TransferWizardUtil.getArticles(articlesToTransfer, productTypeIDs, reversing);
	}

	/**
	 * This is set before the wizard closes and indicates 
	 * whether the transfers could be successfully created.
	 * In case of an error the wizard will show an error
	 * but still close, then this flag will be <code>false</code>.
	 * @return <code>true</code> if the transfers have been created successfully, <code>false</code> otherwise.
	 */
	public boolean isTransfersSuccessful() {
		return transfersSuccessful;
	}
	
//	public Collection getProductIDs(Set productTypeIDs)
//	{
//		return TransferWizardUtil.getProductIDs(articlesToTransfer, productTypeIDs);
//
////		// we return all products of the offer matching the given productTypeIDs
////		Collection res = new ArrayList();
////		for (Iterator it = articlesToTransfer.iterator(); it.hasNext(); ) {
////			Article article = (Article) it.next();
////			ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(
////					article.getProductType());
////
////			if (productTypeIDs.contains(productTypeID))
////				res.add(JDOHelper.getObjectId(article.getProduct()));
////		}
////		return res;
//	}
}
