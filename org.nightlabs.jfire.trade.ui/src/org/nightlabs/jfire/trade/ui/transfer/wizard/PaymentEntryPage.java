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
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.nightlabs.base.ui.composite.DateTimeControl;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.config.Config;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.AccountingManagerRemote;
import org.nightlabs.jfire.accounting.pay.CheckRequirementsEnvironment;
import org.nightlabs.jfire.accounting.pay.ModeOfPayment;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavour;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavourName;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentName;
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.accounting.pay.ServerPaymentProcessor;
import org.nightlabs.jfire.accounting.pay.id.ModeOfPaymentFlavourID;
import org.nightlabs.jfire.accounting.pay.id.ServerPaymentProcessorID;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.ui.config.ConfigUtil;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.trade.config.TradePrintingConfigModule;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.modeofpayment.ModeOfPaymentFlavourTable;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessor;
import org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessorFactory;
import org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessorFactoryRegistry;
import org.nightlabs.jfire.trade.ui.transfer.print.ArticleContainerPrinterRegistry;
import org.nightlabs.jfire.trade.ui.transfer.print.AutomaticPrintingOptionsGroup;
import org.nightlabs.jfire.transfer.RequirementCheckResult;
import org.nightlabs.l10n.IDateFormatter;
import org.nightlabs.l10n.NumberFormatter;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;
import org.nightlabs.util.Util;

/**
 * This page shows the total amount to be paid and
 * asks for selection of a mode of payment.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 */
public class PaymentEntryPage
extends WizardHopPage
implements IPaymentEntryPage
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(PaymentEntryPage.class);

	private Label amountTitleLabel;
	private Label amountValueLabel;
	private Spinner amountSpinner;
	private DateTimeControl paymentDateControl;
	private Boolean paymentDateNextPageInitialized = false;

	private Label modeOfPaymentTitleLabel;
	private List<ModeOfPaymentFlavour> modeOfPaymentFlavourList = new ArrayList<ModeOfPaymentFlavour>();
	//	private org.eclipse.swt.widgets.List modeOfPaymentFlavourGUIList;
	private ModeOfPaymentFlavourTable modeOfPaymentFlavourTable;

	private Label clientPaymentProcessorFactoryLabel;
	/**
	 * Contains items of type {@link ClientPaymentProcessorFactory}.
	 */
	private List<ClientPaymentProcessorFactory> clientPaymentProcessorFactoryList = null;
	private Combo clientPaymentProcessorFactoryCombo;

	private Label serverPaymentProcessorLabel;
	/**
	 * Contains items of type {@link ServerPaymentProcessor}.
	 */
	private final List<ServerPaymentProcessor> serverPaymentProcessorList = new ArrayList<ServerPaymentProcessor>();
	private Combo serverPaymentProcessorCombo;

	private ModeOfPaymentFlavour selectedModeOfPaymentFlavour = null;
	private ClientPaymentProcessorFactory selectedClientPaymentProcessorFactory = null;
	private ClientPaymentProcessor clientPaymentProcessor = null;
	private ServerPaymentProcessor selectedServerPaymentProcessor = null;

	private final Payment payment;

	private TradePrintingConfigModule tradePrintingCfMod = null;
	private AutomaticPrintingOptionsGroup automaticPrintingGroup;

	public PaymentEntryPage(final Payment payment)
	{
		super(PaymentEntryPage.class.getName() + '/' + payment.getPaymentID(), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.title"), //$NON-NLS-1$
				SharedImages.getSharedImageDescriptor(TradePlugin.getDefault(), PaymentEntryPage.class, null, ImageDimension._75x70));
		setMessage(null); // "Please select a mode of payment.");
		setDescription(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.description")); //$NON-NLS-1$
		this.payment = payment;
		//		new PaymentWizardHop(this, payment); // self-registering
	}

	public Payment getPayment()
	{
		return payment;
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#setWizard(org.eclipse.jface.wizard.IWizard)
	 */
	@Override
	public void setWizard(final IWizard newWizard)
	{
		super.setWizard(newWizard);
		new PaymentWizardHop(this, payment); // self-registering
	}

	private AccountingManagerRemote accountingManager = null;

	protected AccountingManagerRemote getAccountingManager()
	throws RemoteException, LoginException, NamingException
	{
		if (accountingManager == null)
			accountingManager = JFireEjb3Factory.getRemoteBean(AccountingManagerRemote.class, Login.getLogin().getInitialContextProperties());

		return accountingManager;
	}

	protected void updateAmountGUI()
	{
		logger.debug("page<"+getName()+">.updateAmountGUI()"); //$NON-NLS-1$ //$NON-NLS-2$

		final PaymentWizard wizard = ((PaymentWizard)getWizard());
		if (wizard == null)
			return;

		if (amountValueLabel != null) {
			logger.debug("page<"+getName()+">.updateAmountGUI(): setting amountValueLabel.text"); //$NON-NLS-1$ //$NON-NLS-2$
			amountValueLabel.setText(
					String.format(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.amountValueLabel.text"), //$NON-NLS-1$
							NumberFormatter.formatCurrency(
									getPaymentWizardHop().getMaxAmount(),
									wizard.getCurrency()),
									NumberFormatter.formatCurrency(
											getPaymentWizardHop().getAmount(),
											wizard.getCurrency())
					));
		}

		if (amountSpinner != null) {
			logger.debug("page<"+getName()+">.updateAmountGUI(): setting amountSpinner.selection"); //$NON-NLS-1$ //$NON-NLS-2$

			// TODO is that ok?
			//			if ((int)getPaymentWizardHop().getMaxAmount() != amountSpinner.getMaximum()) {
			//				amountSpinner.setSelection(0);
			//				amountSpinner.setMaximum((int)getPaymentWizardHop().getMaxAmount());
			//			}

			if ((int)getPaymentWizardHop().getAmount() != amountSpinner.getSelection())
				amountSpinner.setSelection((int)getPaymentWizardHop().getAmount());
		}

		if (getContainer().getCurrentPage() != null)
			getContainer().updateButtons();
	}

	@Override
	public Control createPageContents(final Composite parent)
	{
		try {
			//			PaymentWizard wizafrd = ((PaymentWizard)getWizard());

			final XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

			final XComposite amountComposite = new XComposite(page, SWT.BORDER, LayoutMode.TIGHT_WRAPPER);
			amountComposite.getGridLayout().numColumns = 3;
			amountComposite.getGridData().grabExcessVerticalSpace = false;
			//			(amountTitleLabel = new Label(amountComposite, SWT.NONE)).setText("Amount to pay: ");
			amountTitleLabel = new Label(amountComposite, SWT.NONE);
			if (Payment.PAYMENT_DIRECTION_INCOMING.equals(getPayment().getPaymentDirection()))
				amountTitleLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.amountTitleLabel.text_receive")); //$NON-NLS-1$
			else if (Payment.PAYMENT_DIRECTION_OUTGOING.equals(getPayment().getPaymentDirection())) {
				// TODO very strange: It seems foreground and backround is ignored in linux/gtk
				//				amountTitleLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
				amountTitleLabel.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
				RCPUtil.setControlFontStyle(amountTitleLabel, SWT.BOLD, 0);
				//				amountTitleLabel.setFont(new Font(amountTitleLabel.getDisplay(), amountTitleLabel.getFont().getFontData()[0].getName(), amountTitleLabel.getFont().getFontData()[0].getHeight(), amountTitleLabel.getFont().getFontData()[0].getStyle() | SWT.BOLD));
				amountTitleLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.amountTitleLabel.text_pay")); //$NON-NLS-1$
			}
			else
				throw new IllegalStateException("Payment's paymentDirection is not set!"); //$NON-NLS-1$

			amountValueLabel = new Label(amountComposite, SWT.NONE);

			amountSpinner = new Spinner(amountComposite, SWT.BORDER);
			amountSpinner.setDigits(payment.getCurrency().getDecimalDigitCount());
			amountSpinner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			amountSpinner.setMinimum(0);
			amountSpinner.setMaximum(Integer.MAX_VALUE);
			// Spinner supports only int and is not very userfriendly. We need a currency-edit-composite!
			amountSpinner.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e)
				{
					amountSpinnerValueChanged();
				}
			});

			updateAmountGUI();

			XComposite spacer = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			spacer.getGridData().grabExcessVerticalSpace = false;
			spacer.getGridData().heightHint = 4;
			(modeOfPaymentTitleLabel = new Label(page, SWT.NONE)).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.modeOfPaymentFlavourLabel.text")); //$NON-NLS-1$
			modeOfPaymentFlavourTable = new ModeOfPaymentFlavourTable(page);
			//			modeOfPaymentFlavourGUIList = new org.eclipse.swt.widgets.List(page, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
			//			modeOfPaymentFlavourGUIList.setLayoutData(new GridData(GridData.FILL_BOTH));

			spacer = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			spacer.getGridData().grabExcessVerticalSpace = false;
			spacer.getGridData().heightHint = 4;
			(clientPaymentProcessorFactoryLabel = new Label(page, SWT.NONE)).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.clientPaymentProcessorFactoryLabel.text")); //$NON-NLS-1$
			clientPaymentProcessorFactoryCombo = new Combo(page, SWT.BORDER | SWT.READ_ONLY);
			clientPaymentProcessorFactoryCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			spacer = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			spacer.getGridData().grabExcessVerticalSpace = false;
			spacer.getGridData().heightHint = 4;
			(serverPaymentProcessorLabel = new Label(page, SWT.NONE)).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.serverPaymentProcessorLabel.text")); //$NON-NLS-1$
			serverPaymentProcessorCombo = new Combo(page, SWT.BORDER | SWT.READ_ONLY);
			serverPaymentProcessorCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			modeOfPaymentFlavourTable.addSelectionChangedListener(
					new ISelectionChangedListener() {
						public void selectionChanged(final SelectionChangedEvent event)
						{
							setMessage(null);
							modeOfPaymentFlavourGUIListSelectionChanged();
						}
					});
			//			modeOfPaymentFlavourGUIList.addSelectionListener(new SelectionAdapter() {
			//				public void widgetSelected(SelectionEvent e)
			//				{
			//					try {
			//						setMessage(null);
			//						modeOfPaymentGUIListSelectionChanged();
			////						((DynamicPathWizard)getWizard()).updateDialog();
			//					} catch (Exception x) {
			//						throw new RuntimeException(x);
			//					}
			//				}
			//			});

			clientPaymentProcessorFactoryCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e)
				{
					try {
						setMessage(null);
						clientPaymentProcessorFactoryComboSelectionChanged();
						//						((DynamicPathWizard)getWizard()).updateDialog();
					} catch (final Exception x) {
						throw new RuntimeException(x);
					}
				}
			});

			serverPaymentProcessorCombo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e)
				{
					try {
						setMessage(null);
						serverPaymentProcessorComboSelectionChanged();
						//						((DynamicPathWizard)getWizard()).updateDialog();
					} catch (final Exception x) {
						throw new RuntimeException(x);
					}
				}
			});


			spacer = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			spacer.getGridData().grabExcessVerticalSpace = false;
			spacer.getGridData().heightHint = 4;

			final XComposite dateComposite = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
			dateComposite.getGridLayout().numColumns = 2;
			dateComposite.getGridData().grabExcessVerticalSpace = false;
			new Label(dateComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.PaymentDateLabel.text"));  //$NON-NLS-1$
			paymentDateControl = new DateTimeControl(dateComposite, SWT.NONE, IDateFormatter.FLAGS_DATE_SHORT_TIME_HM);
			paymentDateControl.setDateEditable(false);
			paymentDateControl.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					if(paymentDateControl.getDate() != null)
					{
						setPaymentDate(paymentDateControl.getDate());
					}
				}
			});

			updateDateGUI();
			// TODO default selection!
			//			modeOfPaymentFlavourGUIList.setSelection(selIdx);

			//			Display.getDefault().asyncExec(new Runnable() {
			//				public void run()
			//				{
			//					setMessage(null);
			//					modeOfPaymentFlavourGUIListSelectionChanged();
			//				}
			//			});

			if (!ArticleContainerPrinterRegistry.sharedInstance().getFactories().isEmpty()) {
				automaticPrintingGroup = new AutomaticPrintingOptionsGroup(page,
						Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.group.invoicePrintingOptions"), //$NON-NLS-1$
						Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.invoice"), null); //$NON-NLS-1$
			}

			loadModeOfPayments();

			return page;
		} catch (final RuntimeException x) {
			throw x;
		} catch (final Exception x) {
			throw new RuntimeException(x);
		}
	}

	@Override
	public void onNext() {
		super.onNext();
		if(!paymentDateNextPageInitialized)
		{
			paymentDateNextPageInitialized = true;
			final IWizardPage nextDynamicPage = getNextPage();
			if (nextDynamicPage instanceof PaymentEntryPage) {
				final PaymentEntryPage nextPagePayment = (PaymentEntryPage)nextDynamicPage;
				if(nextPagePayment != null)
					nextPagePayment.setPaymentDate(getPayment().getPaymentDT());
			}
		}
	}

	protected PaymentWizardHop getPaymentWizardHop()
	{
		return (PaymentWizardHop) getWizardHop();
	}

	private volatile Job loadModeOfPaymentsJob = null;

	public void loadModeOfPayments() {
		final Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.job.loadingModesOfPayment")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final ProgressMonitor monitor) throws Exception {
				final PaymentWizard wizard = (PaymentWizard) getWizard();
				final List<ModeOfPaymentFlavour> modeOfPaymentFlavourList = new LinkedList<ModeOfPaymentFlavour>();

				// load ModeOfPaymentFlavour s
				// AnchorID legalEntityID = (AnchorID) JDOHelper.getObjectId(offer.getOrder().getCustomer());
				Collection<ModeOfPaymentFlavour> c;
				try {
					c = getAccountingManager().getAvailableModeOfPaymentFlavoursForAllCustomerGroups(
							wizard.getCustomerGroupIDs(),
							ModeOfPaymentFlavour.MERGE_MODE_INTERSECTION,
							true,
							new String[]{
								FetchPlan.DEFAULT,
								ModeOfPaymentFlavour.FETCH_GROUP_THIS_MODE_OF_PAYMENT_FLAVOUR,
								ModeOfPaymentFlavourName.FETCH_GROUP_NAMES,
								ModeOfPaymentFlavour.FETCH_GROUP_ICON_16X16_DATA,
								ModeOfPayment.FETCH_GROUP_NAME,
								ModeOfPaymentName.FETCH_GROUP_NAMES}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				} catch (final Exception e) {
					throw new RuntimeException(e);
				}

				modeOfPaymentFlavourList.clear();
				modeOfPaymentFlavourList.addAll(c);

				//				// filter the list by the entries in the config-module,
				//				// filtering is now done on server
				//				ModeOfPaymentConfigModule mopCfMod = ConfigUtil.getUserCfMod(
				//						ModeOfPaymentConfigModule.class,
				//						new String[] {FetchPlan.DEFAULT, ModeOfPaymentConfigModule.FETCH_GROUP_MODE_OF_PAYMENT_FLAVOURIDS},
				//						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				//
				//				for (Iterator<ModeOfPaymentFlavour> it = modeOfPaymentFlavourList.iterator(); it.hasNext(); ) {
				//					ModeOfPaymentFlavourID flavourID = (ModeOfPaymentFlavourID) JDOHelper.getObjectId(it.next());
				//					if (mopCfMod.getModeOfPaymentFlavourIDs().contains(flavourID)) {
				//						it.remove();
				//					}
				//				}


				//				Collections.sort(modeOfPaymentFlavourList, new Comparator<ModeOfPaymentFlavour>() {
				//					public int compare(ModeOfPaymentFlavour mopf0, ModeOfPaymentFlavour mopf1)
				//					{
				//						String name0 = mopf0.getName().getText(NLLocale.getDefault().getLanguage());
				//						String name1 = mopf1.getName().getText(NLLocale.getDefault().getLanguage());
				//						return name0.compareTo(name1);
				//					}
				//				});

				final PaymentEntryPageCfMod paymentEntryPageCfMod = getPaymentEntryPageCfMod();
				final List<ModeOfPaymentFlavour> selList = new ArrayList<ModeOfPaymentFlavour>(1);
				for (final ModeOfPaymentFlavour modeOfPaymentFlavour : modeOfPaymentFlavourList) {
					if (Util.equals(paymentEntryPageCfMod.getModeOfPaymentFlavourPK(), modeOfPaymentFlavour.getPrimaryKey())) {
						selList.add(modeOfPaymentFlavour);
						break;
					}
				}

				tradePrintingCfMod = ConfigUtil.getWorkstationCfMod(TradePrintingConfigModule.class,
						new String[] { FetchPlan.DEFAULT }, 1, new NullProgressMonitor());

				final Job thisJob = this;

				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (loadModeOfPaymentsJob != thisJob)
							return;

						PaymentEntryPage.this.modeOfPaymentFlavourList = modeOfPaymentFlavourList;

						if (modeOfPaymentFlavourTable != null && !modeOfPaymentFlavourTable.isDisposed()) {
							modeOfPaymentFlavourTable.setInput(modeOfPaymentFlavourList);
							modeOfPaymentFlavourTable.setSelectedElements(selList);
							setMessage(null);
							modeOfPaymentFlavourGUIListSelectionChanged();
						}

						if (automaticPrintingGroup != null && !automaticPrintingGroup.isDisposed()) {
							automaticPrintingGroup.setEnteredPrintCount(tradePrintingCfMod.getInvoiceCopyCount());
							automaticPrintingGroup.setDoPrint(tradePrintingCfMod.isPrintInvoiceByDefault());
						}
					}
				});


				//				int selIdx = -1;
				//				for (Iterator it = modeOfPaymentFlavourList.iterator(); it.hasNext(); ) {
				//					ModeOfPaymentFlavour modeOfPaymentFlavour = (ModeOfPaymentFlavour) it.next();
				//
				//					if (sessionLastSelectedMOPFPK != null && sessionLastSelectedMOPFPK.equals(modeOfPaymentFlavour.getPrimaryKey()))
				//						selIdx = modeOfPaymentFlavourGUIList.getItemCount();
				//
				//					modeOfPaymentFlavourGUIList.add(modeOfPaymentFlavour.getName().getText(NLLocale.getDefault().getLanguage()));
				//				}
				//				if (selIdx  < 0) selIdx = 0;

				return Status.OK_STATUS;
			}
		};

		loadModeOfPaymentsJob = loadJob;
		loadJob.schedule();
	}

	//	protected void adjustMaxAmount(long diffMaxAmount)
	//	{
	//		PaymentWizardHop pwHop = getPaymentWizardHop();
	//		pwHop.setMaxAmount(
	//				pwHop.getMaxAmount() + diffMaxAmount);
	//
	//		adjustAmount(diffMaxAmount, diffMaxAmount);
	//	}
	//
	//	protected void adjustAmount(long diffMaxAmount, long diffAmount)
	//	{
	//		long newAmount = getPaymentWizardHop().getAmount() + diffAmount;
	//		if (newAmount < 0) {
	//			newAmount = 0; // I think (hope?) the amounts of all following pages are
	//			// automatically adjusted correctly because of maxAmount-adjustment.
	//		}
	//		getPaymentWizardHop().setAmount(newAmount);
	//
	//		PaymentWizard wizard = (PaymentWizard)getWizard();
	//		// create/remove additional PaymentEntryPage s for multi-payments
	//		int pageIndex = wizard.getDynamicWizardPageIndex(this);
	//		if (pageIndex >= 0)
	//			pageIndex += 1;
	//		else {
	//			if (wizard.getPage(this.getName()) != null)
	//				pageIndex = 0;
	//			else
	//				throw new IllegalStateException("Could not find this page in the wizard!");
	//		}
	//
	//
	//		IWizardPage nextDynamicPage =
	//			pageIndex >= wizard.getDynamicWizardPageCount() ? null : wizard.getDynamicWizardPage(pageIndex);
	//
	//		PaymentEntryPage nextPaymentEntryPage = null;
	//		if (nextDynamicPage instanceof PaymentEntryPage) {
	//			nextPaymentEntryPage = (PaymentEntryPage) nextDynamicPage;
	//		}
	//		else {
	//			try {
	//				nextPaymentEntryPage = new PaymentEntryPage(
	//						new Payment(Login.getLogin().getOrganisationID()));
	//			} catch (LoginException e) {
	//				throw new RuntimeException(e);
	//			}
	//			wizard.addDynamicWizardPage(pageIndex, nextPaymentEntryPage);
	//		}
	//
	//		if (nextPaymentEntryPage.getPaymentWizardHop().getMaxAmount() > diffAmount)
	//			nextPaymentEntryPage.adjustMaxAmount(diffMaxAmount - diffAmount);
	//		else {
	//			while (
	//					wizard.getDynamicWizardPageCount() > pageIndex &&
	//					wizard.getDynamicWizardPage(pageIndex) instanceof PaymentEntryPage)
	//			{
	//				wizard.removeDynamicWizardPage(pageIndex);
	//			}
	//		}
	//
	//		updateAmountGUI();
	//	}

	protected int getNextPaymentEntryPageIndex(final PaymentEntryPage currentPage)
	{
		final PaymentWizard wizard = (PaymentWizard)getWizard();
		int pageIndex = wizard.getDynamicWizardPageIndex(currentPage);
		if (pageIndex >= 0)
			pageIndex += 1;
		else {
			if (wizard.getPage(currentPage.getName()) != null)
				pageIndex = 0;
			else
				throw new IllegalStateException("Could not find this page in the wizard!"); //$NON-NLS-1$
		}
		return pageIndex;
	}

	protected PaymentEntryPage createNextPaymentEntryPage(final PaymentEntryPage currentPage, final String paymentDirection, final long maxAmount)
	{
		final PaymentWizard wizard = (PaymentWizard)getWizard();
		// create/remove additional PaymentEntryPage s for multi-payments
		final int pageIndex = getNextPaymentEntryPageIndex(currentPage);


		final IWizardPage nextDynamicPage =
			pageIndex >= wizard.getDynamicWizardPageCount() ? null : wizard.getDynamicWizardPage(pageIndex);

			PaymentEntryPage nextPaymentEntryPage = null;
			if (nextDynamicPage instanceof PaymentEntryPage) {
				nextPaymentEntryPage = (PaymentEntryPage) nextDynamicPage;
				if (!paymentDirection.equals(nextPaymentEntryPage.getPayment().getPaymentDirection())) {
					removeNextPaymentEntryPages();
					nextPaymentEntryPage = null;
				}
			}

			if (nextPaymentEntryPage == null) {
				try {
					final Payment payment = new Payment(Login.getLogin().getOrganisationID(), IDGenerator.nextID(Payment.class));
					nextPaymentEntryPage = new PaymentEntryPage(payment);
					payment.setPaymentDirection(paymentDirection);
					//				payment.setPaymentDirection(getPayment().getPaymentDirection());
				} catch (final LoginException e) {
					throw new RuntimeException(e);
				}
				wizard.addDynamicWizardPage(pageIndex, nextPaymentEntryPage);
				//			wizard.getPaymentEntryPages().add(nextPaymentEntryPage);
				nextPaymentEntryPage.setMaxAmount(maxAmount);

				createOrRemovePaymentSummaryPageIfNecessary();
			}

			return nextPaymentEntryPage;
	}

	//	public static List<PaymentEntryPage> getPaymentEntryPages(IWizard wizard)
	//	{
	//		List<PaymentEntryPage> paymentEntryPages = new ArrayList<PaymentEntryPage>();
	//		IWizardPage page = wizard.getStartingPage();
	//		while (page != null) {
	//			if (page instanceof PaymentEntryPage)
	//				paymentEntryPages.add((PaymentEntryPage) page);
	//
	//			page = page.getNextPage();
	//		}
	//		return paymentEntryPages;
	//	}

	private void createOrRemovePaymentSummaryPageIfNecessary()
	{
		final PaymentWizard wizard = (PaymentWizard)getWizard();

		final List<PaymentEntryPage> paymentEntryPages = wizard.getPaymentEntryPages();
		PaymentSummaryPage paymentSummaryPage = PaymentSummaryPage.getPaymentSummaryPage(wizard);

		if (paymentEntryPages.size() == 1) {
			if (paymentSummaryPage == null)
				return;

			wizard.removeDynamicWizardPage(paymentSummaryPage);
		}
		else {
			if (paymentSummaryPage != null)
				return;

			paymentSummaryPage = new PaymentSummaryPage();
			final int index = wizard.getDynamicWizardPageIndex(paymentEntryPages.get(paymentEntryPages.size() - 1));
			wizard.addDynamicWizardPage(index + 1, paymentSummaryPage);
		}
	}

	public long getMaxAmount()
	{
		return getPaymentWizardHop().getMaxAmount();
	}


	public void setPaymentDate(final Date newDate)
	{
		getPayment().setPaymentDT(newDate);
		updateDateGUI();
	}

	protected void updateDateGUI()
	{
		if (paymentDateControl != null) {
			if (getPayment().getPaymentDT() != paymentDateControl.getDate())
				paymentDateControl.setDate(getPayment().getPaymentDT());
		}
	}

	protected void setMaxAmount(final long newMaxAmount)
	{
		final long oldMaxAmount = getPaymentWizardHop().getMaxAmount();
		logger.debug("page<"+getName()+">.setMaxAmount(newMaxAmount="+newMaxAmount+"): oldMaxAmount="+oldMaxAmount); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		//		if (oldMaxAmount == newMaxAmount)
		//			return;

		getPaymentWizardHop().setMaxAmount(newMaxAmount);
		final long diffAmount = newMaxAmount - oldMaxAmount;
		logger.debug("page<"+getName()+">.setMaxAmount(newMaxAmount="+newMaxAmount+"): diffAmount(new-old)="+diffAmount); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		long newAmount = getPaymentWizardHop().getAmount() + diffAmount;
		logger.debug("page<"+getName()+">.setMaxAmount(newMaxAmount="+newMaxAmount+"): newAmount="+newAmount); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (newAmount < 0)
			newAmount = 0;

		logger.debug("page<"+getName()+">.setMaxAmount(newMaxAmount="+newMaxAmount+"): newAmount="+newAmount+" ...calling setAmount"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		setAmount(newAmount);
	}

	public void setAmount(final long newAmount)
	{
		//		if (newAmount > getPaymentWizardHop().getMaxAmount())
		//			throw new IllegalArgumentException("newAmount="+newAmount+" is greater than maxAmount="+getPaymentWizardHop().getMaxAmount());

		final long oldAmount = getPaymentWizardHop().getAmount();
		logger.debug("page<"+getName()+">.setAmount(newAmount="+newAmount+"): oldAmount="+oldAmount); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		//		if (oldAmount == newAmount)
		//			return;

		if (clientPaymentProcessor != null)
			clientPaymentProcessor.setAmount(newAmount);

		getPaymentWizardHop().setAmount(newAmount);
		long diffAmount = getPaymentWizardHop().getMaxAmount() - newAmount;
		logger.debug("page<"+getName()+">.setAmount(newAmount="+newAmount+"): diffAmount(maxAmount-amount)="+diffAmount); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		PaymentEntryPage nextPaymentEntryPage = null;
		if (diffAmount != 0) {

			String paymentDirection = getPayment().getPaymentDirection();
			if (diffAmount < 0) {
				if (Payment.PAYMENT_DIRECTION_INCOMING.equals(paymentDirection))
					paymentDirection = Payment.PAYMENT_DIRECTION_OUTGOING;
				else if (Payment.PAYMENT_DIRECTION_OUTGOING.equals(paymentDirection))
					paymentDirection = Payment.PAYMENT_DIRECTION_INCOMING;
				else
					throw new IllegalStateException("Unknown paymentDirection: " + paymentDirection); //$NON-NLS-1$

				diffAmount *= -1;
			}

			nextPaymentEntryPage = createNextPaymentEntryPage(this, paymentDirection, diffAmount);
			nextPaymentEntryPage.setMaxAmount(diffAmount);
			//			logger.debug("page<"+getName()+">.setAmount(newAmount="+newAmount+"): calling nextPaymentEntryPage.setMaxAmount(...) ...");

			//			String paymentDirection = getPayment().getPaymentDirection();
			//			if (diffAmount < 0) {
			//				if (Payment.PAYMENT_DIRECTION_INCOMING.equals(paymentDirection))
			//					paymentDirection = Payment.PAYMENT_DIRECTION_OUTGOING;
			//				else if (Payment.PAYMENT_DIRECTION_OUTGOING.equals(paymentDirection))
			//					paymentDirection = Payment.PAYMENT_DIRECTION_INCOMING;
			//				else
			//					throw new IllegalStateException("Unknown paymentDirection: " + paymentDirection);
			//
			//				nextPaymentEntryPage.setPaymentDirection(paymentDirection);
			//
			//				diffAmount *= -1;
			//			}
			//
			//			nextPaymentEntryPage.setMaxAmount(diffAmount);
		}
		else {
			removeNextPaymentEntryPages();
		}

		updateAmountGUI();
	}

	private void removeNextPaymentEntryPages()
	{
		final int pageIndex = getNextPaymentEntryPageIndex(this);
		final PaymentWizard wizard = (PaymentWizard)getWizard();
		while (
				wizard.getDynamicWizardPageCount() > pageIndex &&
				wizard.getDynamicWizardPage(pageIndex) instanceof PaymentEntryPage)
		{
			//			wizard.getPaymentEntryPages().remove(wizard.getDynamicWizardPage(pageIndex));
			wizard.removeDynamicWizardPage(pageIndex);
		}

		createOrRemovePaymentSummaryPageIfNecessary();
	}

	protected void amountSpinnerValueChanged()
	{
		logger.debug("page<"+getName()+">.amountSpinnerValueChanged()"); //$NON-NLS-1$ //$NON-NLS-2$
		setAmount(amountSpinner.getSelection());
		//		long newAmount = amountSpinner.getSelection();
		//		long diffAmount = newAmount - getPaymentWizardHop().getAmount();
		//		adjustAmount(0, diffAmount);
	}

	//	protected static String sessionLastSelectedMOPFPK = null;

	protected PaymentEntryPageCfMod getPaymentEntryPageCfMod()
	{
		return Config.sharedInstance().createConfigModule(PaymentEntryPageCfMod.class);
	}

	protected void modeOfPaymentFlavourGUIListSelectionChanged()
	{
		try {
			setErrorMessage(null);
			//			PaymentWizard wizard = ((PaymentWizard)getWizard());

			// remove all ClientPaymentProcessorFactory s as they will be fetched again
			clientPaymentProcessorFactoryCombo.removeAll();
			clientPaymentProcessorFactoryList = null; // will be replaced

			// remove all ServerPaymentProcessor s as they will be fetched again
			serverPaymentProcessorCombo.removeAll();
			serverPaymentProcessorList.clear();

			selectedModeOfPaymentFlavour = null;
			clientPaymentProcessor = null;
			getPaymentWizardHop().getPayment().setModeOfPaymentFlavour(null);

			// set selectedModeOfPaymentFlavour
			selectedModeOfPaymentFlavour = modeOfPaymentFlavourTable.getSelectedModeOfPaymentFlavour();

			//			int idx = modeOfPaymentFlavourGUIList.getSelectionIndex();
			//			if (idx >= 0) {
			if (selectedModeOfPaymentFlavour != null) {
				//				selectedModeOfPaymentFlavour = (ModeOfPaymentFlavour) modeOfPaymentFlavourList.get(idx);
				getPaymentWizardHop().getPayment().setModeOfPaymentFlavour(selectedModeOfPaymentFlavour);

				getPaymentEntryPageCfMod().setModeOfPaymentFlavourPK(selectedModeOfPaymentFlavour.getPrimaryKey());

				clientPaymentProcessorFactoryList =
					ClientPaymentProcessorFactoryRegistry.sharedInstance().
					getClientPaymentProcessorFactories(selectedModeOfPaymentFlavour);

				Collections.sort(clientPaymentProcessorFactoryList, new Comparator<ClientPaymentProcessorFactory>(){
					public int compare(final ClientPaymentProcessorFactory cppf0, final ClientPaymentProcessorFactory cppf1)
					{
						final String name0 = cppf0.getName();
						final String name1 = cppf1.getName();
						return name0.compareTo(name1);
					}
				});

				for (final ClientPaymentProcessorFactory cppf : clientPaymentProcessorFactoryList) {
					clientPaymentProcessorFactoryCombo.add(cppf.getName());
				}

				if (clientPaymentProcessorFactoryCombo.getItemCount() > 0)
					clientPaymentProcessorFactoryCombo.select(0);
				else
					setMessage(String.format(
							Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.errorNoClientPaymentProcessorFactoryRegisteredForSelectedModeOfPaymentFlavour"), //$NON-NLS-1$
							selectedModeOfPaymentFlavour.getPrimaryKey(),
							selectedModeOfPaymentFlavour.getName().getText()), IMessageProvider.ERROR);

			} // if (selectedModeOfPaymentFlavour != null) {

			clientPaymentProcessorFactoryComboSelectionChanged();
		} catch (final Exception x) {
			throw new RuntimeException(x);
		}
	}

	protected void clientPaymentProcessorFactoryComboSelectionChanged() throws Exception
	{
		final PaymentWizard wizard = ((PaymentWizard)getWizard());

		removePaymentPages();
		//		wizard.removeAllDynamicWizardPages();

		selectedClientPaymentProcessorFactory = null;
		clientPaymentProcessor = null;
		serverPaymentProcessorCombo.removeAll();
		serverPaymentProcessorList.clear();
		getPaymentWizardHop().getPayment().setClientPaymentProcessorFactoryID(null);

		final int idx = clientPaymentProcessorFactoryCombo.getSelectionIndex();
		if (idx >= 0) {
			selectedClientPaymentProcessorFactory = clientPaymentProcessorFactoryList.get(idx);
			getPaymentWizardHop().getPayment().setClientPaymentProcessorFactoryID(selectedClientPaymentProcessorFactory.getID());

			final PaymentWizardHop paymentWizardHop = getPaymentWizardHop();
			clientPaymentProcessor = selectedClientPaymentProcessorFactory.createClientPaymentProcessor();
			if (clientPaymentProcessor == null)
				throw new IllegalStateException("ClientPaymentProcessorFactory.createClientPaymentProcessor() returned null! class: " + selectedClientPaymentProcessorFactory.getClass()); //$NON-NLS-1$
			clientPaymentProcessor.setClientPaymentProcessorFactory(selectedClientPaymentProcessorFactory);
			clientPaymentProcessor.setPaymentEntryPage(this);
			clientPaymentProcessor.setPartnerID(wizard.getPartnerID());
			clientPaymentProcessor.setCurrency(wizard.getCurrency());
			clientPaymentProcessor.setAmount(paymentWizardHop.getAmount());
			clientPaymentProcessor.setPayment(paymentWizardHop.getPayment());
			clientPaymentProcessor.init();

			final String reqMsg = clientPaymentProcessor.getRequirementCheckKey();
			if (reqMsg != null) {
				this.setErrorMessage(reqMsg.trim()); // TODO we need l10n!
				return;
			}

			this.setErrorMessage(null);

			PaymentData paymentData = clientPaymentProcessor.getPaymentData();
			if (paymentData == null)
				paymentData = new PaymentData(getPaymentWizardHop().getPayment());
			paymentWizardHop.setPaymentData(paymentData);

			final Set<ServerPaymentProcessorID> includedSPPs = clientPaymentProcessor.getIncludedServerPaymentProcessorIDs();
			Set<ServerPaymentProcessorID> excludedSPPs = null;
			if (includedSPPs == null)
				excludedSPPs = clientPaymentProcessor.getExcludedServerPaymentProcessorIDs();

			final IWizardHopPage paymentPage = clientPaymentProcessor.createPaymentWizardPage();
			if (paymentPage != null)
				addPaymentPage(paymentPage);

			final CheckRequirementsEnvironment checkRequirementsEnvironment = new CheckRequirementsEnvironment(
					getPaymentWizardHop().getPayment().getPaymentDirection(),
					getPaymentWizardHop().getPayment().getCurrencyID());

			// load ServerPaymentProcessor s
			final ModeOfPaymentFlavourID modeOfPaymentFlavourID = (ModeOfPaymentFlavourID) JDOHelper.getObjectId(selectedModeOfPaymentFlavour);
			final Collection<ServerPaymentProcessor> c = getAccountingManager().getServerPaymentProcessorsForOneModeOfPaymentFlavour(
					modeOfPaymentFlavourID,
					checkRequirementsEnvironment,
					new String[] {
							FetchPlan.DEFAULT,
							ServerPaymentProcessor.FETCH_GROUP_NAME
					}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);

			final String clientPaymentProcessorFactoryID = selectedClientPaymentProcessorFactory.getID();
			for (final Iterator<ServerPaymentProcessor> it = c.iterator(); it.hasNext(); ) {
				final ServerPaymentProcessor spp = it.next();
				final ServerPaymentProcessorID sppID = (ServerPaymentProcessorID) JDOHelper.getObjectId(spp);

				if (includedSPPs != null && !includedSPPs.contains(sppID))
					continue;

				if (excludedSPPs != null && excludedSPPs.contains(sppID))
					continue;

				final Set<String> includedCPPFs = spp.getIncludedClientPaymentProcessorFactoryIDs();
				Set<String> excludedCPPFs = null;
				if (includedCPPFs == null)
					excludedCPPFs = spp.getExcludedClientPaymentProcessorFactoryIDs();

				if (includedCPPFs != null && !includedCPPFs.contains(clientPaymentProcessorFactoryID))
					continue;

				if (excludedCPPFs != null && excludedCPPFs.contains(clientPaymentProcessorFactoryID))
					continue;

				serverPaymentProcessorList.add(spp);
			}

			Collections.sort(serverPaymentProcessorList, new Comparator<ServerPaymentProcessor>(){
				public int compare(final ServerPaymentProcessor spp0, final ServerPaymentProcessor spp1)
				{
					final String name0 = spp0.getName().getText(NLLocale.getDefault().getLanguage());
					final String name1 = spp1.getName().getText(NLLocale.getDefault().getLanguage());
					return name0.compareTo(name1);
				}
			});

			for (final ServerPaymentProcessor serverPaymentProcessor : serverPaymentProcessorList) {
				serverPaymentProcessorCombo.add(serverPaymentProcessor.getName().getText(NLLocale.getDefault().getLanguage()));
			}

			if (serverPaymentProcessorCombo.getItemCount() > 0)
				serverPaymentProcessorCombo.select(0);
			else
				setMessage(String.format(
						Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.PaymentEntryPage.errorNoServerPaymentProcessorRegisteredForSelectedModeOfPaymentFlavour"), //$NON-NLS-1$
						selectedModeOfPaymentFlavour.getPrimaryKey(),
						selectedModeOfPaymentFlavour.getName().getText()), IMessageProvider.ERROR);
		}

		serverPaymentProcessorComboSelectionChanged();
	}

	protected void removePaymentPages()
	{
		getWizardHop().removeAllHopPages();
	}

	protected void addPaymentPage(final IWizardHopPage paymentPage)
	{
		//		((IDynamicPathWizard)getWizard()).addDynamicWizardPage(paymentPage);
		//		paymentPage.setWizard(getWizard());
		getWizardHop().addHopPage(paymentPage);
	}

	protected void serverPaymentProcessorComboSelectionChanged()
	{
		final PaymentWizard wizard = ((PaymentWizard)getWizard());

		selectedServerPaymentProcessor = null;
		getPaymentWizardHop().getPayment().setServerPaymentProcessorID(null);

		final int idx = serverPaymentProcessorCombo.getSelectionIndex();
		if (idx >= 0) {
			selectedServerPaymentProcessor = serverPaymentProcessorList.get(idx);
			final RequirementCheckResult result = selectedServerPaymentProcessor.getRequirementCheckResult();
			if (result != null) {
				this.setErrorMessage(result.getMessage());
				wizard.updateDialog();
				return;
			}

			this.setErrorMessage(null);

			getPaymentWizardHop().getPayment().setServerPaymentProcessorID(
					(ServerPaymentProcessorID) JDOHelper.getObjectId(selectedServerPaymentProcessor));
		}
		else
			getPaymentWizardHop().getPayment().setServerPaymentProcessorID(null);

		wizard.updateDialog();
	}

	/**
	 * @return Returns the selectedModeOfPaymentFlavour.
	 */
	public ModeOfPaymentFlavour getSelectedModeOfPaymentFlavour()
	{
		return selectedModeOfPaymentFlavour;
	}

	/**
	 * @return Returns the clientPaymentProcessor.
	 */
	public ClientPaymentProcessor getClientPaymentProcessor()
	{
		return clientPaymentProcessor;
	}
	/**
	 * @return Returns the selectedClientPaymentProcessorFactory.
	 */
	public ClientPaymentProcessorFactory getSelectedClientPaymentProcessorFactory()
	{
		return selectedClientPaymentProcessorFactory;
	}
	/**
	 * @return Returns the selectedServerPaymentProcessor.
	 */
	public ServerPaymentProcessor getSelectedServerPaymentProcessor()
	{
		return selectedServerPaymentProcessor;
	}
	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete()
	{
		return
		clientPaymentProcessor != null &&
		clientPaymentProcessor.getRequirementCheckKey() == null &&
		selectedClientPaymentProcessorFactory != null &&
		selectedServerPaymentProcessor != null &&
		selectedServerPaymentProcessor.getRequirementCheckResult() == null;
	}

	public int getInvoicesToPrintCount()
	{
		if (automaticPrintingGroup != null) {
			return automaticPrintingGroup.getActualPrintCount();
		}
		return 0;
	}

	//	/**
	//	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	//	 */
	//	public IWizardPage getNextPage()
	//	{
	//		if (paymentPages.isEmpty())
	//			return super.getNextPage();
	//		else
	//			return (IWizardPage)paymentPages.get(0);
	//	}
}
