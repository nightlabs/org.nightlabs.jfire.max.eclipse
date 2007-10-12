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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.CreateException;
import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.ModuleException;
import org.nightlabs.datastructure.Pair;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavourName;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour.ModeOfDeliveryFlavourProductTypeGroup;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour.ModeOfDeliveryFlavourProductTypeGroupCarrier;
import org.nightlabs.jfire.store.deliver.id.ModeOfDeliveryFlavourID;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.transfer.TransferCoordinator;
import org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor;
import org.nightlabs.jfire.trade.ui.transfer.error.ErrorDialog;
import org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class TransferWizardUtil
{
		protected TransferWizardUtil() { }
//
//	private static AccountingManager accountingManager = null;
//
//	private static StoreManager storeManager = null;

	public static AccountingManager getAccountingManager()
	throws RemoteException, LoginException, CreateException, NamingException
	{
		return AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//		
//		if (accountingManager == null)
//			accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//
//		return accountingManager;
	}
	
	public static StoreManager getStoreManager()
	throws RemoteException, LoginException, CreateException, NamingException
	{
		return StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//		
//		if (storeManager == null)
//			storeManager = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//
//		return storeManager;
	}
	
	/**
	 * This method calls
	 * {@link #payAndDeliver(Shell, CombiTransferWizard, PaymentWizard, DeliveryWizard)}
	 * with <tt>transferWizard</tt>, <tt>deliveryWizard</tt> and
	 * <tt>deliveryNoteIDs</tt> being <tt>null</tt>.
	 * @param shell TODO
	 */
	public static boolean pay(Shell shell, PaymentWizard paymentWizard)
	throws RemoteException, LoginException, CreateException, NamingException, ModuleException
	{
		return payAndDeliver(shell, (CombiTransferWizard)null, paymentWizard, (DeliveryWizard)null);
//				bookInvoiceMode);
	}
	
	/**
	 * This method calls
	 * {@link #payAndDeliver(CombiTransferWizard, PaymentWizard, DeliveryWizard, byte)}
	 * with <tt>transferWizard</tt>, <tt>paymentWizard</tt> and
	 * <tt>invoiceIDs</tt> being <tt>null</tt>.
	 * @param shell TODO
	 */
	public static boolean deliver(Shell shell, DeliveryWizard deliveryWizard)
	throws RemoteException, LoginException, CreateException, NamingException, ModuleException
	{
		return payAndDeliver(shell, (CombiTransferWizard)null, (PaymentWizard)null, deliveryWizard);
	}
	

//	/**
//	 * When paying using one of the pay/transfer methods, you can specify if and when
//	 * the invoices shall be booked. Of course, this has no consequence, if your payment
//	 * is not related with invoices.
//	 * <ul>
//	 *  <li>
//	 *   {@link #BOOK_INVOICE_MODE_NO} specifies that no booking shall occur.
//	 *  </li>
//	 *  <li>
//	 *   {@link #BOOK_INVOICE_MODE_BEFORE_PAYMENT} indicates that the booking shall be done before the payment.
//	 *  </li>
//	 *  <li>
//	 *   {@link #BOOK_INVOICE_MODE_AFTER_SUCCESSFUL_PAYMENT} indicates that the booking shall be done after the payment - if it was successful.
//	 *  </li>
//	 * </ul>
//	 */
//	public static final byte BOOK_INVOICE_MODE_NO = 0;
//	/**
//	 * @see #BOOK_INVOICE_MODE_NO
//	 */
//	public static final byte BOOK_INVOICE_MODE_BEFORE_PAYMENT = 1;
//	/**
//	 * @see #BOOK_INVOICE_MODE_NO
//	 */
//	public static final byte BOOK_INVOICE_MODE_AFTER_SUCCESSFUL_PAYMENT = 2;

//	public static boolean payAndDeliver(
//			PaymentWizard paymentWizard, DeliveryWizard deliveryWizard,
//			byte bookInvoiceMode)
//	throws RemoteException, LoginException, CreateException, NamingException, ModuleException
//	{
//		return payAndDeliver((CombiTransferWizard)null, paymentWizard, deliveryWizard, bookInvoiceMode);
//	}
//
	public static boolean payAndDeliver(Shell shell, CombiTransferWizard transferWizard)
//			byte bookInvoiceMode)
	throws RemoteException, LoginException, CreateException, NamingException, ModuleException
	{
		return payAndDeliver(shell, transferWizard, (PaymentWizard)null, (DeliveryWizard)null); //, bookInvoiceMode);
	}
	
	
	/**
	 * This method is able to either perform an isolated payment or an isolated delivery
	 * or to combine both. The combination has the advantage that the 4 phases of each will
	 * be merged and if an error occurs, both payment and delivery will be rolled back.
	 * @param shell TODO
	 * @param transferWizard A combined wizard - leave <tt>paymentWizard</tt> and <tt>deliveryWizard</tt> <tt>null</tt>.
	 * @param paymentWizard Specify this if only a payment shall be done and leave <tt>transferWizard</tt> and <tt>deliveryWizard</tt> <tt>null</tt>.
	 * @param deliveryWizard Specify this if only a delivery shall be done and leave <tt>transferWizard</tt> and <tt>paymentWizard</tt> <tt>null</tt>.
	 *
	 * @return Returns <tt>true</tt> if the payment/delivery was successful, <tt>false</tt> if
	 *		it somehow failed. You can get specific error information out of the {@link Payment} or {@link org.nightlabs.jfire.store.deliver.Delivery} object.
	 *
	 * @see #pay(Shell, PaymentWizard)
	 * @see #deliver(Shell, DeliveryWizard)
	 */
	public static boolean payAndDeliver(Shell shell, CombiTransferWizard transferWizard, PaymentWizard paymentWizard, DeliveryWizard deliveryWizard)
	throws RemoteException, LoginException, CreateException, NamingException, ModuleException {
		if (transferWizard != null) {
			paymentWizard = transferWizard;
			deliveryWizard = transferWizard;
		}

		if (deliveryWizard != null && !deliveryWizard.isDeliveryEnabled())
			deliveryWizard = null;

		if (paymentWizard != null && !paymentWizard.isPaymentEnabled())
			paymentWizard = null;

		if (deliveryWizard == null && paymentWizard == null)
			throw new IllegalStateException("deliveryWizard == null && paymentWizard == null"); //$NON-NLS-1$

//		if (paymentWizard != null && invoiceIDs == null)
//			throw new IllegalArgumentException("paymentWizard is defined, but invoiceIDs == null!");
//
//		if (deliveryWizard != null && deliveryNoteIDs == null)
//			throw new IllegalArgumentException("deliveryWizard is defined, but deliveryNoteIDs == null!");

		// prepare payment
//		CurrencyID currencyID = null;
		if (paymentWizard != null) {
//			if (bookInvoiceMode == BOOK_INVOICE_MODE_BEFORE_PAYMENT) {
//				try {
//					accountingManager.bookInvoices(paymentWizard.getInvoiceIDs(), true, true);
//				} catch (Throwable t) {
//					throw new ModuleException("Book failed!", t);
//				}
//			}

			for (PaymentEntryPage paymentEntryPage : paymentWizard.getPaymentEntryPages()) {
				Payment payment = paymentEntryPage.getPaymentWizardHop().getPayment();
				payment.setInvoiceIDs(paymentWizard.getInvoiceIDs());
			}
		} // if (paymentWizard != null) {

// Marco: Not necessary anymore, because the DeliveryNotes are read from the Articles.
//		// prepare delivery
//		StoreManager storeManager = null;
//		if (deliveryWizard != null) {
//			storeManager = getStoreManager();
//
//			for (Iterator it = deliveryWizard.getDeliveryEntryPages().iterator(); it.hasNext(); ) {
//				DeliveryEntryPage deliveryEntryPage = (DeliveryEntryPage) it.next();
//				Delivery delivery = deliveryEntryPage.getDeliveryWizardHop().getDelivery();
//				delivery.setDeliveryNoteIDs(deliveryWizard.getDeliveryNoteIDs());
//			}
//		}
		
		List<Pair<PaymentData, ClientPaymentProcessor>> paymentTuples = null;
		List<Pair<DeliveryData, ClientDeliveryProcessor>> deliveryTuples = null;
		
		// prepare payment information
		if (paymentWizard != null) {
			paymentTuples = new ArrayList<Pair<PaymentData,ClientPaymentProcessor>>();
			
			for (PaymentEntryPage paymentEntryPage : paymentWizard.getPaymentEntryPages()) {
				PaymentData paymentData = paymentEntryPage.getPaymentWizardHop().getPaymentData();
				paymentTuples.add(new Pair<PaymentData, ClientPaymentProcessor>(paymentData, paymentEntryPage.getClientPaymentProcessor()));
			}
		}
		
		// prepare delivery information
		if (deliveryWizard != null) {
			deliveryTuples = new ArrayList<Pair<DeliveryData,ClientDeliveryProcessor>>();
			
			for (DeliveryEntryPage deliveryEntryPage : deliveryWizard.getDeliveryEntryPages()) {
				DeliveryData deliveryData = deliveryEntryPage.getDeliveryWizardHop().getDeliveryData();
				deliveryTuples.add(new Pair<DeliveryData, ClientDeliveryProcessor>(deliveryData, deliveryEntryPage.getClientDeliveryProcessor()));
			}
		}
		
		// Now do the actual payment/delivery
		return payAndDeliver(shell, paymentTuples, deliveryTuples);
	}
	
	private static boolean payAndDeliver(
			Shell shell,
			List<Pair<PaymentData, ClientPaymentProcessor>> paymentTuples,
			List<Pair<DeliveryData, ClientDeliveryProcessor>> deliveryTuples)
	throws RemoteException, LoginException, CreateException, NamingException, ModuleException
	{
		TransferCoordinator transferCoordinator = new TransferCoordinator();
		boolean toReturn = transferCoordinator.payAndDeliver(paymentTuples, deliveryTuples);
		
		ErrorDialog errorDialog = new ErrorDialog(shell, transferCoordinator.getPaymentDatas(), transferCoordinator.getDeliveryDatas());
		if (errorDialog.isFailed()) {
			errorDialog.open();
			return false;
		}
		
		return toReturn;
	}

//	/**
//	 * This method is meant to be used in an implementation of {@link DeliveryWizard#getProductIDs(Set)}.
//	 *
//	 * @param articlesToTransfer Instances of {@link Article}. They will be filtered with
//	 *		the given <code>productTypeIDs</code>.
//	 * @param productTypeIDs Instances of
//	 *		{@link org.nightlabs.jfire.store.id.ProductTypeID}.
//	 *
//	 * @return Returns all {@link org.nightlabs.jfire.store.id.ProductID}s that match
//	 *		one of the given {@link org.nightlabs.jfire.store.id.ProductTypeID}s
//	 *		exactly (no inheritance).
//	 */
//	public static Collection getProductIDs(Collection articlesToTransfer, Set productTypeIDs)
//	{
//		// we return all products of the offer matching the given productTypeIDs
//		Collection res = new ArrayList();
//		for (Iterator it = articlesToTransfer.iterator(); it.hasNext(); ) {
//			Article article = (Article) it.next();
//			ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(
//					article.getProductType());
//
//			if (productTypeIDs.contains(productTypeID))
//				res.add(JDOHelper.getObjectId(article.getProduct()));
//		}
//		return res;
//	}

	public static Set<ArticleID> getArticleIDsFromArticles(Collection<Article> articles)
	{
		Set<ArticleID> res = new HashSet<ArticleID>(articles.size());
		for (Iterator<Article> it = articles.iterator(); it.hasNext(); ) {
			Article article = (Article) it.next();
			res.add((ArticleID) JDOHelper.getObjectId(article));
		}
		return res;
	}

	/**
	 * This method is meant to be used in an implementation of {@link DeliveryWizard#getArticles(Set, boolean)}.
	 *
	 * @param articlesToTransfer Instances of {@link Article}. They will be filtered with
	 *		the given <code>productTypeIDs</code>.
	 * @param productTypeIDs Instances of
	 *		{@link org.nightlabs.jfire.store.id.ProductTypeID}.
	 * @param reversing This method returns only those {@link Article}s where <code>reversing == </code>{@link Article#isReversing()}.
	 * @return Returns all {@link Article}s that match
	 *		one of the given {@link org.nightlabs.jfire.store.id.ProductTypeID}s
	 *		exactly (no inheritance). This method may return <code>null</code>.
	 */
	public static List<Article> getArticles(Collection<Article> articlesToTransfer, Set<Article> productTypeIDs, boolean reversing)
	{
		// we return all products of the offer matching the given productTypeIDs
		List<Article> res = null;
		for (Article article : articlesToTransfer) {
			if (article.isReversing() != reversing)
				continue;

			ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(
					article.getProductType());

			if (productTypeIDs.contains(productTypeID)) {
				if (res == null)
					res = new ArrayList<Article>(articlesToTransfer.size());

				res.add(article);
			}
		}
		return res;
	}

	/**
	 * This method is meant to be used in an implementation of {@link DeliveryWizard#getProductTypeByIDMap()}.
	 *
	 * @param articles A <tt>Collection</tt> of {@link Article}.
	 * @return A <tt>Map</tt> of {@link org.nightlabs.jfire.store.id.ProductTypeID} as key
	 *		and {@link org.nightlabs.jfire.store.ProductType} as value.
	 */
	public static Map<ProductTypeID, ProductType> getProductTypeByIDMap(Collection<Article> articles)
	{
		Map<ProductTypeID, ProductType> productTypes = new HashMap<ProductTypeID, ProductType>();
		for (Article article : articles) {
				ProductType pt = article.getProductType();
				productTypes.put((ProductTypeID) JDOHelper.getObjectId(pt), pt);
		} // iterate articles

		return productTypes;
	}

	/**
	 * This method is meant to be used in an implementation of {@link DeliveryWizard#getProductTypeIDs()}.
	 *
	 * @param articles A <tt>Collection</tt> of {@link Article}.
	 * @return A <tt>Set</tt> of {@link org.nightlabs.jfire.store.id.ProductTypeID}
	 */
	public static Set<ProductTypeID> getProductTypeIDs(Collection<Article> articles)
	{
		Set<ProductTypeID> productTypeIDs = new HashSet<ProductTypeID>();

		for (Article article : articles) {
			productTypeIDs.add((ProductTypeID) JDOHelper.getObjectId(article.getProductType()));
		} // iterate articles

		return productTypeIDs;
	}

//	/**
//	 * @param articleContainers A <tt>Collection</tt> of {@link ArticleContainer}.
//	 * @return A <tt>Map</tt> of {@link org.nightlabs.jfire.store.id.ProductTypeID} as key
//	 *		and {@link org.nightlabs.jfire.store.ProductType} as value.
//	 */
//	public static Map getProductTypeByIDMap(Collection articleContainers)
//	{
//		Map productTypes = new HashMap();
//
//		for (Iterator itDN = articleContainers.iterator(); itDN.hasNext(); ) {
//			ArticleContainer articleContainer = (ArticleContainer) itDN.next();
//			for (Iterator itA = articleContainer.getArticles().iterator(); itA.hasNext(); ) {
//				Article article = (Article) itA.next();
//				ProductType pt = article.getProductType();
//				productTypes.put(JDOHelper.getObjectId(pt), pt);
//			} // iterate articles
//		} // iterate articleContainers
//
//		return productTypes;
//	}
//
//	/**
//	 * @param articleContainers A <tt>Collection</tt> of {@link ArticleContainer}.
//	 * @return A <tt>Set</tt> of {@link org.nightlabs.jfire.store.id.ProductTypeID}
//	 */
//	public static Set getProductTypeIDs(Collection articleContainers)
//	{
//		Set productTypeIDs = new HashSet();
//
//		for (Iterator itDN = articleContainers.iterator(); itDN.hasNext(); ) {
//			ArticleContainer articleContainer = (ArticleContainer) itDN.next();
//			for (Iterator itA = articleContainer.getArticles().iterator(); itA.hasNext(); ) {
//				Article article = (Article) itA.next();
//				productTypeIDs.add(JDOHelper.getObjectId(article.getProductType()));
//			} // iterate articles
//		} // iterate articleContainers
//
//		return productTypeIDs;
//	}

	public static List<DeliveryEntryPage> createDeliveryEntryPages(DeliveryWizard deliveryWizard)
	throws RemoteException, LoginException, ModuleException, CreateException, NamingException
	{
		List<DeliveryEntryPage> res = new LinkedList<DeliveryEntryPage>();

		ModeOfDeliveryFlavourProductTypeGroupCarrier carrier = getStoreManager().getModeOfDeliveryFlavourProductTypeGroupCarrier(
				deliveryWizard.getProductTypeIDs(),
				deliveryWizard.getCustomerGroupIDs(),
				ModeOfDeliveryFlavour.MERGE_MODE_SUBTRACTIVE,
				new String[]{
						FetchPlan.DEFAULT,
						ModeOfDeliveryFlavour.FETCH_GROUP_MODE_OF_DELIVERY,
						ModeOfDeliveryFlavour.FETCH_GROUP_NAME,
						ModeOfDeliveryFlavour.FETCH_GROUP_ICON_16X16_DATA,
						ModeOfDeliveryFlavourName.FETCH_GROUP_NAMES
				}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

		Map<ProductTypeID, ProductType> productTypeByIDMap = deliveryWizard.getProductTypeByIDMap();

		for (Iterator<ModeOfDeliveryFlavourProductTypeGroup> itG = carrier.getModeOfDeliveryFlavourProductTypeGroups().iterator(); itG.hasNext(); ) {
			ModeOfDeliveryFlavourProductTypeGroup group = (ModeOfDeliveryFlavourProductTypeGroup) itG.next();

			List<ProductType> productTypes = new ArrayList<ProductType>();
			for (Iterator<ProductTypeID> itPT = group.getProductTypeIDs().iterator(); itPT.hasNext();) {
				ProductTypeID productTypeID = (ProductTypeID) itPT.next();
				ProductType productType = (ProductType) productTypeByIDMap.get(productTypeID);
				if (productType == null)
					throw new IllegalStateException("ProductType with ID \"" + productTypeID + "\" missing in map!"); //$NON-NLS-1$ //$NON-NLS-2$

				productTypes.add(productType);
			}

			List<ModeOfDeliveryFlavour> modeOfDeliveryFlavours = new ArrayList<ModeOfDeliveryFlavour>();
			for (Iterator<ModeOfDeliveryFlavourID> itMDOFID = group.getModeOfDeliveryFlavourIDs().iterator(); itMDOFID.hasNext(); ) {
				ModeOfDeliveryFlavourID modfID = (ModeOfDeliveryFlavourID) itMDOFID.next();
				ModeOfDeliveryFlavour modf = carrier.getModeOfDeliveryFlavour(modfID);
				modeOfDeliveryFlavours.add(modf);
			}

////			delivery.setDeliveryNoteIDs(deliveryWizard.getDeliveryNoteIDs());
//			delivery.setProductIDs(deliveryWizard.getProductIDs(group.getProductTypeIDs()));
			boolean[] reverseFilters = new boolean [] {false, true};
			
			for (int i = 0; i < reverseFilters.length; ++i) {
				boolean reverse = reverseFilters[i];
				List<Article> articles = deliveryWizard.getArticles(group.getProductTypeIDs(), reverse);

				if (articles == null || articles.isEmpty())
					continue;
				
				Delivery delivery = new Delivery(IDGenerator.getOrganisationID(), IDGenerator.nextID(Delivery.class));

				delivery.setArticleIDs(getArticleIDsFromArticles(articles));
				String deliveryDirection;
				if (reverse ^ DeliveryWizard.Side.Vendor.equals(deliveryWizard.getSide()))
					deliveryDirection = Delivery.DELIVERY_DIRECTION_OUTGOING;
				else
					deliveryDirection = Delivery.DELIVERY_DIRECTION_INCOMING;

				delivery.setDeliveryDirection(deliveryDirection);

				DeliveryEntryPage page = new DeliveryEntryPage(
						delivery,
						productTypes,
						articles,
						modeOfDeliveryFlavours);

				res.add(page);
			}
		}

		return res;
	}
	
	public static Delivery createDelivery(DeliveryWizard deliveryWizard, ModeOfDeliveryFlavourProductTypeGroup group, List<Article> onlyArticles) {
		Delivery delivery = new Delivery(IDGenerator.getOrganisationID(), IDGenerator.nextID(Delivery.class));
		boolean[] reverseFilters = new boolean [] {false, true};

		// TODO Is it possible that one instance of deliveryWizard covers both articles and reversed articles or can it only contain one type?
		// If it is possible, then the following code is erroneous, since the delivery contains only one type.
		for (int i = 0; i < reverseFilters.length; ++i) {
			boolean reverse = reverseFilters[i];
			List<Article> articles = deliveryWizard.getArticles(group.getProductTypeIDs(), reverse);
			
			if (articles == null || articles.isEmpty())
				continue;
		
			delivery.setArticleIDs(getArticleIDsFromArticles(articles));
			String deliveryDirection;
			if (reverse ^ DeliveryWizard.Side.Vendor.equals(deliveryWizard.getSide()))
				deliveryDirection = Delivery.DELIVERY_DIRECTION_OUTGOING;
			else
				deliveryDirection = Delivery.DELIVERY_DIRECTION_INCOMING;

			delivery.setDeliveryDirection(deliveryDirection);
		}
			
		return delivery;
	}
	
	public <T1, T2> List<Pair<T1, T2>> createTupleList(List<T1> list1, List<T2> list2) {
		Assert.isLegal(list1.size() == list2.size());
		
		List<Pair<T1, T2>> list = new ArrayList<Pair<T1, T2>>(list1.size());
		
		for (Iterator<?> it1 = list1.iterator(), it2 = list2.iterator(); it1.hasNext(); )
			list.add(new Pair<T1, T2>((T1) it1.next(), (T2) it2.next()));
		
		return list;		
	}

}