package org.nightlabs.jfire.issuetracking.ui.issue;

import org.eclipse.jface.viewers.LabelProvider;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;

/**
 * An extended class of the {@link LabelProvider}.
 * 
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueLabelProvider extends LabelProvider{
	@Override
	public String getText(Object element) 
	{
		if (element instanceof StateDefinition) {
			StateDefinition issueSeverityType = (StateDefinition) element;
			return issueSeverityType.getName().getText();
		}

		if (element instanceof IssueSeverityType) {
			IssueSeverityType issueSeverityType = (IssueSeverityType) element;
			return issueSeverityType.getIssueSeverityTypeText().getText();
		}

		if(element instanceof IssueType){
			IssueType issueType = (IssueType)element;
			
			return issueType.getName().getText();
		}

		if (element instanceof IssuePriority) {
			IssuePriority issuePriority = (IssuePriority) element;
			return issuePriority.getIssuePriorityText().getText();
		}
		
		if (element instanceof IssueResolution) {
			IssueResolution issueResolution = (IssueResolution) element;
			return issueResolution.getName().getText();
		}

		if (element instanceof Class) {
			Class c = (Class) element;
			return c.getSimpleName();
		}

		return super.getText(element);
	}		
}
