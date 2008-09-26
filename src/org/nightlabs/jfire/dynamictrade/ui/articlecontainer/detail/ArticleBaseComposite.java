package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDODetachedFieldAccessException;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
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
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.Invoice;
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
import org.nightlabs.jfire.trade.dao.ArticleDAO;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.CustomerGroupID;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.NLLocale;
import org.nightlabs.util.Util;

public abstract class ArticleBaseComposite
extends FadeableComposite
{
	private static Logger logger = Logger.getLogger(ArticleBaseComposite.class);

	protected ArticleContainer articleContainer;
	protected ProductTypeID productTypeID;

	protected Label productTypeNameLabel;
	protected XComboComposite<Tariff> tariffCombo;
	protected XComboComposite<Unit> unitCombo;
	protected Text quantity;
	protected Text productNameText;
	protected Button productNameDialogButton;
	protected InputPriceFragmentTypeTable inputPriceFragmentTypeTable;

	protected XComposite comp1;

	/**
	 * Used for {@link #createPriceCoordinate()} if it could be set
	 * and for the price fragment table.
	 */
	private Currency currency;
	/**
	 * Used for {@link #createPriceCoordinate()} if it could be set.
	 */
	private CustomerGroupID customerGroupID;
	/**
	 * Set when the Composite was created with an article.
	 * Will be nulled again after {@link #_setArticle(Article)} was called
	 * with it.
	 */
	private Article createArticle;

	/**
	 * Whether to check if the composite should be editable.
	 */
	private boolean checkForEditable = false;

	/**
	 * Create a new {@link ArticleBaseComposite} with a {@link DynamicProductType}.
	 *
	 * @param parent
	 * @param articleContainer
	 * @param productType
	 */
	public ArticleBaseComposite(Composite parent, ArticleContainer articleContainer, DynamicProductType productType)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.articleContainer = articleContainer;
		this.productTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);
	}

	public ArticleBaseComposite(Composite parent, ArticleContainer articleContainer, Article article)
	{
		this(parent, articleContainer, (DynamicProductType) article.getProductType());
		createArticle = article;
		checkForEditable = true;
	}

	protected void createUI()
	{
		comp1 = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp1.getGridLayout().numColumns = 4;
		comp1.getGridData().grabExcessVerticalSpace = false;

		productTypeNameLabel = new Label(comp1, SWT.NONE);
		productTypeNameLabel.setLayoutData(new GridData());

//		tariffCombo = new ComboComposite<Tariff>(comp1, SWT.BORDER | SWT.READ_ONLY , new LabelProvider() {
//			@Override
//			public String getText(Object element)
//			{
//				return ((Tariff)element).getName().getText();
//			}
//		});
		tariffCombo = new XComboComposite<Tariff>(comp1, getBorderStyle() | SWT.READ_ONLY , new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				return ((Tariff)element).getName().getText();
			}
		});
		Tariff dummy = new Tariff("dummy", "_dummy_"); //$NON-NLS-1$ //$NON-NLS-2$
		dummy.getName().setText(NLLocale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.pseudoTariff_loading")); //$NON-NLS-1$
		tariffCombo.addElement(dummy);
		tariffCombo.selectElement(dummy);
		tariffCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateInputPriceFragmentTypes();
			}
		});

		SashForm comp2 = new SashForm(this, SWT.HORIZONTAL);
//		comp2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));

		XComposite comp3 = new XComposite(comp2, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp3.getGridLayout().numColumns = 2;
		productNameText = new Text(comp3, getBorderStyle() | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		productNameText.setLayoutData(new GridData(GridData.FILL_BOTH));
		productNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				productName.setText(NLLocale.getDefault().getLanguage(), productNameText.getText());
				productNameModified = true;
			}
		});
//		productNameText.setData(IToolkit.KEY_DRAW_BORDER, IToolkit.TEXT_BORDER);
		((GridData)productNameText.getLayoutData()).heightHint = productTypeNameLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y * 3;
		productNameDialogButton = new Button(comp3, SWT.PUSH);
		productNameDialogButton.setText("..."); //$NON-NLS-1$
		productNameDialogButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		productNameDialogButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0)
			{
				if ((Window.OK == new ProductNameDialog(getShell(), productName, editable).open())) {
					updateProductNameUI();
					productNameModified = true;
				}
			}
		});

		inputPriceFragmentTypeTable = new InputPriceFragmentTypeTable(comp2) {
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

		unitCombo = new XComboComposite<Unit>(comp1, getBorderStyle() | SWT.READ_ONLY, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				return ((Unit)element).getName().getText();
			}
		});
		unitCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				unitSelected();
			}
		});

		quantity = new Text(comp1, getBorderStyle());
		quantity.setText(NumberFormatter.formatFloat(1, 2));
		GridData gd = new GridData();
		gd.widthHint = 200;
		quantity.setLayoutData(gd);
		quantity.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				Unit unit = unitCombo.getSelectedElement();
				if (unit == null)
					return;

				String quantityStr = quantity.getText();
				if (!"".equals(quantityStr)) { //$NON-NLS-1$
					try {
						lastValidQuantity = NumberFormatter.parseFloat(quantityStr);
					} catch (ParseException e) {
						MessageDialog.openError(
								getShell(),
								Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.quantityInvalidDialog.title"), //$NON-NLS-1$
								String.format(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.quantityInvalidDialog.message"), quantityStr) //$NON-NLS-1$
						);
						quantity.setText(NumberFormatter.formatFloat(lastValidQuantity, unit.getDecimalDigitCount()));
					}
				}
			}
		});

		createUI_additionalElements_comp1(comp1);

		setEditable(false);

		loadDynamicProductType();
	}

	private double lastValidQuantity = 1;

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

	private boolean editable = true;

	protected void setEditable(boolean editable) {
		if (this.editable == editable)
			return;
		this.editable = editable;
		if (isFaded())
			return;
		applyEditableState();
	}

	protected boolean isEditable() {
		return editable;
	}

	@Override
	public void setFaded(boolean faded) {
		super.setFaded(faded);
		if (!faded) {
			applyEditableState();
		}
	}

	private void applyEditableState() {
		tariffCombo.setEnabled(editable);
		unitCombo.setEnabled(editable);
		quantity.setEditable(editable && unitCombo.getSelectedElement() != null);
		quantity.setEnabled(unitCombo.getSelectedElement() != null);
		productNameText.setEditable(editable);
		inputPriceFragmentTypeTable.setEditable(editable);
	}

	/**
	 * Creates a new {@link PriceCoordinate} for the customerGroupID,
	 * the tariffID and the currencyID found in the ArticleContainer.
	 * <p>
	 * Note, that this method only creates a valid {@link PriceCoordinate}
	 * if the {@link ArticleContainer} passed is either an {@link Order}
	 * or an {@link Offer}.
	 * </p>
	 *
	 * @return A new {@link PriceCoordinate}.
	 */
	protected IPriceCoordinate createPriceCoordinate()
	{
		Order order = (Order) (articleContainer instanceof Order ? articleContainer : null);
		Offer offer = (Offer) (articleContainer instanceof Offer ? articleContainer : null);

		CurrencyID currencyID = null;
		if (this.currency != null)
			currencyID = (CurrencyID) JDOHelper.getObjectId(this.currency);
		else {
			Currency currency = order != null ? order.getCurrency() : null;
			if (offer != null) {
				currency = offer.getCurrency();
			}
			if (currency != null)
				currencyID = (CurrencyID) JDOHelper.getObjectId(currency);
		}
		CustomerGroupID customerGroupID = null;
		if (this.customerGroupID != null)
			customerGroupID = this.customerGroupID;
		else {
			CustomerGroup customerGroup = order != null ? order.getCustomerGroup() : null;
			customerGroupID = (CustomerGroupID) JDOHelper.getObjectId(customerGroup);
			if (offer != null) {
				customerGroup = offer.getOrder().getCustomerGroup();
			}
			if (customerGroup != null)
				customerGroupID = (CustomerGroupID) JDOHelper.getObjectId(customerGroup);
		}

		Tariff tariff = tariffCombo.getSelectedElement();
		TariffID tariffID = (TariffID) JDOHelper.getObjectId(tariff);

		return new PriceCoordinate(customerGroupID, tariffID, currencyID);
	}

	private static final String[] FETCH_GROUPS_UNIT = {
		FetchPlan.DEFAULT, Unit.FETCH_GROUP_NAME
	};

//	private static long temporaryResultPriceConfigID = IDGenerator.nextID(PriceConfig.class);
	private static String temporaryResultPriceConfigID = "temporary.resultPriceConfigID"; //$NON-NLS-1$

	/**
	 * Though it was volatile I had strange timing issues and changed to synchronizing.
	 * I also initialized the value to true, because in theory (and unfortunately it also happened to me)
	 * setArticle can be called in the time the load job is scheduled (in the constructor)
	 * and the time the Job actually starts.
	 */
//	private volatile boolean loadDynamicProductType_inProcess = false;
	private boolean[] loadDynamicProductType_inProcess = new boolean[] {true};

	private void loadDynamicProductType()
	{
		setFaded(true);
		Job job = new Job(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.loadDynamicProductTypeJob.name")) { //$NON-NLS-1$
			@Override
			@Implement
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				boolean error = true;

				synchronized (loadDynamicProductType_inProcess) {
//					if (loadDynamicProductType_inProcess[0])
//						throw new IllegalStateException("Why the hell is this Job executed twice at the same time?!"); //$NON-NLS-1$
					loadDynamicProductType_inProcess[0] = true;
				}

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
					TariffOrderConfigModule cfMod = ConfigUtil.getUserCfMod(TariffOrderConfigModule.class,
							fetchGroups, -1, new NullProgressMonitor());
					Collections.sort(tariffs, cfMod.getTariffComparator());
					// Sorting done


					// check for the currencyID, that is needed for the
					// labelprovider of the table.
					checkCurrency(monitor);

					// check for the customerGroupID
					checkCustomerGroup(monitor);

					// check if the composite should be editable
					// with the data it has loaded
					final boolean shouldBeEditable = checkEditable(monitor);

					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							Article deferredArticle = null;
							try {
								if (tariffCombo.isDisposed())
									return;

								productTypeNameLabel.setText(
										dynamicProductType.getName().getText(NLLocale.getDefault().getLanguage()));

								tariffCombo.removeAll();
								tariffCombo.addElements(tariffs);
								if (!tariffs.isEmpty())
									tariffCombo.setSelection(0);

								inputPriceFragmentTypes = ipfts;
								inputPriceFragmentTypeTable.setCurrency(currency);
								inputPriceFragmentTypeTable.setInput(ipfts);
								fireCompositeContentChangeEvent();

								inputPriceFragmentTypeTable.layout(true, true);

								unitCombo.removeAll();
								unitCombo.addElements(units);
								unitCombo.setSelection(0);
								unitSelected();

								updateInputPriceFragmentTypes();
								setEditable(shouldBeEditable);

								setFaded(false);
							} finally {
								synchronized (loadDynamicProductType_inProcess) {
									deferredArticle = deferredArticleAssignment;
									deferredArticleAssignment = null;
									loadDynamicProductType_inProcess[0] = false;
								}
							}
							if (createArticle != null)
								_setArticle(createArticle);
							else if (deferredArticle != null)
								_setArticle(deferredArticle);
						}
					});

					error = false;
				} finally {
					if (error) {
						synchronized (loadDynamicProductType_inProcess) {
							loadDynamicProductType_inProcess[0] = false;
						}
					}
				}

				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();
	}

	private void unitSelected()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the UI thread!"); //$NON-NLS-1$

		Unit unit = unitCombo.getSelectedElement();
		if (unit != null) {
			double d;
			try {
				d = NumberFormatter.parseFloat(quantity.getText());
			} catch (ParseException e) {
				logger.warn("Failed to parse the quantity \"" + quantity.getText() + "\" as float value! Using 1 as fallback!", e); //$NON-NLS-1$ //$NON-NLS-2$
				d = 1;
			}
			quantity.setText(NumberFormatter.formatFloat(d, unit.getDecimalDigitCount()));
		}
		applyEditableState();
	}

	private boolean checkEditable(ProgressMonitor monitor) {
		if (!checkForEditable)
			return true;
		boolean finalized = true;
		if (articleContainer instanceof Offer) {
			finalized = ((Offer) articleContainer).isFinalized();
		} else if (createArticle != null) {
			finalized = getArticleContainerFinalized(createArticle, monitor);
		} else if (deferredArticleAssignment != null) {
			finalized = getArticleContainerFinalized(deferredArticleAssignment, monitor);
		} else if (article != null) {
			finalized = getArticleContainerFinalized(article, monitor);
		}
		return !finalized;
	}

	private boolean getArticleContainerFinalized(Article article, ProgressMonitor monitor) {
		try {
			// try to get the offer#finalized value
			return article.getOffer().isFinalized();
		} catch (JDODetachedFieldAccessException ex) {
			// it was not detached, we get the article with the offer
			Article _article = ArticleDAO.sharedInstance().getArticle(
					(ArticleID) JDOHelper.getObjectId(article), new String[] {FetchPlan.DEFAULT, Article.FETCH_GROUP_OFFER},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 100));
			return _article.getOffer().isFinalized();
		}
	}


	private void checkCurrency(ProgressMonitor monitor) {
		if (currency == null) {
			ArticleContainer ac = articleContainer;
			if (ac instanceof Order)
				currency = ((Order) ac).getCurrency();
			else if (ac instanceof Offer)
				currency = ((Offer) ac).getCurrency();
			else if (ac instanceof Invoice) {
				currency = ((Invoice) ac).getCurrency();
			} else {
				// try to get the Currency from the deferredArticle, very ugly, but should work
				if (createArticle != null) {
					/* Hmm, this was instantiated with an article,
					 * but strangely the Currency was not set.
					 * To be sure, we get the Article with correct fetch-groups.
					 */
					Article article = ArticleDAO.sharedInstance().getArticle(
							(ArticleID) JDOHelper.getObjectId(createArticle),
							new String[] {FetchPlan.DEFAULT, Article.FETCH_GROUP_CURRENCY},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 100));
					currency = article.getCurrency();
				} else if (deferredArticleAssignment != null) {
					// we are lucky ;-)
					// Seems that setArticle was called while we were loading other stuff
					currency = deferredArticleAssignment.getCurrency();
				}
				throw new IllegalStateException("Could not determine the Currency for the given articleContainer or article, this is a programming 'mistake' and timing issue!"); //$NON-NLS-1$
			}
		}
	}

	private void checkCustomerGroup(ProgressMonitor monitor) {
		if (customerGroupID == null) {
			CustomerGroup customerGroup = null;
			if (articleContainer instanceof Order) {
				customerGroup = ((Order) articleContainer).getCustomerGroup();
			} else if (articleContainer instanceof Offer) {
				customerGroup = ((Offer) articleContainer).getOrder().getCustomerGroup();
			} else if (createArticle != null) {
				customerGroup = getArticleCustomerGroup(createArticle, monitor);
			} else if (deferredArticleAssignment != null) {
				customerGroup = getArticleCustomerGroup(deferredArticleAssignment, monitor);
			}
			if (customerGroup != null)
				customerGroupID = (CustomerGroupID) JDOHelper.getObjectId(customerGroup);
		}
	}

	private CustomerGroup getArticleCustomerGroup(Article article, ProgressMonitor monitor) {
		try {
			return article.getOrder().getCustomerGroup();
		} catch (JDODetachedFieldAccessException ex) {
			// not detached, need to access it
			Article _article = ArticleDAO.sharedInstance().getArticle(
					(ArticleID) JDOHelper.getObjectId(article),
					new String[] {FetchPlan.DEFAULT, Article.FETCH_GROUP_ORDER, Order.FETCH_GROUP_CUSTOMER_GROUP},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
			return _article.getOrder().getCustomerGroup();
		}
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

		synchronized (loadDynamicProductType_inProcess) {
			if (loadDynamicProductType_inProcess[0]) {
				deferredArticleAssignment = article;
				return;
			}
		}
		_setArticle(article);
	}

	protected void updateProductNameUI()
	{
		productNameText.setText(productName.getText(NLLocale.getDefault().getLanguage()));
	}

	protected Article article = null;

	protected void _setArticle(Article _article)
	{
		if (!_article.getProductType().equals(dynamicProductType))
			throw new IllegalArgumentException("article.productType != this.productType : " + _article.getProductType() + " != " + dynamicProductType); //$NON-NLS-1$ //$NON-NLS-2$

		this.article = _article;

		DynamicProduct product = (DynamicProduct) article.getProduct();
		if (!tariffCombo.selectElement(article.getTariff()))
			throw new IllegalStateException("Tariff not in combo!"); // TODO we should handle this situation - it might happen //$NON-NLS-1$

		productName.copyFrom(product.getName());
		updateProductNameUI();
		productNameModified = false;

		if (!unitCombo.selectElement(product.getUnit()))
			throw new IllegalStateException("Unit not in combo!"); // TODO we should handle this situation - it might happen //$NON-NLS-1$

		quantity.setText(NumberFormatter.formatFloat(product.getQuantityAsDouble(), product.getUnit().getDecimalDigitCount()));

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
					monitor.beginTask("Load article", 100);
					try {
						ArticleID articleID = (ArticleID) JDOHelper.getObjectId(article);
						final Article articleWithPriceFragments = ArticleDAO.sharedInstance().getArticle(
								articleID,
								new String[] { FetchPlan.DEFAULT, Article.FETCH_GROUP_PRODUCT, DynamicProduct.FETCH_GROUP_SINGLE_PRICE, Price.FETCH_GROUP_THIS_PRICE },
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, 100));

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
					} finally {
						monitor.done();
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

