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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManagerRemote;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.security.SecurityReflector;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManagerRemote;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;

public class CombiTransferArticlesWizard extends AbstractCombiTransferWizard
{
	private Set<ArticleID> articleIDs = null;
	private List<Article> articlesToTransfer = new ArrayList<Article>();

	/**
	 * In case some of the {@link Article}s of the given articleIDs do not belong to an
	 * {@link Invoice} or {@link Delivery} (depends on the given transferMode)
	 * an Invoice or Delivery is created.
	 * Therefore it is possible to pass {@link ArticleID}s where some {@link Article}s are
	 * already contained in an Invoice or Delivery and some are not.
	 *
	 * @param articleIDs Instances of {@link ArticleID} specifying all {@link org.nightlabs.jfire.trade.ui.Article}s
	 *		that shall be paid/delivered.
	 * @param transferMode One of {@link AbstractCombiTransferWizard#TRANSFER_MODE_DELIVERY},
	 *		{@link AbstractCombiTransferWizard#TRANSFER_MODE_PAYMENT} or {@link AbstractCombiTransferWizard#TRANSFER_MODE_BOTH}.
	 */
	public CombiTransferArticlesWizard(Collection<ArticleID> articleIDs, byte transferMode)
	{
		super(transferMode);

		if (articleIDs instanceof Set)
			this.articleIDs = (Set<ArticleID>)articleIDs;
		else
			this.articleIDs = new HashSet<ArticleID>(articleIDs);

		loadData();
	}

	protected static final String[] FETCH_GROUPS_ARTICLES = new String[] {
		Order.FETCH_GROUP_CUSTOMER,
		Order.FETCH_GROUP_CUSTOMER_GROUP,
		Article.FETCH_GROUP_ORDER,
		Article.FETCH_GROUP_PRODUCT_TYPE, // for delivery
		Article.FETCH_GROUP_PRODUCT, // for delivery
		Article.FETCH_GROUP_DELIVERY_NOTE, // for delivery
		Article.FETCH_GROUP_INVOICE, // for payment
		Article.FETCH_GROUP_PRICE, // for payment
		Article.FETCH_GROUP_ARTICLE_LOCAL, // for checking
		Price.FETCH_GROUP_CURRENCY, // for payment
		ProductType.FETCH_GROUP_NAME, // for delivery
		FetchPlan.DEFAULT
	};

	protected void loadData()
	{
		try {
			articlesToTransfer.clear();

			Currency currency = null;
			AnchorID customerID = null;

			long amountToPay = 0;

			TradeManagerRemote tradeManager = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			for (Article article : tradeManager.getArticles(articleIDs, FETCH_GROUPS_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT)) {
				if (isPaymentEnabled()) {
					if (invoiceIDs == null)
						invoiceIDs = new HashSet<InvoiceID>();
					invoiceIDs.add(article.getInvoiceID());
					if (article.getInvoice() == null) {
						if (articlesToCreateInvoiceFor == null)
							articlesToCreateInvoiceFor = new HashSet<ArticleID>();
						articlesToCreateInvoiceFor.add((ArticleID) JDOHelper.getObjectId(article));
//						throw new IllegalStateException("isPaymentEnabled() && article.getInvoice() != null"); //$NON-NLS-1$
					}
				}

				if (isDeliveryEnabled()) {
					if (deliveryNoteIDs == null)
						deliveryNoteIDs = new HashSet<DeliveryNoteID>();
					deliveryNoteIDs.add(article.getDeliveryNoteID());
					if (article.getDeliveryNote() == null) {
						if (articlesToCreateDeliveryNoteFor == null)
							articlesToCreateDeliveryNoteFor = new HashSet<ArticleID>();
						articlesToCreateDeliveryNoteFor.add((ArticleID) JDOHelper.getObjectId(article));
//						throw new IllegalStateException("isDeliveryEnabled() && article.getDeliveryNote() != null"); //$NON-NLS-1$
					}
				}

				addCustomerGroupID(
						(CustomerGroupID) JDOHelper.getObjectId(article.getOrder().getCustomerGroup()));

				if (currency == null)
					currency = article.getPrice().getCurrency();
				else if (!currency.getCurrencyID().equals(article.getPrice().getCurrency().getCurrencyID()))
					throw new IllegalArgumentException("The passed Articles have differing currencies! Cannot pay!"); //$NON-NLS-1$

				if (customerID == null)
					customerID = (AnchorID) JDOHelper.getObjectId(article.getOrder().getCustomer());
				else if (!customerID.equals(JDOHelper.getObjectId(article.getOrder().getCustomer())))
					throw new IllegalArgumentException("The passed Articles have differing customers!"); //$NON-NLS-1$

				articlesToTransfer.add(article);
				amountToPay += article.getPrice().getAmount();
			}

			// The LegalEntityID of the local organisation
			AnchorID mandatorID = AnchorID.create(
					SecurityReflector.getUserDescriptor().getOrganisationID(),
					OrganisationLegalEntity.ANCHOR_TYPE_ID_LEGAL_ENTITY, OrganisationLegalEntity.class.getName());
			setCurrency(currency);
			setCustomerID(customerID);
			if (mandatorID.equals(customerID))
				setSide(Side.Customer);
			else
				setSide(Side.Vendor);

			setTotalAmount(amountToPay);

		} catch (RuntimeException x) {
			throw x;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	private Collection<ArticleID> articlesToCreateInvoiceFor = null;
	private Collection<InvoiceID> invoiceIDs = null;

	@Override
	public Collection<InvoiceID> getInvoiceIDs()
	{
		return invoiceIDs;
	}

	/**
	 * This should be set if a subclass overrides {@link #loadData()}.
	 * @param deliveryNoteIDs The invoice ids to set.
	 */
	protected void setInvoiceIDs(Collection<InvoiceID> invoiceIDs) {
		this.invoiceIDs = invoiceIDs;
	}

	private Collection<ArticleID> articlesToCreateDeliveryNoteFor = null;
	private Collection<DeliveryNoteID> deliveryNoteIDs = null;

	// TODO shouldn't this method be defined in the interface DeliveryWizard ?
	public Collection<DeliveryNoteID> getDeliveryNoteIDs()
	{
		return deliveryNoteIDs;
	}

	/**
	 * This should be set if a subclass overrides {@link #loadData()}.
	 * @param deliveryNoteIDs The delivery note ids to set.
	 */
	protected void setDeliveryNoteIDs(Collection<DeliveryNoteID> deliveryNoteIDs) {
		this.deliveryNoteIDs = deliveryNoteIDs;
	}

	@Override
	public Collection<ProductTypeID> getProductTypeIDs()
	{
		return TransferWizardUtil.getProductTypeIDs(articlesToTransfer);
	}

	@Override
	public List<Article> getArticles(Set<? extends ProductTypeID> productTypeIDs, boolean reversing)
	{
		return TransferWizardUtil.getArticles(articlesToTransfer, productTypeIDs, reversing);
	}

//	public Collection getProductIDs(Set productTypeIDs)
//	{
//		return TransferWizardUtil.getProductIDs(articlesToTransfer, productTypeIDs);
//	}

	@Override
	public Map<ProductTypeID, ProductType> getProductTypeByIDMap()
	{
		return TransferWizardUtil.getProductTypeByIDMap(articlesToTransfer);
	}

	@Override
	public boolean performFinish()
	{
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					setTransfersSuccessful(false);
					try {
						monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CombiTransferArticlesWizard.processTransfersJobMonitor.task.name"), 3); //$NON-NLS-1$
						monitor.worked(1);
						if (articlesToCreateInvoiceFor != null) {
							if ((getTransferMode() & TRANSFER_MODE_PAYMENT) != 0) {
								AccountingManagerRemote accountingManager = TransferWizardUtil.getAccountingManager();
//						 FIXME IDPREFIX (next line) should be asked from user if necessary!
								Invoice invoice = accountingManager.createInvoice(articlesToCreateInvoiceFor, null, true, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
								InvoiceID invoiceID = (InvoiceID) JDOHelper.getObjectId(invoice);
								if (invoiceIDs == null)
									invoiceIDs = new ArrayList<InvoiceID>(1);
								invoiceIDs.add(invoiceID);
							}
						}

						if (articlesToCreateDeliveryNoteFor != null) {
							if ((getTransferMode() & TRANSFER_MODE_DELIVERY) != 0) {
								StoreManagerRemote storeManager = TransferWizardUtil.getStoreManager();
//						 FIXME IDPREFIX (next line) should be asked from user if necessary!
								DeliveryNote deliveryNote = storeManager.createDeliveryNote(articlesToCreateDeliveryNoteFor, null, true, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
								DeliveryNoteID deliveryNoteID = (DeliveryNoteID) JDOHelper.getObjectId(deliveryNote);
								if (deliveryNoteIDs == null)
									deliveryNoteIDs = new HashSet<DeliveryNoteID>();
								deliveryNoteIDs.add(deliveryNoteID);
							}
						}
						monitor.worked(1);
						if (TransferWizardUtil.payAndDeliver(getShell(), CombiTransferArticlesWizard.this)) {
							setTransfersSuccessful(true);
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

	protected Set<ArticleID> getArticleIDs() {
		return articleIDs;
	}

	protected List<Article> getArticlesToTransfer() {
		return articlesToTransfer;
	}
}
