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
import org.nightlabs.jfire.issue.IssueLink;
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
			public String getLinkedObjectName(IssueLink issueLink, Object linkedObject) {
				return "Unknown Object Class";
			}
			
			@Override
			public Image getLinkedObjectImage(IssueLink issueLink, Object linkedObject) {
				return null;
			}
			
			@Override
			public void openLinkedObject(IssueLink issueLink, ObjectID linkedObjectID) {
				
			}
			
			@Override
			public Map getLinkedObjects(Set issueLinks, ProgressMonitor monitor) {
				return null;
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
