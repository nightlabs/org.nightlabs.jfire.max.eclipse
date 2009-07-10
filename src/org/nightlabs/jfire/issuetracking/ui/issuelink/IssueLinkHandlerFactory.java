package org.nightlabs.jfire.issuetracking.ui.issuelink;


import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;


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
	
	Image getImage();
	
	void setImage(Image image);
	
	/**
	 * @return a <tt>Collection</tt> of {@link IssueLinkAdder}
	 */
	IssueLinkAdder createIssueLinkAdder(Issue issue);

	IssueLinkHandler<LinkedObjectID, LinkedObject> createIssueLinkHandler();
}
