package org.nightlabs.jfire.trade.ui.overview.account.action;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.action.WorkbenchPartSelectionAction;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.trade.ui.account.transfer.manual.ManualMoneyTransferWizard;
import org.nightlabs.jfire.transfer.id.AnchorID;

public class ManualMoneyTransferAction
extends WorkbenchPartSelectionAction
{
	public boolean calculateEnabled() {
		return !getSelectedObjects().isEmpty();
	}

	public boolean calculateVisible() {
		return true;
	}

	@Override
	public void run() {
		Account account = (Account)(getSelectedObjects().isEmpty() ? null : getSelectedObjects().get(0));
		AnchorID accountID = (AnchorID) JDOHelper.getObjectId(account);

		if (accountID == null)
			return;

		// TODO pass the selected account to the wizard
//		ManualMoneyTransferWizard wizard = new ManualMoneyTransferWizard(accountID);
		ManualMoneyTransferWizard wizard = new ManualMoneyTransferWizard(AnchorID.create(account.getOrganisationID(), account.getAnchorTypeID(), account.getAnchorID()));
		//Instantiates the wizard container with the wizard and opens it
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}
}
