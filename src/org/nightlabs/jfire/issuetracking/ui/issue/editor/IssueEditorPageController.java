/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 ******************************************************************************/
package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.IssueWorkTimeRange;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.history.IssueHistoryItem;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issue.jbpm.JbpmConstants;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.Util;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class IssueEditorPageController extends ActiveEntityEditorPageController<Issue> {

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_SUBJECT,
		Issue.FETCH_GROUP_ISSUE_PROJECT,
		Issue.FETCH_GROUP_DESCRIPTION,
		Issue.FETCH_GROUP_ISSUE_LINKS,
		Issue.FETCH_GROUP_ISSUE_REPORTER,
		Issue.FETCH_GROUP_ISSUE_ASSIGNEE,
		Issue.FETCH_GROUP_ISSUE_TYPE,
		Issue.FETCH_GROUP_ISSUE_PRIORITY,
		Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
		Issue.FETCH_GROUP_ISSUE_RESOLUTION,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		Issue.FETCH_GROUP_ISSUE_COMMENTS,
		Issue.FETCH_GROUP_ISSUE_WORK_TIME_RANGES,
		Issue.FETCH_GROUP_ISSUE_FILELIST,
		Issue.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_STATES,
		Issue.FETCH_GROUP_PROPERTY_SET,
		PropertySet.FETCH_GROUP_DATA_FIELDS, PropertySet.FETCH_GROUP_FULL_DATA,
		IssueType.FETCH_GROUP_NAME,
		IssueType.FETCH_GROUP_ISSUE_PRIORITIES,
		IssueType.FETCH_GROUP_ISSUE_SEVERITY_TYPES,
		IssueType.FETCH_GROUP_ISSUE_RESOLUTIONS,
		IssueType.FETCH_GROUP_PROCESS_DEFINITION,
		IssuePriority.FETCH_GROUP_NAME,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssueResolution.FETCH_GROUP_NAME,
		IssueLink.FETCH_GROUP_ISSUE_LINK_TYPE,
		IssueLink.FETCH_GROUP_LINKED_OBJECT,
		IssueLink.FETCH_GROUP_LINKED_OBJECT_CLASS,
		Statable.FETCH_GROUP_STATE,
		Statable.FETCH_GROUP_STATES,
		StatableLocal.FETCH_GROUP_STATE,
		StatableLocal.FETCH_GROUP_STATES,
		State.FETCH_GROUP_STATE_DEFINITION,
		State.FETCH_GROUP_STATABLE,
		StateDefinition.FETCH_GROUP_NAME,
		User.FETCH_GROUP_NAME,
		IssueComment.FETCH_GROUP_USER,
		IssueLinkType.FETCH_GROUP_NAME,
		IssueHistoryItem.FETCH_GROUP_USER,
		IssueWorkTimeRange.FETCH_GROUP_USER,
		Issue.FETCH_GROUP_ISSUE_MARKERS,         // <-- Since 14.05.2009
		IssueMarker.FETCH_GROUP_NAME,            // <-- Since 14.05.2009
		IssueMarker.FETCH_GROUP_DESCRIPTION,     // <-- Since 14.05.2009
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA, // <-- Since 14.05.2009
/*		Issue.FETCH_GROUP_THIS_ISSUE,
		IssueType.FETCH_GROUP_THIS_ISSUE_TYPE,
//		IssueType.FETCH_GROUP_ISSUE_RESOLUTIONS,
		IssuePriority.FETCH_GROUP_NAME,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssueResolution.FETCH_GROUP_NAME,
		IssueComment.FETCH_GROUP_THIS_COMMENT,
		IssueLink.FETCH_GROUP_THIS_ISSUE_LINK,
		IssueLink.FETCH_GROUP_LINKED_OBJECT,
		IssueLink.FETCH_GROUP_LINKED_OBJECT_CLASS,
		Statable.FETCH_GROUP_STATE,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		User.FETCH_GROUP_NAME,
		IssueLinkType.FETCH_GROUP_NAME,
//		Issue.FETCH_GROUP_SUBJECT,
//		Issue.FETCH_GROUP_ISSUE_TYPE,
//		Issue.FETCH_GROUP_DESCRIPTION,
//		Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
//		Issue.FETCH_GROUP_ISSUE_PRIORITY,
//		Issue.FETCH_GROUP_ISSUE_ASSIGNEE,
//		Issue.FETCH_GROUP_ISSUE_REPORTER,
//		Issue.FETCH_GROUP_ISSUE_RESOLUTION,
//		Issue.FETCH_GROUP_ISSUE_FILELIST,
//		Issue.FETCH_GROUP_ISSUE_LOCAL,
//		Issue.FETCH_GROUP_ISSUE_LINKS,
//		IssueSubject.FETCH_GROUP_THIS_ISSUE_SUBJECT_NAMES,


//		FetchPlan.DEFAULT,
//		Issue.FETCH_GROUP_THIS_ISSUE,
//		IssueType.FETCH_GROUP_THIS_ISSUE_TYPE,
////		IssueDescription.FETCH_GROUP_THIS_DESCRIPTION,
////		IssueSubject.FETCH_GROUP_THIS_ISSUE_SUBJECT,
//		IssueFileAttachment.FETCH_GROUP_THIS_FILEATTACHMENT,
//		IssueSeverityType.FETCH_GROUP_THIS_ISSUE_SEVERITY_TYPE,
////		IssuePriority.FETCH_GROUP_THIS_ISSUE_PRIORITY,
//		IssueResolution.FETCH_GROUP_THIS_ISSUE_RESOLUTION,
//		IssueLocal.FETCH_GROUP_THIS_ISSUE_LOCAL,
//		IssueComment.FETCH_GROUP_THIS_COMMENT,
//		StatableLocal.FETCH_GROUP_STATE,
//		Statable.FETCH_GROUP_STATE,
//		State.FETCH_GROUP_STATE_DEFINITION,
//		StateDefinition.FETCH_GROUP_NAME};*/
	};

	public IssueEditorPageController(EntityEditor editor)
	{
		super(editor);
	}

	protected IssueID getIssueID() {
		IssueEditorInput input = (IssueEditorInput) getEntityEditor().getEditorInput();
		return input.getJDOObjectID();
	}

	public Issue getIssue() {
		return getControllerObject();
	}

	@Override
	protected IEditorInput createNewInstanceEditorInput() {
		return new IssueEditorInput(getIssueID(), true);
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS;
	}

	private IStruct struct;

	@Override
	protected Issue retrieveEntity(ProgressMonitor monitor) {
		Issue issue = IssueDAO.sharedInstance().getIssue(getIssueID(), getEntityFetchGroups(), getEntityMaxFetchDepth(), new SubProgressMonitor(monitor, 70));
		struct = StructLocalDAO.sharedInstance().getStructLocal(issue.getPropertySet().getStructLocalObjectID(), new SubProgressMonitor(monitor, 30));
		return issue;
	}

	@Override
	protected void setControllerObject(Issue controllerObject) {
		if (controllerObject != null)
			controllerObject.getPropertySet().inflate(struct);

		super.setControllerObject(controllerObject);
	}

	@Override
	protected Issue storeEntity(Issue controllerObject, ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorPageController.monitor.savingIssue.text"), 100); //$NON-NLS-1$
		try {
			controllerObject = Util.cloneSerializable(controllerObject);
			controllerObject.getPropertySet().deflate();

			IssueID issueID = (IssueID) JDOHelper.getObjectId(controllerObject);
			if (issueID == null)
				throw new IllegalStateException("JDOHelper.getObjectId(controllerObject) returned null for controllerObject=" + controllerObject); //$NON-NLS-1$

			Issue issue;
			issue = IssueDAO.sharedInstance().storeIssue(
					controllerObject, true/*jbpmTransitionName == null*/, getEntityFetchGroups(), getEntityMaxFetchDepth(),
					new SubProgressMonitor(monitor, 50)
			);

//			if (jbpmTransitionName == null)
//				monitor.worked(50);
//			else {
			if (jbpmTransitionName != null && 
					!jbpmTransitionName.equals(JbpmConstants.TRANSITION_NAME_ASSIGN) && 
					!jbpmTransitionName.equals(JbpmConstants.TRANSITION_NAME_UNASSIGN)) {
				issue = IssueDAO.sharedInstance().signalIssue(
						issueID, jbpmTransitionName, true, getEntityFetchGroups(), getEntityMaxFetchDepth(),
						new SubProgressMonitor(monitor, 50)
				);
				jbpmTransitionName = null;
			}

			return issue;
		} finally {
			monitor.done();
		}
	}

	private String jbpmTransitionName;

	/**
	 * @return <code>null</code> or the name of the jBPM transition that will be performed when saving.
	 */
	public String getJbpmTransitionName() {
		return jbpmTransitionName;
	}
	public void setJbpmTransitionName(String transitionName) {
		this.jbpmTransitionName = transitionName;
	}

//	@Override
//	protected boolean checkForSelfCausedChange(DirtyObjectID dirtyObjectID) {
//		return false;
//	}
}