package org.nightlabs.jfire.issuetracking.ui.issuelink;


import org.eclipse.core.runtime.IExecutableExtension;


/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public interface IssueLinkHandlerFactory extends IExecutableExtension
{
	String getCategoryId();
	
	String getName();
	
	Class<? extends Object> getLinkObjectClass();
	
	void setEntryType(String entryType);
	
	void setName(String name);
	
	/**
	 * @return a <tt>Collection</tt> of {@link IssueLinkAdder}
	 */
	IssueLinkAdder createIssueLinkAdder();
}
