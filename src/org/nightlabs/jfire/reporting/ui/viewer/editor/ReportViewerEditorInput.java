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

package org.nightlabs.jfire.reporting.ui.viewer.editor;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ReportViewerEditorInput implements IEditorInput {

	private ReportRegistryItemID reportRegistryItemID;
	private Map<String, Object> reportParams;
	
	/**
	 * 
	 */
	public ReportViewerEditorInput(ReportRegistryItemID reportRegistryItemID, Map<String, Object> reportParams) {
		super();
		this.reportRegistryItemID = reportRegistryItemID;
		this.reportParams = reportParams;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditorInput.name"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return Messages.getString("org.nightlabs.jfire.reporting.ui.viewer.editor.ReportViewerEditorInput.toolTipText"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public ReportRegistryItemID getReportRegistryItemID() {
		return reportRegistryItemID;
	}
	
	public Map<String, Object> getReportParams() {
		return reportParams;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ReportViewerEditorInput) {
			ReportViewerEditorInput other = (ReportViewerEditorInput)obj;
			boolean regItemEqual = false;
			if (other.getReportRegistryItemID() != null && other.getReportRegistryItemID().equals(getReportRegistryItemID()))
				regItemEqual = true;
			boolean paramsEqual = 
				(other.getReportParams() != null && getReportParams() != null) || 
				(other.getReportParams() == null && getReportParams() == null) ;
			
			if (other.getReportParams() != null) {
				if (getReportParams() != null) {
					for (Entry<String, Object> oEntry : other.getReportParams().entrySet()) {
						Object thisEntry = getReportParams().get(oEntry.getKey());
						if (thisEntry == null)
							paramsEqual = false;
						else
							paramsEqual = thisEntry.equals(oEntry.getValue());
						if (!paramsEqual)
							break;
					}
				}
			}
			return regItemEqual && paramsEqual;
		}
		else
			return false;
	}

}
