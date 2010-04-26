package org.nightlabs.jfire.issuetracking.ui.issuelink.attach;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.WizardDelegateRegistry;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;

/**
 * The pages and the finish logic are provided by default by {@link AttachIssueToObjectWizardDelegate} which is registered
 * via the extension-point with the id {@link WizardDelegateRegistry#EXTENSION_POINT_ID}
 *
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author Daniel Mazurek
 */
public class AttachIssueToObjectWizard
extends DynamicPathWizard
{
	public static String[] FETCH_GROUP = new String[]{
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_ISSUE_TYPE,
		Issue.FETCH_GROUP_SUBJECT,
		Issue.FETCH_GROUP_DESCRIPTION,
		Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
		Issue.FETCH_GROUP_ISSUE_PRIORITY,
		Issue.FETCH_GROUP_ISSUE_RESOLUTION,
		Statable.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		IssueType.FETCH_GROUP_NAME,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssuePriority.FETCH_GROUP_NAME,
		IssueResolution.FETCH_GROUP_NAME,
		StateDefinition.FETCH_GROUP_NAME,
		Issue.FETCH_GROUP_ISSUE_MARKERS,
		IssueMarker.FETCH_GROUP_NAME,
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA,
		Issue.FETCH_GROUP_ISSUE_LINKS,
		IssueLinkType.FETCH_GROUP_NAME,
		Issue.FETCH_GROUP_ISSUE_ASSIGNEE,
		Issue.FETCH_GROUP_ISSUE_REPORTER,
		Issue.FETCH_GROUP_ISSUE_RESOLUTION,
		Issue.FETCH_GROUP_ISSUE_COMMENTS,
		Issue.FETCH_GROUP_ISSUE_FILELIST,
		Issue.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_STATES,
	};

	private Object attachedObject;
	private Issue selectedIssue;

	public AttachIssueToObjectWizard(Object attachedObject) {
		this.attachedObject = attachedObject;
		setWindowTitle(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard.title")); //$NON-NLS-1$
	}

	public void setSelectedIssue(Issue issue) {
		this.selectedIssue = issue;
	}

	public Issue getSelectedIssue() {
		return selectedIssue;
	}

	public Object getAttachedObject() {
		return attachedObject;
	}
}
