/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.wizard.WizardHopPage;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkWizardListPage extends WizardHopPage {

	private Composite objectListComposite;
	private IssueLinkAdder issueLinkAdder;
	private IssueLinkWizard issueLinkWizard;
	
	public IssueLinkWizardListPage(IssueLinkWizard iWizard, IssueLinkAdder adder) {
		super("List of object that you want to link", "Object list");
		this.issueLinkAdder = adder;
		this.issueLinkWizard = iWizard;
	}

	@Override
	public Control createPageContents(Composite parent) {
		this.objectListComposite = issueLinkAdder.createComposite(parent);
		issueLinkAdder.addIssueLinkSelectionListener(new IssueLinkSelectionAdapter() {
			@Override
			public void issueLinkSelectionChanged(
					IssueLinkSelectionChangedEvent selectionChangedEvent) {
//				issueLinkAdder.createIssueLinks(issueLinkWizard.getIssue(), issueLinkType, monitor)getIssueLinkObjectIds());	
//				WizardHopPage page = new IssueLinkWizardRelationPage(issueLinkWizard, issueLinkAdder);
//				getWizardHop().addHopPage(page);
//				
//				getContainer().updateButtons();
			}
		});
		
		issueLinkAdder.addIssueLinkDoubleClickListener(new IssueLinkDoubleClickListener() {
			@Override
			public void issueLinkDoubleClicked(IssueLinkDoubleClickedEvent event) {
//				issueLinkWizard.setIssueLinkObjectIDssueLinkObjectID(issueLinkAdder.getIssueLinkObjectIds());
				issueLinkWizard.finish();
			}
		});
		
		return objectListComposite;
	}
	
	@Override
	public boolean isPageComplete() {
		return issueLinkAdder.isComplete(); 
	}
}