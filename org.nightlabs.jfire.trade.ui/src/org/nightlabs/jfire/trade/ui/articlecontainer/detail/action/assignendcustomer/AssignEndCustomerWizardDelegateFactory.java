package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assignendcustomer;

import org.nightlabs.base.ui.wizard.IWizardDelegate;
import org.nightlabs.base.ui.wizard.IWizardDelegateFactory;

public class AssignEndCustomerWizardDelegateFactory implements IWizardDelegateFactory {
	@Override
	public IWizardDelegate createWizardDelegate() {
		return new AssignEndCustomerWizardDelegate();
	}
}
