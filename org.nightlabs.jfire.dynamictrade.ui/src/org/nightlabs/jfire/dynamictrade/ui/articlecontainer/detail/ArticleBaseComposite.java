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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.FadeableComposite;
import org.nightlabs.base.ui.composite.MessageComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.base.ui.resource.SharedImages;
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
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCell;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCoordinate;
import org.nightlabs.jfire.accounting.gridpriceconfig.StablePriceConfig;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.accounting.id.TariffID;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.dynamictrade.DynamicProductInfo;
import org.nightlabs.jfire.dynamictrade.accounting.priceconfig.DynamicTradePriceConfig;
import org.nightlabs.jfire.dynamictrade.accounting.priceconfig.PriceCalculator;
import org.nightlabs.jfire.dynamictrade.recurring.DynamicProductTypeRecurringArticle;
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
import org.nightlabs.script.JSHTMLExecuter;
import org.nightlabs.util.NLLocale;
import org.nightlabs.util.Util;

public abstract class ArticleBaseComposite
extends FadeableComposite
{
	private static Logger logger = Logger.getLogger(ArticleBaseComposite.class);

	private String nameMessageText;

	protected ArticleContainer articleContainer;
	protected ProductTypeID productTypeID;

	protected Label productTypeNameLabel;
	protected XComboComposite<Tariff> tariffCombo;
	protected XComboComposite<Unit> unitCombo;
	protected Text quantity;
	protected Text productNameText;
	protected MessageComposite nameMessageLabel;
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
	 * Whether to check for a script syntax in the name of the dynamic product type
	 */
	private boolean isScriptable = false;


	public boolean isScriptable() {
		return isScriptable;
	}

	public void setScriptable(final boolean isScriptable) {
		this.isScriptable = isScriptable;
	}

	/**
	 * Create a new {@link ArticleBaseComposite} with a {@link DynamicProductType}.
	 *
	 * @param parent
	 * @param articleContainer
	 * @param productType
	 */
	public ArticleBaseComposite(final Composite parent, final ArticleContainer articleContainer, final DynamicProductType productType)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.articleContainer = articleContainer;
		this.productTypeID = (ProductTypeID) JDOHelper.getObjectId(productType);
	}

	public ArticleBaseComposite(final Composite parent, final ArticleContainer articleContainer, final Article article)
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
			public String getText(final Object element)
			{
				return ((Tariff)element).getName().getText();
			}
		});
		final Tariff dummy = new Tariff(TariffID.create("dummy.org", "_dummy_")); //$NON-NLS-1$ //$NON-NLS-2$
		dummy.getName().setText(NLLocale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.pseudoTariff_loading")); //$NON-NLS-1$
		tariffCombo.addElement(dummy);
		tariffCombo.selectElement(dummy);
		tariffCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event)
			{
				updateInputPriceFragmentTypes();
			}
		});

		final SashForm comp2 = new SashForm(this, SWT.HORIZONTAL);
//		comp2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp2.setLayoutData(new GridData(GridData.FILL_BOTH));

		final XComposite comp3 = new XComposite(comp2, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		comp3.getGridLayout().numColumns = 3;


		final XComposite compName = new XComposite(comp3, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		compName.setLayout(new GridLayout(1,false));

		nameMessageLabel = new MessageComposite(compName, SWT.NONE, "", MessageType.INFORMATION); //$NON-NLS-1$
		nameMessageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	    final GridData data = (GridData)nameMessageLabel.getLayoutData();
	    data.exclude = true;
		nameMessageLabel.setVisible(false);
		productNameText = new Text(compName, getBorderStyle() | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		productNameText.setLayoutData(new GridData(GridData.FILL_BOTH));
		productNameText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e)
			{
				productName.setText(NLLocale.getDefault().getLanguage(), productNameText.getText());
				productNameModified = true;
			}
		});




		final XComposite buttonComp = new XComposite(comp3, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		buttonComp.getGridLayout().numColumns = 1;
//		productNameText.setData(IToolkit.KEY_DRAW_BORDER, IToolkit.TEXT_BORDER);
		((GridData)productNameText.getLayoutData()).heightHint = productTypeNameLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT).y * 3;
		productNameDialogButton = new Button(buttonComp, SWT.PUSH);
		productNameDialogButton.setText("..."); //$NON-NLS-1$
		productNameDialogButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		productNameDialogButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent arg0)
			{
				if ((Window.OK == new ProductNameDialog(getShell(), productName, editable,ProductNameDialogType.TEXT_EDIT).open())) {
					updateProductNameUI();
					productNameModified = true;
				}
			}
		});
		// if the page supports scripting adds the scripting preview button
		if(isScriptable())
		{
			final Button previewScriptButton = new Button(buttonComp, SWT.PUSH);
			previewScriptButton.setImage(SharedImages.PREVIEW_16x16.createImage());
			previewScriptButton.setToolTipText("Preview Script");
			previewScriptButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
			previewScriptButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent arg0)
				{
					ProductNameDialogType productNameDialogType;
					// if the User has entered a valid script then a preview is shown
					final JSHTMLExecuter script = new JSHTMLExecuter();
					if(script.containsValidScript(productNameText.getText()) > 0)
						productNameDialogType = ProductNameDialogType.SCRIPT_PREVIEW;
					else
						productNameDialogType = ProductNameDialogType.SCRIPT_EDIT;

					if ((Window.OK == new ProductNameDialog(getShell(), productName, editable,productNameDialogType).open())) {
						updateProductNameUI();
						productNameModified = true;
					}
				}
			});
		}

		inputPriceFragmentTypeTable = new InputPriceFragmentTypeTable(comp2) {
			@Override
			protected void inputPriceFragmentTypeModified(final InputPriceFragmentType inputPriceFragmentType)
			{
				if (dynamicTradePriceConfig == null)
					return;

				final IPriceCoordinate priceCoordinate = createPriceCoordinate();
				final FormulaCell formulaCell = dynamicTradePriceConfig.createFormulaCell(priceCoordinate);
				formulaCell.setFormula(inputPriceFragmentType.getPriceFragmentType(), String.valueOf(inputPriceFragmentType.getAmount()));
				inputPriceFragmentTypeModified = true;

				try {
					priceCalculator.calculatePrices();
				} catch (final PriceCalculationException e) {
					throw new RuntimeException(e);
				}

			}
		};
		inputPriceFragmentTypeTable.getGridData().grabExcessVerticalSpace = false;

		comp2.setWeights(new int[] { 1, 1 });

		unitCombo = new XComboComposite<Unit>(comp1, getBorderStyle() | SWT.READ_ONLY, new LabelProvider() {
			@Override
			public String getText(final Object element)
			{
				return ((Unit)element).getName().getText();
			}
		});
		unitCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				unitSelected();
			}
		});

		quantity = new Text(comp1, getBorderStyle());
		quantity.setText(NumberFormatter.formatFloat(1, 2));
		final GridData gd = new GridData();
		gd.widthHint = 200;
		quantity.setLayoutData(gd);
		quantity.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent event) {
				final Unit unit = unitCombo.getSelectedElement();
				if (unit == null)
					return;

				final String quantityStr = quantity.getText();
				if (!"".equals(quantityStr)) { //$NON-NLS-1$
					try {
						lastValidQuantity = NumberFormatter.parseFloat(quantityStr);
					} catch (final ParseException e) {
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
		if(isScriptable())
		{
			showTextNameMessage(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.textNameMessage"),MessageType.INFORMATION); //$NON-NLS-1$
			this.nameMessageText = nameMessageLabel.getMessage();
			this.productNameText.addFocusListener(  new FocusListener(){
				/** remove the error message shown once the user clicks
				on the text box to enter the name again*/
				@Override
				public void focusGained(final FocusEvent arg0) {
					showTextNameMessage(nameMessageText,MessageType.INFORMATION);
				}
				@Override
				public void focusLost(final FocusEvent arg0) {
					// TODO Auto-generated method stub

				}

			});
		}

	}

	private double lastValidQuantity = 1;

	protected boolean inputPriceFragmentTypeModified = false;
	protected boolean productNameModified = false;

	protected void createUI_additionalElements_comp1(final Composite parent)
	{
	}

	protected DynamicProductType dynamicProductType;
	protected DynamicTradePriceConfig dynamicTradePriceConfig;
	protected StablePriceConfig resultPriceConfig;
	protected PriceCalculator priceCalculator;
	protected List<InputPriceFragmentType> inputPriceFragmentTypes;

	protected I18nTextBuffer productName = new I18nTextBuffer();

	public I18nTextBuffer getProductName() {
		return productName;
	}

	private boolean editable = true;

	protected void setEditable(final boolean editable) {
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
	public void setFaded(final boolean faded) {
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
		final Order order = (Order) (articleContainer instanceof Order ? articleContainer : null);
		final Offer offer = (Offer) (articleContainer instanceof Offer ? articleContainer : null);

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

		final Tariff tariff = tariffCombo.getSelectedElement();
		final TariffID tariffID = (TariffID) JDOHelper.getObjectId(tariff);

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
		final Job job = new Job(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.loadDynamicProductTypeJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final ProgressMonitor monitor) throws Exception
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

					// TODO we should only list tariffs that are available to the current user according to the TariffUserSet. Marco.

					dynamicTradePriceConfig = (DynamicTradePriceConfig) dynamicProductType.getInnerPriceConfig();
					final List<Tariff> tariffs = new ArrayList<Tariff>(dynamicTradePriceConfig.getTariffs());

					resultPriceConfig = new StablePriceConfig(PriceConfigID.create(IDGenerator.getOrganisationID(), temporaryResultPriceConfigID));
					dynamicProductType.setPackagePriceConfig(resultPriceConfig);
					resultPriceConfig.adoptParameters(dynamicTradePriceConfig);

					priceCalculator = new PriceCalculator(
							dynamicProductType,
							new CustomerGroupMapper(new ArrayList<CustomerGroupMapping>(0)), // no crosstrade supported => no mappings needed!
							new TariffMapper(new ArrayList<TariffMapping>(0)));

					priceCalculator.preparePriceCalculation();
					try {
						priceCalculator.calculatePrices();
					} catch (final PriceCalculationException e) {
						throw new RuntimeException(e);
					}

					Collections.sort(tariffs, new Comparator<Tariff>() {
						public int compare(final Tariff t1, final Tariff t2)
						{
							return t1.getName().getText().compareTo(t2.getName().getText());
						}
					});

					final Set<PriceFragmentType> pfts = dynamicTradePriceConfig.getInputPriceFragmentTypes();
					final List<InputPriceFragmentType> ipfts = new ArrayList<InputPriceFragmentType>(pfts.size());
					for (final PriceFragmentType priceFragmentType : pfts)
						ipfts.add(new InputPriceFragmentType(priceFragmentType));

					Collections.sort(ipfts, new Comparator<InputPriceFragmentType>() {
						public int compare(final InputPriceFragmentType ipft1, final InputPriceFragmentType ipft2)
						{
							return ipft1.getPriceFragmentType().getName().getText().compareTo(ipft2.getPriceFragmentType().getName().getText());
						}
					});

					final List<Unit> units = UnitDAO.sharedInstance().getUnits(FETCH_GROUPS_UNIT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
					Collections.sort(units, new Comparator<Unit>() {
						public int compare(final Unit u1, final Unit u2)
						{
							return u1.getName().getText().compareTo(u2.getName().getText());
						}
					});

					// Sort the tariffs according to the config module
					final String[] fetchGroups = new String[] { TariffOrderConfigModule.FETCH_GROUP_TARIFF_ORDER_CONFIG_MODULE , FetchPlan.DEFAULT };
					final TariffOrderConfigModule cfMod = ConfigUtil.getUserCfMod(TariffOrderConfigModule.class,
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

								if (tariffs.isEmpty()) {
									showMessageNoPricesAvailable();
									return;
								}
								if (currency != null) { // should always be true, but we play safe.
									// check, if the price configuration contains this currency.
									final CurrencyID currencyID = CurrencyID.create(currency.getCurrencyID());
									if (dynamicTradePriceConfig.getCurrency(currencyID, false) == null) {
										showMessageNoPricesAvailable();
										return;
									}
								}

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

	private void _relayout()
	{
		Composite parent = getParent();
		while (parent.getParent() != null) {
			parent = parent.getParent();
		}
		parent.layout(true, true);
	}

	private void showMessageNoPricesAvailable()
	{
		// remove all UI that has been created previously
		for (final Control child : getChildren())
			child.dispose();


		final MessageComposite mc = new MessageComposite(
				this, SWT.NONE,
				String.format(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.message"), dynamicProductType.getName().getText()), //$NON-NLS-1$
				MessageType.WARNING
		);
		mc.adaptToToolkit();

//		Text txt = new Text(this, SWT.WRAP);
//		txt.setText(String.format("The price configuration of the product type \"%s\" contains no tariffs or no currencies; or they are not available to you.", dynamicProductType.getName().getText()));
		_relayout();
	}

	private void unitSelected()
	{
		if (Display.getCurrent() == null)
			throw new IllegalStateException("Wrong thread! This method must be called on the UI thread!"); //$NON-NLS-1$

		final Unit unit = unitCombo.getSelectedElement();
		if (unit != null) {
			double d;
			try {
				d = NumberFormatter.parseFloat(quantity.getText());
			} catch (final ParseException e) {
				logger.warn("Failed to parse the quantity \"" + quantity.getText() + "\" as float value! Using 1 as fallback!", e); //$NON-NLS-1$ //$NON-NLS-2$
				d = 1;
			}
			quantity.setText(NumberFormatter.formatFloat(d, unit.getDecimalDigitCount()));
		}
		applyEditableState();
	}

	private boolean checkEditable(final ProgressMonitor monitor) {
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

	private boolean getArticleContainerFinalized(final Article article, final ProgressMonitor monitor) {
		try {
			// try to get the offer#finalized value
			return article.getOffer().isFinalized();
		} catch (final JDODetachedFieldAccessException ex) {
			// it was not detached, we get the article with the offer
			final Article _article = ArticleDAO.sharedInstance().getArticle(
					(ArticleID) JDOHelper.getObjectId(article), new String[] {FetchPlan.DEFAULT, Article.FETCH_GROUP_OFFER},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 100));
			return _article.getOffer().isFinalized();
		}
	}


	private void checkCurrency(final ProgressMonitor monitor) {
		if (currency == null) {
			final ArticleContainer ac = articleContainer;
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
					final Article article = ArticleDAO.sharedInstance().getArticle(
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

	private void checkCustomerGroup(final ProgressMonitor monitor) {
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

	private CustomerGroup getArticleCustomerGroup(final Article article, final ProgressMonitor monitor) {
		try {
			return article.getOrder().getCustomerGroup();
		} catch (final JDODetachedFieldAccessException ex) {
			// not detached, need to access it
			final Article _article = ArticleDAO.sharedInstance().getArticle(
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

		final IPriceCoordinate priceCoordinate = createPriceCoordinate();
		final PriceCell priceCell = resultPriceConfig.getPriceCell(priceCoordinate, false);
		this.setEnabled(priceCell != null);
		if (priceCell == null) {
			MessageDialog.openError(getShell(), Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.errorNoPriceCellDialog.title"), Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.errorNoPriceCellDialog.message")); //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}

		for (final InputPriceFragmentType ipft : inputPriceFragmentTypes) {
			final long amount = priceCell.getPrice().getAmount(ipft.getPriceFragmentType());
			ipft.setAmount(amount);
		}

		inputPriceFragmentTypeTable.refresh();
	}

	private Article deferredArticleAssignment = null;

	public void setArticle(final Article article)
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

	protected void _setArticle(final Article _article)
	{
		if (!_article.getProductType().equals(dynamicProductType))
			throw new IllegalArgumentException("article.productType != this.productType : " + _article.getProductType() + " != " + dynamicProductType); //$NON-NLS-1$ //$NON-NLS-2$

		this.article = _article;

		final DynamicProduct product = (DynamicProduct) article.getProduct();
		if (!tariffCombo.selectElement(article.getTariff()))
			throw new IllegalStateException("Tariff not in combo!"); // TODO we should handle this situation - it might happen //$NON-NLS-1$

		DynamicProductInfo dynamicProductInfo;
		if (product != null)
			dynamicProductInfo = product;
		else
			dynamicProductInfo = (DynamicProductInfo) article;




		productName.copyFrom(dynamicProductInfo.getName());
		updateProductNameUI();
		productNameModified = false;

		if (!unitCombo.selectElement(dynamicProductInfo.getUnit()))
			throw new IllegalStateException("Unit not in combo!"); // TODO we should handle this situation - it might happen //$NON-NLS-1$

		quantity.setText(NumberFormatter.formatFloat(dynamicProductInfo.getQuantityAsDouble(), dynamicProductInfo.getUnit().getDecimalDigitCount()));

		// product.singlePrice.fragments is probably not detached in the ArticleEdit's Articles
		try {
			for (final InputPriceFragmentType ipft : inputPriceFragmentTypes) {
				final long amount = dynamicProductInfo.getSinglePrice().getAmount(ipft.getPriceFragmentType());
				final PriceCell priceCell = resultPriceConfig.getPriceCell(createPriceCoordinate(), false);
				if (priceCell != null)
					priceCell.getPrice().setAmount(ipft.getPriceFragmentType(), amount);

				ipft.setAmount(amount);
			}

			inputPriceFragmentTypeTable.refresh();
			inputPriceFragmentTypeModified = false;
		} catch (final JDODetachedFieldAccessException x) {
			setFaded(true);
			final Job job = new Job(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.loadArticleWithPriceFragmentsJob.name")) { //$NON-NLS-1$
				@Override
				protected IStatus run(final ProgressMonitor monitor) throws Exception
				{
					monitor.beginTask(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.ArticleBaseComposite.task.loadArticle"), 100); //$NON-NLS-1$
					try {
						final ArticleID articleID = (ArticleID) JDOHelper.getObjectId(article);
						final Article articleWithPriceFragments = ArticleDAO.sharedInstance().getArticle(
								articleID,
								new String[] { FetchPlan.DEFAULT, DynamicProductTypeRecurringArticle.FETCH_GROUP_DYNAMIC_PRODUCT_TYPE_RECURRING_ARTICLE_SINGLEPRICE,
										Article.FETCH_GROUP_PRODUCT, DynamicProduct.FETCH_GROUP_SINGLE_PRICE, Price.FETCH_GROUP_THIS_PRICE },
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
								new SubProgressMonitor(monitor, 100));

						Display.getDefault().asyncExec(new Runnable()
						{
							public void run()
							{
								final DynamicProduct product = (DynamicProduct) articleWithPriceFragments.getProduct();
								DynamicProductInfo dynamicProductInfo;
								if (product != null)
									dynamicProductInfo = product;
								else
									dynamicProductInfo = (DynamicProductInfo) articleWithPriceFragments;

								for (final InputPriceFragmentType ipft : inputPriceFragmentTypes) {
									final long amount = dynamicProductInfo.getSinglePrice().getAmount(ipft.getPriceFragmentType());
									final PriceCell priceCell = resultPriceConfig.getPriceCell(createPriceCoordinate(), false);
									if (priceCell != null)
										priceCell.getPrice().setAmount(ipft.getPriceFragmentType(), amount);

									ipft.setAmount(amount);
								}

								inputPriceFragmentTypeTable.refresh();
								inputPriceFragmentTypeModified = false;
								setFaded(false);
							}
						});
					} catch (final Exception x) {
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


	/**
	 * shows the statues Text Message Label above the name text dialog,
	 * this method should be called after the GUI has been created.
	 */
	public void showTextNameMessage(final String message,final MessageType msgType)
	{
		if(nameMessageLabel != null)
		{
			if(!nameMessageLabel.isVisible())
			{
				final GridData data = (GridData)nameMessageLabel.getLayoutData();
				data.exclude = false;
				nameMessageLabel.setVisible(true);
			}
			nameMessageLabel.setMessage(message, msgType);
			productNameText.setToolTipText(message);
		}
	}

}

