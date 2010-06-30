package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assignendcustomer;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.wizard.IWizardDelegate;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManagerRemote;
import org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntitySearchCreateWizardDelegate;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * This is the {@link IWizardDelegate} that is linked to the {@link AssignEndCustomerWizard}, for use
 * through the wizardDelegateFactory.
 *
 * @author khaireel at nightlabs dot de
 */
public class AssignEndCustomerWizardDelegate extends LegalEntitySearchCreateWizardDelegate {
	@Override
	public boolean performFinish() {
		boolean superResult = super.performFinish();
		if (!superResult)
			return false;
		try {
//			LegalEntity legalEntity = getLegalEntity();
			// Assumption: This delegate MUST correspond to work in tandem with the originally intended AssignEndCustomerWizard.
			AssignEndCustomerWizard wiz = (AssignEndCustomerWizard) getWizard();
			LegalEntity legalEntity = wiz.getLegalEntity();

			TradeManagerRemote tm = JFireEjb3Factory.getRemoteBean(TradeManagerRemote.class, Login.getLogin().getInitialContextProperties());
			AnchorID endCustomerID = (AnchorID) JDOHelper.getObjectId(legalEntity);

			tm.assignEndCustomer(endCustomerID, wiz.getArticleIDs());
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
		return true;
	}
}
