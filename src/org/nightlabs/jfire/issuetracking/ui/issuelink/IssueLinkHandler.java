/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.jdo.ObjectID;

/**
 * @author chairatk
 *
 */
public interface IssueLinkHandler {

	Image getLinkObjectImage(ObjectID objectID);
	
	String getLinkObjectDescription(ObjectID objectID);
	
	void openLinkObject();
	
}
