package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;

public class IssueLinkWizard 
extends DynamicPathWizard
{
	private Map<ObjectID, IssueLinkType> objectID2TypeMap = new HashMap<ObjectID, IssueLinkType>();
	private IssueLinkType issueLinkType;
	private Issue issue;
	
	private IssueLinkAdderComposite parent;
	
	public IssueLinkWizard(IssueLinkAdderComposite parent, Issue issue) {
		this.parent = parent;
		this.issue = issue;
		setWindowTitle("Link Objects to Issue");
	}

	@Override
	public void addPages() {
		IssueLinkWizardCategoryPage page = new IssueLinkWizardCategoryPage(this);
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public boolean canFinish() {
		if(objectID2TypeMap != null && objectID2TypeMap.size() != 0) {
			for (Entry<ObjectID, IssueLinkType> entry : objectID2TypeMap.entrySet()) {
				parent.addObjectID(entry.getKey(), entry.getValue());
			}
			return true;
		}
		return false;
	}

	public void setIssueLinkObjectIDs(Set<ObjectID> objectIDs) {
		for(ObjectID objectID : objectIDs) {
			objectID2TypeMap.put(objectID, null);
		}
	}
	
	public void setIssueLinkType(IssueLinkType issueLinkType) {
		for(ObjectID key : objectID2TypeMap.keySet()) {
			objectID2TypeMap.put(key, issueLinkType);
		}
	}
	
	public Issue getIssue() {
		return issue;
	}
}
