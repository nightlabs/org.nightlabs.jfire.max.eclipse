/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.wizard.WizardHopPage;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkWizardListPage extends WizardHopPage {

	private Composite objectListComposite;
	private IssueLinkAdder adder;
	private IssueLinkWizard iWizard;
	
	public IssueLinkWizardListPage(IssueLinkWizard iWizard, IssueLinkAdder adder) {
		super("Page", "Title");
		this.adder = adder;
		this.iWizard = iWizard;
	}

	@Override
	public Control createPageContents(Composite parent) {
		this.objectListComposite = adder.createComposite(parent);
		adder.addIssueLinkSelectionListener(new IssueLinkSelectionAdapter() {
			@Override
			public void issueLinkSelectionChanged(
					IssueLinkSelectionChangedEvent selectionChangedEvent) {
				iWizard.setIssueLinkObjectID(adder.getIssueLinkObjectIds());	
				getContainer().updateButtons();
			}
		});
		
		adder.addIssueLinkDoubleClickListener(new IssueLinkDoubleClickListener() {
			@Override
			public void issueLinkDoubleClicked(IssueLinkDoubleClickedEvent event) {
				iWizard.setIssueLinkObjectID(adder.getIssueLinkObjectIds());
				iWizard.finish();
			}
		});
		
		return objectListComposite;
	}
	
	@Override
	public boolean isPageComplete() {
		return adder.isComplete(); 
	}
}