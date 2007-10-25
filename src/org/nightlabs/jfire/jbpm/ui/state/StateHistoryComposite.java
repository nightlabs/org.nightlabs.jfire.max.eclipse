package org.nightlabs.jfire.jbpm.ui.state;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.jbpm.dao.StateDAO;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jbpm.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.ProgressMonitor;

public class StateHistoryComposite
extends AbstractTableComposite<State>
{
	private static class StateHistoryLabelProvider extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (!(element instanceof State)) {
				if (columnIndex == 0)
					return String.valueOf(element);
				else
					return ""; //$NON-NLS-1$
			}

			State state = (State) element;

			switch (columnIndex) {
				case 0:
					return DateFormatter.formatDateShortTimeHMS(state.getCreateDT(), true);
				case 1:
					return state.getStateDefinition().getName().getText();
				case 2:
					return state.getUser().getName();
				default:
					return ""; //$NON-NLS-1$
			}
		}
	}

	public StateHistoryComposite(Composite parent, int style)
	{
		super(parent, style);
	}

	@Override
	@Implement
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;

		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.jbpm.ui.state.StateHistoryComposite.columnDateTime.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.jbpm.ui.state.StateHistoryComposite.columnState.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.jbpm.ui.state.StateHistoryComposite.columnUser.text")); //$NON-NLS-1$

		table.setLayout(new WeightedTableLayout(
			new int[] {-1, 30, 70},
			new int[] {140}
		));
	}

	@Override
	@Implement
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setLabelProvider(new StateHistoryLabelProvider());
		tableViewer.setContentProvider(new TableContentProvider());
	}

	public static final String[] FETCH_GROUPS_STATE = {
		FetchPlan.DEFAULT, State.FETCH_GROUP_STATE_DEFINITION,
		State.FETCH_GROUP_USER,
		StateDefinition.FETCH_GROUP_NAME
	};

	private Statable statable;

	public void setStatableID(final ObjectID statableID)
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				setInput(new String[] {Messages.getString("org.nightlabs.jfire.jbpm.ui.state.StateHistoryComposite.loadingInput.text")}); //$NON-NLS-1$
			}
		});

		Job job = new Job(Messages.getString("org.nightlabs.jfire.jbpm.ui.state.StateHistoryComposite.loadJob.name")) { //$NON-NLS-1$
			@Override
			@Implement
			protected IStatus run(ProgressMonitor monitor)
			{
				final List<State> states = StateDAO.sharedInstance().getStates(statableID, FETCH_GROUPS_STATE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

				Collections.sort(states, new Comparator<State>() {
					public int compare(State o1, State o2)
					{
						int res = o1.getCreateDT().compareTo(o2.getCreateDT());
						if (res != 0)
							return res;

						if (o1.getOrganisationID().equals(o2.getOrganisationID())) {
							res = o1.getStateID() < o2.getStateID() ? -1 : (o1.getStateID() == o2.getStateID() ? 0 : 1);
						}

						return res;
					}
				});

				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						setInput(states);
//						getShell().layout(true, true);
					}
				});

				return Status.OK_STATUS;
			}
		};
		job.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		job.schedule();
	}
}
