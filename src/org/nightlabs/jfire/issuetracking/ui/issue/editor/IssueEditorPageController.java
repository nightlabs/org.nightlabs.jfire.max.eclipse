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

import org.eclipse.ui.IEditorInput;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.jfire.base.ui.entity.editor.ActiveEntityEditorPageController;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueComment;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class IssueEditorPageController extends ActiveEntityEditorPageController<Issue> {

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		Issue.FETCH_GROUP_THIS_ISSUE,
		IssueType.FETCH_GROUP_THIS_ISSUE_TYPE,
//		IssueType.FETCH_GROUP_ISSUE_RESOLUTIONS,
		IssuePriority.FETCH_GROUP_NAME,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssueResolution.FETCH_GROUP_THIS_ISSUE_RESOLUTION,
		IssueComment.FETCH_GROUP_THIS_COMMENT,
		IssueLink.FETCH_GROUP_THIS_ISSUE_LINK,
		Statable.FETCH_GROUP_STATE,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME,
		User.FETCH_GROUP_THIS_USER,
		
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
//		StateDefinition.FETCH_GROUP_NAME};
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
	
	public void setIssue(Issue issue) {
		setControllerObject(issue);
	}
	
	@Override
	protected IEditorInput createNewInstanceEditorInput() {
		return new IssueEditorInput(getIssueID(), true);
	}

	@Override
	protected String[] getEntityFetchGroups() {
		return FETCH_GROUPS;
	}

	@Override
	protected Issue retrieveEntity(ProgressMonitor monitor) {
		return IssueDAO.sharedInstance().getIssue(getIssueID(), getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
	}

	@Override
	protected Issue storeEntity(Issue controllerObject, ProgressMonitor monitor) {
		return IssueDAO.sharedInstance().storeIssue(controllerObject, true, getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
	}
}