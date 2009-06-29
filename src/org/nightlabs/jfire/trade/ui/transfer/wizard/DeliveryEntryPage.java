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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.config.Config;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManagerRemote;
import org.nightlabs.jfire.store.deliver.CheckRequirementsEnvironment;
import org.nightlabs.jfire.store.deliver.Delivery;
import org.nightlabs.jfire.store.deliver.DeliveryConfiguration;
import org.nightlabs.jfire.store.deliver.DeliveryData;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavourName;
import org.nightlabs.jfire.store.deliver.ServerDeliveryProcessor;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour.ModeOfDeliveryFlavourProductTypeGroup;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour.ModeOfDeliveryFlavourProductTypeGroupCarrier;
import org.nightlabs.jfire.store.deliver.id.DeliveryConfigurationID;
import org.nightlabs.jfire.store.deliver.id.ModeOfDeliveryFlavourID;
import org.nightlabs.jfire.store.deliver.id.ServerDeliveryProcessorID;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.config.TradePrintingConfigModule;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.modeofdelivery.ModeOfDeliveryFlavourTable;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor;
import org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory;
import org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactoryRegistry;
import org.nightlabs.jfire.trade.ui.transfer.print.ArticleContainerPrinterRegistry;
import org.nightlabs.jfire.trade.ui.transfer.print.AutomaticPrintingOptionsGroup;
import org.nightlabs.jfire.transfer.RequirementCheckResult;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;
import org.nightlabs.util.Util;

/**
 * There's one of these pages for each group of
 * {@link org.nightlabs.jfire.store.ProductType}s with the same
 * {@link org.nightlabs.jfire.store.deliver.ModeOfDelivery}s/{@link org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour}s
 * configured. The dynamic pages generated by the
 * {@link org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessor}s are appended
 * after all the entry pages.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DeliveryEntryPage
extends WizardHopPage
implements IDeliveryEntryPage
{
	private List<? extends ProductType> productTypes;
	private org.eclipse.swt.widgets.List productTypeGUIList;
//	private List articles;
	private List<ModeOfDeliveryFlavour> modeOfDeliveryFlavours;
	private ModeOfDeliveryFlavourTable modeOfDeliveryFlavourTable;
	private Label clientDeliveryProcessorFactoryLabel;
	/**
	 * Contains items of type {@link ClientDeliveryProcessorFactory}.
	 */
	private List<ClientDeliveryProcessorFactory> clientDeliveryProcessorFactoryList = null;
	private Combo clientDeliveryProcessorFactoryCombo;
	private Label serverDeliveryProcessorLabel;
	/**
	 * Contains items of type {@link ServerDeliveryProcessor}.
	 */
	private List<ServerDeliveryProcessor> serverDeliveryProcessorList = new ArrayList<ServerDeliveryProcessor>();
	private Combo serverDeliveryProcessorCombo;
	private ModeOfDeliveryFlavour selectedModeOfDeliveryFlavour = null;
	private ClientDeliveryProcessorFactory selectedClientDeliveryProcessorFactory = null;
	private ClientDeliveryProcessor clientDeliveryProcessor = null;
	private ServerDeliveryProcessor selectedServerDeliveryProcessor = null;

	private TradePrintingConfigModule tradePrintingCfMod = null;
	private AutomaticPrintingOptionsGroup automaticPrintingGroup = null;

	private DeliveryConfigurationID deliveryConfigurationID;

	protected DeliveryWizardHop getDeliveryWizardHop()
	{
		return (DeliveryWizardHop) getWizardHop();
	}

	public DeliveryEntryPage(
			DeliveryConfigurationID deliveryConfigurationID,
			Delivery delivery,
			List<? extends ProductType> productTypes,
			List<Article> articles)
	{
		super(DeliveryEntryPage.class.getName() + '/' + delivery.getDeliveryID(), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.title"), //$NON-NLS-1$
				SharedImages.getSharedImageDescriptor(TradePlugin.getDefault(), DeliveryEntryPage.class, null, ImageDimension._75x70));
		setMessage(null);
		setDescription(
				Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.description")); //$NON-NLS-1$
		new DeliveryWizardHop(this, delivery); // self-registering
		this.deliveryConfigurationID = deliveryConfigurationID;
		this.productTypes = productTypes;
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.productTypesLabel.text")); //$NON-NLS-1$
		productTypeGUIList = new org.eclipse.swt.widgets.List(page, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		productTypeGUIList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		for (Iterator<? extends ProductType> it = productTypes.iterator(); it.hasNext(); ) {
			ProductType productType = it.next();
			productTypeGUIList.add(productType.getName().getText(NLLocale.getDefault().getLanguage()));
		}

		XComposite spacer = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		spacer.getGridData().grabExcessVerticalSpace = false;
		spacer.getGridData().heightHint = 4;

		new Label(page, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.modeOfDeliveryFlavourLabel.text")); //$NON-NLS-1$
		modeOfDeliveryFlavourTable = new ModeOfDeliveryFlavourTable(page);
		modeOfDeliveryFlavourTable.setInput(modeOfDeliveryFlavours);

		modeOfDeliveryFlavourTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent arg0)
			{
				try {
					setMessage(null);
					modeOfDeliveryFlavourGUIListSelectionChanged();
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
			}
		});

		spacer = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		spacer.getGridData().grabExcessVerticalSpace = false;
		spacer.getGridData().heightHint = 4;
		clientDeliveryProcessorFactoryLabel = new Label(page, SWT.NONE);
		clientDeliveryProcessorFactoryLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.clientDeliveryProcessorFactoryLabel.text")); //$NON-NLS-1$
		clientDeliveryProcessorFactoryCombo = new Combo(page, SWT.BORDER | SWT.READ_ONLY);
		clientDeliveryProcessorFactoryCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		clientDeliveryProcessorFactoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try {
					setMessage(null);
					clientDeliveryProcessorFactoryComboSelectionChanged();
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
			}
		});

		spacer = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		spacer.getGridData().grabExcessVerticalSpace = false;
		spacer.getGridData().heightHint = 4;
		serverDeliveryProcessorLabel = new Label(page, SWT.NONE);
		serverDeliveryProcessorLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.serverDeliveryProcessorLabel.text")); //$NON-NLS-1$
		serverDeliveryProcessorCombo = new Combo(page, SWT.BORDER | SWT.READ_ONLY);
		serverDeliveryProcessorCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		serverDeliveryProcessorCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try {
					setMessage(null);
					serverDeliveryProcessorComboSelectionChanged();
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
			}
		});

		if (!ArticleContainerPrinterRegistry.sharedInstance().getFactories().isEmpty()) {
			automaticPrintingGroup = new AutomaticPrintingOptionsGroup(page, Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.group.deliveryNotePrintingOptions"), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.deliveryNote"), null); //$NON-NLS-1$ //$NON-NLS-2$
		}

		loadModeOfDeliveries();

		return page;
	}

	protected static String sessionLastSelectedMOPFPK = null;

	private StoreManagerRemote storeManager = null;

	protected StoreManagerRemote getStoreManager()
		throws RemoteException, LoginException, NamingException
	{
		if (storeManager == null)
			storeManager = JFireEjb3Factory.getRemoteBean(StoreManagerRemote.class, Login.getLogin().getInitialContextProperties());

		return storeManager;
	}

	protected DeliveryEntryPageCfMod getDeliveryEntryPageCfMod()
	{
		return Config.sharedInstance().createConfigModule(DeliveryEntryPageCfMod.class);
	}

	protected void modeOfDeliveryFlavourGUIListSelectionChanged()
	{
		setErrorMessage(null);

		// remove all ClientDeliveryProcessorFactory s as they will be fetched again
		clientDeliveryProcessorFactoryCombo.removeAll();
		clientDeliveryProcessorFactoryList = null; // will be replaced

		// remove all ServerDeliveryProcessor s as they will be fetched again
		serverDeliveryProcessorCombo.removeAll();
		serverDeliveryProcessorList.clear();

		selectedModeOfDeliveryFlavour = null;
		sessionLastSelectedMOPFPK = null;
		clientDeliveryProcessor = null;

		// set selectedModeOfDeliveryFlavour
		selectedModeOfDeliveryFlavour = modeOfDeliveryFlavourTable.getSelectedModeOfDeliveryFlavour();
		if (selectedModeOfDeliveryFlavour != null) {
//			selectedModeOfDeliveryFlavour = (ModeOfDeliveryFlavour) modeOfDeliveryFlavours.get(idx);
			getDeliveryEntryPageCfMod().setModeOfDeliveryFlavourPK(getDeliveryConfigurationPK(), selectedModeOfDeliveryFlavour.getPrimaryKey());

			try {
				clientDeliveryProcessorFactoryList =
						ClientDeliveryProcessorFactoryRegistry.sharedInstance().
								getClientDeliveryProcessorFactories(selectedModeOfDeliveryFlavour);
			} catch (EPProcessorException e) {
				throw new RuntimeException(e);
			}

			Collections.sort(clientDeliveryProcessorFactoryList, new Comparator<ClientDeliveryProcessorFactory>(){
				public int compare(ClientDeliveryProcessorFactory cppf0, ClientDeliveryProcessorFactory cppf1)
				{
					String name0 = cppf0.getName();
					String name1 = cppf1.getName();
					return name0.compareTo(name1);
				}
			});

			for (Iterator<ClientDeliveryProcessorFactory> it = clientDeliveryProcessorFactoryList.iterator(); it.hasNext(); ) {
				ClientDeliveryProcessorFactory cppf = it.next();
				clientDeliveryProcessorFactoryCombo.add(cppf.getName());
			}

			if (clientDeliveryProcessorFactoryCombo.getItemCount() > 0)
				clientDeliveryProcessorFactoryCombo.select(0);
			else
				setMessage(String.format(
						Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.errorNoClientDeliveryProcessorFactoryRegisteredForSelectedModeOfDeliveryFlavour"), //$NON-NLS-1$
						selectedModeOfDeliveryFlavour.getPrimaryKey(),
						selectedModeOfDeliveryFlavour.getName().getText()), IMessageProvider.ERROR);

		} // if (selectedModeOfDeliveryFlavour != null) {

		clientDeliveryProcessorFactoryComboSelectionChanged();
		getContainer().updateButtons();
	}

	@SuppressWarnings("unchecked") //$NON-NLS-1$
	protected void clientDeliveryProcessorFactoryComboSelectionChanged()
	{
		DeliveryWizard wizard = ((DeliveryWizard)getWizard());

		removeDeliveryPages();

		selectedClientDeliveryProcessorFactory = null;
		clientDeliveryProcessor = null;
		serverDeliveryProcessorCombo.removeAll();
		serverDeliveryProcessorList.clear();

		int idx = clientDeliveryProcessorFactoryCombo.getSelectionIndex();
		if (idx >= 0) {
			selectedClientDeliveryProcessorFactory = clientDeliveryProcessorFactoryList.get(idx);

			clientDeliveryProcessor = selectedClientDeliveryProcessorFactory.createClientDeliveryProcessor();
			if (clientDeliveryProcessor == null)
				throw new IllegalStateException("ClientDeliveryProcessorFactory.createClientDeliveryProcessor() returned null! class: " + selectedClientDeliveryProcessorFactory.getClass()); //$NON-NLS-1$
			clientDeliveryProcessor.setClientDeliveryProcessorFactory(selectedClientDeliveryProcessorFactory);
			clientDeliveryProcessor.setDeliveryEntryPage(this);
			clientDeliveryProcessor.setCustomerID(wizard.getPartnerID());
			clientDeliveryProcessor.setDelivery(getDeliveryWizardHop().getDelivery());
			clientDeliveryProcessor.init();

			RequirementCheckResult checkResult = clientDeliveryProcessor.getRequirementCheckResult();
			if (checkResult != null) {
				this.setErrorMessage(checkResult.getMessage());
				return;
			}

			this.setErrorMessage(null);

			DeliveryData deliveryData = clientDeliveryProcessor.getDeliveryData();
			if (deliveryData == null)
				deliveryData = new DeliveryData(getDeliveryWizardHop().getDelivery());
			deliveryData.getDelivery().setModeOfDeliveryFlavour(selectedModeOfDeliveryFlavour);
			deliveryData.getDelivery().setClientDeliveryProcessorFactoryID(
					selectedClientDeliveryProcessorFactory.getID());
			getDeliveryWizardHop().setDeliveryData(deliveryData);

			Set<ServerDeliveryProcessorID> includedSPPs = clientDeliveryProcessor.getIncludedServerDeliveryProcessorIDs();
			Set<ServerDeliveryProcessorID> excludedSPPs = null;
			if (includedSPPs == null)
				excludedSPPs = clientDeliveryProcessor.getExcludedServerDeliveryProcessorIDs();

			IWizardHopPage paymentPage = clientDeliveryProcessor.createDeliveryWizardPage();
			if (paymentPage != null)
				addDeliveryPage(paymentPage);

			CheckRequirementsEnvironment checkRequirementsEnvironment = new CheckRequirementsEnvironment(
					getDeliveryWizardHop().getDelivery().getDeliveryDirection(),
					getDeliveryWizardHop().getDelivery().getArticleIDs());

			// load ServerDeliveryProcessor s
			ModeOfDeliveryFlavourID modeOfDeliveryFlavourID = (ModeOfDeliveryFlavourID) JDOHelper.getObjectId(selectedModeOfDeliveryFlavour);
			Collection<ServerDeliveryProcessor> c;
			try {
				c = getStoreManager().getServerDeliveryProcessorsForOneModeOfDeliveryFlavour(
						modeOfDeliveryFlavourID,
						checkRequirementsEnvironment,
						new String[] {
								FetchPlan.DEFAULT,
								ServerDeliveryProcessor.FETCH_GROUP_NAME
						}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			String clientDeliveryProcessorFactoryID = selectedClientDeliveryProcessorFactory.getID();
			for (Iterator<ServerDeliveryProcessor> it = c.iterator(); it.hasNext(); ) {
				ServerDeliveryProcessor spp = it.next();
				ServerDeliveryProcessorID sppID = (ServerDeliveryProcessorID) JDOHelper.getObjectId(spp);

				if (includedSPPs != null && !includedSPPs.contains(sppID))
						continue;

				if (excludedSPPs != null && excludedSPPs.contains(sppID))
					continue;

				Set<String> includedCPPFs = spp.getIncludedClientDeliveryProcessorFactoryIDs();
				Set<String> excludedCPPFs = null;
				if (includedCPPFs == null)
					excludedCPPFs = spp.getExcludedClientDeliveryProcessorFactoryIDs();

				if (includedCPPFs != null && !includedCPPFs.contains(clientDeliveryProcessorFactoryID))
					continue;

				if (excludedCPPFs != null && excludedCPPFs.contains(clientDeliveryProcessorFactoryID))
					continue;

				serverDeliveryProcessorList.add(spp);
			}

			Collections.sort(serverDeliveryProcessorList, new Comparator<ServerDeliveryProcessor>(){
				public int compare(ServerDeliveryProcessor spp0, ServerDeliveryProcessor spp1)
				{
					String name0 = spp0.getName().getText(NLLocale.getDefault().getLanguage());
					String name1 = spp1.getName().getText(NLLocale.getDefault().getLanguage());
					return name0.compareTo(name1);
				}
			});

			for (Iterator<ServerDeliveryProcessor> it = serverDeliveryProcessorList.iterator(); it.hasNext(); ) {
				ServerDeliveryProcessor serverDeliveryProcessor = it.next();
				serverDeliveryProcessorCombo.add(serverDeliveryProcessor.getName().getText(NLLocale.getDefault().getLanguage()));
			}

			if (serverDeliveryProcessorCombo.getItemCount() > 0)
				serverDeliveryProcessorCombo.select(0);
			else
				setMessage(String.format(
						Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.errorNoServerDeliveryProcessorRegisteredForSelectedModeOfDeliveryFlavour"), //$NON-NLS-1$
						selectedModeOfDeliveryFlavour.getPrimaryKey(),
						selectedModeOfDeliveryFlavour.getName().getText()), IMessageProvider.ERROR);
		}

		serverDeliveryProcessorComboSelectionChanged();
	}

	protected void removeDeliveryPages()
	{
		getWizardHop().removeAllHopPages();
	}

	protected void addDeliveryPage(IWizardHopPage deliveryPage)
	{
		getWizardHop().addHopPage(deliveryPage);
	}

	protected void serverDeliveryProcessorComboSelectionChanged()
	{
		DeliveryWizard wizard = ((DeliveryWizard)getWizard());

		selectedServerDeliveryProcessor = null;

		int idx = serverDeliveryProcessorCombo.getSelectionIndex();
		if (idx >= 0) {
			selectedServerDeliveryProcessor = serverDeliveryProcessorList.get(idx);
			getDeliveryWizardHop().getDelivery().setServerDeliveryProcessorID(
					(ServerDeliveryProcessorID) JDOHelper.getObjectId(selectedServerDeliveryProcessor));

			RequirementCheckResult result = selectedServerDeliveryProcessor.getRequirementCheckResult();
			if (result != null) {
				this.setErrorMessage(result.getMessage());
//				wizard.updateDialog(); // this is already done be setErrorMessage(...)
				return;
			}

			this.setErrorMessage(null);
		}
		else
			getDeliveryWizardHop().getDelivery().setServerDeliveryProcessorID(null);

		wizard.updateDialog();
	}

	/**
	 * @return Returns the selectedModeOfDeliveryFlavour.
	 */
	public ModeOfDeliveryFlavour getSelectedModeOfDeliveryFlavour()
	{
		return selectedModeOfDeliveryFlavour;
	}

	/**
	 * @return Returns the clientDeliveryProcessor.
	 */
	public ClientDeliveryProcessor getClientDeliveryProcessor()
	{
		return clientDeliveryProcessor;
	}
	/**
	 * @return Returns the selectedClientDeliveryProcessorFactory.
	 */
	public ClientDeliveryProcessorFactory getSelectedClientDeliveryProcessorFactory()
	{
		return selectedClientDeliveryProcessorFactory;
	}
	/**
	 * @return Returns the selectedServerDeliveryProcessor.
	 */
	public ServerDeliveryProcessor getSelectedServerDeliveryProcessor()
	{
		return selectedServerDeliveryProcessor;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete()
	{
		return
				clientDeliveryProcessor != null &&
				clientDeliveryProcessor.getRequirementCheckResult() == null &&
				selectedClientDeliveryProcessorFactory != null &&
				selectedServerDeliveryProcessor != null &&
				selectedServerDeliveryProcessor.getRequirementCheckResult() == null;
	}

	private String getDeliveryConfigurationPK() {
		return DeliveryConfiguration.getPrimaryKey(deliveryConfigurationID.organisationID, deliveryConfigurationID.deliveryConfigurationID);
	}

	private Job loadModeOfDeliveriesJob = null;

	public void loadModeOfDeliveries() {

		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.DeliveryEntryPage.job.loadingDeliveryModes")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				DeliveryWizard deliveryWizard = (DeliveryWizard) getWizard();
				final List<ModeOfDeliveryFlavour> modeOfDeliveryFlavours = new ArrayList<ModeOfDeliveryFlavour>();

				Set<ProductTypeID> ptids = NLJDOHelper.getObjectIDSet(productTypes);
				ModeOfDeliveryFlavourProductTypeGroupCarrier carrier = getStoreManager().getModeOfDeliveryFlavourProductTypeGroupCarrier(
//						deliveryWizard.getProductTypeIDs(), // this is wrong, since it loads the ModeOfDeliveryFlavours for all productTypes of the whole wizard!
						ptids, // only the productTypeIDs for this one page! not for all!
						deliveryWizard.getCustomerGroupIDs(),
						ModeOfDeliveryFlavour.MERGE_MODE_SUBTRACTIVE,
						true,
						new String[]{
							FetchPlan.DEFAULT,
							ModeOfDeliveryFlavour.FETCH_GROUP_MODE_OF_DELIVERY,
							ModeOfDeliveryFlavour.FETCH_GROUP_NAME,
							ModeOfDeliveryFlavour.FETCH_GROUP_ICON_16X16_DATA,
							ModeOfDeliveryFlavourName.FETCH_GROUP_NAMES
						}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

				Map<ProductTypeID, ProductType> productTypeByIDMap = deliveryWizard.getProductTypeByIDMap();

				for (Iterator<ModeOfDeliveryFlavourProductTypeGroup> itG = carrier.getModeOfDeliveryFlavourProductTypeGroups().iterator(); itG.hasNext(); ) {
					ModeOfDeliveryFlavourProductTypeGroup group = itG.next();

					List<ProductType> productTypes = new ArrayList<ProductType>();
					for (Iterator<ProductTypeID> itPT = group.getProductTypeIDs().iterator(); itPT.hasNext();) {
						ProductTypeID productTypeID = itPT.next();
						ProductType productType = productTypeByIDMap.get(productTypeID);
						if (productType == null)
							throw new IllegalStateException("ProductType with ID \"" + productTypeID + "\" missing in map!"); //$NON-NLS-1$ //$NON-NLS-2$

						productTypes.add(productType);
					}

					for (Iterator<ModeOfDeliveryFlavourID> itMDOFID = group.getModeOfDeliveryFlavourIDs().iterator(); itMDOFID.hasNext(); ) {
						ModeOfDeliveryFlavourID modfID = itMDOFID.next();
						ModeOfDeliveryFlavour modf = carrier.getModeOfDeliveryFlavour(modfID);
						modeOfDeliveryFlavours.add(modf);
					}
				}

				Collections.sort(modeOfDeliveryFlavours, new Comparator<ModeOfDeliveryFlavour>() {
					public int compare(ModeOfDeliveryFlavour mopf0, ModeOfDeliveryFlavour mopf1)
					{
						String name0 = mopf0.getName().getText(NLLocale.getDefault().getLanguage());
						String name1 = mopf1.getName().getText(NLLocale.getDefault().getLanguage());
						return name0.compareTo(name1);
					}
				});

				DeliveryEntryPageCfMod deliveryEntryPageCfMod = getDeliveryEntryPageCfMod();
				ModeOfDeliveryFlavour selectedModeOfDeliveryFlavour = null;

				String lastSelectedModeOfDeliveryFlavourPK = deliveryEntryPageCfMod.getDeliveryConfigurationPK2modeOfDeliveryFlavourPK().get(getDeliveryConfigurationPK());
				if (lastSelectedModeOfDeliveryFlavourPK != null) {
					for (ModeOfDeliveryFlavour modeOfDeliveryFlavour : modeOfDeliveryFlavours) {
						if (lastSelectedModeOfDeliveryFlavourPK.equals(modeOfDeliveryFlavour.getPrimaryKey())) {
							selectedModeOfDeliveryFlavour = modeOfDeliveryFlavour;
							break;
						}
					}
				}

				if (selectedModeOfDeliveryFlavour == null) {
					for (ModeOfDeliveryFlavour modeOfDeliveryFlavour : modeOfDeliveryFlavours) {
						if (Util.equals(deliveryEntryPageCfMod.getModeOfDeliveryFlavourPK(), modeOfDeliveryFlavour.getPrimaryKey())) {
							selectedModeOfDeliveryFlavour = modeOfDeliveryFlavour;
							break;
						}
					}
				}
				final List<ModeOfDeliveryFlavour> selList = new ArrayList<ModeOfDeliveryFlavour>(1);
				if (selectedModeOfDeliveryFlavour != null)
					selList.add(selectedModeOfDeliveryFlavour);

				tradePrintingCfMod = ConfigUtil.getWorkstationCfMod(TradePrintingConfigModule.class,
						new String[] { FetchPlan.DEFAULT }, 1, new NullProgressMonitor());

				final Job thisJob = this;

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (loadModeOfDeliveriesJob != thisJob)
							return;

						DeliveryEntryPage.this.modeOfDeliveryFlavours = modeOfDeliveryFlavours;

						setMessage(null);
						if (modeOfDeliveryFlavourTable != null) {
							modeOfDeliveryFlavourTable.setInput(modeOfDeliveryFlavours);
							modeOfDeliveryFlavourTable.setSelectedElements(selList); // JFace changed its behaviour?!!?!?! It now fires an event when programmatically setting a selection.
							setMessage(null);
							modeOfDeliveryFlavourGUIListSelectionChanged();
						}

						if (automaticPrintingGroup != null) {
							automaticPrintingGroup.setEnteredPrintCount(tradePrintingCfMod.getDeliveryNoteCopyCount());
							automaticPrintingGroup.setDoPrint(tradePrintingCfMod.isPrintDeliveryNoteByDefault());
						}
					}
				});

				return Status.OK_STATUS;
			}
		};

		loadModeOfDeliveriesJob = loadJob;
		loadJob.schedule();
	}

	public int getDeliveryNotesToPrintCount()
	{
		if (automaticPrintingGroup != null)
			return automaticPrintingGroup.getActualPrintCount();
		else
			return 0;
	}
}
