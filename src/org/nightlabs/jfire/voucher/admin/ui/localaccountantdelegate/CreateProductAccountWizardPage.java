package org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate;

import javax.jdo.FetchPlan;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountType;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.AccountTypeDAO;
import org.nightlabs.jfire.accounting.id.AccountTypeID;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.transfer.Anchor;
import org.nightlabs.math.Base36Coder;
import org.nightlabs.progress.NullProgressMonitor;


public  class CreateProductAccountWizardPage extends WizardHopPage{

	private Currency currency;
	private String editorTitle;
	private I18nTextBuffer accountName = new I18nTextBuffer();
	private I18nTextEditor accountNameEditor;

	public CreateProductAccountWizardPage(Currency currency, String title)
	{
		this(currency,title,"");
	}

	public CreateProductAccountWizardPage(Currency currency, String title, String editorTitle)
	{
		super(CreateProductAccountWizardPage.class.getName(), title);
		this.currency = currency;
		this.editorTitle = editorTitle;
	}	


	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE);

		accountNameEditor = new I18nTextEditor(page, editorTitle);
		accountNameEditor.setI18nText(accountName);

		return page;
	}

	
	public Account createAccount(AccountTypeID accountTypeID)
	{
		// TODO this method should be called async! if it is, it should get a monitor - if it doesn't we need refactoring
		AccountType accountType = AccountTypeDAO.sharedInstance().getAccountType(
				accountTypeID,
				new String[] { FetchPlan.DEFAULT }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());

		Account account = new Account(
				IDGenerator.getOrganisationID(),
				new Base36Coder(false).encode(IDGenerator.nextID(Anchor.class, accountTypeID.accountTypeID), 1),
				accountType,
				(LegalEntity)null, // this will be set by the EJB to the OrganisationLegalEntity
				getCurrency());
		account.getName().copyFrom(getAccountName());
		return account;
	}
	
	
	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public I18nTextBuffer getAccountName() {
		return accountName;
	}

}
