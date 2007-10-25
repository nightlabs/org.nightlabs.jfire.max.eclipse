package org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.transfer.Anchor;
import org.nightlabs.jfire.voucher.accounting.VoucherLocalAccountantDelegate;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.math.Base36Coder;

public class CreateAccountWizardPage
		extends WizardHopPage
{
	private Currency currency;
	private I18nTextBuffer accountName = new I18nTextBuffer();
	private I18nTextEditor accountNameEditor;

	public CreateAccountWizardPage(Currency currency)
	{
		super(CreateAccountWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.CreateAccountWizardPage.title")); //$NON-NLS-1$
		this.currency = currency;
	}

	@Override
	@Implement
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE);

		accountNameEditor = new I18nTextEditor(page, Messages.getString("org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.CreateAccountWizardPage.accountNameEditor.caption")); //$NON-NLS-1$
		accountNameEditor.setI18nText(accountName);

		return page;
	}

	public Account createAccount()
	{
		Account account = new Account(
				IDGenerator.getOrganisationID(), VoucherLocalAccountantDelegate.ACCOUNT_ANCHOR_TYPE_ID_VOUCHER,
				new Base36Coder(false).encode(IDGenerator.nextID(Anchor.class, VoucherLocalAccountantDelegate.ACCOUNT_ANCHOR_TYPE_ID_VOUCHER), 1),
				null, // this will be set by the EJB to the OrganisationLegalEntity
				currency,
				false);
		account.getName().copyFrom(accountName);
		return account;
	}

}
