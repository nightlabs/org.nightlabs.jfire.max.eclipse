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

package org.nightlabs.jfire.trade.legalentity.config;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jfire.base.ui.config.AbstractUserConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.config.ConfigModule;
import org.nightlabs.jfire.trade.config.LegalEntityViewConfigModule;
import org.nightlabs.jfire.trade.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LegalEntityViewConfigPreferencePage 
extends AbstractUserConfigModulePreferencePage
{
	private XComposite wrapper;
	private LEViewPersonStructFieldTable structFieldTable;
	
	private XComposite buttonWrapper;
	private Button addButton;
	private Button removeButton;
	private Button upButton;
	private Button downButton;
	
	
	public LegalEntityViewConfigPreferencePage() {
		super();
	}

	/**
	 * @param title
	 */
	public LegalEntityViewConfigPreferencePage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public LegalEntityViewConfigPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	/**
	 * @see org.nightlabs.jfire.base.ui.config.AbstractConfigModulePreferencePage#createPreferencePage(org.eclipse.swt.widgets.Composite)
	 */
	protected void createPreferencePage(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		wrapper.getGridLayout().numColumns = 2;
		wrapper.getGridLayout().makeColumnsEqualWidth = false;
		
		structFieldTable = new LEViewPersonStructFieldTable(wrapper, SWT.NONE	);
		structFieldTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		buttonWrapper = new XComposite(wrapper, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		buttonWrapper.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		
		addButton = new Button(buttonWrapper, SWT.PUSH);
		addButton.setText(Messages.getString("org.nightlabs.jfire.trade.legalentity.config.LegalEntityViewConfigPreferencePage.addButton.text")); //$NON-NLS-1$
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		removeButton = new Button(buttonWrapper, SWT.PUSH);
		removeButton.setText(Messages.getString("org.nightlabs.jfire.trade.legalentity.config.LegalEntityViewConfigPreferencePage.removeButton.text")); //$NON-NLS-1$
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				structFieldTable.removeSelected();
				structFieldTable.refresh();
				setConfigChanged(true);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			} 
		});
		
		upButton = new Button(buttonWrapper, SWT.PUSH);
		upButton.setText(Messages.getString("org.nightlabs.jfire.trade.legalentity.config.LegalEntityViewConfigPreferencePage.upButton.text")); //$NON-NLS-1$
		upButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		upButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				structFieldTable.moveSelectedUp();
				structFieldTable.refresh();
				setConfigChanged(true);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			} 
			
		});
		
		downButton = new Button(buttonWrapper, SWT.PUSH);
		downButton.setText(Messages.getString("org.nightlabs.jfire.trade.legalentity.config.LegalEntityViewConfigPreferencePage.downButton.text")); //$NON-NLS-1$
		downButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		downButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				structFieldTable.moveSelectedDown();
				structFieldTable.refresh();
				setConfigChanged(true);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			} 
			
		});
	}

//	@Override
//	protected void updatePreferencePage(ConfigModule configModule) {
//		structFieldTable.setInput(configModule);
//	}
	@Override
	protected void updatePreferencePage() {		
		structFieldTable.setInput(getConfigModuleController().getConfigModule());
	}

	protected void discardPreferencePageWidgets() {
		wrapper = null;
		structFieldTable = null;
		
		buttonWrapper = null;
		addButton = null;
		removeButton = null;
		upButton = null;
		downButton = null;
	}

	@Override
	public void updateConfigModule() 
	{
		LegalEntityViewConfigModule configModule = (LegalEntityViewConfigModule) getConfigModuleController().getConfigModule(); 
		configModule.getStructFields().clear();
		configModule.getStructFields().addAll(structFieldTable.getStructFields());
		setConfigChanged(false);
	}

	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new LegalEntityViewConfigController(this);
	}
}
