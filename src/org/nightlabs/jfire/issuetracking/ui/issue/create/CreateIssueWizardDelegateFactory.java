package org.nightlabs.jfire.issuetracking.ui.issue.create;

import org.nightlabs.base.ui.wizard.IWizardDelegate;
import org.nightlabs.base.ui.wizard.IWizardDelegateFactory;

/**
 * @author Daniel Mazurek
 *
 */
public class CreateIssueWizardDelegateFactory 
implements IWizardDelegateFactory 
{
	/* (non-Javadoc)
	 * @see org.nightlabs.eclipse.ui.wizard.IWizardActionHandlerFactory#createWizardActionHandler()
	 */
	@Override
	public IWizardDelegate createWizardDelegate() {
		return new CreateIssueWizardDelegate();
	}

}
