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

package org.nightlabs.jfire.trade.ui.currency;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.custom.XCombo;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.CurrencyDAO;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author marco schulze - marco at nightlabs dot de
 * @author chairat kongarayawetchakun - chairat at nightlabs dot com
 */
public class CurrencyCombo
extends XComposite
implements ISelectionProvider
{

	private List<Currency> currencies = new ArrayList<Currency>(0);
	private XCombo combo;

	public static final String PROPERTY_KEY_LOAD_JOB_FINISHED = "LoadJobFinished";

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public CurrencyCombo(Composite parent, int style) {
		super(parent, style, LayoutMode.TIGHT_WRAPPER);
		setLayoutData( new GridData(GridData.FILL_HORIZONTAL));
		combo = new XCombo(this, getBorderStyle() | SWT.READ_ONLY);
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

				selectedCurrencyID = selectedCurrency == null ? null : selectedCurrency.getCurrencyID();

				fireSelectionChangedEvent();
			}
		});

		combo.add(null, Messages.getString("org.nightlabs.jfire.trade.ui.accounting.CurrencyCombo.pseudoEntry_loading")); //$NON-NLS-1$
		combo.select(0);

		org.nightlabs.base.ui.job.Job loadCurrenciesJob = new org.nightlabs.base.ui.job.Job(Messages.getString("org.nightlabs.jfire.trade.ui.accounting.CurrencyCombo.loadCurrenciesJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				try {
					final List<Currency> currencyList = CurrencyDAO.sharedInstance().getCurrencies(monitor);

					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							combo.removeAll();
							currencies = currencyList;
							int idx = -1; int i = 0;
//							boolean fireSelectionChangedEvent = false;

							selectedCurrency = null;

							for (Currency currency : currencies) {
								combo.add(null, currency.getCurrencySymbol());
								if (selectedCurrencyID != null && selectedCurrencyID.equals(currency.getCurrencyID())) {
									idx = i;
								}
								++i;
							}
							if (idx >= 0)
								combo.select(idx);
							else
								combo.select(0);

							idx = combo.getSelectionIndex();
							if (idx >= 0)
								selectedCurrency = currencies.get(idx);

//							if (selectedCurrency == null && !currencies.isEmpty()) {
//								selectedCurrency = currencies.get(0);
////								fireSelectionChangedEvent = true;
//							}

//							We do not fire this selection event, since other listeners added from outside are
//								also triggered and hence we cannot know what will happen by doing so. (marius)
//							Instead we fire a property change event for those who are interested. Chairat & Marco.
//							if (fireSelectionChangedEvent)
//							fireSelectionChangedEvent();
							propertyChangeSupport.firePropertyChange(PROPERTY_KEY_LOAD_JOB_FINISHED, null, selectedCurrency);
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

	private String selectedCurrencyID = null;

	private void setSelectedCurrencyID(String currencyID) {
		selectedCurrencyID = currencyID;
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

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyKey, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyKey, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyKey, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyKey, listener);
	}
}
