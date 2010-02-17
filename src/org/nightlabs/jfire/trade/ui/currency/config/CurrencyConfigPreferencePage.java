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

package org.nightlabs.jfire.trade.ui.currency.config;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.CurrencyDAO;
import org.nightlabs.jfire.base.ui.config.AbstractUserConfigModulePreferencePage;
import org.nightlabs.jfire.base.ui.config.IConfigModuleController;
import org.nightlabs.jfire.trade.config.TradeConfigModule;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 */
public class CurrencyConfigPreferencePage
extends AbstractUserConfigModulePreferencePage
{
	private Shell shell;
	private Display display;
	
	private XComboComposite<Currency> currencyCombo;
	
	@Override
	protected IConfigModuleController createConfigModuleController() {
		return new CurrencyConfigController(this);
	}

	@Override
	protected void createPreferencePage(Composite parent) {
		shell = getShell();
		display = shell.getDisplay();
		
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		wrapper.getGridLayout().numColumns = 2;
		wrapper.getGridLayout().makeColumnsEqualWidth = false;
		
		new Label(wrapper, SWT.NONE).setText("Currency: ");
		currencyCombo = new XComboComposite<Currency>(wrapper, SWT.NONE | SWT.READ_ONLY);
		
		currencyCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				Currency currency = (Currency) element;
				return currency.getCurrencySymbol();
			}
		});
		
		GridData gridDatad = new GridData(GridData.FILL_HORIZONTAL);
		currencyCombo.setLayoutData(gridDatad);
		currencyCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent s) {
				getConfigModule().setCurrency(currencyCombo.getSelectedElement());
				getPageDirtyStateManager().markDirty();
			}
		});
		
		Job job = new Job("Loading Currencies...") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final List<Currency> currencies = CurrencyDAO.sharedInstance().getCurrencies(monitor);
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						currencyCombo.setInput(currencies);
						currencyCombo.setSelection(getConfigModule().getCurrency());
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	@Override
	public void updateConfigModule() {
	}

	@Override
	protected void updatePreferencePage() {
		if (display != currencyCombo.getDisplay())
			throw new IllegalStateException("display != currencyCombo.getDisplay()");

		if (Display.getCurrent() != display)
			throw new IllegalStateException("Thread mismatch! This method should be called on the UI thread! What happened here!?");

		TradeConfigModule tradeConfigModule = getConfigModule();
		currencyCombo.setSelection(tradeConfigModule.getCurrency());
	}
	
	private TradeConfigModule getConfigModule() {
		return (TradeConfigModule) getConfigModuleController().getConfigModule();
	}
}