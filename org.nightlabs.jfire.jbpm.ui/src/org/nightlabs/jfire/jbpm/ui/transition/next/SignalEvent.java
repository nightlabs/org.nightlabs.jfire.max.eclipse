package org.nightlabs.jfire.jbpm.ui.transition.next;

import java.util.EventObject;

import org.nightlabs.jfire.jbpm.graph.def.Transition;
import org.nightlabs.jfire.jbpm.graph.def.id.StateID;

public class SignalEvent
extends EventObject
{
	private static final long serialVersionUID = 1L;

	private StateID stateID;
	private Transition transition;

	public SignalEvent(Object source, StateID stateID, Transition transition)
	{
		super(source);
		this.stateID = stateID;
		this.transition = transition;
	}

	/**
	 * @return the current state where the workflow is right now.
	 */
	public StateID getStateID()
	{
		return stateID;
	}
	/**
	 * @return the transition chosen by the user.
	 */
	public Transition getTransition()
	{
		return transition;
	}
}
