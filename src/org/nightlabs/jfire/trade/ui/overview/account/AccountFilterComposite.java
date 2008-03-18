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
import org.nightlabs.util.Util;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AccountFilterComposite
	extends AbstractQueryFilterComposite<Account, AccountQuery>
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
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
	 * @param style
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
	private long minBalance = Long.MIN_VALUE;
	private SpinnerSearchEntry maxBalanceEntry;
	private long maxBalance = Long.MAX_VALUE;
	
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
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				getQuery().setMinBalance(minBalance);
				setUIChangedQuery(false);
			}
		});
		minBalanceEntry.addActiveStateChangeListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button) e.getSource()).getSelection();
				setSearchSectionActive(active);
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setMinBalance( computeValue(minBalanceEntry.getSpinnerComposite()) );
				}
				else
				{
					getQuery().setMinBalance( Long.MIN_VALUE );
				}
				setUIChangedQuery(false);
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
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				getQuery().setMaxBalance(maxBalance);
				setUIChangedQuery(false);
			}
		});
		maxBalanceEntry.addActiveStateChangeListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button) e.getSource()).getSelection();
				setSearchSectionActive(active);
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setMaxBalance( computeValue(maxBalanceEntry.getSpinnerComposite()) );
				}
				else
				{
					getQuery().setMaxBalance( Long.MAX_VALUE );
				}
				setUIChangedQuery(false);
			}
		});
		
		Group currencyGroup = new Group(this, SWT.NONE);
		currencyGroup.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.currencyGroup.text")); //$NON-NLS-1$
		currencyGroup.setLayout(new GridLayout());
		XComposite.setLayoutDataMode(LayoutDataMode.GRID_DATA_HORIZONTAL, currencyGroup);
		activeCurrencyButton = new Button(currencyGroup, SWT.CHECK);
		activeCurrencyButton.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.activeCurrencyButton.text")); //$NON-NLS-1$
		activeCurrencyButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				boolean active = activeCurrencyButton.getSelection();
				currencyCombo.setEnabled(active);
				setSearchSectionActive(active);
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setCurrencyID(selectedCurrencyID);
				}
				else
				{
					getQuery().setCurrencyID(null);
				}
				setUIChangedQuery(false);
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
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				getQuery().setCurrencyID(selectedCurrencyID);
				setUIChangedQuery(false);
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
		ownerActiveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				boolean active = ownerActiveButton.getSelection();
				ownerText.setEnabled(active);
				ownerBrowseButton.setEnabled(active);
				setSearchSectionActive(active);
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setOwnerID(selectedOwnerID);
				}
				else
				{
					getQuery().setOwnerID(null);
				}
				setUIChangedQuery(false);
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
					setUIChangedQuery(true);
					getQuery().setOwnerID(selectedOwnerID);
					setUIChangedQuery(false);
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
		accountTypeActiveButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				boolean active = accountTypeActiveButton.getSelection();
				accountTypeList.setEnabled(active);
				setSearchSectionActive(active);
				
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setAccountTypeID(selectedAccountTypeID);
				}
				else
				{
					getQuery().setAccountTypeID(null);
				}
				setUIChangedQuery(false);
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
		accountTypeList.setSelection(0);
		accountTypeList.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				selectedAccountTypeID = (AccountTypeID) JDOHelper.getObjectId(accountTypeList.getSelectedElement());
				
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				getQuery().setAccountTypeID(selectedAccountTypeID);
				setUIChangedQuery(false);
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
		query.setMinBalance(Long.MIN_VALUE);
		query.setMaxBalance(Long.MAX_VALUE);
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
	
	@Override
	protected void doUpdateUI(QueryEvent event)
	{
		if (event.getChangedQuery() == null)
		{
			minBalanceEntry.getSpinnerComposite().setValue(Long.MIN_VALUE);
			minBalanceEntry.setActive(false);
			maxBalanceEntry.getSpinnerComposite().setValue(Long.MAX_VALUE);
			maxBalanceEntry.setActive(false);
			currencyCombo.setSelectedCurrency((Currency) null);
			activeCurrencyButton.setSelection(false);
			selectedOwnerID = null;
			ownerText.setText("");
			ownerActiveButton.setSelection(false);
			accountTypeList.setSelection((AccountType) null);
			accountTypeActiveButton.setSelection(false);
		}
		else
		{ // there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{
				if (AccountQuery.PROPERTY_MAX_BALANCE.equals(changedField.getPropertyName()))
				{
					maxBalanceEntry.getSpinnerComposite().setValue((Number) changedField.getNewValue());
				}
				
				if (AccountQuery.PROPERTY_MIN_BALANCE.equals(changedField.getPropertyName()))
				{
					minBalanceEntry.getSpinnerComposite().setValue((Number) changedField.getNewValue());
				}
				
				if (AccountQuery.PROPERTY_CURRENCY_ID.equals(changedField.getPropertyName()))
				{
					final CurrencyID tmpID = (CurrencyID) changedField.getNewValue();
					// FIXME: here seems to be something wrong: It always is different!
					if (! Util.equals(selectedCurrencyID, tmpID) )
					{
						currencyCombo.setSelectedCurrency((CurrencyID) changedField.getNewValue());
						activeCurrencyButton.setSelection(selectedCurrencyID != null);
					}
				}
				
				if (AccountQuery.PROPERTY_ACCOUNT_TYPE_ID.equals(changedField.getPropertyName()))
				{
					final AccountTypeID newTypeID = (AccountTypeID) changedField.getNewValue();
					if (! Util.equals(newTypeID, selectedAccountTypeID) )
					{
						if (changedField.getNewValue() == null)
						{
							accountTypeList.setSelection((AccountType) null);
						}
						else
						{
							final AccountType newAccountType = AccountTypeDAO.sharedInstance().getAccountType(
								selectedAccountTypeID, ACCOUNT_TYPE_FETCH_GROUPS,
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
							);
							accountTypeList.setSelection(newAccountType);
						}
						accountTypeActiveButton.setSelection(selectedAccountTypeID != null);
					}
				}
				
				if (AccountQuery.PROPERTY_OWNER_ID.equals(changedField.getPropertyName()))
				{
					final AnchorID tmpID = (AnchorID) changedField.getNewValue();
					if (! Util.equals(selectedOwnerID, tmpID))
					{
						if (tmpID == null)
						{
							selectedOwnerID = null;
							ownerText.setText("");
						}
						else
						{
							selectedOwnerID = tmpID;				
							final LegalEntity legalEntity = LegalEntityDAO.sharedInstance().getLegalEntity(selectedOwnerID,
								new String[] {LegalEntity.FETCH_GROUP_PERSON, FetchPlan.DEFAULT},
								NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
							);
							ownerText.setText(legalEntity.getPerson().getDisplayName());
						}
						ownerActiveButton.setSelection(selectedOwnerID != null);
					}
				}
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
		} // changedQuery != null
	}
	
}
