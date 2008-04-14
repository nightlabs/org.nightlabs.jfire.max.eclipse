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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.NumberSpinnerComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.AccountTypeDAO;
import org.nightlabs.jfire.accounting.id.AccountTypeID;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.SpinnerSearchEntry;
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
public class AccountFilterComposite
	extends AbstractQueryFilterComposite<Account, AccountQuery>
{
	/**
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param layoutMode
	 *          The layout mode to use. See {@link XComposite.LayoutMode}.
	 * @param layoutDataMode
	 *          The layout data mode to use. See {@link XComposite.LayoutDataMode}.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public AccountFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<Account, ? super AccountQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite(this);
	}

	/**
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public AccountFilterComposite(Composite parent, int style,
		QueryProvider<Account, ? super AccountQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite(this);
	}

	@Override
	public Class<AccountQuery> getQueryClass()
	{
		return AccountQuery.class;
	}

	private SpinnerSearchEntry minBalanceEntry;
	private SpinnerSearchEntry maxBalanceEntry;
	private Long minBalance;
	private Long maxBalance;
	
	private Button activeCurrencyButton;
	private CurrencyCombo currencyCombo;
	protected CurrencyID selectedCurrencyID;
	
	private Button ownerActiveButton;
	private Text ownerText;
	private Button ownerBrowseButton;
	private AnchorID selectedOwnerID;
	
	private Button accountTypeActiveButton;
	private XComboComposite<AccountType> accountTypeList;
	private static final String[] ACCOUNT_TYPE_FETCH_GROUPS = 
		new String[] { FetchPlan.DEFAULT, AccountType.FETCH_GROUP_NAME };
	protected AccountTypeID selectedAccountTypeID;
	
	@Override
	protected void createComposite(Composite parent)
	{
		setLayout(new GridLayout(3, false));
		
		minBalanceEntry = new SpinnerSearchEntry(this, SWT.NONE, 
			LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL,
			Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.minBalanceEntry.caption")); //$NON-NLS-1$
		minBalanceEntry.getSpinnerComposite().setMinimum(Integer.MIN_VALUE);
		minBalanceEntry.getSpinnerComposite().addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				minBalance = computeValue(minBalanceEntry.getSpinnerComposite());
				getQuery().setMinBalance(minBalance);
			}
		});
		minBalanceEntry.addActiveStateChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				if (active)
				{
					if (minBalance == null)
					{
						setInitialValue(true);
						// for consistency we need to update the field according to the initial value of
						// the spinner.
						minBalance = computeValue(minBalanceEntry.getSpinnerComposite());
						getQuery().setMinBalance( minBalance );
						setInitialValue(false);
					}
					else
					{
						getQuery().setMinBalance( minBalance );
					}
				}
				else
				{
					getQuery().setMinBalance( null );
				}
			}
		});
		
		maxBalanceEntry = new SpinnerSearchEntry(this, SWT.NONE,
			LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA_HORIZONTAL,
			Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.maxBalanceEntry.caption")); //$NON-NLS-1$
		maxBalanceEntry.getSpinnerComposite().setMinimum(Integer.MIN_VALUE);
		maxBalanceEntry.getSpinnerComposite().addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent arg0)
			{
				maxBalance = computeValue(maxBalanceEntry.getSpinnerComposite());
				getQuery().setMaxBalance(maxBalance);
			}
		});
		maxBalanceEntry.addActiveStateChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				if (active)
				{
					if (maxBalance == null)
					{
						setInitialValue(true);
						// for consistency we need to update the field according to the initial value of
						// the spinner.
						maxBalance = computeValue(maxBalanceEntry.getSpinnerComposite());
						getQuery().setMaxBalance( maxBalance );
						setInitialValue(false);
					}
					else
					{
						getQuery().setMaxBalance( maxBalance );
					}
				}
				else
				{
					getQuery().setMaxBalance( null );
				}
			}
		});
		
		Group currencyGroup = new Group(this, SWT.NONE);
		currencyGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.currencyGroup.text")); //$NON-NLS-1$
		currencyGroup.setLayout(new GridLayout());
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, currencyGroup);
		activeCurrencyButton = new Button(currencyGroup, SWT.CHECK);
		activeCurrencyButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.activeCurrencyButton.text")); //$NON-NLS-1$
		activeCurrencyButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				if (active)
				{
					if (selectedCurrencyID == null)
					{
						setInitialValue(true);
						getQuery().setCurrencyID(selectedCurrencyID);
						setInitialValue(false);
					}
					else
					{
						getQuery().setCurrencyID(selectedCurrencyID);
					}
				}
				else
				{
					getQuery().setCurrencyID(null);
				}
			}
		});
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
		
		final Group ownerGroup = new Group(this, SWT.NONE);
		ownerGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.AccountSearchComposite.ownerGroup.text")); //$NON-NLS-1$
		ownerGroup.setLayout(new GridLayout(2, false));
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, ownerGroup);
		ownerActiveButton = new Button(ownerGroup, SWT.CHECK);
		ownerActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.AccountSearchComposite.ownerActiveButton.text")); //$NON-NLS-1$
		GridData vendorLabelData = new GridData(GridData.FILL_HORIZONTAL);
		vendorLabelData.horizontalSpan = 2;
		ownerActiveButton.setLayoutData(vendorLabelData);
		ownerActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				if (active)
				{
					if (selectedOwnerID == null)
					{
						setInitialValue(true);
						getQuery().setOwnerID(selectedOwnerID);
						setInitialValue(false);
					}
					else
					{
						getQuery().setOwnerID(selectedOwnerID);						
					}
				}
				else
				{
					getQuery().setOwnerID(null);
				}
			}
		});
		ownerText = new Text(ownerGroup, XComposite.getBorderStyle(ownerGroup));
		ownerText.setEnabled(false);
		ownerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		ownerBrowseButton = new Button(ownerGroup, SWT.NONE);
		ownerBrowseButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.AccountSearchComposite.ownerBrowseButton.text")); //$NON-NLS-1$
		ownerBrowseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				LegalEntity _legalEntity = LegalEntitySearchCreateWizard.open(ownerText.getText(), false);
				if (_legalEntity != null) {
					selectedOwnerID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
					getQuery().setOwnerID(selectedOwnerID);
					// TODO perform this expensive code asynchronously
					LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedOwnerID,
							new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					ownerText.setText(legalEntity.getPerson().getDisplayName());
				}
			}
		});
		ownerBrowseButton.setEnabled(false);
		
		final Group accountTypeGroup = new Group(this, SWT.NONE);
		accountTypeGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.AccountSearchComposite.accountTypeGroup.text")); //$NON-NLS-1$
		accountTypeGroup.setLayout(new GridLayout());
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, accountTypeGroup);
		accountTypeActiveButton = new Button(accountTypeGroup, SWT.CHECK);
		accountTypeActiveButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountListComposite.AccountSearchComposite.accountTypeActiveButton.text")); //$NON-NLS-1$
		accountTypeActiveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		accountTypeActiveButton.addSelectionListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				if (active)
				{
					if (selectedAccountTypeID == null)
					{
						setInitialValue(true);
						getQuery().setAccountTypeID(selectedAccountTypeID);
						setInitialValue(false);
					}
					else
					{
						getQuery().setAccountTypeID(selectedAccountTypeID);
					}
				}
				else
				{
					getQuery().setAccountTypeID(null);
				}
			}
		});
		accountTypeList = new XComboComposite<AccountType>(
				accountTypeGroup, XComposite.getBorderStyle(accountTypeGroup),
				new LabelProvider() {
					@Override
					public String getText(Object element) {
						if (element instanceof AccountType)
							return ((AccountType)element).getName().getText();
						return ""; //$NON-NLS-1$
					}
				}
		);
		accountTypeList.setEnabled(false);

		AccountType dummyAccountType = new AccountType("dummy.b.c", "dummy", false); //$NON-NLS-1$ //$NON-NLS-2$
		dummyAccountType.getName().setText(Locale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.loadingAccountTypes")); //$NON-NLS-1$
		accountTypeList.setInput(Collections.singletonList(dummyAccountType));
		accountTypeList.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				selectedAccountTypeID = (AccountTypeID) JDOHelper.getObjectId(accountTypeList.getSelectedElement());
				getQuery().setAccountTypeID(selectedAccountTypeID);
			}
		});

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.job.loadingAccountTypes")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
					throws Exception
			{
				final List<AccountType> accountTypes = AccountTypeDAO.sharedInstance().getAccountTypes(
						ACCOUNT_TYPE_FETCH_GROUPS,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						monitor
				);

				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run()
					{
						accountTypeList.setInput(accountTypes);
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	@Override
	protected void resetSearchQueryValues(AccountQuery query)
	{
		query.setMinBalance(minBalance);
		query.setMaxBalance(maxBalance);
		query.setCurrencyID(selectedCurrencyID);
		query.setOwnerID(selectedOwnerID);
		query.setAccountTypeID(selectedAccountTypeID);
	}

	@Override
	protected void unsetSearchQueryValues(AccountQuery query)
	{
		if (! minBalanceEntry.isActive())
		{
			minBalance = null;
		}
		if (! maxBalanceEntry.isActive())
		{
			maxBalance = null;
		}
		if (! activeCurrencyButton.getSelection())
		{
			selectedCurrencyID = null;
		}
		if (! ownerActiveButton.getSelection())
		{
			selectedOwnerID = null;
		}
		if (! accountTypeActiveButton.getSelection())
		{
			selectedAccountTypeID = null;
		}
		
		query.setMinBalance(null);
		query.setMaxBalance(null);
		query.setCurrencyID(null);
		query.setOwnerID(null);
		query.setAccountTypeID(null);
	}
	
	private long computeValue(NumberSpinnerComposite spinner)
	{
		int numDigits = spinner.getNumDigits();
		double multiplier = Math.pow(10, numDigits);
		Number number = spinner.getValue();
		double val = number.doubleValue() * multiplier;
		return new Double(val).longValue();
	}
	
	private double computeDoubleFromQueryValue(long value, NumberSpinnerComposite spinner)
	{
		int numDigits = spinner.getNumDigits();
		double divider = Math.pow(10, numDigits);
		return value / divider;
	}
	
	@Override
	protected void updateUI(QueryEvent event)
	{
		if (event.getChangedQuery() == null)
		{
			minBalance = null;
			minBalanceEntry.getSpinnerComposite().setValue(null);
			if (minBalanceEntry.isActive())
			{
				minBalanceEntry.setActive(false);
				setSearchSectionActive(false);
			}
			
			maxBalance = null;
			maxBalanceEntry.getSpinnerComposite().setValue(null);
			if (maxBalanceEntry.isActive())
			{
				maxBalanceEntry.setActive(false);
				setSearchSectionActive(false);
			}
			
			selectedCurrencyID = null;
			currencyCombo.setSelectedCurrency((Currency) null);
			setSearchSectionActive(activeCurrencyButton, false);
			
			selectedOwnerID = null;
			ownerText.setText(""); //$NON-NLS-1$
			setSearchSectionActive(ownerActiveButton, false);
			
			selectedAccountTypeID = null;
			accountTypeList.setSelection((AccountType) null);
			setSearchSectionActive(accountTypeActiveButton, false);
		}
		else
		{ // there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{
				boolean active = isInitialValue();
				if (AccountQuery.PROPERTY_MAX_BALANCE.equals(changedField.getPropertyName()))
				{
					Long tmpMaxBalance = (Long) changedField.getNewValue();
					double tmpSpinnerValue = computeDoubleFromQueryValue(
						tmpMaxBalance == null ? 0 : tmpMaxBalance, maxBalanceEntry.getSpinnerComposite());
					maxBalanceEntry.getSpinnerComposite().setValue(tmpSpinnerValue);
					active |= tmpMaxBalance != null;
					if (maxBalanceEntry.isActive() != active)
					{
						maxBalanceEntry.setActive(active);
						setSearchSectionActive(active);
					}
				}
				
				if (AccountQuery.PROPERTY_MIN_BALANCE.equals(changedField.getPropertyName()))
				{
					Long tmpMinBalance = (Long) changedField.getNewValue();
					double tmpSpinnerValue = computeDoubleFromQueryValue(
						tmpMinBalance == null ? 0 : tmpMinBalance, minBalanceEntry.getSpinnerComposite());
					minBalanceEntry.getSpinnerComposite().setValue(tmpSpinnerValue);
					active |= tmpMinBalance != null;
					if (minBalanceEntry.isActive() != active)
					{
						minBalanceEntry.setActive(active);
						setSearchSectionActive(active);
					}
				}
				
				if (AccountQuery.PROPERTY_CURRENCY_ID.equals(changedField.getPropertyName()))
				{
					CurrencyID currencyID = (CurrencyID) changedField.getNewValue();
					currencyCombo.setSelectedCurrency(currencyID);
					
					active |= currencyID != null;
					currencyCombo.setEnabled(active);
					setSearchSectionActive(activeCurrencyButton, active);
				}
				
				if (AccountQuery.PROPERTY_ACCOUNT_TYPE_ID.equals(changedField.getPropertyName()))
				{
					AccountTypeID accountTypeID = (AccountTypeID) changedField.getNewValue();
					if (accountTypeID == null)
					{
						accountTypeList.setSelection((AccountType) null);
					}
					else
					{
						final AccountType newAccountType = AccountTypeDAO.sharedInstance().getAccountType(
							accountTypeID, ACCOUNT_TYPE_FETCH_GROUPS,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
						);
						accountTypeList.setSelection(newAccountType);
					}
					
					active |= accountTypeID != null;
					accountTypeList.setEnabled(active);
					setSearchSectionActive(accountTypeActiveButton, active);
				}
				
				if (AccountQuery.PROPERTY_OWNER_ID.equals(changedField.getPropertyName()))
				{
					AnchorID ownerID = (AnchorID) changedField.getNewValue();
					if (ownerID == null)
					{
						ownerText.setText(""); //$NON-NLS-1$
					}
					else
					{
						final LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(
							ownerID,
							new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
						);
						ownerText.setText(legalEntity.getPerson().getDisplayName());
					}
					active |= ownerID != null;
					ownerText.setEnabled(active);
					ownerBrowseButton.setEnabled(active);
					setSearchSectionActive(ownerActiveButton, active);
				}
			} // for (FieldChangeCarrier changedField : event.getChangedFields())			
		} // changedQuery != null		
	}
	
}
