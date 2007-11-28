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
package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class IssuePageController extends EntityEditorPageController
{
//	private IssueQuery issueQuery = new IssueQuery();
	private Collection<Issue> issues = null;
	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(IssuePageController.class);

	public IssuePageController(EntityEditor editor)
	{
		super(editor);
//		issueQuery.setToExclude(100);
	}

	@Override
	public void dispose()
	{
		// TODO remove listener for new transfers
		super.dispose();
	}

	public void doLoad(IProgressMonitor monitor)
	{
		monitor.beginTask("Loading Issues....", 100);
		
		monitor.done();
		fireModifyEvent(null, null);
	}

	public void doSave(IProgressMonitor monitor)
	{
		// nothing to do
	}

	/**
	 * This method must be called on the UI thread!
	 */
	public void fireIssueQueryChange()
	{
//		propertyChangeSupport.firePropertyChange(PROPERTY_MONEY_TRANSFER_QUERY, null, issueQuery);

		Job job = new Job("Loading Issues....") {
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				doLoad(monitor);
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
	}

//	public IssueQuery getIssueQuery()
//	{
//		return issueQuery;
//	}

	public Collection<Issue> getIssues()
	{
		return issues;
	}

	
//	public static final String PROPERTY_MONEY_TRANSFER_QUERY = "issueQuery"; //$NON-NLS-1$
//
//	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
//
//	/**
//	 * Add a {@link PropertyChangeListener} which will be triggered on the UI thread. Currently,
//	 * the only property available is {@link #PROPERTY_MONEY_TRANSFER_QUERY} which
//	 * references the object returned by {@link #getIssueQuery()}.
//	 *
//	 * @param listener The listener to be added.
//	 */
//	public void addPropertyChangeListener(PropertyChangeListener listener)
//	{
//		propertyChangeSupport.addPropertyChangeListener(listener);
//	}
//	public void addPropertyChangeListener(String propertyName,
//			PropertyChangeListener listener)
//	{
//		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
//	}
//	public void removePropertyChangeListener(PropertyChangeListener listener)
//	{
//		propertyChangeSupport.removePropertyChangeListener(listener);
//	}
//	public void removePropertyChangeListener(String propertyName,
//			PropertyChangeListener listener)
//	{
//		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
//	}
}
