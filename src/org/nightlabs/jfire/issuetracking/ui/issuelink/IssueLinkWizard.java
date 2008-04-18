package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Set;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItem;

public class IssueLinkWizard 
extends DynamicPathWizard
{
	private Issue issue;
	
	private IssueLinkAdderComposite parent;
	private Set<IssueLinkTableItem> linkItems;
	
	public IssueLinkWizard(IssueLinkAdderComposite linkAdderComposite, Issue issue) {
		this.parent = linkAdderComposite;
		this.issue = issue;
		setWindowTitle("Link Objects to Issue");
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
			
			for (IssueLinkTableItem linkItem : linkItems) {
				linkItem.setIssueLinkType(issueLinkType);
				parent.addItem(linkItem);
			}
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
	
	public IssueLinkAdderComposite getLinkAdderComposite() {
		return parent;
	}
	
	public Set<IssueLinkTableItem> getLinkItems() {
		return linkItems;
	}
	
	public void setLinkItems(Set<IssueLinkTableItem> linkItems) {
		this.linkItems = linkItems;
	}
	
	private Class linkedClass;
	public void setLinkedClass(Class c) {
		this.linkedClass = c;
	}
	
	public Class getLinkedClass() {
		return linkedClass;
	}
}
