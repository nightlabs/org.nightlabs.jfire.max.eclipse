package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;

public class IssueLinkWizard 
extends DynamicPathWizard
{
	
//	private Map<ObjectID, IssueLinkType> objectID2TypeMap = new HashMap<ObjectID, IssueLinkType>();
//	private IssueLinkType issueLinkType;
	private Issue issue;
	
	private IssueLinkAdderComposite linkAdderComposite;
	
	public IssueLinkWizard(IssueLinkAdderComposite linkAdderComposite, Issue issue) {
		this.linkAdderComposite = linkAdderComposite;
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
//		linkAdderComposite.getIssueLinkTable().setInput(objectID2TypeMap.entrySet());
		return true;
	}

	@Override
	public boolean canFinish() {
//		if(objectID2TypeMap != null && objectID2TypeMap.size() != 0) {
//			for (Entry<ObjectID, IssueLinkType> entry : objectID2TypeMap.entrySet()) {
//				linkAdderComposite.addObjectID(entry.getKey(), entry.getValue());
//			}
//			return true;
//		}
		return true;
	}

//	/**
//	 * The method that should be used in IssueLinkWizardListPage
//	 * @param objectIDs
//	 */
//	public void setIssueLinkObjectIDs(Set<ObjectID> objectIDs) {
//		for(ObjectID objectID : objectIDs) {
//			objectID2TypeMap.put(objectID, null);
//		}
//	}
//	
//	/**
//	 * The method that should be used in IssueLinkWizardRelationPage
//	 * @param issueLinkType
//	 */
//	public void setIssueLinkType(IssueLinkType issueLinkType) {
//		for(ObjectID key : objectID2TypeMap.keySet()) {
//			objectID2TypeMap.put(key, issueLinkType);
//		}
//	}
	
	public Issue getIssue() {
		return issue;
	}
}
