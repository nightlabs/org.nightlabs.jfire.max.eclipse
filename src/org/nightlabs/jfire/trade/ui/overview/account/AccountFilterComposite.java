package org.nightlabs.jfire.trade.ui.overview.account;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.dao.AccountTypeDAO;
import org.nightlabs.jfire.accounting.id.AccountTypeID;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.SpinnerSearchEntry;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.ui.currency.CurrencyCombo;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AccountFilterComposite
	extends AbstractQueryFilterComposite<AccountQuery>
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
			QueryProvider<? super AccountQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite();
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
		QueryProvider<? super AccountQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite();
	}

	@Override
	public Class<AccountQuery> getQueryClass()
	{
		return AccountQuery.class;
	}

	private SpinnerSearchEntry minBalanceEntry;
	private SpinnerSearchEntry maxBalanceEntry;

	private Button activeCurrencyButton;
	private CurrencyCombo currencyCombo;
	protected CurrencyID selectedCurrencyID;

	private Button ownerActiveButton;
	private Text ownerText;
	private Button ownerBrowseButton;

	private Button accountTypeActiveButton;
	private XComboComposite<AccountType> accountTypeList;
	private static final String[] ACCOUNT_TYPE_FETCH_GROUPS =
		new String[] { FetchPlan.DEFAULT, AccountType.FETCH_GROUP_NAME };
	protected AccountTypeID selectedAccountTypeID;

	@Override
	protected void createComposite()
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
				final Long minBalance = computeValue(minBalanceEntry.getSpinnerComposite());
				getQuery().setMinBalance(minBalance);
			}
		});
		minBalanceEntry.addActiveStateChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(AccountQuery.FieldName.minBalance, active);
				if (getQuery().getMinBalance() == null)
					getQuery().setMinBalance( computeValue(minBalanceEntry.getSpinnerComposite()) );
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
				final Long maxBalance = computeValue(maxBalanceEntry.getSpinnerComposite());
				getQuery().setMaxBalance(maxBalance);
			}
		});
		maxBalanceEntry.addActiveStateChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(AccountQuery.FieldName.maxBalance, active);
				if (getQuery().getMaxBalance() == null)
					getQuery().setMaxBalance( computeValue(maxBalanceEntry.getSpinnerComposite()) );
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
				getQuery().setFieldEnabled(AccountQuery.FieldName.currencyID, active);
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
				final CurrencyID selectedCurrencyID = (CurrencyID)
					JDOHelper.getObjectId(currencyCombo.getSelectedCurrency());

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
				getQuery().setFieldEnabled(AccountQuery.FieldName.ownerID, active);
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
					final AnchorID selectedOwnerID = (AnchorID) JDOHelper.getObjectId(_legalEntity);
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
				getQuery().setFieldEnabled(AccountQuery.FieldName.accountTypeID, active);
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
		dummyAccountType.getName().setText(NLLocale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.AccountSearchComposite.loadingAccountTypes")); //$NON-NLS-1$
		accountTypeList.setInput(Collections.singletonList(dummyAccountType));
		accountTypeList.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final AccountTypeID selectedAccountTypeID = (AccountTypeID)
					JDOHelper.getObjectId(accountTypeList.getSelectedElement());

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

	private static final String ACCOUNT_GROUP_ID = "AccountFilterComposite"; //$NON-NLS-1$
	private static Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>();
		fieldNames.add(AccountQuery.FieldName.accountTypeID);
		fieldNames.add(AccountQuery.FieldName.currencyID);
		fieldNames.add(AccountQuery.FieldName.minBalance);
		fieldNames.add(AccountQuery.FieldName.maxBalance);
		fieldNames.add(AccountQuery.FieldName.ownerID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite#getFieldNames()
	 */
	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite#getGroupID()
	 */
	@Override
	protected String getGroupID()
	{
		return ACCOUNT_GROUP_ID;
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
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		// apply changes.
		for (FieldChangeCarrier changedField : changedFields)
		{
			if (AccountQuery.FieldName.maxBalance.equals(changedField.getPropertyName()))
			{
				Long tmpMaxBalance = (Long) changedField.getNewValue();
				double tmpSpinnerValue = computeDoubleFromQueryValue(
						tmpMaxBalance == null ? 0 : tmpMaxBalance, maxBalanceEntry.getSpinnerComposite());
				maxBalanceEntry.getSpinnerComposite().setValue(tmpSpinnerValue);
			}
			else if (getEnableFieldName(AccountQuery.FieldName.maxBalance).equals(
					changedField.getPropertyName()))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				maxBalanceEntry.setActive(active);
				setSearchSectionActive(active);
			}
			else if (AccountQuery.FieldName.minBalance.equals(changedField.getPropertyName()))
			{
				Long tmpMinBalance = (Long) changedField.getNewValue();
				double tmpSpinnerValue = computeDoubleFromQueryValue(
						tmpMinBalance == null ? 0 : tmpMinBalance, minBalanceEntry.getSpinnerComposite());
				minBalanceEntry.getSpinnerComposite().setValue(tmpSpinnerValue);
			}
			else if (getEnableFieldName(AccountQuery.FieldName.minBalance).equals(
					changedField.getPropertyName()))
			{
				boolean active = (Boolean) changedField.getNewValue();
				minBalanceEntry.setActive(active);
				setSearchSectionActive(active);
			}
			else if (AccountQuery.FieldName.currencyID.equals(changedField.getPropertyName()))
			{
				CurrencyID currencyID = (CurrencyID) changedField.getNewValue();
				currencyCombo.setSelectedCurrency(currencyID);
			}
			else if (getEnableFieldName(AccountQuery.FieldName.currencyID).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				currencyCombo.setEnabled(active);
				setSearchSectionActive(activeCurrencyButton, active);
			}
			else if (AccountQuery.FieldName.accountTypeID.equals(changedField.getPropertyName()))
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
			}
			else if (getEnableFieldName(AccountQuery.FieldName.accountTypeID).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				accountTypeList.setEnabled(active);
				setSearchSectionActive(accountTypeActiveButton, active);
			}
			else if (AccountQuery.FieldName.ownerID.equals(changedField.getPropertyName()))
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
			}
			else if (getEnableFieldName(AccountQuery.FieldName.ownerID).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				ownerText.setEnabled(active);
				ownerBrowseButton.setEnabled(active);
				setSearchSectionActive(ownerActiveButton, active);
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())
	}

}
