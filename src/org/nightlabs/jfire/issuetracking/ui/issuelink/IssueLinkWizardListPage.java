/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Set;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueLinkType;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkWizardListPage extends WizardHopPage {

	private Composite objectListComposite;
	private IssueLinkAdder issueLinkAdder;
	private IssueLinkWizard issueLinkWizard;
	
	public IssueLinkWizardListPage(IssueLinkWizard issueLinkWizard, IssueLinkAdder issueLinkAdder) {
		super("Object List Page");
		this.issueLinkAdder = issueLinkAdder;
		this.issueLinkWizard = issueLinkWizard;
		
		new WizardHop(this);
	}
	
	@Override
	public Control createPageContents(Composite parent) {
		objectListComposite = issueLinkAdder.createComposite(parent);
		
		issueLinkAdder.addIssueLinkSelectionListener(new IssueLinkSelectionAdapter() {
			@Override
			public void issueLinkSelectionChanged(IssueLinkSelectionChangedEvent selectionChangedEvent) {
				WizardHopPage page = new IssueLinkWizardRelationPage(issueLinkWizard, issueLinkAdder);
				getWizardHop().addHopPage(page);
				
				Set<ObjectID> objectIDs = issueLinkAdder.getIssueLinkObjectIds();
//				issueLinkWizard.setIssueLinkObjectIDs(objectIDs);
				
				getContainer().updateButtons();
			}
		});
		
		issueLinkAdder.addIssueLinkDoubleClickListener(new IssueLinkDoubleClickListener() {
			@Override
			public void issueLinkDoubleClicked(IssueLinkDoubleClickedEvent event) {
				Set<ObjectID> objectIDs = issueLinkAdder.getIssueLinkObjectIds();
//				issueLinkWizard.setIssueLinkObjectIDs(objectIDs);
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

class IssueLinkTypeLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) 
	{
		if (element instanceof IssueLinkType) {
			IssueLinkType issueLinkType = (IssueLinkType) element;
			return issueLinkType.getName().getText();
		}

		return super.getText(element);
	}		
}