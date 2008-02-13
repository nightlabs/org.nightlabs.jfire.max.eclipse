/**
 * 
 */
package org.nightlabs.jfire.scripting.ui.admin.parameter;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.notification.NotificationAdapterSWTThreadSync;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.scripting.ScriptParameterSet;
import org.nightlabs.jfire.scripting.admin.ui.resource.Messages;
import org.nightlabs.jfire.scripting.id.ScriptParameterSetID;
import org.nightlabs.jfire.scripting.ui.ScriptParameterSetProvider;
import org.nightlabs.jfire.scripting.ui.ScriptParameterSetTable;
import org.nightlabs.jfire.scripting.ui.ScriptRegistryListener;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ScriptParameterSetEditComposite extends XComposite {


	private ScriptParameterSetID selectedParameterSetID;
	
	private XComposite wrapper;
	private ScriptParameterSetTable parameterSetTable;
	private ScriptParameterSetTableMenuManager menuManager;
	private ScriptParameterSetDetailComposite detailComposite;
	
	private Job fetchParameterSetsJob = new Job(Messages.getString("org.nightlabs.jfire.scripting.ui.admin.parameter.ScriptParameterSetEditComposite.fetchParameterSetsJob.name")){ //$NON-NLS-1$
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
	
	private NotificationListener setChangeListener = new NotificationAdapterSWTThreadSync() {
		public void notify(NotificationEvent evt) {
			parameterSetTable.refresh(true);
		}
	};
	
	private ScriptRegistryListener registryListener = new ScriptRegistryListener() {
		public void scriptRegistryChanged() {
			fetchParameterSetsJob.schedule();
		}
	};
	
	private DisposeListener disposeListener = new DisposeListener() {
		public void widgetDisposed(DisposeEvent e) {
			ScriptParameterSetProvider.sharedInstance().remveReportRegistryListener(registryListener);
			JDOLifecycleManager.sharedInstance().removeNotificationListener(ScriptParameterSetID.class, setChangeListener);
		}
	};
	
	
	private IDoubleClickListener tableDoubleClickListener = new IDoubleClickListener () {

		public void doubleClick(DoubleClickEvent event) {
		}
	};

	
	/**
	 * @param parent
	 * @param style
	 */
	public ScriptParameterSetEditComposite(Composite parent, int style) {
		super(parent, style);
		init();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 */
	public ScriptParameterSetEditComposite(Composite parent, int style,
			LayoutMode layoutMode) {
		super(parent, style, layoutMode);
		init();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public ScriptParameterSetEditComposite(Composite parent, int style,
			LayoutDataMode layoutDataMode) {
		super(parent, style, layoutDataMode);
		init();
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public ScriptParameterSetEditComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
		init();
	}
	
	private void init() {
		wrapper = new XComposite(this, SWT.NONE);
		wrapper.getGridLayout().numColumns = 2;
		wrapper.getGridLayout().makeColumnsEqualWidth = true;
		parameterSetTable = new ScriptParameterSetTable(wrapper, SWT.NONE, true);
		Collection<String> input = new ArrayList<String>();
		input.add(Messages.getString("org.nightlabs.jfire.scripting.ui.admin.parameter.ScriptParameterSetEditComposite.fetchParameterSets.loadingMessage")); //$NON-NLS-1$
		parameterSetTable.setInput(input);
		parameterSetTable.getTableViewer().addDoubleClickListener(tableDoubleClickListener);
		menuManager = new ScriptParameterSetTableMenuManager(
				this.getClass().getName()+"#SetContextMenu",  //$NON-NLS-1$
				parameterSetTable.getTable()
			);
		JDOLifecycleManager.sharedInstance().addNotificationListener(ScriptParameterSetID.class, setChangeListener);
		fetchParameterSetsJob.schedule();
		detailComposite = new ScriptParameterSetDetailComposite(wrapper, SWT.NONE, XComposite.LayoutMode.TIGHT_WRAPPER);
		ScriptParameterSetProvider.sharedInstance().addScriptRegistryListener(registryListener);
		addDisposeListener(disposeListener);
	}

}
