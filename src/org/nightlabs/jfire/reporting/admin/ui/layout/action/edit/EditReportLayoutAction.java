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

package org.nightlabs.jfire.reporting.admin.ui.layout.action.edit;

import java.util.Collection;

import javax.jdo.JDOHelper;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.reporting.admin.ui.category.editor.ReportCategoryEditor;
import org.nightlabs.jfire.reporting.admin.ui.category.editor.ReportRegistryItemEditorInput;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireRemoteReportEditorInput;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireReportEditor;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.layout.action.ReportRegistryItemAction;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class EditReportLayoutAction extends ReportRegistryItemAction {

	/**
	 *
	 */
	public EditReportLayoutAction() {
		super();
	}

	/**
	 * @param text
	 */
	public EditReportLayoutAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public EditReportLayoutAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public EditReportLayoutAction(String text, int style) {
		super(text, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.ui.layout.ReportRegistryItemAction#run(org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItem)
	 */
	public @Override void run(Collection<ReportRegistryItem> reportRegistryItems) {
		ReportRegistryItem registryItem = reportRegistryItems.iterator().next();

		String editorID = null;
		IEditorInput input = null;
		if (registryItem instanceof ReportLayout) {
			ReportLayout layout = (ReportLayout)reportRegistryItems.iterator().next();
			editorID = JFireReportEditor.ID_EDITOR;
			input = new JFireRemoteReportEditorInput((ReportRegistryItemID)JDOHelper.getObjectId(layout));
		} else if (registryItem instanceof ReportCategory) {
			input = new ReportRegistryItemEditorInput((ReportRegistryItemID) JDOHelper.getObjectId(registryItem));
			editorID = ReportCategoryEditor.ID_EDITOR;
		} else {
			return;
		}
		try {
			RCPUtil.openEditor(input, editorID);
		} catch (PartInitException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean calculateEnabled(Collection<ReportRegistryItem> registryItems) {
		if (registryItems.isEmpty() || (registryItems.size() != 1))
			return false;
		return true;
	}

}
