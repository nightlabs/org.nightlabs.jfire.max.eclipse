package org.nightlabs.jfire.voucher.admin.ui.createvouchertype;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.voucher.accounting.VoucherLocalAccountantDelegate;
import org.nightlabs.jfire.voucher.admin.ui.VoucherAdminPlugin;
import org.nightlabs.jfire.voucher.admin.ui.localaccountantdelegate.VoucherLocalAccountantDelegateComposite;
import org.nightlabs.jfire.voucher.admin.ui.resource.Messages;
import org.nightlabs.math.Base36Coder;

public class CreateLocalAccountantDelegatePage
		extends WizardHopPage
{
	private I18nTextBuffer accountantDelegagteName = new I18nTextBuffer();
	private I18nTextEditor accountantDelegateNameEditor;

	private VoucherLocalAccountantDelegateComposite accountantDelegateComposite;

	public CreateLocalAccountantDelegatePage()
	{
		super(CreateLocalAccountantDelegatePage.class.getName(), Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.CreateLocalAccountantDelegatePage.title"), //$NON-NLS-1$
				SharedImages.getWizardPageImageDescriptor(VoucherAdminPlugin.getDefault(), CreateLocalAccountantDelegatePage.class));
		setDescription(Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.CreateLocalAccountantDelegatePage.description")); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent)
	{
		XComposite page = new XComposite(parent, SWT.NONE);

		accountantDelegateNameEditor = new I18nTextEditor(page, Messages.getString("org.nightlabs.jfire.voucher.admin.ui.createvouchertype.CreateLocalAccountantDelegatePage.accountantDelegateNameEditor.caption")); //$NON-NLS-1$
		accountantDelegateNameEditor.setI18nText(accountantDelegagteName);

		accountantDelegateComposite = new VoucherLocalAccountantDelegateComposite(page, true);
		accountantDelegateComposite.setMap(new HashMap<Currency, Account>());

		return page;
	}

	public VoucherLocalAccountantDelegate createVoucherLocalAccountantDelegate()
	{
		VoucherLocalAccountantDelegate delegate = new VoucherLocalAccountantDelegate(
				IDGenerator.getOrganisationID(),
				Base36Coder.sharedInstance(false).encode(IDGenerator.nextID(LocalAccountantDelegate.class), 1));
		delegate.getName().copyFrom(accountantDelegagteName);
		for (Map.Entry<Currency, Account> me : accountantDelegateComposite.getMap().entrySet()) {
			delegate.setAccount(me.getKey().getCurrencyID(), me.getValue());
		}
		return delegate;
	}
}
