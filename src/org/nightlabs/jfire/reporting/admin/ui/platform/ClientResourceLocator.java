/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.platform;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.birt.report.model.api.DefaultResourceLocator;
import org.eclipse.birt.report.model.api.IResourceLocator;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ClientResourceLocator extends DefaultResourceLocator implements
		IResourceLocator {

	/**
	 * 
	 */
	public ClientResourceLocator() {
	}
	
	@Override
	public URL findResource(ModuleHandle handle, String fileName, int type) {
//		String locale = ReportLayoutLocalisationData.extractLocale(fileName);
		if (currentReportLayoutID == null)
			return null;
		IFolder bundleFolder = getReportLayoutResourceFolder(currentReportLayoutID);
		IFile resourceFile = bundleFolder.getFile(fileName);
		if (resourceFile.exists())
			try {
				File file = RCPUtil.getResourceAsFile(resourceFile);
				if (!file.exists())
					return null;
				return file.toURL();
			} catch (MalformedURLException e) {
				throw new RuntimeException(e);
			}
		return null;
	}

	private static ReportRegistryItemID currentReportLayoutID;
	
	public static void setCurrentReportLayoutID(ReportRegistryItemID currentReportLayoutID) {
		ClientResourceLocator.currentReportLayoutID = currentReportLayoutID;
	}
	
	public static IFolder getReportLayoutResourceFolder(ReportRegistryItemID reportLayoutID) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("ReportLocalisation"); //$NON-NLS-1$
		try {
			if (!project.exists())
				project.create(null);
			if (!project.isOpen())
				project.open(null);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		IFolder bundleFolder = project.getFolder(reportLayoutID.reportRegistryItemType+"-"+reportLayoutID.reportRegistryItemID); //$NON-NLS-1$
		return bundleFolder;
	}
	
	public static File getReportLayoutResourceFolderAsFile(ReportRegistryItemID reportLayoutID) {
		IFolder folder = getReportLayoutResourceFolder(reportLayoutID);
		return RCPUtil.getResourceAsFile(folder);
	}
	
}
