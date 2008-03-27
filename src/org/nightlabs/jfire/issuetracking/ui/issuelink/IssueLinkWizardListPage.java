/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Set;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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
		super("List of object that you want to link", "Object list");
		this.issueLinkAdder = issueLinkAdder;
		this.issueLinkWizard = issueLinkWizard;
	}
	
	@Override
	public Control createPageContents(Composite parent) {
//		XComposite c = new XComposite(parent, SWT.NONE);
//		c.getGridLayout().numColumns = 2;
//		
//		Label issueLinkTypeLabel = new Label(c, SWT.NONE);
//		issueLinkTypeLabel.setText("Issue Link Type: ");
//		
//		XComboComposite<IssueLinkType> issueLinkTypeCombo = new XComboComposite<IssueLinkType>(c, SWT.NONE, new IssueLinkTypeLabelProvider());
//		issueLinkTypeCombo.addSelectionChangedListener(new ISelectionChangedListener(){
//			public void selectionChanged(SelectionChangedEvent e) {
//				selectedIssueType = issueTypeCombo.getSelectedElement();
//				
//			}
//		});
		
		objectListComposite = issueLinkAdder.createComposite(parent);
		
		issueLinkAdder.addIssueLinkSelectionListener(new IssueLinkSelectionAdapter() {
			@Override
			public void issueLinkSelectionChanged(IssueLinkSelectionChangedEvent selectionChangedEvent) {
//				issueLinkAdder.createIssueLinks(issueLinkWizard.getIssue(), issueLinkType, monitor)
//				issueLinkAdder.createIssueLinks(issueLinkWizard.getIssue(), issueLinkType, monitor)getIssueLinkObjectIds());	
				WizardHopPage page = new IssueLinkWizardRelationPage(issueLinkWizard, issueLinkAdder);
				getWizardHop().addHopPage(page);
//				
				getContainer().updateButtons();
			}
		});
		
		issueLinkAdder.addIssueLinkDoubleClickListener(new IssueLinkDoubleClickListener() {
			@Override
			public void issueLinkDoubleClicked(IssueLinkDoubleClickedEvent event) {
				Set<ObjectID> objectIDs = issueLinkAdder.getIssueLinkObjectIds();
//				for (ObjectID objectID : issueLinkAdder.getIssueLinkObjectIds()) {
//				}
//				
//				issueLinkWizard.setIssueLinks();
//				issueLinkWizard.finish();
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