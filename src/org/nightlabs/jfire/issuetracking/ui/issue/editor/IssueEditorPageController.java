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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.NotificationListenerJob;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
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
import org.nightlabs.jfire.issue.id.IssueLocalID;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
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
		IssueComment.FETCH_GROUP_THIS,
		StatableLocal.FETCH_GROUP_STATE,
		Statable.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		StateDefinition.FETCH_GROUP_NAME};

	private String KEEP_LOCAL_CHANGES = "Keep Local Changes";
	private String RELOAD = "Reload";
	private String LOAD_REMOTE_CHANGES = "Load Remote Changes";
	
	private String[] CHOICE_LIST = new String[]{KEEP_LOCAL_CHANGES, RELOAD, LOAD_REMOTE_CHANGES};
	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(IssueEditorPageController.class);
	
	private Issue issue;

	private boolean doReload;
	private NotificationListenerJob issueChangeListener = new NotificationAdapterJob("Loading changes...") {
		public void notify(NotificationEvent notificationEvent) {
			doReload = false;
			for (Iterator iterator = notificationEvent.getSubjects().iterator(); iterator.hasNext();) {
				// TODO: Implement listener correctly
				DirtyObjectID doID = (DirtyObjectID) iterator.next();
				final Object s = doID.getObjectID();
				if (s instanceof IssueID) {
					if (issue != null && JDOHelper.getObjectId(issue).equals(s)) {
						if (isDirty()) {
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									IssueEditorChoiceDialog dialog = new IssueEditorChoiceDialog(Display.getDefault().getActiveShell());
									if (dialog.open() == Dialog.OK) {
										if (dialog.getSelectedChoice().equals(KEEP_LOCAL_CHANGES)) {
											
										}
										
										else if (dialog.getSelectedChoice().equals(RELOAD)) {
											doReload = true;
										}
										
										else if (dialog.getSelectedChoice().equals(LOAD_REMOTE_CHANGES)) {
											try {
												RCPUtil.openEditor(new IssueEditorInput((IssueID)s), IssueEditor.EDITOR_ID);
											} catch (PartInitException e) {
												throw new RuntimeException(e);
											}								
										}
									}
								}
							});
						}
						
						break;
					} 
				} else if (s instanceof IssueLocalID && issue != null) {
					if (JDOHelper.getObjectId(issue.getStatableLocal()).equals(s)) {
						doReload = true;
						break;
					}
				}
			}
			
			if (doReload) {
				reload(getProgressMonitor());
			}
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
	
	class IssueEditorChoiceDialog extends CenteredDialog 
	{
		private List choiceList;
		private Label errorLabel;

		public IssueEditorChoiceDialog(Shell parentShell) {
			super(parentShell);
		}
		
		@Override
		protected Control createDialogArea(Composite parent) {
			XComposite wrapper = new XComposite(parent, SWT.NONE, LayoutMode.ORDINARY_WRAPPER, LayoutDataMode.GRID_DATA);
			Label label = new Label(wrapper, SWT.BOLD);
			label.setText("The object currently edited was changed on the server. what do you want to do?");
			GridData gd = new GridData();
			gd.heightHint = 40;
			label.setLayoutData(gd);
			
			new Label(wrapper, SWT.NONE).setText("Please Choose");
			choiceList = new List(wrapper, SWT.BORDER | SWT.SINGLE);
			
			for (String choice : CHOICE_LIST) {
				choiceList.add(choice);
			}
		
			choiceList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			return wrapper;
		}
		
		@Override
		protected void okPressed() {
			super.okPressed();
		}
		
		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("Choice Shell");
			newShell.setSize(400, 300);
		}
		
		public String getSelectedChoice() {
			return choiceList.getSelection()[0];
		}
	}
}