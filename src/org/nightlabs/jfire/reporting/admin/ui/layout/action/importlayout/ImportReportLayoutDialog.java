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

package org.nightlabs.jfire.reporting.admin.ui.layout.action.importlayout;

import java.io.File;
import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.FileSelectionComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.reporting.ReportManagerRemote;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.util.IOUtil;

/**
 * @author  Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class ImportReportLayoutDialog 
extends ResizableTrayDialog 
{
	private XComposite wrapper;
	private FileSelectionComposite fileSelectionComposite;
	private I18nTextEditor nameEditor;
	
	private ReportRegistryItemID reportCategoryID;

	/**
	 * @param parentShell
	 */
	public ImportReportLayoutDialog(Shell parentShell, ReportRegistryItemID reportCategoryID) 
	{
		super(parentShell, null);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.reportCategoryID = reportCategoryID;
	}

	@Override
	protected void configureShell(Shell newShell) 
	{
		super.configureShell(newShell);
		newShell.setText("Import Report Layout");
		newShell.setSize(400, 400);
	}

//	private Button autogenerateIDCheckbox;
//	private Text reportRegistryItemIDText;
//	private Text reportRegistryItemTypeText;
//	private Combo reportRegistryItemTypeCombo;
	@Override
	protected Control createDialogArea(Composite parent) 
	{
		wrapper = new XComposite(parent, SWT.NONE);
		
//		autogenerateIDCheckbox = new Button(wrapper, SWT.CHECK);
//		autogenerateIDCheckbox.setText("Auto generate ReportRegistryItemID: ");
//		autogenerateIDCheckbox.setSelection(true);
//		autogenerateIDCheckbox.addSelectionListener(new SelectionAdapter() 
//		{
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				reportRegistryItemIDText.setEnabled(!autogenerateIDCheckbox.getSelection());
//			}
//		});
//		
//		new Label(wrapper, SWT.NONE).setText("Report Registry Item ID: ");
//		reportRegistryItemIDText = new Text(wrapper, SWT.BORDER);
//		reportRegistryItemIDText.setEnabled(false);
//		reportRegistryItemIDText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		new Label(wrapper, SWT.NONE).setText("Report layout name: ");
//		nameEditor = new I18nTextEditor(wrapper);
//		nameEditor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		nameEditor.addModifyListener(new ModifyListener() 
//		{
//			@Override
//			public void modifyText(ModifyEvent arg0) {
//				if (autogenerateIDCheckbox.getSelection()) {
//					reportRegistryItemIDText.setText(nameEditor.getEditText());
//				}
//			}
//		});
		
		
		fileSelectionComposite = new FileSelectionComposite(
				wrapper,
				SWT.NONE, FileSelectionComposite.OPEN_FILE,
				"File: ",	"Caption");
		
		return wrapper;
	}

	@Override
	protected void okPressed() 
	{
		try {
			File tmpFolder = IOUtil.createUserTempDir("jfire_report.imported.", "report");
			tmpFolder.deleteOnExit();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		ReportManagerRemote rm;
		try {
			rm = JFireEjb3Factory.getRemoteBean(ReportManagerRemote.class, Login.getLogin().getInitialContextProperties());
			rm.importReportLayoutZipFile(fileSelectionComposite.getFile());
		} catch (LoginException e) {
			throw new RuntimeException(e);
		}
		super.okPressed();
	}
}
