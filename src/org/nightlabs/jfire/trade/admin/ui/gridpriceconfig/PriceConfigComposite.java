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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.security.auth.login.LoginException;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.TariffMapper;
import org.nightlabs.jfire.accounting.dao.TariffMappingDAO;
import org.nightlabs.jfire.accounting.gridpriceconfig.AssignInnerPriceConfigCommand;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCalculationException;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCalculator;
import org.nightlabs.jfire.accounting.priceconfig.FetchGroupsPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.IInnerPriceConfig;
import org.nightlabs.jfire.accounting.priceconfig.PriceConfigName;
import org.nightlabs.jfire.accounting.priceconfig.id.PriceConfigID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.CustomerGroupMapper;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.wizard.AbstractChooseGridPriceConfigWizard;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.dao.CustomerGroupMappingDAO;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * This composite can be used to display and edit the whole grid-price-configuration
 * of one ProductType.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class PriceConfigComposite extends XComposite
{
	private static final Logger logger = Logger.getLogger(PriceConfigComposite.class);

	private ProductTypeSelector productTypeSelector;
	private DimensionValueSelector dimensionValueSelector;
	private DimensionXYSelector dimensionXYSelector;
	private PriceConfigGrid priceConfigGrid;
	private CellDetail cellDetail;
	private PriceCalculator priceCalculator;


	// -------> Kai: 2009-11-13
	// Handles property changes related to all components in this PriceConfigComposite more efficiently.
	// Was previously handled specifically in PriceConfigGrid.
	public static final String PROPERTY_CHANGE_KEY_PRICE_CONFIG_CHANGED = "priceConfigChanged"; //$NON-NLS-1$
	public static final String PROPERTY_CHANGE_KEY_PRICE_CONFIG_ERROR = "priceConfigError"; //$NON-NLS-1$
	public static final String PROPERTY_CHANGE_KEY_NO_PRICE_CONFIG_COMPOSITE = "noPriceConfigComp"; //$NON-NLS-1$

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	// <-------

	protected ProductTypeSelector createProductTypeSelector(Composite parent)
	{
		return new ProductTypeSelectorListImpl(parent, SWT.NONE);
	}

	/**
	 * @return Returns the productTypeSelector.
	 */
	public ProductTypeSelector getProductTypeSelector()
	{
		return productTypeSelector;
	}

	protected DimensionValueSelector createDimensionValueSelector(Composite parent)
	{
		return new DimensionValueSelectorComboImpl(parent, SWT.NONE);
	}

	/**
	 * @return Returns the dimensionValueSelector.
	 */
	public DimensionValueSelector getDimensionValueSelector()
	{
		return dimensionValueSelector;
	}

	protected DimensionXYSelector createDimensionXYSelector(Composite parent)
	{
		return new DimensionXYSelectorComboImpl(parent, SWT.NONE, getDimensionValueSelector());
	}

	protected PriceConfigGrid createPriceConfigGrid(Composite parent)
	{
		return new PriceConfigGrid(
				parent,
				getProductTypeSelector(),
				getDimensionValueSelector(),
				getDimensionXYSelector());
	}


	protected CellDetail createCellDetail(Composite parent)
	{
		return new CellDetail(parent, SWT.NONE, getPriceConfigGrid(), this);
	}

	/**
	 * @return Returns the priceConfigGrid.
	 */
	public PriceConfigGrid getPriceConfigGrid()
	{
		return priceConfigGrid;
	}

	/**
	 * @return Returns the dimensionXYSelector.
	 */
	public DimensionXYSelector getDimensionXYSelector()
	{
		return dimensionXYSelector;
	}

	/**
	 * @return Returns the cellDetail.
	 */
	public CellDetail getCellDetail()
	{
		return cellDetail;
	}

	/**
	 * @return Returns the priceCalculator.
	 */
	public PriceCalculator getPriceCalculator()
	{
		return priceCalculator;
	}

	public PriceConfigComposite(Composite parent) {
		this(parent, null);
	}

	private IDirtyStateManager dirtyStateManager;
	public IDirtyStateManager getDirtyStateManager() {
		return dirtyStateManager;
	}

	private boolean changed = true;
	public boolean isChanged() {
		return changed;
	}

	private Composite productTypeNotSetComposite;

	private Composite stackWrapper;
	private StackLayout stackLayout;
	private Composite priceConfigEditComposite = null;
	private PriceConfigInInnerProductTypeNotEditableComposite priceConfigInInnerProductTypeNotEditableComposite = null;
	private Composite noPriceConfigAssignedComposite = null;
	public PriceConfigComposite(Composite parent, IDirtyStateManager dirtyStateManager)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		this.dirtyStateManager = dirtyStateManager;

		stackWrapper = new XComposite(this, SWT.NONE);
		stackLayout = new StackLayout();
		stackWrapper.setLayout(stackLayout);
		stackWrapper.setLayoutData(new GridData(GridData.FILL_BOTH));

//		priceConfigEditComposite = createPriceConfigEditComposite(stackWrapper);
//		noPriceConfigAssignedComposite = createNoPriceConfigAssignedComposite(stackWrapper);
//		stackLayout.topControl = priceConfigEditComposite;
		productTypeNotSetComposite = new XComposite(stackWrapper, SWT.NONE);
		new Label(productTypeNotSetComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite.label.text")); //$NON-NLS-1$
		stackLayout.topControl = productTypeNotSetComposite;

		// Forces the composites to be created.
		getPriceConfigEditComposite();

		// Incorporates the inactivity-timeout, to check if formula is valid while idle after typing.
		initKeyTimeOutListener();
	}

	/**
	 * Initialises the inactivity-timeout, to check if formula is valid while idle after typing.
	 * Should call this only after getPriceConfigEditComposite() has been executed.
	 */
	protected void initKeyTimeOutListener() {
		// Kai 2009-11-19:
		// Incorporating the inactivity-timeout, to check if formula is valid.
		// Alternatively, we can use the TimerText class. But that would require further amendments to
		// the JSEdiorComposite class, which should then extend the TimerTextClass and manage its Document.
		keyTimeOutListener = new KeyTimeOutListener();
		timer = new Timer(TIMEOUT, keyTimeOutListener);
		cellDetail.getCellDetailText().addKeyListener(keyTimeOutListener);
		cellDetail.getCellDetailFallbackText().addKeyListener(keyTimeOutListener);

		timer.setRepeats(true);
	}

	// Kai 2009-11-19:
	// Very lightweight machination (instead of calibrating the TimerTextClass?), in addition to the initKeyTimeOutListener().
	public static final int TIMEOUT = 1005;	// milliseconds. Seems to be a comfortable timeout delay.
	private Timer timer;
	private KeyTimeOutListener keyTimeOutListener;
	private class KeyTimeOutListener implements KeyListener, ActionListener {
		private Object delayedModifyListenersMutex = new Object();
		private long lastEventMarkTm = System.currentTimeMillis();

		@Override
		public void keyPressed(KeyEvent event)  {}

		@Override
		public void keyReleased(KeyEvent event) { recordLastEvent(event); }

		private void recordLastEvent(KeyEvent event) {
			synchronized (delayedModifyListenersMutex) {
				if (!timer.isRunning())
					timer.start();

				lastEventMarkTm = System.currentTimeMillis();
			}
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			synchronized (delayedModifyListenersMutex) {
				if (System.currentTimeMillis() - lastEventMarkTm >= TIMEOUT) {
					timer.stop();

					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							cellDetail.forceFocus(); // Need this to trigger the formula checking. The current listeners will know how to react already.
							cellDetail.setFocus();   // To return the focus and cursor back to the last typed position.
						}
					});
				}
			}
		}
	}


	protected Composite createLeftCarrierComposite(Composite parent)
	{
		return new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
	}

	protected Composite createRightCarrierComposite(Composite parent)
	{
		return new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
	}

	protected Composite getPriceConfigEditComposite()
	{
		if (priceConfigEditComposite == null)
			priceConfigEditComposite = createPriceConfigEditComposite(stackWrapper);

		return priceConfigEditComposite;
	}

	protected PriceConfigInInnerProductTypeNotEditableComposite getPriceConfigInInnerProductTypeNotEditableComposite()
	{
		if (priceConfigInInnerProductTypeNotEditableComposite == null)
			priceConfigInInnerProductTypeNotEditableComposite = createPriceConfigInInnerProductTypeNotEditableComposite(stackWrapper);

		return priceConfigInInnerProductTypeNotEditableComposite;
	}

	class PriceConfigInInnerProductTypeNotEditableComposite extends XComposite
	{
		private Text priceConfigName;

		public PriceConfigInInnerProductTypeNotEditableComposite(Composite parent) {
			super(parent, SWT.NONE);
			getGridLayout().numColumns = 2;

			Label title = new Label(this, SWT.NONE);
			title.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite.label.title")); //$NON-NLS-1$
			priceConfigName = new Text(this, getBorderStyle() | SWT.READ_ONLY);
			priceConfigName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Label info = new Label(this, SWT.NONE | SWT.WRAP);
			GridData gdInfo = new GridData(GridData.FILL_HORIZONTAL);
			gdInfo.horizontalSpan = 2;
			info.setLayoutData(gdInfo);

			info.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite.label.info.text")); //$NON-NLS-1$
		}

		public void setPackageProductType(ProductType packageProductType)
		{
			if (packageProductType == null)
				priceConfigName.setText(""); //$NON-NLS-1$
			else
				priceConfigName.setText(packageProductType.getInnerPriceConfig().getName().getText());
		}
	}

	protected PriceConfigInInnerProductTypeNotEditableComposite createPriceConfigInInnerProductTypeNotEditableComposite(Composite parent)
	{
		return new PriceConfigInInnerProductTypeNotEditableComposite(parent);
	}

	protected Composite createPriceConfigEditComposite(Composite parent)
	{
		SashForm sfLeftRight = new SashForm(parent, SWT.NONE | SWT.HORIZONTAL);
		sfLeftRight.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite left = createLeftCarrierComposite(sfLeftRight);
		Composite right = createRightCarrierComposite(sfLeftRight);
		sfLeftRight.setWeights(new int[] {1, 2});

		productTypeSelector = createProductTypeSelector(left);
		dimensionValueSelector = createDimensionValueSelector(left);
		dimensionXYSelector = createDimensionXYSelector(right);

		SashForm sfGrid = new SashForm(right, SWT.NONE | SWT.VERTICAL);
		sfGrid.setLayoutData(new GridData(GridData.FILL_BOTH));

		priceConfigGrid = createPriceConfigGrid(sfGrid);
		priceConfigGrid.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt)
			{
				propertyChangeSupport.firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());

				if (dirtyStateManager != null)
					dirtyStateManager.markDirty();
			}
		});

		cellDetail = createCellDetail(sfGrid);
		// add listeners to notify dirty state
//		cellDetail.getCellDetailText().addModifyListener(cellEditModifyListener);
//		cellDetail.getCellDetailFallbackText().addModifyListener(cellEditModifyListener);

//		cellDetail.getCellDetailText().getDocument().addDocumentListener(cellEditModifyListener);
//		cellDetail.getCellDetailFallbackText().getDocument().addDocumentListener(cellEditModifyListener);

//		dimensionValueSelector.addPropertyChangeListener(
//			DimensionValueSelector.PROPERTYCHANGEKEY_ADDDIMENSIONVALUE, new PropertyChangeListener() {
//				public void propertyChange(PropertyChangeEvent evt) {
//					if (priceCalculator != null) {
//						priceCalculator.preparePriceCalculation_createPackagedResultPriceConfigs();
//					}
//				}
//			}
//		);

		dimensionValueSelector.addPropertyChangeListener(
				DimensionValueSelector.PROPERTYCHANGEKEY_ADDDIMENSIONVALUE,
				dimensionValueChangeListener);

		dimensionValueSelector.addPropertyChangeListener(
				DimensionValueSelector.PROPERTYCHANGEKEY_REMOVEDIMENSIONVALUE,
				dimensionValueChangeListener);

		sfGrid.setWeights(new int[] {1, 1});

		return sfLeftRight;
	}

	protected Composite getNoPriceConfigAssignedComposite()
	{
		if (noPriceConfigAssignedComposite == null)
			noPriceConfigAssignedComposite = createNoPriceConfigAssignedComposite(stackWrapper);

		return noPriceConfigAssignedComposite;
	}

	protected Composite createNoPriceConfigAssignedComposite(Composite parent)
	{
		Composite noPriceConfigComp = new XComposite(parent, SWT.NONE);
		Label label = new Label(noPriceConfigComp, SWT.NONE);
		label.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite.noPriceConfigAssignedLabel.text")); //$NON-NLS-1$
		Button assignButton = new Button(noPriceConfigComp, SWT.NONE);
		assignButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite.assignButton.text")); //$NON-NLS-1$
		assignButton.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				AbstractChooseGridPriceConfigWizard wizard = createChoosePriceConfigWizard(
						(ProductTypeID) JDOHelper.getObjectId(packageProductType.getExtendedProductType()));
				DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
//				dialog.setTitle("Choose Price Configuration");
				int returnCode = dialog.open();
				if (returnCode == Window.OK) {
					assignNewPriceConfig(wizard);

//					if (dirtyStateManager instanceof SectionPart) {
//						SectionPart sectionPart = (SectionPart) dirtyStateManager;
//						sectionPart.getSection().setText(packageProductType.getInnerPriceConfig().getName().getText());
//					}

					// Kai: 2009-11-13
					// A revision of the above codes: Without explicitly casting dirtyStateManager as a SectionPart.
					// SEE -- The corresponding registered listener in AbstractGridPriceConfigSection.
					// BUT -- Is the triggering of the properChangeListener appropriate here?? Mebbe other more appropriate listeners are avaiable.
					String innerPriceConfigName = packageProductType.getInnerPriceConfig().getName().getText();
					propertyChangeSupport.firePropertyChange(PriceConfigComposite.PROPERTY_CHANGE_KEY_NO_PRICE_CONFIG_COMPOSITE, null, innerPriceConfigName);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		return noPriceConfigComp;
	}

	protected static final String[] FETCH_GROUPS_INNER_PRICE_CONFIG_FOR_EDITING = {
		FetchPlan.DEFAULT,
		FetchGroupsPriceConfig.FETCH_GROUP_EDIT};

	protected abstract IInnerPriceConfig retrieveInnerPriceConfigForEditing(PriceConfigID priceConfigID);

//	private ModifyListener cellEditModifyListener = new ModifyListener(){
//		public void modifyText(ModifyEvent e) {
//			if (dirtyStateManager != null)
//				dirtyStateManager.markDirty();
//		}
//	};

//	private boolean initalState = false;
//	/**
//	 * sets the initalState, to determine that while in this state no calls to the
//	 * dirtyStateManager are performed, to avoid dirty states when calling
//	 * setPackageProductType(ProductType packageProductType)
//	 *
//	 * @param initalState the initalState to set
//	 */
//	public void setInitaliseState(boolean initalState) {
//		this.initalState = initalState;
//	}
//	/**
//	 * returns the initalState
//	 * @return the initalState
//	 */
//	public boolean isInitalState() {
//		return initalState;
//	}

//	private IDocumentListener cellEditModifyListener = new IDocumentListener(){
//		public void documentAboutToBeChanged(DocumentEvent arg0) {
//		}
//
//		public void documentChanged(DocumentEvent arg0) {
////			// FIXME: documentChanged is called even if only a productType is selected
////			if (dirtyStateManager != null && !initalState)
////				dirtyStateManager.markDirty();
//		}
//	};

	/**
	 * This listener is triggered, whenever a DimensionValue is either added or removed.
	 * It is NOT triggered, when a DimensionValue is merely selected (i.e. no real change to the PriceConfig).
	 */
	private PropertyChangeListener dimensionValueChangeListener = new PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt) {
			if (logger.isDebugEnabled())
				logger.debug("dimensionValueChangeListener#propertyChange: propertyName=" + evt.getPropertyName()); //$NON-NLS-1$
//			if (dirtyStateManager != null && !initalState)
//				dirtyStateManager.markDirty();

			if (priceCalculator != null)
				priceCalculator.preparePriceCalculation_createPackagedResultPriceConfigs();

			if (dirtyStateManager != null)
				dirtyStateManager.markDirty();
		}
	};

	public static final String[] FETCH_GROUPS_TARIFF_MAPPING = {
		FetchPlan.DEFAULT
	};

	public static final String[] FETCH_GROUPS_CUSTOMER_GROUP_MAPPING = {
		FetchPlan.DEFAULT
	};

	protected CustomerGroupMapper createCustomerGroupMapper()
	{
		return new CustomerGroupMapper(
				CustomerGroupMappingDAO.sharedInstance().getCustomerGroupMappings(
						FETCH_GROUPS_CUSTOMER_GROUP_MAPPING, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor())); // TODO do this asynchronously with a real ProgressMonitor?!
	}

	protected TariffMapper createTariffMapper()
	{
		return new TariffMapper(
				TariffMappingDAO.sharedInstance().getTariffMappings(
						FETCH_GROUPS_TARIFF_MAPPING, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor())); // TODO do this asynchronously with a real ProgressMonitor?!
	}

	protected PriceCalculator createPriceCalculator(ProductType packageProductType)
	{
		return new PriceCalculator(packageProductType, createCustomerGroupMapper(), createTariffMapper());
	}

	private ProductType packageProductType = null;
	public ProductType getPackageProductType() {
		return packageProductType;
	}

	public void _setPackageProductType(ProductType packageProductType)
//	throws ModuleException
	{
		if (productTypeSelector != null)
			productTypeSelector.setPackageProductType(null);

		if (dimensionValueSelector != null)
			dimensionValueSelector.setGridPriceConfig(null);

		if (priceConfigInInnerProductTypeNotEditableComposite != null)
			priceConfigInInnerProductTypeNotEditableComposite.setPackageProductType(null);

		priceCalculator = null;
		this.packageProductType = packageProductType;

		if (packageProductType == null) {
			stackLayout.topControl = productTypeNotSetComposite;
		}
		else {
			if (packageProductType.getInnerPriceConfig() == null) {
				stackLayout.topControl = getNoPriceConfigAssignedComposite();
			}
			else {
				if (packageProductType.getPackageNature() == ProductType.PACKAGE_NATURE_INNER) {
					stackLayout.topControl = getPriceConfigInInnerProductTypeNotEditableComposite();
					getPriceConfigInInnerProductTypeNotEditableComposite().setPackageProductType(packageProductType);
				}
				else { // package nature outer
					// show price config comp
					stackLayout.topControl = getPriceConfigEditComposite();

//					if (packageProductType.getInnerPriceConfig() != null || packageProductType.getPackagePriceConfig() != null) {
					priceCalculator = createPriceCalculator(packageProductType);
					priceCalculator.preparePriceCalculation();
					try {
						priceCalculator.calculatePrices();
					} catch (PriceCalculationException e) {
						throw new RuntimeException(e);
					}

					// The packagePriceConfig defines all parameters (dimension values) we need to know.
					// When the packagePriceConfig comes from the server (after preparePriceCalculation hase been called)
					// it has the same parameters (except PriceFragmentTypes) as the innerPriceConfig.
					// The PriceFragmentTypes have already been collected from all the packaged PriceConfigs.
					GridPriceConfig gridPriceConfig = (GridPriceConfig) packageProductType.getPackagePriceConfig();
					if (gridPriceConfig == null) {
						// if the package priceConfig is null in the prodcut-type we
						// take the one created by the PriceCalculator (this might be a non-persistent temporal one)
						gridPriceConfig = (GridPriceConfig) priceCalculator.getPackagePriceConfig();
					}

					if (gridPriceConfig == null)
						throw new IllegalStateException("packageProductType.getPackagePriceConfig() and priceCalculator.getPackagePriceConfig() both returned null!"); //$NON-NLS-1$
//						gridPriceConfig = (GridPriceConfig) packageProductType.getInnerPriceConfig();

					dimensionValueSelector.setGridPriceConfig(gridPriceConfig);
					productTypeSelector.setPackageProductType(packageProductType);
//					}

				} // package nature outer
			} // if (packageProductType.getInnerPriceConfig() != null) {
		} // if (packageProductType != null) {
		stackWrapper.layout(true, true);

		if (priceConfigGrid != null)
			priceConfigGrid.setPriceCalculator(priceCalculator);
	}

	/**
	 * stores the Price Configurations
	 *
	 * @param priceConfigs the priceConfigs to store
	 * @param assignInnerPriceConfigCommand TODO
	 * @return a Collection of the stored gridPriceConfigs
	 */
	protected abstract <P extends GridPriceConfig> Collection<P> storePriceConfigs(Collection<P> priceConfigs, AssignInnerPriceConfigCommand assignInnerPriceConfigCommand);

	/**
	 * returns an implementation of {@link AbstractChooseGridPriceConfigWizard} to let the user
	 * choose a Price Configuration, if no Price Configuration has been assigned yet
	 *
	 * @param parentProductTypeID the parent product Type
	 * @return an implementation of AbstractChooseGridPriceConfigWizard
	 */
	public abstract AbstractChooseGridPriceConfigWizard createChoosePriceConfigWizard(ProductTypeID parentProductTypeID);

	/**
	 * Create an implementation of {@link CellReferenceProductTypeSelector} to let the user
	 * choose a <code>ProductType</code> or return <code>null</code>, if this dimension is not
	 * used in the concrete price config.
	 *
	 * @return an implementation of {@link CellReferenceProductTypeSelector} or <code>null</code>.
	 */
	public abstract CellReferenceProductTypeSelector createCellReferenceProductTypeSelector();

	/**
	 * Stores the current PriceConfig.
	 * Prior to saving it will show the user which other ProductTypes
	 * are affected by this change and give him the possibility to
	 * cancel this action.
	 *
	 * @return Whether the PriceConfig was saved or not.
	 */
	public boolean submit()
	{
//		ProductType packageProductType = productTypeSelector.getPackageProductType();
		if (packageProductType == null)
			return false;

		ProductTypeID productTypeID = (ProductTypeID) JDOHelper.getObjectId(packageProductType);
//		PriceConfigID innerPriceConfigID = (PriceConfigID) JDOHelper.getObjectId(packageProductType.getInnerPriceConfig()); // This doesn't work, if the PC is new!
		IInnerPriceConfig ipc = packageProductType.getInnerPriceConfig();
		PriceConfigID innerPriceConfigID = ipc == null ? null : PriceConfigID.create(ipc.getOrganisationID(), ipc.getPriceConfigID());

		String localOrganisationID;
		try {
			localOrganisationID = Login.getLogin().getOrganisationID();
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}

		// collect all price configurations
		Set<GridPriceConfig> priceConfigs;
		Set<PriceConfigID> priceConfigIDs;

		Map<GridPriceConfig, List<ProductTypeSelector.Item>> priceConfig2ProductTypeSelectorItemList = null;
		if (packageProductType.getPackageNature() == ProductType.PACKAGE_NATURE_INNER) {
			priceConfigs = new HashSet<GridPriceConfig>(1);
			priceConfigs.add((GridPriceConfig) packageProductType.getInnerPriceConfig());
		}
		else {
			if (packageProductType.getInnerPriceConfig() == null) {
				priceConfigs = new HashSet<GridPriceConfig>(0);
			}
			else {
				priceConfig2ProductTypeSelectorItemList = new HashMap<GridPriceConfig, List<ProductTypeSelector.Item>>(productTypeSelector.getProductTypeItems().size());
				for (ProductTypeSelector.Item item : productTypeSelector.getProductTypeItems()) {
					if (!localOrganisationID.equals(item.getProductType().getOrganisationID())) // ignore partner-ProductTypes as we must not modify their prices
						continue;

					GridPriceConfig priceConfig = item.getPriceConfig();
					if (priceConfig != null) {
						List<ProductTypeSelector.Item> items = priceConfig2ProductTypeSelectorItemList.get(priceConfig);
						if (items == null) {
							items = new ArrayList<ProductTypeSelector.Item>();
							priceConfig2ProductTypeSelectorItemList.put(priceConfig, items);
						}
						items.add(item);
					}
				}
				priceConfigs = new HashSet<GridPriceConfig>(priceConfig2ProductTypeSelectorItemList.keySet()); // we copy the set, because the keySet is not serializable and cannot be sent to the server
			}
		}

		// remove null values - maybe not every product type has a price config assigned
		while (priceConfigs.remove(null));

		priceConfigIDs = NLJDOHelper.getObjectIDSet(priceConfigs);
		priceConfigIDs = new HashSet<PriceConfigID>(priceConfigIDs);

		// if there are priceConfigs which have never been stored (i.e. no ID assigned), we ignore them silently.
		while (priceConfigIDs.remove(null));


		// Kai: 2009-11-13
		// Check to see if there are STILL any more errors contained in the price configs; i.e. dont save if errors persist.
		try {
			priceCalculator.calculatePrices();
		} catch (PriceCalculationException e) {
			throw new RuntimeException("Invalid or incomplete formula in the price configuration(s): " + e.getShortenedErrorMessage().trim() + ".");
		}

		if (!priceConfigIDs.isEmpty()) {
			// show the consequences and ask the user whether he really wants to save
			Shell shell = null;
			try {
				shell = getShell();
			} catch (SWTException e) {
				// the composite might be disposed already, try to get another shell
				shell = Display.getDefault().getActiveShell();
			}
			StorePriceConfigsConfirmationDialog dialog = new StorePriceConfigsConfirmationDialog(
					shell,
					priceConfigIDs,
					productTypeID, innerPriceConfigID);
			if (dialog.open() != Window.OK)
				return false;
		}

		// store the price configs to the server (it will recalculate all affected product types)
		Collection<GridPriceConfig> newPCs = storePriceConfigs(
				priceConfigs,
				new AssignInnerPriceConfigCommand(
						productTypeID,
						innerPriceConfigID,
						packageProductType.getFieldMetaData(ProductType.FieldName.innerPriceConfig).isValueInherited()));

		// and replace the local price configs by the new ones (freshly detached from the server)
		if (priceConfig2ProductTypeSelectorItemList != null) {
			for (GridPriceConfig priceConfig : newPCs) {
				List<ProductTypeSelector.Item> items = priceConfig2ProductTypeSelectorItemList.get(priceConfig);
				for (ProductTypeSelector.Item item : items)
					item.setPriceConfig(priceConfig);
			}
		}

		// In case the new price configs have not all data that's necessary now
		// (e.g. that's the case with DynamicTradePriceConfig, which does not store packagingResultPriceConfigs),
		// we ensure this now:
		if (packageProductType.getInnerPriceConfig() != null) {
			priceCalculator.preparePriceCalculation();
			try {
				// We must actually recalculate as well, because otherwise the TransientStablePriceConfig contains only 0.00
				// in every cell (which will be shown in the UI as soon as a dimension-value is modified).
				// See: https://www.jfire.org/modules/bugs/view.php?id=1084
				priceCalculator.calculatePrices();
			} catch (PriceCalculationException e) {
				throw new RuntimeException(e);
			}
		}

		return true;
	}

	public void assignNewPriceConfig(IInnerPriceConfig innerPC, final boolean inherited, final ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.PriceConfigComposite.job.assignPriceConfig.name"), 100); //$NON-NLS-1$
		try {
			if (innerPC != null) {
				PriceConfigID innerPCID = (PriceConfigID) JDOHelper.getObjectId(innerPC);
				if (innerPCID != null)
					innerPC = retrieveInnerPriceConfigForEditing(innerPCID);
			}
			monitor.worked(80);

			final IInnerPriceConfig finalInnerPC = innerPC;

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (isDisposed())
						return;

					packageProductType.setInnerPriceConfig(finalInnerPC);
					packageProductType.getFieldMetaData(ProductType.FieldName.innerPriceConfig).setValueInherited(inherited);
					if (packageProductType.getInnerPriceConfig() != null) {
						GridPriceConfig packagePriceConfig = (GridPriceConfig)packageProductType.getPackagePriceConfig();
						if (packagePriceConfig != null)
							packagePriceConfig.adoptParameters(packageProductType.getInnerPriceConfig(), false);
					}

					_setPackageProductType(packageProductType);
					dirtyStateManager.markDirty();
					monitor.worked(20);
				}
			});
		} finally {
			monitor.done();
		}
	}

	public void assignNewPriceConfig(AbstractChooseGridPriceConfigWizard wizard) // TODO ProgressMonitor
	{
		wizard.getAbstractChooseGridPriceConfigPage().configureProductType(packageProductType);

		PriceConfigName newName = null;
		IInnerPriceConfig innerPC = packageProductType.getInnerPriceConfig();
		if (innerPC != null) {
			newName = packageProductType.getInnerPriceConfig().getName(); //Backup it!!!
			PriceConfigID innerPCID = (PriceConfigID) JDOHelper.getObjectId(innerPC);
			if (innerPCID != null) {
				innerPC = retrieveInnerPriceConfigForEditing(innerPCID);
				packageProductType.setInnerPriceConfig(innerPC);
			}
		}

		if (newName != null)
			packageProductType.getInnerPriceConfig().getName().copyFrom(newName);

		if (packageProductType.getInnerPriceConfig() != null) {
			GridPriceConfig packagePriceConfig = (GridPriceConfig)packageProductType.getPackagePriceConfig();
			if (packagePriceConfig != null)
				packagePriceConfig.adoptParameters(packageProductType.getInnerPriceConfig(), false);
		}

		_setPackageProductType(packageProductType);
		dirtyStateManager.markDirty();
	}


	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
}
