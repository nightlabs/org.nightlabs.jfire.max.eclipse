package org.nightlabs.jfire.jbpm.ui.transition.next;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.jbpm.dao.TransitionDAO;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.jbpm.graph.def.Transition;
import org.nightlabs.jfire.jbpm.graph.def.id.StateID;
import org.nightlabs.jfire.jbpm.ui.JFireJbpmPlugin;
import org.nightlabs.jfire.jbpm.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class NextTransitionComposite
extends XComposite
implements ISelectionProvider
{
	
	private XComboComposite<Transition> nextTransitionCombo;
	private static final Transition EMPTY_TRANSITION;
	static {
		// Create EMPTY transition for allowing the user to deselect a previous selection.
		@SuppressWarnings("deprecation")
		StateDefinition stateDefinition = new StateDefinition() {
			private static final long serialVersionUID = 1L;
		};
		 EMPTY_TRANSITION = new Transition(stateDefinition, "empty"); //$NON-NLS-1$
		 EMPTY_TRANSITION.getName().setText(Locale.ENGLISH, " "); //$NON-NLS-1$
	}

	private Button signalButton;

	public NextTransitionComposite(Composite parent, int style)
	{
		this(parent, style, true);
	}

	public NextTransitionComposite(Composite parent, int style, boolean withSignalButton)
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
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateUI();
				fireSelectionChangedEvent();
			}
		});

		if (withSignalButton) {
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
	}

	private void updateUI()
	{
		if (signalButton != null)
			signalButton.setEnabled(getSelectedTransition() != null);
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
		Job job = new Job(Messages.getString("org.nightlabs.jfire.jbpm.ui.transition.next.NextTransitionComposite.loadJob.name")) { //$NON-NLS-1$
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
	public void setStatable(Statable _statable, ProgressMonitor monitor)
	{
		this.statable = _statable;
		final State state = statable.getStatableLocal().getState();
		stateID = (StateID) JDOHelper.getObjectId(state);

		// fetch the possible further transitions for the current state and filter them with the ITransitionFilters
		final List<Transition> transitions = filterTransitions(state, new SubProgressMonitor(monitor, 1));
		
		Collections.sort(transitions, new Comparator<Transition>() {
			@Override
			public int compare(Transition t1, Transition t2)
			{
				return t1.getName().getText().compareTo(t2.getName().getText());
			}
		});

		Runnable runnable = new Runnable()
		{
			@Override
			public void run()
			{
				if (nextTransitionCombo.isDisposed())
					return;

				nextTransitionCombo.removeAll();
				nextTransitionCombo.addElement(EMPTY_TRANSITION);
				nextTransitionCombo.addElements(transitions);
				nextTransitionCombo.setSelection(0);
				setEnabled(true);
				updateUI();
				layout(true);
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
		if (nextTransitionCombo.getSelectedElement() == EMPTY_TRANSITION)
			return null;
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

	private ListenerList transitionFilters = new ListenerList();
	
	public void addTransitionFilter(ITransitionFilter transitionFilter) {
		transitionFilters.add(transitionFilter);
	}

	public void removeTransitionFilter(ITransitionFilter transitionFilter) {
		transitionFilters.remove(transitionFilter);
	}
	
	protected List<Transition> filterTransitions(State state, ProgressMonitor monitor) {
		// fetch the possible further transitions for the current state
		final List<Transition> transitions = new LinkedList<Transition>(
				TransitionDAO.sharedInstance().getTransitions(
				stateID, Boolean.TRUE, FETCH_GROUPS_TRANSITION, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));

		Object[] listeners = transitionFilters.getListeners();
		
		
		Iterator<Transition> transIt = transitions.iterator();
		while(transIt.hasNext()) {
			Transition transition = transIt.next();
			boolean doShow = true;
			for (Object transitionFilter : listeners) {
				if (!((ITransitionFilter) transitionFilter).acceptTransition(state, transition)) {
					doShow = false;
					break;
				}
			}
			if (!doShow) {
				transIt.remove();
			}
		}
		return transitions;
	}
	
	private ListenerList selectionChangedListeners = new ListenerList();
	private void fireSelectionChangedEvent()
	{
		if (selectionChangedListeners.isEmpty())
			return;

		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

		for (Object l : selectionChangedListeners.getListeners()) {
			ISelectionChangedListener listener = (ISelectionChangedListener) l;
			listener.selectionChanged(event);
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionChangedListeners.add(listener);
	}

	@Override
	public ISelection getSelection() {
		Transition selectedTransition = getSelectedTransition();
		if (selectedTransition == null)
			return new StructuredSelection(new Object[0]);

		return new StructuredSelection(selectedTransition);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			throw new IllegalArgumentException("selection is not an instance of " + IStructuredSelection.class.getName() + " but " + (selection == null ? null : selection.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$

		IStructuredSelection sel = (IStructuredSelection) selection;
		Object selObj = sel.getFirstElement();
		if (selObj instanceof Transition)
			nextTransitionCombo.setSelection((Transition) selObj);
	}

}
