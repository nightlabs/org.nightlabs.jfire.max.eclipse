/**
 * 
 */
package org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.reporting.ReportManagerRemote;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireRemoteReportEditorInput;
import org.nightlabs.jfire.reporting.admin.ui.platform.ClientResourceLocator;
import org.nightlabs.jfire.reporting.layout.ReportLayoutLocalisationData;
import org.nightlabs.jfire.security.SecurityReflector;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportLayoutL10nUtil {

	public static final String PROJECT_NAME_REPORT_LOCALISATION = "ReportLocalisation";

	public static class PreparedLayoutL10nData {
		private IFolder bundleFolder;
		private Map<String, ReportLayoutLocalisationData> localisationBundle;
		
		public PreparedLayoutL10nData() {
		}
		public IFolder getBundleFolder() {
			return bundleFolder;
		}
		public void setBundleFolder(IFolder bundleFolder) {
			this.bundleFolder = bundleFolder;
		}
		public Map<String, ReportLayoutLocalisationData> getLocalisationBundle() {
			return localisationBundle;
		}
		public void setLocalisationBundle(Map<String, ReportLayoutLocalisationData> localisationBundle) {
			this.localisationBundle = localisationBundle;
		}
	}
	
	public static PreparedLayoutL10nData prepareReportLayoutL10nData(JFireRemoteReportEditorInput input) {
		PreparedLayoutL10nData result = new PreparedLayoutL10nData();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECT_NAME_REPORT_LOCALISATION);		
		IFolder bundleFolder = ClientResourceLocator.getReportLayoutResourceFolder(input.getReportRegistryItemID());
		if (bundleFolder.exists()) {
			try {
				bundleFolder.delete(true, new NullProgressMonitor());
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
		try {
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		try {
			createFolderRecursive(bundleFolder);
		} catch (CoreException e) {
			throw new RuntimeException(e);
		}
		result.setBundleFolder(bundleFolder);
		try {
			ReportManagerRemote reportManager = JFireEjb3Factory.getRemoteBean(
					ReportManagerRemote.class, SecurityReflector.getInitialContextProperties());
			Collection<ReportLayoutLocalisationData> bundle = reportManager.getReportLayoutLocalisationBundle(
					input.getReportRegistryItemID(),
					new String[] {FetchPlan.DEFAULT, ReportLayoutLocalisationData.FETCH_GROUP_LOCALISATOIN_DATA},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
			);
			result.setLocalisationBundle(new HashMap<String, ReportLayoutLocalisationData>());
			for (ReportLayoutLocalisationData data : bundle) {
				result.getLocalisationBundle().put(data.getLocale(), data);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		for (ReportLayoutLocalisationData data : result.getLocalisationBundle().values()) {
			String fileName = ReportLayoutLocalisationData.PROPERIES_FILE_PREFIX;
			if ("".equals(data.getLocale())) //$NON-NLS-1$
				fileName = fileName + ".properties"; //$NON-NLS-1$
			else
				fileName = fileName + "_" + data.getLocale() + ".properties";  //$NON-NLS-1$ //$NON-NLS-2$
			IFile dataFile = result.getBundleFolder().getFile(fileName);
			try {
				InputStream in = data.createLocalisationDataInputStream();
				try {
					dataFile.create(in, true, new NullProgressMonitor());
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}		
		return result;
	}
	
	private static void createFolderRecursive(IFolder folder) throws CoreException {
		IFolder parentFolder = getParentFolder(folder);
		if (parentFolder != null && parentFolder.exists()) {
			folder.create(true, true, new NullProgressMonitor());
		} else {
			if (parentFolder != null) {
				createFolderRecursive(parentFolder);
			}
			folder.create(true, true, new NullProgressMonitor());
		}
	}
	
	private static IFolder getParentFolder(IFolder folder) {
		IContainer parent = folder.getParent();
		if (parent != null && (parent instanceof IFolder)) {
			return (IFolder) parent;
		}
		return null;
	}
}
