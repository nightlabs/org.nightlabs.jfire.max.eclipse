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

import org.eclipse.birt.report.designer.ui.ReportPlugin;
import org.eclipse.birt.report.designer.ui.editors.RCPMultiPageReportEditor;
import org.eclipse.birt.report.model.api.SessionHandle;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.action.IActionUpdateDelegate;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.l10n.IReportLayoutL10nManager;
import org.nightlabs.jfire.reporting.admin.ui.platform.ClientResourceLocator;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class JFireReportEditor extends RCPMultiPageReportEditor {
	
	public static final String ID_EDITOR = JFireReportEditor.class.getName();

	@Override
	public void init(IEditorSite editorSite, IEditorInput editorInput) throws PartInitException {
		super.init(editorSite, editorInput);
		setPartName(editorInput.getName());
		editorSite.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				for (int i = 0; i < getPageCount(); i++) {
					Object page = pages.get(i);
					if (page instanceof IAdaptable) {
						Object adapter = ((IAdaptable)page).getAdapter(IActionUpdateDelegate.class);
						if (adapter instanceof IActionUpdateDelegate) {
							((IActionUpdateDelegate)adapter).updateActions();
						}
					}
				}
			}
		});
	}
	
	@Override
	public boolean isDirty() {
		return super.isDirty();
	}
	
	@Override
	public void setFocus() {		
		super.setFocus();
		
		if (getEditorInput() instanceof JFireRemoteReportEditorInput) {
			ClientResourceLocator.setCurrentReportLayoutID(((JFireRemoteReportEditorInput)getEditorInput()).getReportRegistryItemID());
			
			String resourceString = ClientResourceLocator.getReportLayoutResourceFolderAsFile(((JFireRemoteReportEditorInput)getEditorInput()).getReportRegistryItemID()).getAbsoluteFile().toString();
			
			ReportPlugin.getDefault( ).setResourcePreference( resourceString );
			SessionHandle.setBirtResourcePath(resourceString);
//			SessionHandleAdapter.getInstance( )
//					.getSessionHandle( )
//					.setBirtResourcePath( resourceString );
//			SessionHandleAdapter.getInstance( )
//					.getSessionHandle( )
//					.setResourceFolder( resourceString );
		}
	}
	
	private PageBookView propView = null;
	
	private void refreshPropertiesView() {
		if (propView == null)
			return;
		// forces the PageBookView to re-initialize the 
		// page for this part
		propView.partClosed(this);
		propView.partActivated(this);
	}
	
	
	@Override
	public Object getAdapter(Class type) {
		if ( type == IPropertySheetPage.class )
		{
			IWorkbenchPart propPart = RCPUtil.findView(IPageLayout.ID_PROP_SHEET);
			if (propPart instanceof PageBookView) {
				propView = (PageBookView) propPart;
			}
		}
		return super.getAdapter(type);
	}
	
	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		refreshPropertiesView();
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		
		if (getEditorInput() instanceof JFireRemoteReportEditorInput)
			JFireRemoteReportEditorInput.saveRemoteLayout((JFireRemoteReportEditorInput)getEditorInput(), monitor);
		
		IReportLayoutL10nManager layoutL10nManager = getReportLayoutL10nManager();
		if (layoutL10nManager != null)
			layoutL10nManager.saveLocalisationBundle(monitor);
	}
	
	/**
	 * Returns the first page implementing {@link IReportLayoutL10nManager} found, <code>null</code> otherwise. 
	 * @return The first page implementing {@link IReportLayoutL10nManager} found, <code>null</code> otherwise.
	 */
	public IReportLayoutL10nManager getReportLayoutL10nManager() {
		for (int i = 0; i < getPageCount(); i++) {
			Object page = pages.get(i);
			if (page instanceof IReportLayoutL10nManager) {
				return (IReportLayoutL10nManager) page;
			}
		}
		return null;
	}

}


