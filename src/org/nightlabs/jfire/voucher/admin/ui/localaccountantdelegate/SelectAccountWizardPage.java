package org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate;

import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.AbstractListComposite;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountSearchFilter;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.accounting.id.AccountTypeID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.jfire.voucher.JFireVoucherEAR;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.util.NLLocale;

public class SelectAccountWizardPage
extends WizardHopPage
{
	public static enum Mode {
		CREATE,
		SELECT
	}

	private Currency currency;
	private Account preselectedAccount;
 
	private Button createAccount;
	private Button selectAccount;
	private ListComposite<Account> accountList;
	private Mode mode;

	public SelectAccountWizardPage(Currency currency, Account preselectedAccount)
	{
		super(SelectAccountWizardPage.class.getName(), "Account configuration"); //$NON-NLS-1$
		this.currency = currency;
		this.preselectedAccount = preselectedAccount;
		this.selectedAccount = preselectedAccount;
		new WizardHop(this);
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE);

		createAccount = new Button(page, SWT.RADIO);
		createAccount.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.SelectAccountWizardPage.createAccountRadio.text")); //$NON-NLS-1$
		createAccount.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateUI();
			}
		});

		selectAccount = new Button(page, SWT.RADIO);
		selectAccount.setText(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.SelectAccountWizardPage.selectAccountRadio.text")); //$NON-NLS-1$
		selectAccount.setSelection(true);
		mode = Mode.SELECT;
		selectAccount.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				updateUI();
			}
		});

		accountList = new ListComposite<Account>(page, AbstractListComposite.getDefaultWidgetStyle(page),
				(String) null, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				return ((Account)element).getName().getText();
			}
		});
		accountList.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				createAccount.setSelection(false);
				selectAccount.setSelection(true);
				//selectedAccount. accountList.
				updateUI();
			}
		});

		
		
		Account dummy = new Account("a.b", "a.b", new AccountType("a.b", "a.b", false), new LegalEntity("a.b", "a.b"), new Currency("a.b", "a.b", 0)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
		dummy.getName().setText(NLLocale.getDefault().getLanguage(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.SelectAccountWizardPage.accountList.item_loadingData")); //$NON-NLS-1$
		accountList.addElement(dummy);

		Job job = new Job(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.SelectAccountWizardPage.loadJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
			{
				try {
					AccountSearchFilter accountSearchFilter = new AccountSearchFilter();
//					accountSearchFilter.setAnchorTypeID(VoucherLocalAccountantDelegate.ACCOUNT_ANCHOR_TYPE_ID_VOUCHER);
					accountSearchFilter.setAccountTypeID(JFireVoucherEAR.ACCOUNT_TYPE_ID_VOUCHER);
					accountSearchFilter.setCurrencyID(currency.getCurrencyID());
					accountSearchFilter.setOwner(
							AnchorID.create(Login.getLogin().getOrganisationID(), LegalEntity.ANCHOR_TYPE_ID_LEGAL_ENTITY, OrganisationLegalEntity.class.getName()));

					final List<Account> accounts = AccountDAO.sharedInstance().getAccounts(accountSearchFilter, FETCH_GROUPS_ACCOUNT, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							accountList.removeAll();
							accountList.addElements(accounts);

							if (preselectedAccount != null) {
								if (!accountList.selectElement(preselectedAccount)) {
									accountList.addElement(preselectedAccount);
									accountList.selectElement(preselectedAccount);
								}
							}
						}
					});

				} catch (Exception x) {
					throw new RuntimeException(x);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();

		return page;
	}

	protected static final String[] FETCH_GROUPS_ACCOUNT = {
		FetchPlan.DEFAULT, Account.FETCH_GROUP_NAME
	};

	private CreateProductAccountWizardPage createAccountWizardPage = null;

	private void addCreateAccountWizardPage()
	{
		if (createAccountWizardPage == null)
			createAccountWizardPage = new CreateProductAccountWizardPage(currency,
					Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.CreateAccountWizardPage.title"), //$NON-NLS-1$
					Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.CreateAccountWizardPage.accountNameEditor.caption")); //$NON-NLS-1$
		if (!getWizardHop().getHopPages().contains(createAccountWizardPage))
			getWizardHop().addHopPage(createAccountWizardPage);
	}

	private void removeCreateAccountWizardPage()
	{
		if (createAccountWizardPage == null)
			return;

		getWizardHop().removeHopPage(createAccountWizardPage);
	}

	private void updateUI()
	{
		if (createAccount.getSelection())
			mode = Mode.CREATE;
		else if (selectAccount.getSelection())
		{
			selectedAccount = accountList.getSelectedElement();
			mode = Mode.SELECT;
		}
			else
			throw new IllegalStateException("What's that?!"); //$NON-NLS-1$

		if (Mode.CREATE == mode)
			addCreateAccountWizardPage();
		else
			removeCreateAccountWizardPage();

		getContainer().updateButtons();
	}

	private Account selectedAccount;

	public Account getSelectedAccount()
	{
		if (mode == Mode.SELECT)
			return selectedAccount;
		else
			return null;
	}

	public Mode getMode()
	{
		return mode;
	}

	public Account createAccount(AccountTypeID selectedAccountTypeID)
	{
		if (mode != Mode.CREATE)
			throw new IllegalStateException("Cannot create Account in mode " + mode); //$NON-NLS-1$

		return createAccountWizardPage.createAccount(selectedAccountTypeID);
	}
}
