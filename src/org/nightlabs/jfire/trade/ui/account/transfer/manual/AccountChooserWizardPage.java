package org.nightlabs.jfire.trade.ui.account.transfer.manual;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.trade.ui.account.editor.AccountChooserComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * @author Chairat Kongarayawetchakun - chairatk[at]nightlabs[dot]de
 */
class AccountChooserWizardPage extends WizardHopPage {

	private AnchorID selectedAccountAnchorID;

	private AccountChooserComposite accountChooserComposite;

	public AccountChooserWizardPage() {
		super(AccountChooserWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.AccountChooserWizardPage.title"));  //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.AccountChooserWizardPage.description")); //$NON-NLS-1$
	}

	@Override
	public Control createPageContents(Composite parent) {
		XComposite mainComposite = new XComposite(parent, SWT.NONE);
		mainComposite.getGridLayout().numColumns = 1;

		accountChooserComposite = new AccountChooserComposite(mainComposite, SWT.NONE);
		accountChooserComposite.getAccountListComposite().addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent e) {
				if(accountChooserComposite.getAccountListComposite().getFirstSelectedElement() != null){
					Account selectedAccount = accountChooserComposite.getAccountListComposite().getFirstSelectedElement();
					selectedAccountAnchorID = AnchorID.create(selectedAccount.getOrganisationID(), selectedAccount.getAnchorTypeID(), selectedAccount.getAnchorID());
					String validationResult = ManualMoneyTransferWizard.isValidManualTransferAnchor(selectedAccountAnchorID);
					if (validationResult != null) {
						selectedAccountAnchorID = null;
						setErrorMessage(Messages.getString("org.nightlabs.jfire.trade.ui.account.transfer.manual.AccountChooserWizardPage.invalidSelection." + validationResult));
					} else {
						setErrorMessage(null);
					}
					getContainer().updateButtons();
				}//if
			}
		});
		return mainComposite;
	}

	@Override
	public boolean isPageComplete() {
		return selectedAccountAnchorID != null ? true :false;
	}

	@Override
	public boolean canFlipToNextPage() {
		return selectedAccountAnchorID != null ? true :false;
	}

	@Override
	public boolean canBeLastPage() {
		return false;
	}
	
	public AnchorID getSelectedAccount() {
		return selectedAccountAnchorID;
	}
	
	public void setSelectedAccount(AnchorID selectedAccountAnchorID) {
		this.selectedAccountAnchorID = selectedAccountAnchorID;
	}
}
