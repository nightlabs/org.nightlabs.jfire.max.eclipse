package org.nightlabs.jfire.trade.ui.overview.account;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.dao.AccountTypeDAO;
import org.nightlabs.jfire.accounting.id.AccountTypeID;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.SpinnerSearchEntry;
import org.nightlabs.jfire.base.ui.search.JDOQueryComposite;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.currency.CurrencyCombo;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AccountSearchComposite
	extends JDOQueryComposite<Account, AccountQuery>
{
	public AccountSearchComposite(AbstractQueryFilterComposite<Account, AccountQuery> parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);
		createComposite(this);
	}

	public AccountSearchComposite(AbstractQueryFilterComposite<Account, AccountQuery> parent, int style)
	{
		super(parent, style);
		createComposite(this);
	}

	private SpinnerSearchEntry minBalanceEntry = null;
	private long minBalance = Long.MIN_VALUE;
	private SpinnerSearchEntry maxBalanceEntry = null;
	private long maxBalance = Long.MAX_VALUE;
	
	private Button activeCurrencyButton = null;
	private CurrencyCombo currencyCombo = null;
	protected CurrencyID selectedCurrencyID = null;
	
	private Button ownerActiveButton = null;
	private Text ownerText = null;
	private Button ownerBrowseButton = null;
	
	private Button accountTypeActiveButton = null;
	private XComboComposite<AccountType> accountTypeList = null;
	protected AccountTypeID selectedAccountTypeID;
	
	@Override
	protected void createComposite(Composite parent)
	{
		parent.setLayout(new GridLayout(3, false));
		
		minBalanceEntry = new SpinnerSearchEntry(parent, SWT.NONE, Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.minBalanceEntry.caption")); //$NON-NLS-1$
		minBalanceEntry.getSpinnerComposite().setMinimum(-Integer.MAX_VALUE);
		minBalanceEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		minBalanceEntry.getSpinnerComposite().addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				int numDigits = minBalanceEntry.getSpinnerComposite().getNumDigits();
				double multiplier = Math.pow(10, numDigits);
				Number number = minBalanceEntry.getSpinnerComposite().getValue();
				double val = number.doubleValue() * multiplier;
				minBalance = new Double(val).longValue();
				getQuery().setMinBalance(minBalance);
			}
		});
		maxBalanceEntry = new SpinnerSearchEntry(parent, SWT.NONE, Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.maxBalanceEntry.caption")); //$NON-NLS-1$
		maxBalanceEntry.getSpinnerComposite().setMinimum(-Integer.MAX_VALUE);
		maxBalanceEntry.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		maxBalanceEntry.getSpinnerComposite().addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent arg0)
			{
				int numDigits = maxBalanceEntry.getSpinnerComposite().getNumDigits();
				double multiplier = Math.pow(10, numDigits);
				Number number = maxBalanceEntry.getSpinnerComposite().getValue();
				double val = number.doubleValue() * multiplier;
				maxBalance = new Double(val).longValue();
				getQuery().setMaxBalance(maxBalance);
			}
		});
		
		Group currencyGroup = new Group(parent, SWT.NONE);
		currencyGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.currencyGroup.text")); //$NON-NLS-1$
		currencyGroup.setLayout(new GridLayout());
		currencyGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		currencyGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		activeCurrencyButton = new Button(currencyGroup, SWT.CHECK);
		activeCurrencyButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.activeCurrencyButton.text")); //$NON-NLS-1$
		activeCurrencyButton.addSelectionListener(activeCurrencyListener);
		activeCurrencyButton.setSelection(false);
		currencyCombo = new CurrencyCombo(currencyGroup, SWT.NONE);
		currencyCombo.setEnabled(false);
		currencyCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent e)
			{
				selectedCurrencyID = (CurrencyID)JDOHelper.getObjectId(currencyCombo.getSelectedCurrency());
				getQuery().setCurrencyID(selectedCurrencyID);
			}
		});
		
		final Group ownerGroup = new Group(parent, SWT.NONE);
		ownerGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.AccountSearchComposite.ownerGroup.text")); //$NON-NLS-1$
		ownerGroup.setLayout(new GridLayout(2, false));
		ownerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerActiveButton = new Button(ownerGroup, SWT.CHECK);
		ownerActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.AccountSearchComposite.ownerActiveButton.text")); //$NON-NLS-1$
		GridData vendorLabelData = new GridData(GridData.FILL_HORIZONTAL);
		vendorLabelData.horizontalSpan = 2;
		ownerActiveButton.setLayoutData(vendorLabelData);
		ownerActiveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				boolean active = ownerActiveButton.getSelection();
				ownerText.setEnabled(active);
				ownerBrowseButton.setEnabled(active);
				if (active)
				{
					getQuery().setOwnerID(selectedOwnerID);
				}
				else
				{
					getQuery().setOwnerID(null);
				}
			}
		});
		ownerText = new Text(ownerGroup, SWT.BORDER);
		ownerText.setEnabled(false);
		ownerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		ownerText.addSelectionListener(ownerSelectionListener);
		ownerBrowseButton = new Button(ownerGroup, SWT.NONE);
		ownerBrowseButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.AccountSearchComposite.ownerBrowseButton.text")); //$NON-NLS-1$
		ownerBrowseButton.addSelectionListener(ownerSelectionListener);
		ownerBrowseButton.setEnabled(false);
//		ownerActiveButton.addSelectionListener(new SelectionListener(){
//			public void widgetSelected(SelectionEvent e) {
//				ownerText.setEnabled(((Button)e.getSource()).getSelection());
//				ownerBrowseButton.setEnabled(((Button)e.getSource()).getSelection());
//			}
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//		});
		
		final Group accountTypeGroup = new Group(parent, SWT.NONE);
		accountTypeGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.AccountSearchComposite.accountTypeGroup.text")); //$NON-NLS-1$
		accountTypeGroup.setLayout(new GridLayout());
		accountTypeGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		accountTypeActiveButton = new Button(accountTypeGroup, SWT.CHECK);
		accountTypeActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.AccountSearchComposite.accountTypeActiveButton.text")); //$NON-NLS-1$
		accountTypeActiveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		accountTypeActiveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				boolean active = accountTypeActiveButton.getSelection();
				accountTypeList.setEnabled(active);
				if (active)
				{
					getQuery().setAccountTypeID(selectedAccountTypeID);
				}
				else
				{
					getQuery().setAccountTypeID(null);
				}
			}
		});
		accountTypeList = new XComboComposite<AccountType>(
				accountTypeGroup, SWT.BORDER,
				new LabelProvider() {
					@Override
					public String getText(Object element) {
						if (element instanceof AccountType)
							return ((AccountType)element).getName().getText();
						return ""; //$NON-NLS-1$
//						return AccountListComposite.getAnchorTypeIDName((String)element);
					}
				}
		);
		accountTypeList.setEnabled(false);

		AccountType dummyAccountType = new AccountType("dummy.b.c", "dummy", false); //$NON-NLS-1$ //$NON-NLS-2$
		dummyAccountType.getName().setText(Locale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.loadingAccountTypes")); //$NON-NLS-1$
		accountTypeList.setInput(Collections.singletonList(dummyAccountType));
		accountTypeList.setSelection(0);
		accountTypeList.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				selectedAccountTypeID = (AccountTypeID) JDOHelper.getObjectId(accountTypeList.getSelectedElement());
				getQuery().setAccountTypeID(selectedAccountTypeID);
			}
		});

		Job job = new Job("Loading account types") {
			@Override
			protected IStatus run(ProgressMonitor monitor)
					throws Exception
			{
				final List<AccountType> accountTypes = AccountTypeDAO.sharedInstance().getAccountTypes(
						new String[] { FetchPlan.DEFAULT, AccountType.FETCH_GROUP_NAME },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor
				);

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run()
					{
						accountTypeList.setInput(accountTypes);
						accountTypeList.setSelection(0);
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.schedule();
		
//		accountTypeActiveButton.addSelectionListener(new SelectionListener(){
//			public void widgetSelected(SelectionEvent e) {
//				accountTypeList.setEnabled(((Button)e.getSource()).getSelection());
//			}
//			public void widgetDefaultSelected(SelectionEvent e) {
//				widgetSelected(e);
//			}
//		});
		
//		Group nameGroup = new Group(parent, SWT.NONE);
//		nameGroup.setText("Account Name");
//		nameGroup.setLayout(new GridLayout());
//		nameGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		activeNameButton = new Button(nameGroup, SWT.CHECK);
//		activeNameButton.setText("Active");
//		activeNameButton.addSelectionListener(activeNameListener);
//		accountNameText = new Text(nameGroup, SWT.BORDER);
//		accountNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		activeNameButton.setSelection(false);
//		accountNameText.setEnabled(false);
//
//		Group anchorIDGroup = new Group(parent, SWT.NONE);
//		anchorIDGroup.setText("Account ID");
//		anchorIDGroup.setLayout(new GridLayout());
//		anchorIDGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		activeAnchorIDButton = new Button(anchorIDGroup, SWT.CHECK);
//		activeAnchorIDButton.setText("Active");
//		activeAnchorIDButton.addSelectionListener(activeNameListener);
//		anchorIDText = new Text(anchorIDGroup, SWT.BORDER);
//		anchorIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		activeAnchorIDButton.setSelection(false);
//		anchorIDText.setEnabled(false);
	}
	
	private SelectionListener activeCurrencyListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			boolean active = activeCurrencyButton.getSelection();
			currencyCombo.setEnabled(active);
			if (active)
			{
				getQuery().setCurrencyID(selectedCurrencyID);
			}
			else
			{
				getQuery().setCurrencyID(null);
			}
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};

	private AnchorID selectedOwnerID = null;
	private SelectionListener ownerSelectionListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(ownerText.getText(), false);
			if (_legalEntity != null) {
				selectedOwnerID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
				getQuery().setOwnerID(selectedOwnerID);
				getQuery().setOwnerID(selectedOwnerID);
				// TODO perform this expensive code asynchronously
				LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedOwnerID,
						new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
				ownerText.setText(legalEntity.getPerson().getDisplayName());
			}
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}
	};
	
//	private SelectionListener activeNameListener = new SelectionListener(){
//		public void widgetSelected(SelectionEvent e) {
//			accountNameText.setEnabled(activeNameButton.getSelection());
//		}
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}
//	};
//
//	private SelectionListener activeAnchorIDListener = new SelectionListener(){
//		public void widgetSelected(SelectionEvent e) {
//			anchorIDText.setEnabled(activeAnchorIDButton.getSelection());
//		}
//		public void widgetDefaultSelected(SelectionEvent e) {
//			widgetSelected(e);
//		}
//	};
	
//	@Override
//	public AbstractJDOQuery getJDOQuery()
//	{
//		AccountQuery accountQuery = new AccountQuery();
//		
//		if (minBalanceEntry.isActive()) {
//			int numDigits = minBalanceEntry.getSpinnerComposite().getNumDigits();
//			double multiplier = Math.pow(10, numDigits);
//			Number number = minBalanceEntry.getSpinnerComposite().getValue();
//			double val = number.doubleValue() * multiplier;
//			long value = new Double(val).longValue();
//			accountQuery.setMinBalance(value);
//		}
//
//		if (maxBalanceEntry.isActive()) {
//			int numDigits = maxBalanceEntry.getSpinnerComposite().getNumDigits();
//			double multiplier = Math.pow(10, numDigits);
//			Number number = maxBalanceEntry.getSpinnerComposite().getValue();
//			double val = number.doubleValue() * multiplier;
//			long value = new Double(val).longValue();
//			accountQuery.setMaxBalance(value);
//		}
//		
//		if (activeCurrencyButton.getSelection())
//			accountQuery.setCurrencyID((CurrencyID)JDOHelper.getObjectId(currencyCombo.getSelectedCurrency()));
//		
//		if (ownerActiveButton.getSelection())
//			accountQuery.setOwnerID(selectedOwnerID);
//		
//		if (accountTypeActiveButton.getSelection() && accountTypeList.getSelectedElement() != null)
//			accountQuery.setAccountTypeID((AccountTypeID) JDOHelper.getObjectId(accountTypeList.getSelectedElement()));
//
////		if (activeNameButton.getSelection() && accountNameText.getText() != null && !accountNameText.getText().trim().equals(""))
////			accountQuery.setName(accountNameText.getText());
////
////		if (activeAnchorIDButton.getSelection() && anchorIDText.getText() != null && !anchorIDText.getText().trim().equals(""))
////			accountQuery.setAnchorID(anchorIDText.getText());
//		
//		return accountQuery;
//	}

	@Override
	protected void resetSearchQueryValues()
	{
		getQuery().setMinBalance(minBalance);		
		getQuery().setMaxBalance(maxBalance);
		getQuery().setCurrencyID(selectedCurrencyID);
		getQuery().setOwnerID(selectedOwnerID);
		getQuery().setAccountTypeID(selectedAccountTypeID);
	}

	@Override
	protected void unsetSearchQueryValues()
	{
		getQuery().setMinBalance(Long.MIN_VALUE);
		getQuery().setMaxBalance(Long.MAX_VALUE);
		getQuery().setCurrencyID(null);
		getQuery().setOwnerID(null);
		getQuery().setAccountTypeID(null);
	}
	
//	private List<String> availableAccountAnchorTypeIDs = null;
//	protected List<String> getAvailableAccountAnchorTypeIDs()
//	{
//		if (availableAccountAnchorTypeIDs == null) {
//			List<String> list = new ArrayList<String>();
//			list.add(Account.ANCHOR_TYPE_ID_LOCAL_EXPENSE);
//			list.add(Account.ANCHOR_TYPE_ID_LOCAL_REVENUE);
////			list.add(Account.ANCHOR_TYPE_ID_LOCAL_REVENUE_IN);
////			list.add(Account.ANCHOR_TYPE_ID_LOCAL_REVENUE_OUT);
//			list.add(Account.ANCHOR_TYPE_ID_OUTSIDE);
//			list.add(Account.ANCHOR_TYPE_ID_PARTNER_CUSTOMER);
//			list.add(Account.ANCHOR_TYPE_ID_PARTNER_NEUTRAL);
//			list.add(Account.ANCHOR_TYPE_ID_PARTNER_VENDOR);
//			list.add(SummaryAccount.ANCHOR_TYPE_ID_SUMMARY);
//			// WORKAROUND: Cannot use VoucherLocalAccountantDelegate.ACCOUNT_ANCHOR_TYPE_ID_VOUCHER
//			// as no dependency exists
//			// TODO Information should come from an extension point
//			list.add("Account.Voucher"); //$NON-NLS-1$
//			availableAccountAnchorTypeIDs = list;
//		}
//		return availableAccountAnchorTypeIDs;
//	}
}
