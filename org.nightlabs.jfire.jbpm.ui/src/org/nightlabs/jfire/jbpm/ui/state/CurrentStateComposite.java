package org.nightlabs.jfire.jbpm.ui.state;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.jbpm.dao.StateDAO;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jbpm.graph.def.id.StateID;
import org.nightlabs.jfire.jbpm.ui.JFireJbpmPlugin;
import org.nightlabs.jfire.jbpm.ui.resource.Messages;
import org.nightlabs.l10n.GlobalDateFormatter;
import org.nightlabs.l10n.IDateFormatter;
import org.nightlabs.progress.ProgressMonitor;

public class CurrentStateComposite
		extends XComposite
{
	private Hyperlink stateDefinitionName;
	private Hyperlink timestamp;
	private Hyperlink userName;

	public CurrentStateComposite(Composite parent, int style)
	{
		super(parent, style);
		getGridLayout().numColumns = 3;
		getGridData().grabExcessHorizontalSpace = false;
		getGridData().grabExcessVerticalSpace = false;
		getGridData().horizontalAlignment = SWT.BEGINNING;
		getGridData().verticalAlignment = SWT.BEGINNING;

		this.stateDefinitionName = new Hyperlink(this, SWT.NONE);
		this.stateDefinitionName.setText(""); //$NON-NLS-1$
		this.timestamp = new Hyperlink(this, SWT.NONE);
		this.timestamp.setText(""); //$NON-NLS-1$
		this.userName = new Hyperlink(this, SWT.NONE);
		this.userName.setText(""); //$NON-NLS-1$

		addMouseListenerForStateHistoryDialog(this);
	}

	private MouseListener mouseListenerForStateHistoryDialog = new MouseAdapter() {
		@Override
		public void mouseUp(MouseEvent e)
		{
			if (statable == null)
				return;

			StateHistoryDialog stateHistoryDialog = new StateHistoryDialog(getShell(), (ObjectID) JDOHelper.getObjectId(statable));
			stateHistoryDialog.open();
		}
	};

	private void addMouseListenerForStateHistoryDialog(Composite composite)
	{
		Control[] children = composite.getChildren();
		for (Control child : children) {
			child.addMouseListener(mouseListenerForStateHistoryDialog);
			child.setToolTipText(Messages.getString("org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite.statableHistoryWidget.tooltip")); //$NON-NLS-1$

			if (child instanceof Composite)
				addMouseListenerForStateHistoryDialog((Composite) child);
		}
	}

	private Statable statable;

	public Statable getStatable()
	{
		return statable;
	}
	public void setStatable(final Statable _statable)
	{
		Job job = new Job(Messages.getString("org.nightlabs.jfire.jbpm.ui.state.CurrentStateComposite.loadJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor)
			{
				setStatable(_statable, monitor);
				return Status.OK_STATUS;
			}
		};
		job.setRule(JFireJbpmPlugin.stateCompositeSchedulingRule);
		job.schedule();
	}

	private State state;

	public static final String[] FETCH_GROUPS_STATE = {
		FetchPlan.DEFAULT, State.FETCH_GROUP_STATE_DEFINITION,
		State.FETCH_GROUP_USER,
		StateDefinition.FETCH_GROUP_NAME
	};

	public void setStatable(Statable _statable, ProgressMonitor monitor)
	{
		this.statable = _statable;
		StateID stateID = (StateID) JDOHelper.getObjectId(statable.getStatableLocal().getState());

		this.state = StateDAO.sharedInstance().getState(stateID, FETCH_GROUPS_STATE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

		Runnable runnable = new Runnable()
		{
			public void run()
			{
				if (stateDefinitionName.isDisposed())
					return;

				stateDefinitionName.setText(state.getStateDefinition().getName().getText());
				timestamp.setText(GlobalDateFormatter.sharedInstance().formatDate(state.getCreateDT(), IDateFormatter.FLAGS_DATE_SHORT_TIME_HMS));
				userName.setText(state.getUser().getName()); // + " (" + state.getUser().getOrganisationID() + ")");
				getParent().layout(true, true);
			}
		};

		if (Display.getCurrent() == null)
			Display.getDefault().asyncExec(runnable);
		else
			runnable.run();
	}
}
