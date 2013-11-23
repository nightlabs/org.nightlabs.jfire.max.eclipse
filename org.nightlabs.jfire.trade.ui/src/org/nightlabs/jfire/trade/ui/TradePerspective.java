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
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateView;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeView;
import org.nightlabs.jfire.trade.ui.legalentity.view.LegalEntityEditorView;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.ProductTypeQuickListView;
import org.nightlabs.jfire.trade.ui.transfer.deliver.DeliveryQueueBrowsingView;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class TradePerspective implements IPerspectiveFactory
{
	public static final String ID_PERSPECTIVE = TradePerspective.class.getName();

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
		layout.addView(
				HeaderTreeView.ID_VIEW,
				IPageLayout.LEFT,
				0.3f,
				IPageLayout.ID_EDITOR_AREA
			);
		layout.addView(
				LegalEntityEditorView.ID_VIEW,
				IPageLayout.TOP,
				IPageLayout.DEFAULT_VIEW_RATIO,
				// IPageLayout.RATIO_MAX,
				HeaderTreeView.ID_VIEW
			);
//		layout.addView(
//				ProductTypeQuickListView.ID_VIEW,
//				IPageLayout.TOP,
//				0.3f, // IPageLayout.DEFAULT_VIEW_RATIO,
//				IPageLayout.ID_EDITOR_AREA
//			);
		IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.TOP, 0.3f, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		topRight.addView(ProductTypeQuickListView.ID_VIEW);
		topRight.addView(DeliveryQueueBrowsingView.ID_VIEW);
		topRight.addView(DeliveryDateView.ID_VIEW);

		layout.addPerspectiveShortcut(ID_PERSPECTIVE);
		layout.addShowViewShortcut(HeaderTreeView.ID_VIEW);
		layout.addShowViewShortcut(LegalEntityEditorView.ID_VIEW);
		layout.addShowViewShortcut(ProductTypeQuickListView.ID_VIEW);
		layout.addShowViewShortcut(DeliveryQueueBrowsingView.ID_VIEW);
		layout.addShowViewShortcut(DeliveryDateView.ID_VIEW);

		RCPUtil.addAllPerspectiveShortcuts(layout);
	}

}
