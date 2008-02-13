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

package org.nightlabs.jfire.reporting.admin.ui.layout.action.add;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.birt.report.designer.internal.ui.wizards.WizardTemplateChoicePage;
import org.eclipse.birt.report.designer.ui.ReportPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.reporting.ReportManager;
import org.nightlabs.jfire.reporting.admin.ui.ReportingAdminPlugin;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireRemoteReportEditorInput;
import org.nightlabs.jfire.reporting.admin.ui.layout.editor.JFireReportEditor;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.layout.ReportCategory;
import org.nightlabs.jfire.reporting.layout.ReportLayout;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.security.SecurityReflector;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AddReportLayoutWizard extends DynamicPathWizard {

	private ReportRegistryItem reportRegistryItem;
	private WizardTemplateChoicePage templateChoicePage;
	private AddReportLayoutWizardPage wizardPage;
	
	/**
	 * 
	 */
	public AddReportLayoutWizard(ReportRegistryItem reportRegistryItem) {
		super();
		this.reportRegistryItem = reportRegistryItem;
		wizardPage = new AddReportLayoutWizardPage();
		templateChoicePage = new WizardTemplateChoicePage(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.layout.action.add.AddReportLayoutWizard.pageName")); //$NON-NLS-1$
		addPage(wizardPage);
		addPage(templateChoicePage);
		templateChoicePage.setPageComplete(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		ReportManager rm = ReportingAdminPlugin.getReportManager();
		if ((reportRegistryItem == null) || (!(reportRegistryItem instanceof ReportCategory)))
			throw new IllegalArgumentException("Can only add a ReportLayout to a ReportCategory instance of ReportRegistryItem. The given reportRegistryItem is instanceof "+((reportRegistryItem == null)?"null":reportRegistryItem.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$
		String reportRegistryItemType = ""; //$NON-NLS-1$
		if (reportRegistryItem != null)
			reportRegistryItemType = reportRegistryItem.getReportRegistryItemType();
		final ReportLayout layout = new ReportLayout(
				(ReportCategory)reportRegistryItem,
				SecurityReflector.getUserDescriptor().getOrganisationID(),
				reportRegistryItemType, wizardPage.getRegistryItemID()
			);
		layout.getName().copyFrom(wizardPage.getI18nText());
		if (wizardPage.isCreateFromFile()) {
			try {
				layout.loadFile(wizardPage.getFileSelectComposite().getFile());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			InputStream in = null;
			URL url = FileLocator.find(
					Platform.getBundle(ReportPlugin.REPORT_UI),
//					new Path(templateChoicePage.getTemplate().getReportPath()),
					new Path(templateChoicePage.getTemplate().getFileName()),
					null
				);
			if (url != null) {
				try {
					in = url.openStream( );
				} catch ( IOException e1 ) {
				}
			}
			else {
				try {
//					in = new FileInputStream(templateChoicePage.getTemplate().getReportPath());
					in = new FileInputStream(templateChoicePage.getTemplate().getFileName());
				} catch ( FileNotFoundException e ) {
				}
			}
			
			if (in != null) {
				try {
					layout.loadStream(in, templateChoicePage.getTemplate().getName());
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					try {
						in.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
		
		try {
			rm.storeRegistryItem(layout, false, null, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					RCPUtil.openEditor(
							new JFireRemoteReportEditorInput(ReportRegistryItemID.create(layout.getOrganisationID(), layout.getReportRegistryItemType(), layout.getReportRegistryItemID())),
							JFireReportEditor.ID_EDITOR
						);
				} catch (PartInitException e) {
					throw new RuntimeException(e);
				}
			}
		});
		return true;
	}
	
	public static int show(ReportRegistryItem reportRegistryItem) {
		AddReportLayoutWizard wizard = new AddReportLayoutWizard(reportRegistryItem);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		return dialog.open();
	}

}
