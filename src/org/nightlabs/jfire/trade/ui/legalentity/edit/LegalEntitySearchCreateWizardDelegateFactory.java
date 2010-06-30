package org.nightlabs.jfire.trade.ui.legalentity.edit;

import org.nightlabs.base.ui.wizard.IWizardDelegate;
import org.nightlabs.base.ui.wizard.IWizardDelegateFactory;

/**
 * The factory that generates the {@link LegalEntitySearchCreateWizardDelegate}.
 *
 * @author khaireel at nightlabs dot de
 */
public class LegalEntitySearchCreateWizardDelegateFactory implements IWizardDelegateFactory {
	@Override
	public IWizardDelegate createWizardDelegate() {
		return new LegalEntitySearchCreateWizardDelegate();
	}
}
