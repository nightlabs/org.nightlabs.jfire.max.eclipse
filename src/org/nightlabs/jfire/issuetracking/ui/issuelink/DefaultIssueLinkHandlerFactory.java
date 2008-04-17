/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issuelink;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.progress.ProgressMonitor;

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

			@Override
			public Image getLinkedObjectImage() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map getLinkedObjects(Set issueLinks, ProgressMonitor monitor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void openLinkedObject(ObjectID linkedObjectID) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getLinkedObjectName(ObjectID linkedObjectID) {
				// TODO Auto-generated method stub
				return "Unknown Object Class";
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
