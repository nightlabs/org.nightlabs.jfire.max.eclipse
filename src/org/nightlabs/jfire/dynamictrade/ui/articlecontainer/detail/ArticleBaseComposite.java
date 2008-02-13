package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDODetachedFieldAccessException;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.ComboComposite;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.TariffMapper;
import org.nightlabs.jfire.accounting.TariffMapping;
import org.nightlabs.jfire.accounting.TariffOrderConfigModule;
import org.nightlabs.jfire.accounting.dao.PriceConfigEditDAO;
import org.nightlabs.jfire.accounting.gridpriceconfig.FormulaCell;
import org.nightlabs.jfire.accounting.gridpriceconfig.IPriceCoordinate;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCalculationException;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCalculator;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCell;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCoordinate;
import org.nightlabs.jfire.accounting.gridpriceconfig.StablePriceConfig;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.accounting.priceconfig.DynamicTradePriceConfig;
import org.nightlabs.jfire.dynamictrade.store.DynamicProduct;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.Unit;
import org.nightlabs.jfire.store.dao.UnitDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.CustomerGroup;
import org.nightlabs.jfire.trade.CustomerGroupMapper;
import org.nightlabs.jfire.trade.CustomerGroupMapping;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.Util;

public abstract class ArticleBaseComposite
extends FadeableComposite
{
	protected ArticleContainer articleContainer;
	protected ProductTypeID productTypeID;

	protected Label productTypeNameLabel;
	protected ComboComposite<Tariff> tariffCombo;
	protected Text quantity;
	protected ComboComposite<Unit> unitCombo;
	protected Text productNameText;
	protected Button productNameDialogButton;
	protected InputPriceFragmentTypeTable inputPriceFragmentTypeTable;

	protected XComposite comp1;

	public ArticleBaseComposite(Composite parent, ArticleContainer articleContainer, DynamicProductType productType)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.articleContainer = articleContainer;
		this.productTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);
	}

	protected void createUI()
	{
		comp1 = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp1.getGridLayout().numColumns = 4;
		comp1.getGridData().grabExcessVerticalSpace = false;

		productTypeNameLabel = new Label(comp1, SWT.NONE);
		productTypeNameLabel.setLayoutData(new GridData());

		tariffCombo = new ComboComposite<Tariff>(comp1, SWT.BORDER | SWT.READ_ONLY , new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				return ((Tariff)element).getName().getText();
			}
		});
		Tariff dummy = new Tariff("dummy", "_dummy_"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy.getName().setText(Locale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.pseudoTariff_loading")); //$NON-NLS-1$
		tariffCombo.addElement(dummy);
		tariffCombo.selectElement(dummy);
		tariffCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateInputPriceFragmentTypes();
			}
		});

		ArticleContainer ac = articleContainer;
		Currency currency;
		if (ac instanceof Order)
			currency = ((Order)ac).getCurrency();
		else if (ac instanceof Offer)
			currency = ((Offer)ac).getCurrency();
		else
			throw new IllegalStateException("articleAdder.getSegmentEdit().getArticleContainer() is an unsupported type: " + (ac == null ? null : ac.getClass().getName())); //$NON-NLS-1$


		SashForm comp2 = new SashForm(this, SWT.HORIZONTAL);
//		comp2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));

		XComposite comp3 = new XComposite(comp2, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp3.getGridLayout().numColumns = 2;
		productNameText = new Text(comp3, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		productNameText.setLayoutData(new GridData(GridData.FILL_BOTH));
		productNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				productName.setText(Locale.getDefault().getLanguage(), productNameText.getText());
				productNameModified = true;
			}
		});
		((GridData)productNameText.getLayoutData()).heightHint = productTypeNameLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y * 3;
		productNameDialogButton = new Button(comp3, SWT.PUSH);
		productNameDialogButton.setText("..."); //$NON-NLS-1$
		productNameDialogButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		productNameDialogButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				if (Window.OK == new ProductNameDialog(getShell(), productName).open()) {
					updateProductNameUI();
					productNameModified = true;
				}
			}
		});

		inputPriceFragmentTypeTable = new InputPriceFragmentTypeTable(comp2, currency) {
			@Override
			@Implement
			protected void inputPriceFragmentTypeModified(InputPriceFragmentType inputPriceFragmentType)
			{
				if (dynamicTradePriceConfig == null)
					return;

				IPriceCoordinate priceCoordinate = createPriceCoordinate();
				FormulaCell formulaCell = dynamicTradePriceConfig.createFormulaCell(priceCoordinate);
				formulaCell.setFormula(inputPriceFragmentType.getPriceFragmentType(), String.valueOf(inputPriceFragmentType.getAmount()));
				inputPriceFragmentTypeModified = true;

				try {
					priceCalculator.calculatePrices();
				} catch (PriceCalculationException e) {
					throw new RuntimeException(e);
				}

			}
		};
		inputPriceFragmentTypeTable.getGridData().grabExcessVerticalSpace = false;

		comp2.setWeights(new int[] { 1, 1 });

		quantity = new Text(comp1, SWT.BORDER);
		quantity.setText(NumberFormatter.formatFloat(1, 2));
		GridData gd = new GridData();
		gd.widthHint = 200;
		quantity.setLayoutData(gd);

		unitCombo = new ComboComposite<Unit>(comp1, SWT.BORDER | SWT.READ_ONLY, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				return ((Unit)element).getName().getText();
			}
		});

		createUI_additionalElements_comp1(comp1);

		loadDynamicProductType();
		setFaded(true);
	}

	protected boolean inputPriceFragmentTypeModified = false;
	protected boolean productNameModified = false;

	protected void createUI_additionalElements_comp1(Composite parent)
	{
	}

	protected DynamicProductType dynamicProductType;
	protected DynamicTradePriceConfig dynamicTradePriceConfig;
	protected StablePriceConfig resultPriceConfig;
	protected PriceCalculator priceCalculator;
	protected List<InputPriceFragmentType> inputPriceFragmentTypes;

	protected I18nTextBuffer productName = new I18nTextBuffer();

	protected IPriceCoordinate createPriceCoordinate()
	{
		Order order = (Order) (articleContainer instanceof Order ? articleContainer : null);
		Offer offer = (Offer) (articleContainer instanceof Offer ? articleContainer : null);

		Currency currency = order != null ? order.getCurrency() : null;
		CustomerGroup customerGroup = order != null ? order.getCustomerGroup() : null;
		if (offer != null) {
			customerGroup = offer.getOrder().getCustomerGroup();
			currency = offer.getCurrency();
		}

		CurrencyID currencyID = (CurrencyID) JDOHelper.getObjectId(currency);
		CustomerGroupID customerGroupID = (CustomerGroupID) JDOHelper.getObjectId(customerGroup);
		Tariff tariff = tariffCombo.getSelectedElement();
		TariffID tariffID = (TariffID) JDOHelper.getObjectId(tariff);

		return new PriceCoordinate(customerGroupID, tariffID, currencyID);
	}

	private static final String[] FETCH_GROUPS_UNIT = {
		FetchPlan.DEFAULT, Unit.FETCH_GROUP_NAME
	};

//	private static long temporaryResultPriceConfigID = IDGenerator.nextID(PriceConfig.class);
	private static String temporaryResultPriceConfigID = "temporary.resultPriceConfigID";

	private volatile boolean loadDynamicProductType_inProcess = false;

	private void loadDynamicProductType()
	{
		Job job = new Job(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.loadDynamicProductTypeJob.name")) { //$NON-NLS-1$
			@Override
			@Implement
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				if (loadDynamicProductType_inProcess)
					throw new IllegalStateException("Why the hell is this Job executed twice at the same time?!"); //$NON-NLS-1$

				boolean error = true;
				loadDynamicProductType_inProcess = true;
				try {

					dynamicProductType = (DynamicProductType) Util.cloneSerializable(
							PriceConfigEditDAO.sharedInstance().getProductTypeForPriceConfigEditing(productTypeID, monitor));

					dynamicTradePriceConfig = (DynamicTradePriceConfig) dynamicProductType.getInnerPriceConfig();
					final List<Tariff> tariffs = new ArrayList<Tariff>(dynamicTradePriceConfig.getTariffs());

					resultPriceConfig = new StablePriceConfig(IDGenerator.getOrganisationID(), temporaryResultPriceConfigID);
					dynamicProductType.setPackagePriceConfig(resultPriceConfig);
					resultPriceConfig.adoptParameters(dynamicTradePriceConfig);

					priceCalculator = new PriceCalculator(
							dynamicProductType,
							new CustomerGroupMapper(new ArrayList<CustomerGroupMapping>(0)), // no crosstrade supported => no mappings needed!
							new TariffMapper(new ArrayList<TariffMapping>(0)));

					priceCalculator.preparePriceCalculation();
					try {
						priceCalculator.calculatePrices();
					} catch (PriceCalculationException e) {
						throw new RuntimeException(e);
					}

					Collections.sort(tariffs, new Comparator<Tariff>() {
						public int compare(Tariff t1, Tariff t2)
						{
							return t1.getName().getText().compareTo(t2.getName().getText());
						}
					});

					Set<PriceFragmentType> pfts = dynamicTradePriceConfig.getInputPriceFragmentTypes();
					final List<InputPriceFragmentType> ipfts = new ArrayList<InputPriceFragmentType>(pfts.size());
					for (PriceFragmentType priceFragmentType : pfts)
						ipfts.add(new InputPriceFragmentType(priceFragmentType));

					Collections.sort(ipfts, new Comparator<InputPriceFragmentType>() {
						public int compare(InputPriceFragmentType ipft1, InputPriceFragmentType ipft2)
						{
							return ipft1.getPriceFragmentType().getName().getText().compareTo(ipft2.getPriceFragmentType().getName().getText());
						}
					});

					final List<Unit> units = UnitDAO.sharedInstance().getUnits(FETCH_GROUPS_UNIT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
					Collections.sort(units, new Comparator<Unit>() {
						public int compare(Unit u1, Unit u2)
						{
							return u1.getName().getText().compareTo(u2.getName().getText());
						}
					});
					
				// Sort the tariffs according to the config module
					String[] fetchGroups = new String[] { TariffOrderConfigModule.FETCH_GROUP_TARIFF_ORDER_CONFIG_MODULE , FetchPlan.DEFAULT };
					TariffOrderConfigModule cfMod = (TariffOrderConfigModule) ConfigUtil.getUserCfMod(TariffOrderConfigModule.class,
							fetchGroups, -1, new NullProgressMonitor());
					Collections.sort(tariffs, cfMod.getTariffComparator());
					// Sorting done

					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							try {
								if (tariffCombo.isDisposed())
									return;

								productTypeNameLabel.setText(
										dynamicProductType.getName().getText(Locale.getDefault().getLanguage()));

								tariffCombo.removeAll();
								tariffCombo.addElements(tariffs);
								if (!tariffs.isEmpty())
									tariffCombo.setSelection(0);

								inputPriceFragmentTypes = ipfts;
								inputPriceFragmentTypeTable.setInput(ipfts);
								fireCompositeContentChangeEvent();

								inputPriceFragmentTypeTable.layout(true, true);

								unitCombo.removeAll();
								unitCombo.addElements(units);
								if (!units.isEmpty())
									unitCombo.setSelection(0);

								updateInputPriceFragmentTypes();
								setFaded(false);

								if (deferredArticleAssignment != null)
									_setArticle(deferredArticleAssignment);
							} finally {
								loadDynamicProductType_inProcess = false;
							}
						}
					});

					error = false;
				} finally {
					if (error)
						loadDynamicProductType_inProcess = false;
				}

				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();
	}

	protected abstract void fireCompositeContentChangeEvent();

	private void updateInputPriceFragmentTypes()
	{
		if (inputPriceFragmentTypes == null)
			return;

		IPriceCoordinate priceCoordinate = createPriceCoordinate();
		PriceCell priceCell = resultPriceConfig.getPriceCell(priceCoordinate, false);
		this.setEnabled(priceCell != null);
		if (priceCell == null) {
			MessageDialog.openError(getShell(), Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.errorNoPriceCellDialog.title"), Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.errorNoPriceCellDialog.message")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		for (InputPriceFragmentType ipft : inputPriceFragmentTypes) {
			long amount = priceCell.getPrice().getAmount(ipft.getPriceFragmentType());
			ipft.setAmount(amount);
		}

		inputPriceFragmentTypeTable.refresh();
	}

	private Article deferredArticleAssignment = null;

	public void setArticle(Article article)
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("This method must be called on the UI Thread! Wrong Thread!"); //$NON-NLS-1$

		if (loadDynamicProductType_inProcess) {
			deferredArticleAssignment = article;
		}
		else {
			_setArticle(article);
		}
	}

	protected void updateProductNameUI()
	{
		productNameText.setText(productName.getText(Locale.getDefault().getLanguage()));
	}

	protected Article article = null;

	protected void _setArticle(Article _article)
	{
		if (!_article.getProductType().equals(dynamicProductType))
			throw new IllegalArgumentException("article.productType != this.productType"); //$NON-NLS-1$

		this.article = _article;

		DynamicProduct product = (DynamicProduct) article.getProduct();
		if (!tariffCombo.selectElement(article.getTariff()))
			throw new IllegalStateException("Tariff not in combo!"); // TODO we should handle this situation - it might happen //$NON-NLS-1$

		productName.copyFrom(product.getName());
		updateProductNameUI();
		productNameModified = false;

		quantity.setText(NumberFormatter.formatFloat(product.getQuantityAsDouble(), 2));
		if (!unitCombo.selectElement(product.getUnit()))
			throw new IllegalStateException("Unit not in combo!"); // TODO we should handle this situation - it might happen //$NON-NLS-1$

		// product.singlePrice.fragments is probably not detached in the ArticleEdit's Articles
		try {
			for (InputPriceFragmentType ipft : inputPriceFragmentTypes) {
				long amount = product.getSinglePrice().getAmount(ipft.getPriceFragmentType());
				PriceCell priceCell = resultPriceConfig.getPriceCell(createPriceCoordinate(), false);
				if (priceCell != null)
					priceCell.getPrice().setAmount(ipft.getPriceFragmentType(), amount);

				ipft.setAmount(amount);
			}
	
			inputPriceFragmentTypeTable.refresh();
			inputPriceFragmentTypeModified = false;
		} catch (JDODetachedFieldAccessException x) {
			setFaded(true);
			Job job = new Job(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.loadArticleWithPriceFragmentsJob.name")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor) throws Exception
				{
					try {
						ArticleID articleID = (ArticleID) JDOHelper.getObjectId(article);
						TradeManager tm = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
						// TODO need DAO!
						final Article articleWithPriceFragments = tm.getArticle(
								articleID,
								new String[] { FetchPlan.DEFAULT, Article.FETCH_GROUP_PRODUCT, DynamicProduct.FETCH_GROUP_SINGLE_PRICE, Price.FETCH_GROUP_THIS_PRICE },
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

						Display.getDefault().asyncExec(new Runnable()
						{
							public void run()
							{
								DynamicProduct product = (DynamicProduct) articleWithPriceFragments.getProduct();
								for (InputPriceFragmentType ipft : inputPriceFragmentTypes) {
									long amount = product.getSinglePrice().getAmount(ipft.getPriceFragmentType());
									PriceCell priceCell = resultPriceConfig.getPriceCell(createPriceCoordinate(), false);
									if (priceCell != null)
										priceCell.getPrice().setAmount(ipft.getPriceFragmentType(), amount);

									ipft.setAmount(amount);
								}

								inputPriceFragmentTypeTable.refresh();
								inputPriceFragmentTypeModified = false;
								setFaded(false);
							}
						});
					} catch (Exception x) {
						setFadedOnUIThread(false);
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	private void setFadedOnUIThread(final boolean faded)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				setFaded(faded);
			}
		});
	}
}

