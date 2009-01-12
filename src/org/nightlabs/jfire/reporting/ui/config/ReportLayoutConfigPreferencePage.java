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

package org.nightlabs.jfire.reporting.ui.config;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.tree.AbstractTreeComposite;
import org.nightlabs.jfire.base.ui.config.AbstractUserConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.reporting.config.ReportLayoutConfigModule;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class ReportLayoutConfigPreferencePage
extends AbstractUserConfigModulePreferencePage
{
	/**
	 * The default constructor is needed by the Registry to create this page in another context
	 * than the Preferences Dialog.
	 */
	public ReportLayoutConfigPreferencePage() {
		super();
	}

	/**
	 * @param title
	 */
	public ReportLayoutConfigPreferencePage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public ReportLayoutConfigPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}


	private ReportLayoutTree reportLayoutTree;

	@Override
	protected void createPreferencePage(Composite parent) {
		reportLayoutTree = new ReportLayoutTree(parent, this,
				AbstractTreeComposite.DEFAULT_STYLE_SINGLE | SWT.FULL_SELECTION |
				XComposite.getBorderStyle(parent)
			);
	}

	@Override
	protected void updatePreferencePage() {
		if (!reportLayoutTree.isDisposed()) {
			reportLayoutTree.setConfigModule((ReportLayoutConfigModule)
					getConfigModuleController().getConfigModule());
			reportLayoutTree.getTreeViewer().refresh();
		}
	}

	@Override
	public void updateConfigModule() {
		// WORKAROUND This is a
//		(ReportLayoutConfigModule)getConfigModuleManager().getConfigModule()).copyFrom(

//		((ReportLayoutConfigModule)getConfigModuleManager().getConfigModule()).copyFrom(
//				(ReportLayoutConfigModule)getConfigModuleManager().getConfigModule());
	}


	public void setChanged(boolean changed) {
		setConfigChanged(changed);
	}

	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new ReportLayoutConfigController(this);
	}

	@Override
	protected void setBodyContentEditable(boolean editable)
	{
		if (!reportLayoutTree.isDisposed()) {
			reportLayoutTree.setEditable(editable);
		}
	}

}
