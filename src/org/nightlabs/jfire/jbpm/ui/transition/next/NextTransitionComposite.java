package org.nightlabs.jfire.jbpm.ui.transition.next;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.jbpm.dao.TransitionDAO;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.Transition;
import org.nightlabs.jfire.jbpm.ui.resource.Messages;
import org.nightlabs.jfire.trade.state.id.StateID;
import org.nightlabs.progress.ProgressMonitor;

public class NextTransitionComposite
		extends XComposite
{
	private XComboComposite<Transition> nextTransitionCombo;
	private Button signalButton;

	public NextTransitionComposite(Composite parent, int style)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		getGridLayout().numColumns = 2;
		getGridData().grabExcessVerticalSpace = false;
		getGridData().verticalAlignment = SWT.BEGINNING;

		nextTransitionCombo = new XComboComposite<Transition>(this, SWT.DROP_DOWN | SWT.READ_ONLY, new LabelProvider() {
			@Override
			public String getText(Object element)
			{
				Transition transition = (Transition) element;
				return transition.getName().getText();
			}
		});
		nextTransitionCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Implement
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateUI();
			}
		});
		signalButton = new Button(this, SWT.PUSH);
		signalButton.setText(Messages.getString("org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite.signalButton.text")); //$NON-NLS-1$
		signalButton.setEnabled(false);
		signalButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				setEnabled(false);
				fireSignalEvent();
			}
		});
	}

	private void updateUI()
	{
		signalButton.setEnabled(nextTransitionCombo.getSelectedElement() != null);
	}

	private Statable statable;

	public static final String[] FETCH_GROUPS_TRANSITION = {
		FetchPlan.DEFAULT,
		Transition.FETCH_GROUP_NAME
	};

	public Statable getStatable()
	{
		return statable;
	}
	public void setStatable(final Statable _statable)
	{
		new Job(Messages.getString("org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite.loadJob.name")) { //$NON-NLS-1$
			@Override
			@Implement
			protected IStatus run(ProgressMonitor monitor)
			{
				setStatable(_statable, monitor);
				return Status.OK_STATUS;
			}
		}.schedule();
	}
	public void setStatable(Statable _statable, ProgressMonitor monitor)
	{
		this.statable = _statable;
		State state = statable.getStatableLocal().getState();
		stateID = (StateID) JDOHelper.getObjectId(state);

		// fetch the possible further transitions for the current state
		final List<Transition> transitions = TransitionDAO.sharedInstance().getTransitions(
				stateID, Boolean.TRUE, FETCH_GROUPS_TRANSITION, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		Collections.sort(transitions, new Comparator<Transition>() {
			@Implement
			public int compare(Transition t1, Transition t2)
			{
				return t1.getName().getText().compareTo(t2.getName().getText());
			}
		});

		Runnable runnable = new Runnable()
		{
			@Implement
			public void run()
			{
				if (nextTransitionCombo.isDisposed())
					return;

				nextTransitionCombo.removeAll();
				nextTransitionCombo.addElements(transitions);
				setEnabled(true);
				updateUI();
			}
		};

		if (Display.getCurrent() == null)
			Display.getDefault().asyncExec(runnable);
		else
			runnable.run();
	}

	private StateID stateID;

	public StateID getStateID()
	{
		return stateID;
	}

	private ListenerList signalListeners = new ListenerList();

	public void addSignalListener(SignalListener listener)
	{
		signalListeners.add(listener);
	}

	public void removeSignalListener(SignalListener listener)
	{
		signalListeners.remove(listener);
	}

	public Transition getSelectedTransition()
	{
		return nextTransitionCombo.getSelectedElement();
	}

	private void fireSignalEvent()
	{
		Object[] listeners = signalListeners.getListeners();
		if (listeners.length < 1)
			return;

		Transition transition = getSelectedTransition();
		if (transition == null)
			return;

		SignalEvent event = new SignalEvent(this, stateID, getSelectedTransition());
		for (Object listener : listeners) {
			SignalListener l = (SignalListener) listener;
			l.signal(event);
		}
	}
}
