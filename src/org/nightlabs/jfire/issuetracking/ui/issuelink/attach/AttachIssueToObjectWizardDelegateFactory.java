package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import org.nightlabs.base.ui.wizard.IWizardDelegate;
import org.nightlabs.base.ui.wizard.IWizardDelegateFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class AttachIssueToObjectWizardDelegateFactory
implements IWizardDelegateFactory
{
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.IWizardDelegateFactory#createWizardActionHandler()
	 */
	@Override
	public IWizardDelegate createWizardDelegate() {
		return new AttachIssueToObjectWizardDelegate();
	}

}
