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

import java.util.Date;
import java.util.Iterator;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.NotificationListenerJob;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.issue.Issue;
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
import org.nightlabs.jfire.issue.id.IssueLocalID;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.progress.SubProgressMonitor;
import org.nightlabs.util.Util;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class IssueEditorPageController extends EntityEditorPageController
{
	
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
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME};
	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(IssueEditorPageController.class);
	
	private Issue issue;

	private NotificationListenerJob issueChangeListener = new NotificationAdapterJob("Loading changes...") {
		public void notify(NotificationEvent notificationEvent) {
			boolean doReload = false;
			for (Iterator iterator = notificationEvent.getSubjects().iterator(); iterator.hasNext();) {
				// TODO: Implement listener correctly
				DirtyObjectID doID = (DirtyObjectID) iterator.next();
				Object s = doID.getObjectID();
				if (s instanceof IssueID) {
					if (issue != null && JDOHelper.getObjectId(issue).equals(s)) {
//						doReload = true;
//						break;
					} 
				} else if (s instanceof IssueLocalID && issue != null) {
					if (JDOHelper.getObjectId(issue.getStatableLocal()).equals(s)) {
						doReload = true;
						break;
					}
				}
			}
			if (doReload)
				reload(getProgressMonitor());				
		}
	};
	
	public IssueEditorPageController(EntityEditor editor)
	{
		super(editor);
		JDOLifecycleManager.sharedInstance().addNotificationListener(
				new Class[] {Issue.class}, 
				issueChangeListener
		);
	}

	@Override
	public void dispose()
	{
		JDOLifecycleManager.sharedInstance().removeNotificationListener(
				new Class[] {Issue.class}, 
				issueChangeListener
		);
		super.dispose();
	}
	
	protected IssueID getIssueID() {
		IssueEditorInput input = (IssueEditorInput) getEntityEditor().getEditorInput();
		return input.getJDOObjectID();
	}

	public void doLoad(IProgressMonitor monitor)
	{
		monitor.beginTask("Loading Issue...", 100);
		Issue oldIssue = issue;
		ProgressMonitorWrapper pMonitor = new ProgressMonitorWrapper(monitor);
		Issue _issue = IssueDAO.sharedInstance().getIssue(getIssueID(), FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(pMonitor, 100));
		issue = Util.cloneSerializable(_issue);
		monitor.done();
		fireModifyEvent(oldIssue, issue);
	}

	public void doSave(IProgressMonitor monitor)
	{
		monitor.beginTask("Saving Issue...", 100);
		Issue oldIssue = issue;
		issue.setUpdateTimestamp(new Date());
		ProgressMonitorWrapper pMonitor = new ProgressMonitorWrapper(monitor);
		Issue _issue = IssueDAO.sharedInstance().storeIssue(issue, true, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(pMonitor, 100));
		issue = Util.cloneSerializable(_issue);
		monitor.done();
		fireModifyEvent(oldIssue, issue);
		monitor.done();
	}
	
	public Issue getIssue() {
		return issue;
	}
}
