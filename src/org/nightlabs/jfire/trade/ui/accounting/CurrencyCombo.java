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

package org.nightlabs.jfire.trade.ui.accounting;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author marco schulze - marco at nightlabs dot de
 */
public class CurrencyCombo
extends XComposite
implements ISelectionProvider
{

	private List<Currency> currencies = new ArrayList<Currency>(0);
	private Combo combo;
	
	public CurrencyCombo(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		setLayoutData( new GridData(GridData.FILL_HORIZONTAL));		
		combo = new Combo(this, SWT.NONE);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				int idx = combo.getSelectionIndex();
				if (idx < 0 || idx > currencies.size() - 1)
					selectedCurrency = null;
				else
					selectedCurrency = currencies.get(idx);

				fireSelectionChangedEvent();
			}
		});

		combo.add(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.CurrencyCombo.pseudoEntry_loading")); //$NON-NLS-1$
		combo.select(0);

		org.nightlabs.base.ui.job.Job loadCurrenciesJob = new org.nightlabs.base.ui.job.Job(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.CurrencyCombo.loadCurrenciesJob.name")) { //$NON-NLS-1$
			@Override
			@SuppressWarnings("unchecked") 
			protected IStatus run(ProgressMonitor monitor) {
				try {
//					final List<Currency> currencyList = new ArrayList<Currency>(
//							AccountingUtil.getAccountingManager().getCurrencies(
//									new String[]{ FetchPlan.ALL }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT));

					final List<Currency> currencyList = CurrencyDAO.sharedInstance().getCurrencies(monitor);

					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							combo.removeAll();
							currencies = currencyList;
							int idx = -1; int i = 0;
//							boolean fireSelectionChangedEvent = false;

							if (selectedCurrency != null && !currencies.contains(selectedCurrency)) {
//								fireSelectionChangedEvent = true;
								selectedCurrency = null;
							}

							if (selectedCurrency == null && !currencies.isEmpty()) {
								selectedCurrency = currencies.get(0);
//								fireSelectionChangedEvent = true;
							}

							for (Currency currency : currencies) {
								combo.add(currency.getCurrencySymbol());
								if (selectedCurrency != null && selectedCurrency.equals(currency)) {
									idx = i;
								}
								++i;
							}
							if (idx >= 0)
								combo.select(idx);

//							if (fireSelectionChangedEvent)
							fireSelectionChangedEvent();
						}
					});

					return Status.OK_STATUS;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
		loadCurrenciesJob.setPriority(Job.SHORT);
		loadCurrenciesJob.schedule();
	}

	private void fireSelectionChangedEvent()
	{
		if (selectionChangedListeners.isEmpty())
			return;

		SelectionChangedEvent event = new SelectionChangedEvent(this, getSelection());

		for (Object l : selectionChangedListeners.getListeners()) {
			ISelectionChangedListener listener = (ISelectionChangedListener) l;
			listener.selectionChanged(event);
		}
	}

	public Currency getSelectedCurrency() {
		return selectedCurrency;
	}

	private void setSelectedCurrencyID(String currencyID) {
		int idx = -1;
		int i = 0;
		for (Currency currency : currencies) {
			if (currency.getCurrencyID().equals(currencyID)) {
				idx = i; 
				break;
			}
			++i;
		}

		if (idx < 0) {
			combo.deselectAll();
			selectedCurrency = null;
		}
		else {
			combo.select(idx);
			selectedCurrency = currencies.get(idx);
		}
	}

	public void setSelectedCurrency(Currency currency) {
		setSelectedCurrencyID(currency == null ? null : currency.getCurrencyID());
	}

	public void setSelectedCurrency(CurrencyID currencyID) {
		setSelectedCurrencyID(currencyID == null ? null : currencyID.currencyID);
	}

	private Currency selectedCurrency;

	private ListenerList selectionChangedListeners = new ListenerList();

	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	public ISelection getSelection()
	{
		if (selectedCurrency == null)
			return new StructuredSelection(new Object[0]);

		return new StructuredSelection(selectedCurrency);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	/**
	 * @param selection An instance of {@link IStructuredSelection} which is either empty or contains an instance of either {@link Currency} or {@link CurrencyID}. 
	 */
	public void setSelection(ISelection selection)
	{
		if (!(selection instanceof IStructuredSelection))
			throw new IllegalArgumentException("selection is not an instance of " + IStructuredSelection.class.getName() + " but " + (selection == null ? null : selection.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$

		IStructuredSelection sel = (IStructuredSelection) selection;
		Object selObj = sel.getFirstElement();

		if (selObj == null)
			setSelectedCurrencyID(null);
		if (selObj instanceof Currency)
			setSelectedCurrency((Currency) selObj);
		else if (selObj instanceof CurrencyID)
			setSelectedCurrency((CurrencyID) selObj);
		else
			throw new IllegalArgumentException("selection.getFirstElement() is neither null, nor an instanceof " +Currency.class.getName()+ " or " +CurrencyID.class.getName()+ "! It is an instance of " + (selObj == null ? null : selObj.getClass().getName())); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
