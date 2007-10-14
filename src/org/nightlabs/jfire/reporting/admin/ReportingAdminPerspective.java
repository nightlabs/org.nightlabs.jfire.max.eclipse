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

package org.nightlabs.jfire.reporting.admin;

import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.reporting.admin.layout.ReportLayoutView;

/**
 * Perspective for report-editing within the JFire framework.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public class ReportingAdminPerspective implements IPerspectiveFactory {
	
	public static final String ID_PERSPECTIVE = ReportingAdminPerspective.class.getName();
	
	public static final String ID_DATA_VIEW = "org.eclipse.birt.report.designer.ui.views.data.DataView"; //$NON-NLS-1$
	public static final String ID_ATTRIBUTE_VIEW = "org.eclipse.birt.report.designer.ui.attributes.AttributeView"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) {
		createLayout(layout);
		createShortcuts(layout);
	}

	/**
	 * Creates the action and perspective shortcuts 
	 */
	private void createShortcuts( IPageLayout layout )
	{
		// Add "show views".
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(PaletteView.ID);
		layout.addShowViewShortcut(ID_ATTRIBUTE_VIEW);
		layout.addShowViewShortcut(ID_DATA_VIEW);
//		layout.addShowViewShortcut( LibraryExplorerView.ID );
		layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
		layout.addShowViewShortcut(ReportLayoutView.ID_VIEW);

		RCPUtil.addAllPerspectiveShortcuts(layout);
	}

	/**
	 * Creates the layout for this perspective
	 */
	private void createLayout( IPageLayout layout )
	{
		// Editors are placed for free.
		String editorArea = layout.getEditorArea();

		// Top left.
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, (float) 0.26, editorArea); //$NON-NLS-1$
		topLeft.addView( PaletteView.ID );
		topLeft.addView( ID_DATA_VIEW );
//		topLeft.addView( LibraryExplorerView.ID );

		// Bottom left.
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float) 0.50, "topLeft");		 //$NON-NLS-1$ //$NON-NLS-2$
		bottomLeft.addView(ReportLayoutView.ID_VIEW);
		bottomLeft.addView(IPageLayout.ID_OUTLINE);

		// Bottom right.
		IFolderLayout bootomRight = layout.createFolder("bootomRight", IPageLayout.BOTTOM, (float) 0.66, editorArea); //$NON-NLS-1$
		bootomRight.addView(ID_ATTRIBUTE_VIEW);
	}
}
