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

package org.nightlabs.jfire.trade.admin.gridpriceconfig;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.admin.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DimensionXYSelectorComboImpl extends XComposite
		implements DimensionXYSelector
{
	private Label gridDimensionXLabel;
	private Label gridDimensionYLabel;
	private Combo gridDimensionXCombo;
	private Combo gridDimensionYCombo;

	private DimensionValueSelector dimensionValueSelector;
	private Dimension[] dimensions;

	private Dimension gridDimensionX;
	private Dimension gridDimensionY;

	/**
	 * @return Returns the dimensions.
	 */
	public Dimension[] getDimensions()
	{
		return dimensions;
	}

	/**
	 * @param parent
	 * @param style
	 * @param setLayoutData
	 */
	public DimensionXYSelectorComboImpl(Composite parent, int style, DimensionValueSelector dimensionValueSelector)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		getGridLayout().numColumns = 2;
		getGridData().grabExcessVerticalSpace = false;

		this.dimensionValueSelector = dimensionValueSelector;
		this.dimensions = dimensionValueSelector.getDimensions();

		gridDimensionYLabel = new Label(this, SWT.NONE);
		gridDimensionYLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.gridpriceconfig.DimensionXYSelectorComboImpl.gridDimensionYLabel.text")); //$NON-NLS-1$
		gridDimensionXLabel = new Label(this, SWT.NONE);
		gridDimensionXLabel.setText(Messages.getString("org.nightlabs.jfire.trade.admin.gridpriceconfig.DimensionXYSelectorComboImpl.gridDimensionXLabel.text")); //$NON-NLS-1$
		gridDimensionYCombo = new Combo(this, SWT.READ_ONLY);
		gridDimensionXCombo = new Combo(this, SWT.READ_ONLY);

		int xidx = -1;
		int yidx = -1;
		Dimension dimX = getDefaultDimensionX();
		Dimension dimY = getDefaultDimensionY();
		for (int i = 0; i < dimensions.length; ++i) {
			Dimension dimension = dimensions[i];

			if (dimension == dimX)
				xidx = i;

			if (dimension == dimY)
				yidx = i;

			gridDimensionXCombo.add(dimension.getName());
			gridDimensionYCombo.add(dimension.getName());
		}
		gridDimensionXCombo.select(xidx);
		gridDimensionYCombo.select(yidx);

		gridDimensionsChanged();

		gridDimensionXCombo.addSelectionListener(gridComboSelectionListener);
		gridDimensionYCombo.addSelectionListener(gridComboSelectionListener);
	}

	protected Dimension getDefaultDimensionX()
	{
		return dimensions[3];
	}
	protected Dimension getDefaultDimensionY()
	{
		return dimensions[1];
	}

	private SelectionListener gridComboSelectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
			gridDimensionsChanged();
		}
	};

	private void gridDimensionsChanged()
	{
		if (gridDimensionXCombo.getSelectionIndex() < 0)
			throw new IllegalStateException("gridDimensionXCombo.getSelectionIndex() < 0!"); //$NON-NLS-1$

		if (gridDimensionYCombo.getSelectionIndex() < 0)
			throw new IllegalStateException("gridDimensionYCombo.getSelectionIndex() < 0!"); //$NON-NLS-1$

		// If both select the same dimension, reset and return.
		if (gridDimensionXCombo.getSelectionIndex() == gridDimensionYCombo.getSelectionIndex()) {
			for (int dimensionIdx = 0; dimensionIdx < dimensions.length; ++dimensionIdx) {
				Dimension dim = dimensions[dimensionIdx];
				if (gridDimensionX == dim)
					gridDimensionXCombo.select(dimensionIdx);

				if (gridDimensionY == dim)
					gridDimensionYCombo.select(dimensionIdx);
			}

			return;
		}

		gridDimensionX = dimensions[gridDimensionXCombo.getSelectionIndex()];
		gridDimensionY = dimensions[gridDimensionYCombo.getSelectionIndex()];

		for (int dimensionIdx = 0; dimensionIdx < dimensions.length; ++dimensionIdx) {
			Dimension dim = dimensions[dimensionIdx];

			dimensionValueSelector.setDimensionEnabled(
					dimensionIdx,
					dim != gridDimensionX && dim != gridDimensionY);
		}

		fireSelectionChangedEvent();
	}

	/**
	 * @see org.nightlabs.jfire.ticketing.admin.priceconfig.DimensionXYSelector#getDimensionX()
	 */
	public Dimension getDimensionX()
	{
		return gridDimensionX;
	}

	/**
	 * @see org.nightlabs.jfire.ticketing.admin.priceconfig.DimensionXYSelector#getDimensionY()
	 */
	public Dimension getDimensionY()
	{
		return gridDimensionY;
	}

	private List selectionChangedListeners = new LinkedList();

	protected void fireSelectionChangedEvent()
	{
		SelectionChangedEvent e = new SelectionChangedEvent(this, getSelection());
		for (Iterator it = selectionChangedListeners.iterator(); it.hasNext(); ) {
			ISelectionChangedListener l = (ISelectionChangedListener)it.next();
			l.selectionChanged(e);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection()
	{
		return new DimensionXYSelection(gridDimensionX, gridDimensionY);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection)
	{
		throw new UnsupportedOperationException("NYI"); //$NON-NLS-1$
	}

}
