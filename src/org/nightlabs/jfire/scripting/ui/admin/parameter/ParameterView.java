/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.scripting.ui.admin.parameter;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.NotificationAdapterSWTThreadSync;
import org.nightlabs.base.ui.part.ControllablePart;
import org.nightlabs.base.ui.part.PartVisibilityListener;
import org.nightlabs.base.ui.part.PartVisibilityTracker;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.base.ui.login.part.LSDPartController;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;
import org.nightlabs.jfire.scripting.id.ScriptParameterSetID;
import org.nightlabs.jfire.scripting.ui.ScriptParameterSetProvider;
import org.nightlabs.jfire.scripting.ui.ScriptParameterSetTable;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * A View displaying the parameter-Sets of the local organisation
 * and providing actions to manipulate them.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ParameterView
extends
	ViewPart
implements
	ControllablePart,
	PartVisibilityListener
{

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ParameterView.class);
	
	public static final String ID_VIEW = ParameterView.class.getName();
	
	private XComposite wrapper;
	private ScriptParameterSetTable parameterSetTable;
	private ScriptParameterSetTableMenuManager menuManager;
	
	private Job fetchParameterSetsJob = new Job(Messages.getString("org.nightlabs.jfire.scripting.ui.admin.parameter.ParameterView.fetchParameterSetsJob.name")){ //$NON-NLS-1$
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			final Collection<ScriptParameterSet> sets = ScriptParameterSetProvider.sharedInstance().getParameterSets();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					parameterSetTable.setInput(sets);
					parameterSetTable.refresh(true);
				}
			});
			return Status.OK_STATUS;
		}
		
	};
	
	private IDoubleClickListener tableDoubleClickListener = new IDoubleClickListener () {

		public void doubleClick(DoubleClickEvent event) {
		}
	};
	
	/**
	 * 
	 */
	public ParameterView() {
		super();
		LSDPartController.sharedInstance().registerPart(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		LSDPartController.sharedInstance().createPartControl(this, parent);
		PartVisibilityTracker.sharedInstance().addVisibilityListener(this, this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}

	public void createPartContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
		parameterSetTable = new ScriptParameterSetTable(wrapper, SWT.NONE, true);
		fetchParameterSetsJob.schedule();
		Collection<String> input = new ArrayList<String>();
		input.add(Messages.getString("org.nightlabs.jfire.scripting.ui.admin.parameter.ParameterView.fetchParameterSets.loadingMessage")); //$NON-NLS-1$
		parameterSetTable.setInput(input);
		parameterSetTable.getTableViewer().addDoubleClickListener(tableDoubleClickListener);
		// TODO: Still work to be done on ParameterSets
		menuManager = new ScriptParameterSetTableMenuManager(
				"#scriptParameterSetContextMenu", //$NON-NLS-1$
				parameterSetTable.getTableViewer().getControl(),
				this,
				parameterSetTable.getTableViewer()
			);
		JDOLifecycleManager.sharedInstance().addNotificationListener(ScriptParameterSetID.class, changeListener);
	}
	
	private NotificationListener changeListener = new NotificationAdapterSWTThreadSync() {
		public void notify(NotificationEvent evt) {
			logger.info("changeListener got notified with event "+evt); //$NON-NLS-1$
			parameterSetTable.refresh(true);
		}
	};
	
	public boolean canDisplayPart() {
		return Login.isLoggedIn();
	}

	public void partVisible(IWorkbenchPartReference partRef) {
	}

	public void partHidden(IWorkbenchPartReference partRef) {
	}

}
