/**
 * 
 */
package org.nightlabs.jfire.jbpm.ui.transition.next;

import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.Transition;

/**
 * Filter that can be used to filter the list of transitions the {@link NextTransitionComposite} shows for a state.
 * 
 * @author abieber
 */
public interface ITransitionFilter {
	
	/**
	 * Check whether the given transition should be shown. 
	 * 
	 * @return <code>true</code> if the transition should be shown.
	 */
	boolean acceptTransition(State state, Transition transition);
}