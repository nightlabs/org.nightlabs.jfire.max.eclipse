package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assigncustomer;

import org.nightlabs.base.ui.wizard.IWizardDelegate;
import org.nightlabs.base.ui.wizard.IWizardDelegateFactory;

public class AssignCustomerWizardDelegateFactory implements IWizardDelegateFactory {
	@Override
	public IWizardDelegate createWizardDelegate() {
		return new AssignCustomerWizardDelegate();
	}
}
