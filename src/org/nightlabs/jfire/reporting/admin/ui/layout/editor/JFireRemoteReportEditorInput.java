/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.reporting.admin.ui.layout.editor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.jdo.FetchPlan;

import org.eclipse.birt.report.designer.internal.ui.editors.ReportEditorInput;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.reporting.dao.ReportRegistryItemDAO;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItemName;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.ReportingPlugin;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.IOUtil;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class JFireRemoteReportEditorInput
extends ReportEditorInput
implements IJFireRemoteReportEditorInput
{

	/**
	 * Fetch groups used when fetching a layout with its design file
	 */
	public static final String[] REPORT_LAYOUT_COMPLETE_FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ReportRegistryItem.FETCH_GROUP_NAME,
		ReportLayout.FETCH_GROUP_THIS_REPORT_LAYOUT
	};
	
	private ReportRegistryItemID reportRegistryItemID;
	private JFireLocalReportEditorInput localInput;
	private ReportRegistryItemName layoutName;

	
	public JFireRemoteReportEditorInput(ReportRegistryItemID reportRegistryItemID) {
		super((File)null);
		this.reportRegistryItemID = reportRegistryItemID;
	}
	
	
	protected JFireLocalReportEditorInput getLocalInput() {
		if (localInput == null) {
			ReportLayout layout = (ReportLayout) ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(reportRegistryItemID, REPORT_LAYOUT_COMPLETE_FETCH_GROUPS, new NullProgressMonitor());
			
			File tempFolder = ReportingPlugin.createReportTempFolder();
			
//			if (!pathFile.exists()) {
//				if (!pathFile.mkdirs())
//					throw new IllegalStateException("Could not create directory for temporary remote layouts: "+pathFile.getPath());
//			}
			File file = new File(tempFolder, "ReportLayout_"+layout.getOrganisationID()+"_"+layout.getReportRegistryItemID()+".rptdesign"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					throw new IllegalStateException("Could not create temporary file for remote layout: "+file.getAbsolutePath(), e); //$NON-NLS-1$
				}
			}
			try {
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				try {
					InputStream in = layout.createReportDesignInputStream();
					try {
						IOUtil.transferStreamData(in, out);
					} finally {
						in.close();
					}
				} finally {
					out.close();
				}
			} catch (FileNotFoundException e) {
				throw new IllegalStateException("Could not find temporary file for remote layout: "+file.getAbsolutePath()); //$NON-NLS-1$
			} catch (IOException e) {
				throw new RuntimeException("Could not write temporary file for remote layout: "+file.getAbsolutePath(), e); //$NON-NLS-1$
			}
			layoutName = layout.getName();
			localInput = new JFireLocalReportEditorInput(file);
		}
		return localInput;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.editors.IReportEditorInput#getStorage()
	 */
	@Override
	public IStorage getStorage() throws CoreException {
		return getLocalInput().getStorage();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPathEditorInput#getPath()
	 */
	@Override
	public IPath getPath() {
		return getLocalInput().getPath();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	@Override
	public boolean exists() {
		return getLocalInput().exists();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	@Override
	public ImageDescriptor getImageDescriptor() {
		return getLocalInput().getImageDescriptor();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	@Override
	public String getName() {
		getLocalInput();
		return layoutName.getText(Locale.getDefault().getLanguage());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	@Override
	public IPersistableElement getPersistable() {
//		return getLocalInput().getPersistable();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return getLocalInput().getToolTipText();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@Override
	public Object getAdapter(Class adapter) {
		return getLocalInput().getAdapter(adapter);
	}
	
	@Override
	public File getFile() {
		return getLocalInput().getFile();
	}

	public ReportRegistryItemID getReportRegistryItemID() {
		return reportRegistryItemID;
	}
	
	/**
	 * The <code>ReportEditorInput</code> implementation of this <code>Object</code>
	 * method bases the equality of two <code>JFireRemoteReportEditorInput</code> objects
	 * on the equality of their underlying reportRegistryItemIDs.
	 */
	@Override
	public boolean equals( Object obj )
	{
		if (this == obj)
			return true;
		if ( !(obj instanceof JFireRemoteReportEditorInput))
			return false;
		
		return reportRegistryItemID.equals(((JFireRemoteReportEditorInput)obj).reportRegistryItemID);
	}
	
	/**
	 * Loads the local report file (that is used for the report editor) in the
	 * appropriate {@link ReportLayout} and stores it on the jfire Server.
	 * @param input
	 * @param monitor
	 */
	public static void saveRemoteLayout(IJFireRemoteReportEditorInput input, IProgressMonitor monitor) {
		ReportLayout layout = (ReportLayout) ReportRegistryItemDAO.sharedInstance().getReportRegistryItem(input.getReportRegistryItemID(), REPORT_LAYOUT_COMPLETE_FETCH_GROUPS, new ProgressMonitorWrapper(monitor));
		try {
			layout.loadFile(input.getPath().toFile());
		} catch (IOException e) {
			throw new RuntimeException("Could not load the ReportLayouts file "+input.getPath().toOSString(),e); //$NON-NLS-1$
		}
		try {
			ReportRegistryItemDAO.sharedInstance().storeReportRegistryItem(layout, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
		} catch (Exception e) {
			throw new RuntimeException("Failed to store report layout", e); //$NON-NLS-1$
		}
	}
	
}
