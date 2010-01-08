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

package org.nightlabs.jfire.trade.ui.articlecontainer.config;

import java.util.Collection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.base.ui.config.AbstractUserConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.trade.config.SummedPriceFracmentTypeConfigModule;
import org.nightlabs.jfire.trade.ui.accounting.PriceFragmentTypeTable;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class SummedPriceFracmentTypeConfigPreferencePage
extends AbstractUserConfigModulePreferencePage
{
	private Shell shell;
	private Display display;
	
	private PriceFragmentTypeTable priceFragmentTypeTable;
	
	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new SummedPriceFracmentTypeConfigController(this);
	}

	@Override
	protected void createPreferencePage(Composite parent) {
		shell = getShell();
		display = shell.getDisplay();
		
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		wrapper.getGridLayout().numColumns = 2;
		wrapper.getGridLayout().makeColumnsEqualWidth = false;
		
		priceFragmentTypeTable = new PriceFragmentTypeTable(wrapper, SWT.NONE);
		priceFragmentTypeTable.setInput(getConfigModule().getSummedPriceFracmentTypeList());
		
		XComposite buttonComposite = new XComposite(wrapper, SWT.NONE);
		buttonComposite.getGridData().grabExcessHorizontalSpace = false;
		
		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setText("Add");
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PriceFragmentTypeChooserDialog chooserDialog = new PriceFragmentTypeChooserDialog(getShell());
				chooserDialog.setExcludedPriceFragmentTypes(getConfigModule().getSummedPriceFracmentTypeList());
			
				int returnCode = chooserDialog.open();
				if (returnCode == Dialog.OK) {
					getConfigModule().addPriceFracmentTypes(chooserDialog.getSelectedPriceFragmentTypes());
					priceFragmentTypeTable.setInput(getConfigModule().getSummedPriceFracmentTypeList());
					getPageDirtyStateManager().markDirty();
				}
			}
		});
		
		Button removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getConfigModule().removePriceFracmentTypes(priceFragmentTypeTable.getSelectedPriceFragmentTypes());
				priceFragmentTypeTable.setInput(getConfigModule().getSummedPriceFracmentTypeList());
			}
		});
	}

	@Override
	public void updateConfigModule() {
	}

	@Override
	protected void updatePreferencePage() {
		if (display != priceFragmentTypeTable.getDisplay())
			throw new IllegalStateException("display != priceFragmentTypeTable.getDisplay()");

		if (Display.getCurrent() != display)
			throw new IllegalStateException("Thread mismatch! This method should be called on the UI thread! What happened here!?");

		SummedPriceFracmentTypeConfigModule configModule = getConfigModule();
		
		priceFragmentTypeTable.setInput(configModule.getSummedPriceFracmentTypeList());
	}
	
	private SummedPriceFracmentTypeConfigModule getConfigModule() {
		return (SummedPriceFracmentTypeConfigModule) getConfigModuleController().getConfigModule();
	}
}