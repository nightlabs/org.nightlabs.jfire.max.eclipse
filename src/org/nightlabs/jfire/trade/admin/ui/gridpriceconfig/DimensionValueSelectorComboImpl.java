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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.gridpriceconfig.GridPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCoordinate;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class DimensionValueSelectorComboImpl
	extends XComposite
	implements DimensionValueSelector
{
//	private MappingDimension[] dimensions = new MappingDimension[] {
//			new MappingDimension.CustomerGroupDimension(),
//			new MappingDimension.TariffDimension(),
//			new MappingDimension.CurrencyDimension(),
//			new MappingDimension.PriceFragmentTypeDimension()
//		};

	private Dimension[] dimensions;
	private Label[] dimensionLabels;
	private Combo[] dimensionCombos;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * @return Returns instances of {@link MappingDimension}.
	 */
	protected List<Dimension> createDimensions()
	{
		ArrayList<Dimension> res = new ArrayList<Dimension>();
		res.add(new Dimension.CustomerGroupDimension());
		res.add(new Dimension.TariffDimension());
		res.add(new Dimension.CurrencyDimension());
		res.add(new Dimension.PriceFragmentTypeDimension());
		return res;
	}

	/**
	 * @param parent
	 * @param style
	 */
	public DimensionValueSelectorComboImpl(Composite parent, int style)
	{
		super(parent, style, LayoutMode.TIGHT_WRAPPER);

		List<Dimension> dimensionList = createDimensions();
		dimensions = new Dimension[dimensionList.size()];
		dimensions = dimensionList.toArray(dimensions);
//		int i = 0;
//		for (Iterator it = dimensionList.iterator(); it.hasNext(); ) {
//			dimensions[i++] = (Dimension) it.next();
//		}

		dimensionLabels = new Label[dimensions.length];
		dimensionCombos = new Combo[dimensions.length];
		this.getGridLayout().numColumns = 2;
		this.getGridData().grabExcessVerticalSpace = false;
		for (int dimensionIdx = 0; dimensionIdx < dimensions.length; ++dimensionIdx) {
			final Dimension dimension = dimensions[dimensionIdx];
			createDimensionUIElements(dimensionLabels, dimensionCombos, dimensionIdx, dimension);
			final Combo dimensionCombo = dimensionCombos[dimensionIdx];

			dimension.addPropertyChangeListener(
					Dimension.PROPERTYCHANGEKEY_ADDDIMENSIONVALUE,
					new PropertyChangeListener()
					{
						public void propertyChange(PropertyChangeEvent evt)
						{
							DimensionValue dimensionValue = (DimensionValue) evt.getNewValue();
							fillDimensionCombo(dimension, dimensionCombo);
							propertyChangeSupport.firePropertyChange(
									PROPERTYCHANGEKEY_ADDDIMENSIONVALUE,
									null, dimensionValue);
							fireSelectionChangedEvent();
						}
					});
		}
	}

	protected void createDimensionUIElements(Label[] dimensionLabels, Combo[] dimensionCombos, int dimensionIdx, Dimension dimension)
	{
		dimensionLabels[dimensionIdx] = new Label(this, SWT.NONE);
		Label dimensionLabel = dimensionLabels[dimensionIdx];
		GridData gdLabel = new GridData(GridData.FILL_HORIZONTAL);
		gdLabel.horizontalSpan = 2;
		dimensionLabel.setLayoutData(gdLabel);
		dimensionLabel.setText(dimensions[dimensionIdx].getName());
		dimensionCombos[dimensionIdx] = new Combo(this, SWT.READ_ONLY);
		Combo dimensionCombo = dimensionCombos[dimensionIdx];
		GridData gdCombo = new GridData(GridData.FILL_HORIZONTAL);
		dimensionCombo.setLayoutData(gdCombo);
		dimensionCombo.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent event) {
				fireSelectionChangedEvent();
			}
		});

		createDimensionUIElements_createAddButton(dimensionLabels, dimensionCombos, dimensionIdx, dimension);
	}

	protected void createDimensionUIElements_createAddButton(Label[] dimensionLabels, Combo[] dimensionCombos, int dimensionIdx, final Dimension dimension)
	{
		Button addButton = new Button(this, SWT.NONE);
		addButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValueSelectorComboImpl.addButton.text")); //$NON-NLS-1$
		addButton.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event)
			{
				dimension.guiAddDimensionValue();
			}
		});
	}


	protected void fillDimensionCombo(Dimension dimension, Combo dimensionCombo)
	{
		dimensionCombo.removeAll();
		for (Iterator it = dimension.getValues().iterator(); it.hasNext(); ) {
			DimensionValue dimVal = (DimensionValue) it.next();
			dimensionCombo.add(dimVal.getName());
		}
		dimensionCombo.select(0);
	}

	private GridPriceConfig gridPriceConfig;

	@Implement
	public void setGridPriceConfig(GridPriceConfig gridPriceConfig)
	{
		this.gridPriceConfig = gridPriceConfig;
//		if (gridPriceConfig != null && !(gridPriceConfig instanceof IPackagePriceConfig))
//			throw new IllegalArgumentException("packagePriceConfig \"" + gridPriceConfig.getPrimaryKey() + "\" does not implement interface \"" + IPackagePriceConfig.class.getName() + "\"!");

		for (int dimensionIdx = 0; dimensionIdx < dimensions.length; ++dimensionIdx) {
			Dimension dim = dimensions[dimensionIdx];
			dim.setGridPriceConfig(gridPriceConfig);
			fillDimensionCombo(dim, dimensionCombos[dimensionIdx]);
		}
	}

	@Implement
	public GridPriceConfig getGridPriceConfig()
	{
		return gridPriceConfig;
	}

	protected PriceCoordinate createPriceCoordinate()
	{
		return new PriceCoordinate();
	}

//	protected DimensionValue preparePriceCoordinate_getDimensionValue(Dimension dimension, Combo dimensionCombo)
//	{
//		if (dimensionCombo.getSelectionIndex() < 0)
//			return null;
//
//		return (DimensionValue) dimension.getValues().get(dimensionCombo.getSelectionIndex());
//	}

	@Implement
	public PriceCoordinate preparePriceCoordinate()
	{
		PriceCoordinate res = createPriceCoordinate();
		for (int i = 0; i < dimensionCombos.length; ++i) {
//			DimensionValue selectedDV = preparePriceCoordinate_getDimensionValue(dimensions[i], dimensionCombos[i]);
			DimensionValue selectedDV = getSelectedDimensionValue(i, false);
			if (selectedDV == null)
				return null;

//			if (dimensionCombos[i].getSelectionIndex() < 0)
//				return null;
//
//			DimensionValue selectedDV = (DimensionValue) dimensions[i].getValues().get(dimensionCombos[i].getSelectionIndex());
			if (!(selectedDV instanceof DimensionValue.PriceFragmentTypeDimensionValue))
				selectedDV.adjustPriceCoordinate(res);
		}
		return res;
	}

	@Implement
	public DimensionValue getSelectedDimensionValue(int dimensionIdx, boolean throwExceptionIfNothingSelected)
	{
		Combo combo = dimensionCombos[dimensionIdx];
		if (combo.getSelectionIndex() < 0) {
			if (throwExceptionIfNothingSelected)
				throw new IllegalStateException("Nothing selected with dimensionIdx="+dimensionIdx+"!"); //$NON-NLS-1$ //$NON-NLS-2$

			return null;
		}

		return (DimensionValue) dimensions[dimensionIdx].getValues().get(combo.getSelectionIndex());
	}

	@Implement
	public void setDimensionEnabled(int dimensionIdx, boolean enabled)
	{
		dimensionLabels[dimensionIdx].setEnabled(enabled);
		dimensionCombos[dimensionIdx].setEnabled(enabled);
	}

	@Implement
	public boolean isDimensionEnabled(int dimensionIdx)
	{
		return dimensionLabels[dimensionIdx].isEnabled();
	}

	@Implement
	public Dimension[] getDimensions()
	{
		return dimensions;
	}

	private ListenerList selectionChangedListeners = new ListenerList();

	protected void fireSelectionChangedEvent()
	{
		SelectionChangedEvent e = new SelectionChangedEvent(this, getSelection());
		Object[] listeners = selectionChangedListeners.getListeners();
		for (Object listener : listeners) {
			ISelectionChangedListener l = (ISelectionChangedListener)listener;
			l.selectionChanged(e);
		}
	}

	@Implement
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.add(listener);
	}

	@Implement
	public ISelection getSelection()
	{
		return new DimensionValueSelection(this);
	}

	@Implement
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		selectionChangedListeners.remove(listener);
	}

	@Implement
	public void setSelection(ISelection selection)
	{
		throw new UnsupportedOperationException("NYI"); //$NON-NLS-1$
	}

	@Implement
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Implement
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Implement
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Implement
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	private int dimensionIdxPriceFragmentType = -1;

	@Implement
	public int getDimensionIdxPriceFragmentType()
	{
		if (dimensionIdxPriceFragmentType < 0) {
			Dimension[] dimensions = getDimensions();
			for (int i = 0; i < dimensions.length; ++i) {
				if (dimensions[i] instanceof Dimension.PriceFragmentTypeDimension) {
					dimensionIdxPriceFragmentType = i;
					break;
				}
			}
		}

		return dimensionIdxPriceFragmentType;
	}
}
