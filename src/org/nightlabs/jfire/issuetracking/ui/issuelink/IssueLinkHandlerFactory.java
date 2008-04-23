package org.nightlabs.jfire.issuetracking.ui.issuelink;


import org.eclipse.core.runtime.IExecutableExtension;
import org.nightlabs.jdo.ObjectID;


/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public interface IssueLinkHandlerFactory<LinkedObjectID extends ObjectID, LinkedObject> extends IExecutableExtension
{
	String getCategoryId();
	
	Class<? extends Object> getLinkedObjectClass();
	
	String getName();
	
	void setName(String name);
	
	/**
	 * @return a <tt>Collection</tt> of {@link IssueLinkAdder}
	 */
	IssueLinkAdder createIssueLinkAdder();

	IssueLinkHandler<LinkedObjectID, LinkedObject> createIssueLinkHandler();
}
