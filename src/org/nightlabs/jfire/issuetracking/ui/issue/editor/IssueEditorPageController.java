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
import org.nightlabs.jfire.issue.IssueDescription;
import org.nightlabs.jfire.issue.IssueFileAttachment;
import org.nightlabs.jfire.issue.IssueLocal;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueSubject;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class IssueEditorPageController extends ActiveEntityEditorPageController<Issue> {

	private static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT, 
		Issue.FETCH_GROUP_THIS,
		IssueType.FETCH_GROUP_THIS,
		IssueDescription.FETCH_GROUP_THIS, 
		IssueSubject.FETCH_GROUP_THIS,
		IssueFileAttachment.FETCH_GROUP_THIS,
		IssueSeverityType.FETCH_GROUP_THIS,
		IssuePriority.FETCH_GROUP_THIS,
		IssueResolution.FETCH_GROUP_THIS,
		IssueLocal.FETCH_GROUP_THIS,
		IssueComment.FETCH_GROUP_THIS,
		StatableLocal.FETCH_GROUP_STATE,
		Statable.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME};

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

	@Override
	protected Issue retrieveEntity(ProgressMonitor monitor) {
		return IssueDAO.sharedInstance().getIssue(getIssueID(), getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
	}

	@Override
	protected Issue storeEntity(Issue controllerObject, ProgressMonitor monitor) {
		return IssueDAO.sharedInstance().storeIssue(controllerObject, true, getEntityFetchGroups(), getEntityMaxFetchDepth(), monitor);
	}
}