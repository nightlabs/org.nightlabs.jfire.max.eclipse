package org.nightlabs.jfire.trade.ui.account.transfer.manual;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerHome;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.ManualMoneyTransfer;
import org.nightlabs.jfire.accounting.MoneyTransfer;
import org.nightlabs.jfire.accounting.dao.AccountDAO;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Chairat Kongarayawetchakun - chairatk[at]nightlabs[dot]de
 */
public class ManualMoneyTransferWizard extends DynamicPathWizard{

	private AccountChooserWizardPage fromAccountChooserPage;
	private AccountChooserWizardPage toAccountChooserPage;
	private ManualMoneyTransferWizardPage moneyTransferPage;
	private AnchorID selectedAccountAnchorID;
	
	public ManualMoneyTransferWizard(AnchorID selectedAccountAnchorID){
		setWindowTitle(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferWizard.windowTitle")); //$NON-NLS-1$
		this.selectedAccountAnchorID = selectedAccountAnchorID;
	}

	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		if(selectedAccountAnchorID == null){
			fromAccountChooserPage = new AccountChooserWizardPage();
			fromAccountChooserPage.setTitle(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferWizard.fromAccountChooserPage.title")); //$NON-NLS-1$
			fromAccountChooserPage.setSelectedAccount(selectedAccountAnchorID);
			addPage(fromAccountChooserPage);
		}//if
		
		toAccountChooserPage = new AccountChooserWizardPage();
		toAccountChooserPage.setTitle(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferWizard.toAccountChooserPageTitle")); //$NON-NLS-1$
		addPage(toAccountChooserPage);
		
		moneyTransferPage = new ManualMoneyTransferWizardPage();
		addPage(moneyTransferPage);
	}

	@Override
	public boolean performFinish() {
		ManualMoneyTransfer moneyTransfer = null;
		try {
			AccountingManagerHome accountingManagerHome = (AccountingManagerHome) AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties());
			AccountingManager accountingManager = accountingManagerHome.create();
			
			if(fromAccountChooserPage != null){
				selectedAccountAnchorID = fromAccountChooserPage.getSelectedAccount();
			}//if
			
			AnchorID toAccountID = moneyTransferPage.getToAccount();
			
			I18nText reason = moneyTransferPage.getReason();
			
			Account fromAccount = AccountDAO.sharedInstance().getAccount(moneyTransferPage.getFromAccount(), new String[]{Account.FETCH_GROUP_THIS_ACCOUNT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, null);
			moneyTransfer = accountingManager.createManualMoneyTransfer(moneyTransferPage.getFromAccount(), 
					toAccountID,
					CurrencyID.create(fromAccount.getCurrency().getCurrencyID()), 
					moneyTransferPage.getAmount(),
					reason,
					true,
					new String[]{MoneyTransfer.FETCH_GROUP_THIS_TRANSFER},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return moneyTransfer == null? false:true;
	}
	
	@Override
	public boolean canFinish() {
		if(selectedAccountAnchorID == null || toAccountChooserPage.getSelectedAccount() == null || moneyTransferPage.getAmount() <= 0)
			return false;
		return true;
	}
	
	public AccountChooserWizardPage getFromAccountChooserPage() {
		return fromAccountChooserPage;
	}
	
	public AccountChooserWizardPage getToAccountChooserPage() {
		return toAccountChooserPage;
	}
	
	public AnchorID getSelectedAccountAnchorID() {
		return selectedAccountAnchorID;
	}
}