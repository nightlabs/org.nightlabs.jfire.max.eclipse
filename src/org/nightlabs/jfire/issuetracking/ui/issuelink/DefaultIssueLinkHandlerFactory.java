/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.jdo.ObjectID;

/**
 * @author chairatk
 *
 */
public class DefaultIssueLinkHandlerFactory 
implements IssueLinkHandlerFactory 
{
	public IssueLinkAdder createIssueLinkAdder() {
		return null;
	}

	public IssueLinkHandler createIssueLinkHandler() {
		
		return new IssueLinkHandler() {

			public String getLinkObjectDescription(ObjectID objectID) {
				return "Unknown Object Class";
			}

			public Image getLinkObjectImage(ObjectID objectID) {
				return null;
			}

			public void openLinkObject() {

			}
		};
	}

	public String getCategoryId() {
		return null;
	}

	public Class<? extends Object> getLinkObjectClass() {
		return null;
	}

	public String getName() {
		return null;
	}

	public void setName(String name) {

	}

	public void setInitializationData(IConfigurationElement arg0, String arg1,
			Object arg2) throws CoreException {

	}
}
