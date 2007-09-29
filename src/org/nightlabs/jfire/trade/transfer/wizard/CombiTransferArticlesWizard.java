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

package org.nightlabs.jfire.trade.transfer.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;

public class CombiTransferArticlesWizard extends AbstractCombiTransferWizard
{
	private Set<ArticleID> articleIDs = null;
	private List<Article> articlesToTransfer = new ArrayList<Article>();

	/**
	 * @param articleIDs Instances of {@link ArticleID} specifying all {@link org.nightlabs.jfire.trade.Article}s
	 *		that shall be paid/delivered.
	 * @param transferMode One of {@link AbstractCombiTransferWizard#TRANSFER_MODE_DELIVERY},
	 *		{@link AbstractCombiTransferWizard#TRANSFER_MODE_PAYMENT} or {@link AbstractCombiTransferWizard#TRANSFER_MODE_BOTH}.
	 * @param side Specifying whether we (the local organisation) is the vendor or the customer.
	 */
	public CombiTransferArticlesWizard(Collection articleIDs, byte transferMode, Side side)
	{
		super(transferMode, side);

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

			TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
			for (Iterator it = tradeManager.getArticles(articleIDs, FETCH_GROUPS_ARTICLES, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT).iterator(); it.hasNext(); ) {
				Article article = (Article) it.next();
				if (isPaymentEnabled() && article.getInvoice() != null)
					throw new IllegalStateException("isPaymentEnabled() && article.getInvoice() != null"); //$NON-NLS-1$

				if (isDeliveryEnabled() && article.getDeliveryNote() != null)
					throw new IllegalStateException("isDeliveryEnabled() && article.getDeliveryNote() != null"); //$NON-NLS-1$

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

			setCurrency(currency);
			setCustomerID(customerID);
			setTotalAmount(amountToPay);

		} catch (RuntimeException x) {
			throw x;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
	
	private Collection invoiceIDs = null;

	public Collection getInvoiceIDs()
	{
		return invoiceIDs;
	}

	private Collection deliveryNoteIDs = null;

	public Collection getDeliveryNoteIDs()
	{
		return deliveryNoteIDs;
	}

	public Collection getProductTypeIDs()
	{
		return TransferWizardUtil.getProductTypeIDs(articlesToTransfer);
	}

	public List getArticles(Set productTypeIDs, boolean reversing)
	{
		return TransferWizardUtil.getArticles(articlesToTransfer, productTypeIDs, reversing);
	}

//	public Collection getProductIDs(Set productTypeIDs)
//	{
//		return TransferWizardUtil.getProductIDs(articlesToTransfer, productTypeIDs);
//	}

	public Map getProductTypeByIDMap()
	{
		return TransferWizardUtil.getProductTypeByIDMap(articlesToTransfer);
	}

	public boolean performFinish()
	{
		try {
			getContainer().run(false, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {						
						monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.transfer.wizard.CombiTransferArticlesWizard.processTransfersJobMonitor.task.name"), 3); //$NON-NLS-1$
						monitor.worked(1);
						if (invoiceIDs == null) {
							if ((getTransferMode() & TRANSFER_MODE_PAYMENT) != 0) {
								AccountingManager accountingManager = TransferWizardUtil.getAccountingManager();
								invoiceIDs = new ArrayList(1);
//						 FIXME IDPREFIX (next line) should be asked from user if necessary!
								Invoice invoice = accountingManager.createInvoice(articleIDs, null, true, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
								InvoiceID invoiceID = (InvoiceID) JDOHelper.getObjectId(invoice);
								invoiceIDs.add(invoiceID);
							}
						}

						if (deliveryNoteIDs == null) {
							if ((getTransferMode() & TRANSFER_MODE_DELIVERY) != 0) {
								StoreManager storeManager = TransferWizardUtil.getStoreManager();
								deliveryNoteIDs = new ArrayList(1);
//						 FIXME IDPREFIX (next line) should be asked from user if necessary!
								DeliveryNote deliveryNote = storeManager.createDeliveryNote(articleIDs, null, true, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
								DeliveryNoteID deliveryNoteID = (DeliveryNoteID) JDOHelper.getObjectId(deliveryNote);
								deliveryNoteIDs.add(deliveryNoteID);
							}
						}
						monitor.worked(1);
						if (!TransferWizardUtil.payAndDeliver(getShell(), CombiTransferArticlesWizard.this)) {
							// the TransferWizardUtil already shows a specialised ErrorDialog
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
