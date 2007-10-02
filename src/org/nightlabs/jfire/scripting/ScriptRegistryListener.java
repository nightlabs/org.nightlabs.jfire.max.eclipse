/**
 * 
 */
package org.nightlabs.jfire.scripting;

/**
 * Simple interface to let listeners react on changes of the
 * ScriptRegistry (category structure). Register instances
 * of this class to {@link org.nightlabs.jfire.scripting.ScriptRegistryItemProvider}
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public interface ScriptRegistryListener {
	
	/**
	 * Will be called when the ScriptRegistry has changed.
	 * E.g. a new script was created, or an item was moved
	 * or deleted. This will not be called if an items
	 * name or other properties changed, only if it affects
	 * the structure of the ScriptRegistry.
	 * This method is likely to be called from other threads
	 * than the SWT GUI thread.
	 */
	public void scriptRegistryChanged();	
}
