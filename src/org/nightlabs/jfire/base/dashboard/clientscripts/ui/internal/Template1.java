package org.nightlabs.jfire.base.dashboard.clientscripts.ui.internal;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.jboss.security.jndi.LoginInitialContextFactory;
import org.nightlabs.ModuleException;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.j2ee.LoginData;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManagerRemote;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.TariffMapper;
import org.nightlabs.jfire.accounting.TariffMapping;
import org.nightlabs.jfire.accounting.gridpriceconfig.FormulaCell;
import org.nightlabs.jfire.accounting.gridpriceconfig.FormulaPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCalculationException;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCoordinate;
import org.nightlabs.jfire.accounting.gridpriceconfig.StablePriceConfig;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.accounting.id.PriceFragmentTypeID;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.accounting.priceconfig.PriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.JFireClient;
import org.nightlabs.jfire.base.login.JFireSecurityConfiguration;
import org.nightlabs.jfire.dynamictrade.DynamicTradeManagerRemote;
import org.nightlabs.jfire.dynamictrade.accounting.priceconfig.DynamicTradePriceConfig;
import org.nightlabs.jfire.dynamictrade.accounting.priceconfig.PriceCalculator;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.security.GlobalSecurityReflector;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeLocal;
import org.nightlabs.jfire.store.StoreManagerRemote;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.store.id.UnitID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.CustomerGroupMapper;
import org.nightlabs.jfire.trade.CustomerGroupMapping;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.Segment;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.id.SegmentID;
import org.nightlabs.jfire.trade.id.SegmentTypeID;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author sschefczyk
 *
 * Aus JIRA:
 * "Die Vorlage soll für einen im Skript hardgecodeten Kunden eine neue Order erzeugen und zur Order eine neue Offer. 
 * In diese Offer soll ein Artikel für einen im Skript hardgecodeten DynamicProductType erzeugt werden; 
 * der Text des DynamicProducts wird vom Skript erzeugt (hier statisch). 
 * Danach soll das Skript eine Rechnung für diesen Artikel erzeugen und diese Rechnung finalisieren."
 *
 */
public class Template1 {
	
	static final String offerIDPrefix = null; //TODO
	static final String organisationID = "chezfrancois.jfire.org"; //IDGenerator.getOrganisationID();
	static final AnchorID customerID = AnchorID.create(organisationID, "LegalEntity", "LegalEntity-anonymous"); //TODO hardcoded
	static Currency currency = currency = new Currency("EUR", "€", 2);
	
	
	public static void main(String args[]) throws LoginException
	{
		LoginInitialContextFactory.class.getName();
		JFireClient.registerDefaultStaticJFireClientClasses();
		LoginData loginData = new LoginData("chezfrancois.jfire.org", "admin", "test");
		loginData.setDefaultValues();
		JFireClient client = new JFireClient(loginData);
		JFireSecurityConfiguration.declareConfiguration();
//		System.setProperty(GlobalSecurityReflector.PROPERTY_KEY_SECURITY_REFLECTOR_CLASS, SecurityReflectorClient.class.getName());
		
//		JFireLogin login = new JFireLogin("chezfrancois.jfire.org", "admin", "test");
//		login.getLoginData().setDefaultValues();
		try {
			client.login();
			
			doit();
			
		} finally {
			if (client != null)
				client.logout();
		}
	}


	public static void doit() {
		OrderID orderId = createOrder();
		OfferID offerId = createOffer(orderId);
		addOneArticle(offerId);
		createInvoiceAndFinalize();
	}
	

	public static OrderID createOrder() 
	{
		try {
			TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, 
					GlobalSecurityReflector.sharedInstance().getInitialContextProperties());
			
			AccountingManagerRemote accountingBean = JFireEjb3Factory.getRemoteBean(AccountingManagerRemote.class,
					GlobalSecurityReflector.sharedInstance().getInitialContextProperties());
			
			CurrencyID currencyID = CurrencyID.create("EUR");
			Collection<Currency> currencies = accountingBean.getCurrencies(Collections.singleton(currencyID), new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			currency = currencies.iterator().next();
			
			Order order = tm.createSaleOrder(
					customerID, null,
					currencyID,
					new SegmentTypeID[] {null}, // null here is a shortcut for default segment type
					null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			if (order == null)
				throw new IllegalStateException("Could not create an order, (order == null)!");

			final OrderID orderID = (OrderID) JDOHelper.getObjectId(order);
			return orderID;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static OfferID createOffer(OrderID orderID)
	{
		Offer offer = null;
		TradeManagerRemote tm;
		tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, GlobalSecurityReflector.sharedInstance().getInitialContextProperties());
		try {
			offer = tm.createOffer(orderID, offerIDPrefix, null, 1);
		} catch (ModuleException e) {
			throw new RuntimeException(e);
		}
		if (offer == null)
			throw new IllegalStateException("Could not create an offer, (offer == null)!");

		final OfferID offerID = (OfferID) JDOHelper.getObjectId(offer);
		return offerID;
	}
	
	public static ArticleID addOneArticle(OfferID offerID) 
	{
		String productTypeIDString = "dproducttype_1";
		String productName_US = "EXAMPLE-PRODUCT-NAME";
		long quantity = 1000;

		ProductTypeID productTypeID = ProductTypeID.create(organisationID, productTypeIDString);
		
		UnitID unitID = UnitID.create("dev.jfire.org", "piece");
		TariffID tariffID = TariffID.create(organisationID, "1");
		I18nText productName = new I18nTextBuffer();
		productName.setText(Locale.US.getLanguage(), productName_US);
//		long priceID = PriceID.create(organisationID, 113);
//		long priceID = 113;
		Price singlePrice = createArticlePrice(
				productTypeID, tariffID, (CurrencyID)JDOHelper.getObjectId(currency),
				PriceFragmentTypeID.create(organisationID, "asdf-gwgprurh-ddo")
//				PriceFragmentTypeID.create(organisationID, "vat-19-de-net")
				);
		
		boolean allocate = true;
		boolean allocateSynchronously = false;
		
		TradeManagerRemote tm;
		tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, GlobalSecurityReflector.sharedInstance().getInitialContextProperties());
		String[] offerFetchGroup = new String[]{FetchPlan.DEFAULT, Offer.FETCH_GROUP_SEGMENTS};
		Offer offer = tm.getOffer(offerID, offerFetchGroup, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		Collection<Segment> segments = offer.getSegments();
		Segment segment = segments.iterator().next();
		SegmentID segmentID = (SegmentID) JDOHelper.getObjectId(segment);
		
		try {
			Article article = storeArticle(
					segmentID,
					offerID,
					productTypeID,
					quantity, 
					unitID, 
					tariffID, 
					productName, 
					singlePrice, 
					allocate, 
					allocateSynchronously
			);

			if (article == null)
				throw new IllegalStateException("Could not create an article, (article == null)!");
			
			final ArticleID articleID = (ArticleID) JDOHelper.getObjectId(article);
			return articleID;
 
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private static Price createArticlePrice(
			ProductTypeID productTypeID, TariffID tariffID, CurrencyID currencyID, PriceFragmentTypeID pftID) 
	{
		StoreManagerRemote sm = JFireEjb3Factory.getRemoteBean(StoreManagerRemote.class, GlobalSecurityReflector.sharedInstance().getInitialContextProperties());
		List<ProductType> productTypes = sm.getProductTypes(Collections.singleton(productTypeID), 
				new String[]{
					DynamicProductType.FETCH_GROUP_INNER_PRICE_CONFIG, 
					GridPriceConfig.FETCH_GROUP_CUSTOMER_GROUPS, 
					GridPriceConfig.FETCH_GROUP_TARIFFS,
					PriceConfig.FETCH_GROUP_CURRENCIES,
					PriceConfig.FETCH_GROUP_PRICE_FRAGMENT_TYPES,
					ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL,
					FormulaPriceConfig.FETCH_GROUP_FORMULA_CELLS,
					FormulaPriceConfig.FETCH_GROUP_PACKAGING_RESULT_PRICE_CONFIGS,
					FormulaPriceConfig.FETCH_GROUP_FALLBACK_FORMULA_CELL,
					ProductTypeLocal.FETCH_GROUP_NESTED_PRODUCT_TYPE_LOCALS,
					ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_NO_LIMIT,
					FormulaCell.FETCH_GROUP_PRICE_FRAGMENT_FORMULAS,
					FetchPlan.DEFAULT, 
				}, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
		);
		ProductType next = productTypes.iterator().next();
		if (!(next instanceof DynamicProductType))
			throw new IllegalStateException("!(next instanceof DynamicProductType)");
		DynamicProductType productType = (DynamicProductType) next;
		
		DynamicTradePriceConfig dynamicTradePriceConfig = (DynamicTradePriceConfig) productType.getInnerPriceConfig();
//		final List<Tariff> tariffs = new ArrayList<Tariff>(dynamicTradePriceConfig.getTariffs());

		StablePriceConfig resultPriceConfig = new StablePriceConfig(PriceConfigID.create(IDGenerator.getOrganisationID(), "temporary.resultPriceConfigID"));
		productType.setPackagePriceConfig(resultPriceConfig);
		resultPriceConfig.adoptParameters(dynamicTradePriceConfig);

		PriceCalculator priceCalculator = new PriceCalculator(
				productType,
				new CustomerGroupMapper(new ArrayList<CustomerGroupMapping>(0)), // no crosstrade supported => no mappings needed!
				new TariffMapper(new ArrayList<TariffMapping>(0)));		

		PriceCoordinate priceCoordinate = new PriceCoordinate(
				CustomerGroupID.create(organisationID, "CustomerGroup-anonymous"), tariffID, currencyID);
		
		final FormulaCell formulaCell = dynamicTradePriceConfig.createFormulaCell(priceCoordinate);
		formulaCell.setFormula(pftID, "12500");
		
		priceCalculator.preparePriceCalculation();
		try {
			priceCalculator.calculatePrices();
		} catch (final PriceCalculationException e) {
			throw new RuntimeException(e);
		}
		final Price singlePriceOrig = resultPriceConfig.getPriceCell(priceCoordinate, true).getPrice();
		// we must create a new instance (with a new ID), because it would otherwise cause duplicate-key-exceptions when adding multiple articles
//		final Price singlePrice = new Price(resultPriceConfig.getOrganisationID(), resultPriceConfig.getPriceConfigID(), resultPriceConfig.createPriceID(), singlePriceOrig.getCurrency());
		final Price singlePrice = new Price(IDGenerator.getOrganisationID(), IDGenerator.nextID(Price.class), singlePriceOrig.getCurrency());
		singlePrice.sumPrice(singlePriceOrig);
		return singlePrice;
	}

	
	public static void createInvoiceAndFinalize() 
	{
//		User creator = ;
//		LegalEntity vendor;
//		LegalEntity customer;
//		String invoiceIDPrefix;
//		long _invoiceID;
//		Invoice invoice = new Invoice(creator, vendor, customer, invoiceIDPrefix, _invoiceID, currency);
//		
		
	}

	
	
	private static Article storeArticle(
			SegmentID segmentID,
			OfferID offerID,
			ProductTypeID productTypeID,
			long quantity,
			UnitID unitID,
			TariffID tariffID,
			I18nText productName,
			Price singlePrice,
			boolean allocate,
			boolean allocateSynchronously) 
	throws RemoteException, LoginException, NamingException, ModuleException
	{
		DynamicTradeManagerRemote dm = JFireEjb3Factory.getRemoteBean(DynamicTradeManagerRemote.class, GlobalSecurityReflector.sharedInstance().getInitialContextProperties());

		return dm.createArticle(
				segmentID, offerID, productTypeID, quantity, unitID, tariffID, productName, singlePrice, allocate, allocateSynchronously,
				new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
	}

}
