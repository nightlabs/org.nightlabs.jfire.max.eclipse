package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Set;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItem;

public class IssueLinkWizard 
extends DynamicPathWizard
{
	private Issue issue;

	private IssueLinkTable issueLinkTable;
	private Set<IssueLinkTableItem> linkItems;

	public IssueLinkWizard(IssueLinkTable issueLinkTable, Issue issue) {
		this.issueLinkTable = issueLinkTable;
		this.issue = issue;
		setWindowTitle("Link objects to an issue");
	}

	@Override
	public void addPages() {
		IssueLinkWizardCategoryPage categoryPage = new IssueLinkWizardCategoryPage(this);
		addPage(categoryPage);
	}

	/**
	 * This method is used for adding issueLink to IssueLinkAdderComposite.
	 */
	@Override
	public boolean performFinish() {
		if (linkItems != null) {
			IssueLinkType issueLinkType = null;
			
			if (getPage(IssueLinkWizardRelationPage.class.getName()) != null) {
				IssueLinkWizardRelationPage relationPage = (IssueLinkWizardRelationPage)getPage(IssueLinkWizardRelationPage.class.getName());
				issueLinkType = relationPage.getIssueLinkType();
			}
			
			issueLinkTable.addIssueLinkTableItems(linkItems);
		}
		return true;
	}

	@Override
	public boolean canFinish() {
		if (linkItems != null) {
			return true;
		}
		return false;
	}

	public Issue getIssue() {
		return issue;
	}
	
	public IssueLinkTable getIssueLinkTable() {
		return issueLinkTable;
	}
	
	public Set<IssueLinkTableItem> getLinkItems() {
		return linkItems;
	}
	
	public void setLinkItems(Set<IssueLinkTableItem> linkItems) {
		this.linkItems = linkItems;
	}
	
	private Class linkedObjectClass;
	public void setLinkedClass(Class c) {
		this.linkedObjectClass = c;
	}
	
	public Class getLinkedObjectClass() {
		return linkedObjectClass;
	}
}
