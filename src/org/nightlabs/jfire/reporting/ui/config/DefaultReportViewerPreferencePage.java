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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.jfire.reporting.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class DefaultReportViewerPreferencePage
extends PreferencePage
implements IWorkbenchPreferencePage
{

	private XComposite wrapper;
	private Button useAcrobatJavaBeanForPDFs;
	
	/**
	 * 
	 */
	public DefaultReportViewerPreferencePage() {
	}

	/**
	 * @param title
	 */
	public DefaultReportViewerPreferencePage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param img
	 */
	public DefaultReportViewerPreferencePage(String title, ImageDescriptor img) {
		super(title, img);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, LayoutDataMode.NONE);
		Label description = new Label(wrapper, SWT.WRAP);
		description.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.config.DefaultReportViewerPreferencePage.descriptionLabel.text")); //$NON-NLS-1$
		description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		useAcrobatJavaBeanForPDFs = new Button(wrapper, SWT.CHECK);
		useAcrobatJavaBeanForPDFs.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.config.DefaultReportViewerPreferencePage.useAcrobatJavaBeanForPDFsButton.text")); //$NON-NLS-1$
		DefaultReportViewerCfMod cfMod = DefaultReportViewerCfMod.sharedInstance();
		useAcrobatJavaBeanForPDFs.setSelection(cfMod.isUseAcrobatJavaBeanForPDFs());
		return wrapper;
	}
	
	@Override
	public boolean performOk() {
		DefaultReportViewerCfMod cfMod = DefaultReportViewerCfMod.sharedInstance();
		cfMod.setUseAcrobatJavaBeanForPDFs(useAcrobatJavaBeanForPDFs.getSelection());
		try {
			Config.sharedInstance().save();
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}
		return super.performOk();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench arg0) {
		// TODO Auto-generated method stub

	}

}
