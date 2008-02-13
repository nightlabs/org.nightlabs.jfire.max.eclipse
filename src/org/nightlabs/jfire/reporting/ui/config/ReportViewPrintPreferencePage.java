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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.nightlabs.base.ui.composite.AbstractListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;
import org.nightlabs.jfire.base.ui.preferences.LSDPreferencePage;
import org.nightlabs.jfire.reporting.ui.config.ReportViewPrintConfigModule.UseCaseConfig;
import org.nightlabs.jfire.reporting.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportViewPrintPreferencePage
//extends PreferencePage
//implements IWorkbenchPreferencePage
extends LSDPreferencePage
{
	private XComposite wrapper;
	private ReportUseCaseCombo useCaseCombo;
	private Button sameForAll;
	
	private EditUseCaseConfigComposite editUseCaseConfigComposite;
	private Map<String, UseCaseConfig> useCaseConfigs;
	
	private String currentlyEditedID;
	
	public ReportViewPrintPreferencePage() {
	}

	/**
	 * @param arg0
	 */
	public ReportViewPrintPreferencePage(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public ReportViewPrintPreferencePage(String arg0, ImageDescriptor arg1) {
		super(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, LayoutDataMode.NONE);
		Label descriptionLabel = new Label(wrapper, SWT.WRAP);
		descriptionLabel.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.config.ReportViewPrintPreferencePage.descriptionLabel.text")); //$NON-NLS-1$
		useCaseCombo = new ReportUseCaseCombo(wrapper, AbstractListComposite.getDefaultWidgetStyle(wrapper));
		useCaseCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				updateEditUseCaseConfigComposite();
			}
		});
		sameForAll = new Button(wrapper, SWT.CHECK);
		sameForAll.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.config.ReportViewPrintPreferencePage.sameForAllButton.text")); //$NON-NLS-1$
		editUseCaseConfigComposite = new EditUseCaseConfigComposite(wrapper, SWT.NONE);
		useCaseConfigs = new HashMap<String, UseCaseConfig>();
		ReportViewPrintConfigModule cfMod = ReportViewPrintConfigModule.sharedInstance();
		sameForAll.setSelection(cfMod.isUseSameForAll());
		Map<String, UseCaseConfig> configs = cfMod.getReportUseCaseConfigs();
		for (Entry<String, UseCaseConfig> entry : configs.entrySet()) {
			useCaseConfigs.put(entry.getKey(), (UseCaseConfig)entry.getValue().clone());
		}
		updateEditUseCaseConfigComposite();
//		return wrapper;
	}

	protected void updateEditUseCaseConfigComposite() {
		readCurrentlyEdited();
		ReportUseCase useCase = null;
		if (sameForAll.getSelection()) {
			 useCase = useCaseCombo.getElements().get(0);
		}
		else if (useCaseCombo.getSelectedElement() != null) {
			currentlyEditedID = useCaseCombo.getSelectedElement().getId();
			useCase = useCaseCombo.getSelectedElement();
		}
		if (useCase != null) {
			
			UseCaseConfig config = useCaseConfigs.get(useCase.getId());
			if (sameForAll.getSelection())
				editUseCaseConfigComposite.setUseCaseConfig(null, config);
			else
				editUseCaseConfigComposite.setUseCaseConfig(useCase.getId(), config);
		}
	}
	
	protected void readCurrentlyEdited() {
		String readUseCaseID = currentlyEditedID;
		if (readUseCaseID != null)
			useCaseConfigs.put(readUseCaseID, editUseCaseConfigComposite.readUseCaseConfig());
		else if (sameForAll.getSelection()) {
			for (ReportUseCase useCase : useCaseCombo.getElements()) {
				useCaseConfigs.put(useCase.getId(), editUseCaseConfigComposite.readUseCaseConfig());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench arg0) {
	}

	@Override
	public boolean performOk() {
		readCurrentlyEdited();
		ReportViewPrintConfigModule cfMod = ReportViewPrintConfigModule.sharedInstance();
		cfMod.setReportUseCaseConfigs(useCaseConfigs);
		cfMod.setUseSameForAll(sameForAll.getSelection());
		cfMod.setChanged();
		try {
			Config.sharedInstance().save();
		} catch (ConfigException e) {
			MessageDialog dlg = new MessageDialog(RCPUtil.getActiveShell(), Messages.getString("org.nightlabs.jfire.reporting.ui.config.ReportViewPrintPreferencePage.savingFailedMessage"), null, e.getMessage(), 0, new String[]{Messages.getString("org.nightlabs.jfire.reporting.ui.config.ReportViewPrintPreferencePage.okButton.text")}, 0); //$NON-NLS-1$ //$NON-NLS-2$
			dlg.open();
		}
		return super.performOk();
	}
}
