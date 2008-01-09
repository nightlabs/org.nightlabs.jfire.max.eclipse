/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.wizard.WizardHopPage;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkWizardListPage extends WizardHopPage {

	/**
	 * @param pageName
	 * @param title
	 */
	public IssueLinkWizardListPage(String pageName, String title) {
		super(pageName, title);
	}

	private Composite objectListComposite;
	private IssueLinkAdder adder;
	
	public IssueLinkWizardListPage(IssueLinkAdder adder) {
		this("Page Name", "Title");
		this.adder = adder;
	}

	@Override
	public Control createPageContents(Composite parent) {
		this.objectListComposite = adder.createComposite(parent);
		return objectListComposite;
	}

}
