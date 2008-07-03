package org.nightlabs.jfire.issuetracking.ui.issuelink.create;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItem;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkAdder;
import org.nightlabs.xml.NLDOMUtil;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class CreateIssueLinkWizard 
extends DynamicPathWizard
{
	private Issue issue;

	private IssueLinkTable issueLinkTable;

	private IssueLinkAdder issueLinkAdder;
	private SelectIssueLinkHandlerFactoryWizardPage selectIssueLinkHandlerFactoryPage;
	private SelectLinkedObjectWizardPage selectLinkedObjectPage;
	private SelectIssueLinkTypeWizardPage selectIssueLinkTypePage;

	public CreateIssueLinkWizard(IssueLinkTable issueLinkTable, Issue issue) {
		this.issueLinkTable = issueLinkTable;
		this.issue = issue;
		setWindowTitle("Link objects to an issue");
	}

	@Override
	public void addPages() {
		selectIssueLinkHandlerFactoryPage = new SelectIssueLinkHandlerFactoryWizardPage();
		addPage(selectIssueLinkHandlerFactoryPage);

		selectLinkedObjectPage = new SelectLinkedObjectWizardPage();
		addPage(selectLinkedObjectPage);

		selectIssueLinkTypePage = new SelectIssueLinkTypeWizardPage();
		addPage(selectIssueLinkTypePage);

		selectIssueLinkHandlerFactoryPage.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (selectIssueLinkHandlerFactoryPage.getIssueLinkHandlerFactory() == null)
					issueLinkAdder = null;
				else
					issueLinkAdder = selectIssueLinkHandlerFactoryPage.getIssueLinkHandlerFactory().createIssueLinkAdder(issue);
				
				selectLinkedObjectPage.setIssueLinkAdder(issueLinkAdder);
				selectIssueLinkTypePage.setIssueLinkAdder(issueLinkAdder);
			}
		});
		
	}

	/**
	 * This method is used for adding issueLink to IssueLinkAdderComposite.
	 */
	@Override
	public boolean performFinish() {
		IssueLinkType issueLinkType = selectIssueLinkTypePage.getSelectedIssueLinkType();
		
		Set<ObjectID> linkedObjectIDs = selectLinkedObjectPage.getLinkedObjectIDs();
		Set<IssueLinkTableItem> issueLinkTableItems = new HashSet<IssueLinkTableItem>();
		for(ObjectID linkedObjectID : linkedObjectIDs) {
			IssueLinkTableItem linkedTableItem = new IssueLinkTableItem(linkedObjectID, issueLinkType);
			issueLinkTableItems.add(linkedTableItem);
			
			boolean isExist = issueLinkTable.getIssueLinkTableItems().contains(linkedTableItem);
			if (isExist) {
				MessageDialog.openError(getShell(), "This link is already existed!!", "This " + issueLinkAdder.getIssueLinkHandlerFactory().getLinkedObjectClass().getSimpleName() + " : " + linkedObjectID.toString() +  " with IssueLinkType = " + linkedTableItem.getIssueLinkType().getName().getText() + " is already existed.");
				return false;
			}
		}
		
		issueLinkTable.addIssueLinkTableItems(issueLinkTableItems);
		
		return true;
	}

	public Issue getIssue() {
		return issue;
	}

	public IssueLinkTable getIssueLinkTable() {
		return issueLinkTable;
	}
}
