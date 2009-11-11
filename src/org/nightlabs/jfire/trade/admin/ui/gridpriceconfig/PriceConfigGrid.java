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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.gridpriceconfig.IFormulaPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.IPriceCoordinate;
import org.nightlabs.jfire.accounting.gridpriceconfig.IResultPriceConfig;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCalculationException;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCalculator;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCell;
import org.nightlabs.jfire.accounting.gridpriceconfig.PriceCoordinate;
import org.nightlabs.jfire.store.NestedProductTypeLocal;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.l10n.NumberFormatter;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class PriceConfigGrid extends XComposite
	implements ISelectionProvider
{
	private PriceCalculator priceCalculator;
	private TableViewer gridTableViewer;
	private Table gridTable;
	private TableCursor gridTableCursor;

	private ProductTypeSelector productTypeSelector;
	private DimensionValueSelector dimensionValueSelector;
	private DimensionXYSelector dimensionXYSelector;	
	
	private PriceConfigGridCell[][] cells = null;

	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	public static final String PROPERTY_CHANGE_KEY_PRICE_CONFIG_CHANGED = "priceConfigChanged"; //$NON-NLS-1$
	public static final String PROPERTY_PRICE_CALCULATION_DONE = "priceCalculationDone"; //$NON-NLS-1$
		
	public PriceConfigGrid(
			Composite parent,
			ProductTypeSelector productInfoSelector,
			DimensionValueSelector dimensionValueSelector,
			DimensionXYSelector dimensionXYSelector)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		this.productTypeSelector = productInfoSelector;
		this.dimensionValueSelector = dimensionValueSelector;
		this.dimensionXYSelector = dimensionXYSelector;

		productInfoSelector.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateTableData();
			}
		});

		dimensionValueSelector.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateTableData();
			}
		});

		dimensionXYSelector.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateTableData();
			}
		});

		gridTableViewer = new TableViewer(this, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL); // | SWT.FULL_SELECTION | SWT.MULTI);
		gridTable = gridTableViewer.getTable();
		gridTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		gridTable.setLayout(new PriceConfigTableLayout());
		gridTable.setLinesVisible(true);
		gridTable.addMouseListener(gridTableMouseListener);
		gridTableCursor = new TableCursor(gridTable, SWT.NONE);
		gridTableCursor.addKeyListener(gridTableCursorKeyListener);
		gridTableCursor.addSelectionListener(gridTableCursorSelectionListener);
	}

	
	private KeyListener gridTableCursorKeyListener = new KeyListener() {
		public void keyPressed(KeyEvent event) {
		}
		public void keyReleased(KeyEvent event) {
			if (event.character == ' ') {
				toggleSelection(gridTableCursor.getColumn(), gridTable.indexOf(gridTableCursor.getRow()));
			}
			updateTableDisplay();
		}
	};

	private MouseMoveListener gridTableMouseMoveListener = new MouseMoveListener() {
		public void mouseMove(MouseEvent event) {
			
		}
	};

	private MouseListener gridTableMouseListener = new MouseAdapter() {
		private Point gridTableSelectionStart = null;
		private Point gridTableSelectionStop = null;

		protected Point getCellCoordinate(MouseEvent event)
		{
			Point pt = new Point(event.x, event.y);
			TableItem tableItem = gridTable.getItem(pt);

			if (tableItem != null) {
				int colCount = gridTable.getColumnCount();
				for (int i = 0; i < colCount; ++i) {
					Rectangle rect = tableItem.getBounds(i);
          if (rect.contains (pt)) {
          	return new Point(i, gridTable.indexOf(tableItem));
          }
				}
			}
			return null;
		}
		@Override
		public void mouseDown(MouseEvent event) {
			gridTable.setSelection(-1); // deactivate the tables own selection shit

			gridTableSelectionStart = getCellCoordinate(event);
		}
		@Override
		public void mouseUp(MouseEvent event) {
			gridTableSelectionStop = getCellCoordinate(event);
			
			if (gridTableSelectionStart != null && gridTableSelectionStop != null) {
				selectedCellCoordinates.clear();

				toggleSelection(
						gridTableSelectionStart.x, gridTableSelectionStart.y,
						gridTableSelectionStop.x, gridTableSelectionStop.y);
			}
			updateTableDisplay();
		}
	};

	private static final int DEFAULT_DATALEFT = 1;
	private static final int DEFAULT_DATATOP = 1;
	private static final Point DEFAULT_DATALEFTTOP = new Point(1, 1);
	
	/**
	 * @return Returns the column index, where data is starting.
	 * @see #getDataLeftTop()
	 */
	public int getDataLeft()
	{
		return DEFAULT_DATALEFT;
	}
	/**
	 * @return Returns the row index, where data is starting.
	 * @see #getDataLeftTop()
	 */
	public int getDataTop()
	{
		return DEFAULT_DATATOP;
	}
	/**
	 * @return Returns the coordinate where the data is starting. Because there is usually
	 *    a horizontal and a vertical header, this method usually returns <tt>Point(1, 1)</tt>,
	 *    but there might be none or multiple header rows/columns.
	 * @see #getDataLeft()
	 * @see #getDataTop()
	 */
	public Point getDataLeftTop()
	{
		return DEFAULT_DATALEFTTOP;
	}

	private PriceConfigGridCell cursorCell = null;
	private Point cursorCellCoordinate = null;

	public PriceConfigGridCell getCursorCell()
	{
		if (cursorCell == null) {
			Point cursorCoordinate = getCursorCellCoordinate();
			if (cursorCoordinate.x >= getDataLeft() && cursorCoordinate.y >= getDataTop())
				cursorCell = cells[cursorCoordinate.y - getDataTop()][cursorCoordinate.x - getDataLeft()];
		}
		return cursorCell;
	}

	/**
	 * @return Returns the coordinate of the table's cursor where y is the row and x is the
	 *   column. Note, that the data does usually start at x = 1 and y = 1, because there
	 *   is a horizontal and a vertical header.
	 *
	 * @see #getDataLeftTop()
	 * @see #getSelectedCellCoordinates()
	 */
	public Point getCursorCellCoordinate()
	{
		if (cursorCellCoordinate == null) {
			int cursorX = -1;
			int cursorY = -1;
			if (gridTableCursor.getRow() != null) {
				cursorX = gridTableCursor.getColumn();
				cursorY = gridTable.indexOf(gridTableCursor.getRow());
			}

			if (cursorX > gridTable.getColumnCount() - 1)
				cursorX = gridTable.getColumnCount() - 1;

			if (cursorY > gridTable.getItemCount() - 1)
				cursorY = gridTable.getItemCount() - 1;

			cursorCellCoordinate = new Point(cursorX, cursorY);
		}
		return cursorCellCoordinate;
	}

	private SelectionListener gridTableCursorSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent event) {
			cursorCellCoordinate = null;
			cursorCell = null;
			fireSelectionChangedEvent();
		}
	};

	/**
	 * Stores instances of Point where x and y specify the coordinates as columnIndex and
	 * rowIndex.
	 */
	private HashSet selectedCellCoordinates = new HashSet();

	private Collection selectedCells = null;
	protected void resetSelectedCells() {
		selectedCells = null;
		cursorCell = null;
	}

	/**
	 * @return Returns a <tt>Collection</tt> of <tt>PriceConfigGridCell</tt>.
	 */
	public Collection getSelectedCells() {
		if (selectedCells == null) {
			selectedCells = new HashSet();
			int dataLeft = getDataLeft();
			int dataTop = getDataTop();
			for (Iterator it = selectedCellCoordinates.iterator(); it.hasNext(); ) {
				Point coordinate = (Point)it.next();
				if (coordinate.x >= dataLeft && coordinate.y >= dataTop)
					selectedCells.add(cells[coordinate.y - dataTop][coordinate.x - dataLeft]);
			}
		}
		return selectedCells;
	}

	/**
	 * @return Returns instances of <tt>Point</tt> where x and y specify the column and the row
	 *   index of the selected cells. Note, that the cursor coordinate might be missing in
	 *   this <tt>Collection</tt>. Additionally, you should be aware that header cells might be
	 *   selected.
	 *
	 * @see #getCursorCellCoordinate()
	 * @see #getDataLeftTop()
	 */
	public Collection getSelectedCellCoordinates()
	{
		return selectedCellCoordinates;
	}

	protected void toggleSelection(int col, int row)
	{
		toggleSelection(col, row, col, row);
	}
	/**
	 * Automatically switches if left > right or top > bottom, thus the order doesn't matter.
	 */
	protected void toggleSelection(int left, int top, int right, int bottom)
	{
		if (left > right) {
			int i = left;
			left = right;
			right = i;
		}

		if (top > bottom) {
			int i = top;
			top = bottom;
			bottom = i;
		}

		int colCount = gridTable.getColumnCount();
		int rowCount = gridTable.getItemCount();

		if (left < getDataLeft()) {
			if (top < getDataTop()) bottom = rowCount - 1;
			boolean addMode = true;
			for (int col = left; col < colCount; ++col) {
				for (int row = top; row <= bottom; ++row) {
					Point cellCoordinate = new Point(col, row);
					if (col == 0 && selectedCellCoordinates.contains(cellCoordinate))
						addMode = false;
					
					if (addMode)
						selectedCellCoordinates.add(cellCoordinate);
					else
						selectedCellCoordinates.remove(cellCoordinate);
				}
			}
		}
		else if (top < getDataTop()) {
			boolean addMode = true;
			for (int col = left; col <= right; ++col) {
				for (int row = top; row < rowCount; ++row) {
					Point cellCoordinate = new Point(col, row);
					if (row == 0 && selectedCellCoordinates.contains(cellCoordinate))
						addMode = false;

					if (addMode)
						selectedCellCoordinates.add(cellCoordinate);
					else
						selectedCellCoordinates.remove(cellCoordinate);
				}
			}
		}
		else {
			for (int col = left; col <= right; ++col) {
				for (int row = top; row <= bottom; ++row) {
					Point cellCoordinate = new Point(col, row);
					if (selectedCellCoordinates.contains(cellCoordinate))
						selectedCellCoordinates.remove(cellCoordinate);
					else
						selectedCellCoordinates.add(cellCoordinate);
				}
			}
		}

		resetSelectedCells();
		fireSelectionChangedEvent();
	}

//	private EventPackageProductInfo assemblyPackageProductInfo = null;
//
//	public void setEventPackageProductInfo(EventPackageProductInfo assemblyPackageProductInfo)
//	throws ModuleException
//	{
//		this.assemblyPackageProductInfo = assemblyPackageProductInfo;
//	}

//	private MappingDimension gridDimensionX;
//	private MappingDimension gridDimensionY;
//
//	public void setGridDimensions(MappingDimension gridDimensionX, MappingDimension gridDimensionY)
//	{
//		this.gridDimensionX = gridDimensionX;
//		this.gridDimensionY = gridDimensionY;
//	}

	protected void updateTableDisplay()
	{
		// TODO Global JFire Colors
		Color gray = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
		Color black = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
		Color darkGray = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
		Color white = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
//		Color blue = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
		Color darkBlue = Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE);

		int cursorY = gridTableCursor.getRow() == null ? -1 : gridTable.indexOf(gridTableCursor.getRow());
		int cursorX = gridTableCursor.getColumn();
		Point cursor = new Point(cursorX, cursorY);
		if (selectedCellCoordinates.contains(cursor)) {
			gridTableCursor.setBackground(darkBlue);
			gridTableCursor.setForeground(white);
		}
		else {
			gridTableCursor.setBackground(white);
			gridTableCursor.setForeground(black);
		}

		int colCount = gridTable.getColumnCount();
		TableItem[] rows = gridTable.getItems();
		for (int r = 0; r < rows.length; ++r) {
			TableItem row = rows[r];

			for (int c = 0; c < colCount; ++c) {
				Point cellCoordinate = new Point(c, r);

				if (selectedCellCoordinates.contains(cellCoordinate)) {
					// SELECTED
//					if (c == cursorX && r == cursorY) {
//						row.setBackground(c, darkBlue);
//						row.setForeground(c, white);
//					}
//					else {
						row.setBackground(c, darkGray);
						row.setForeground(c, white);
//					}
				}
				else {
					// NOT SELECTED
//					if (c == cursorX && r == cursorY) {
//						row.setBackground(c, blue);
//						row.setForeground(c, black);
//					}
//					else {
						row.setForeground(c, black);
						if (r == 0 && c == 0)
							row.setBackground(c, white);
						else if (r == 0 || c == 0)
							row.setBackground(c, gray);
						else
							row.setBackground(c, white);
//					}
				}
			} // for (int c = 0; c < colCount; ++c) {
		} // for (int r = 0; r < rows.length; ++r) {
	}

	public PriceFragmentType getSelectedPriceFragmentType(boolean throwExceptionIfNothingSelected)
	{
		DimensionValue dv = dimensionValueSelector.getSelectedDimensionValue(
				dimensionValueSelector.getDimensionIdxPriceFragmentType(), throwExceptionIfNothingSelected);
		if (dv == null)
			return null;
	
		return (PriceFragmentType) dv.getObject();
	}

	protected void updateTableData()
	{
		boolean doResetSelectedCells = false; // does not mean that the selection will ge lost,
    																			// but the list of selected cell instances gets
    																			// cleared and a new selection event fired.

		int oldRowCount = gridTable.getItemCount();
		int oldColCount = gridTable.getColumnCount();

		gridTable.removeAll();
		TableColumn[] cols = gridTable.getColumns();
		for (int i = 0; i < cols.length; ++i)
			cols[i].dispose();

		if (productTypeSelector.getSelectedProductTypeItem(false) == null)
			doResetSelectedCells = true;
		else {
	
			NestedProductTypeLocal nestedProductTypeLocal = null;
			ProductType selectedProductType = productTypeSelector.getSelectedProductTypeItem(true).getProductType();
			if (!productTypeSelector.getPackageProductType().getPrimaryKey().equals(selectedProductType.getPrimaryKey())) {
				nestedProductTypeLocal = productTypeSelector.getPackageProductType().getProductTypeLocal().getNestedProductTypeLocal(
						selectedProductType.getOrganisationID(), selectedProductType.getProductTypeID(), true);
			}
	
			IFormulaPriceConfig formulaPriceConfig = productTypeSelector.getSelectedProductType_FormulaPriceConfig(false);
//			IResultPriceConfig resultPriceConfig = productTypeSelector.getSelectedProductType_ResultPriceConfig(true);
			IResultPriceConfig resultPriceConfig = productTypeSelector.getSelectedProductType_ResultPriceConfig(false);
			if (resultPriceConfig == null)
				doResetSelectedCells = true;
			else {
		//		gridTable.removeAll();
		//		TableColumn[] cols = gridTable.getColumns();
		//		for (int i = 0; i < cols.length; ++i)
		//			cols[i].dispose();
		
				new TableColumn(gridTable, SWT.LEFT);
				
				Dimension gridDimensionX = dimensionXYSelector.getDimensionX();
				Dimension gridDimensionY = dimensionXYSelector.getDimensionY();
		
				int x = 1;
				TableItem headerX = new TableItem(gridTable, SWT.NONE);
				for (Iterator it = gridDimensionX.getValues().iterator(); it.hasNext(); ) {
					DimensionValue dvX = (DimensionValue)it.next();
					new TableColumn(gridTable, SWT.RIGHT);
					headerX.setText(x, dvX.getName());
					++x;
				}
		
				IPriceCoordinate priceCoordinate = dimensionValueSelector.preparePriceCoordinate();
				if (priceCoordinate == null) // we have no dimensionvalues in at least one dimension
					return;
				PriceFragmentType priceFragmentType = getSelectedPriceFragmentType(true);
		
				if (cells != null) {
					if (cells.length != gridDimensionY.getValues().size())
						cells = null;
					else if (cells.length < 1)
						cells = null;
					else if (cells[0].length != gridDimensionX.getValues().size())
						cells = null;
				}
				
				if (cells == null) {
					cells = new PriceConfigGridCell[gridDimensionY.getValues().size()][gridDimensionX.getValues().size()];
					doResetSelectedCells = true;
				}
		
				int cellsY = 0;
				for (Iterator itY = gridDimensionY.getValues().iterator(); itY.hasNext(); ) {
					DimensionValue dvY = (DimensionValue)itY.next();
					TableItem row = new TableItem(gridTable, SWT.NONE);
					row.setText(0, dvY.getName());
		
					if (dvY instanceof DimensionValue.PriceFragmentTypeDimensionValue)
						priceFragmentType = (PriceFragmentType) dvY.getObject();
					else
						dvY.adjustPriceCoordinate(priceCoordinate);
		
					int tableX = 1; int cellsX = 0;
					for (Iterator it = gridDimensionX.getValues().iterator(); it.hasNext(); ) {
						DimensionValue dvX = (DimensionValue)it.next();
		
						if (dvX instanceof DimensionValue.PriceFragmentTypeDimensionValue)
							priceFragmentType = (PriceFragmentType) dvX.getObject();
						else
							dvX.adjustPriceCoordinate(priceCoordinate);
		
						if (nestedProductTypeLocal != null)
							priceCoordinate = priceCalculator.createMappedLocalPriceCoordinate(nestedProductTypeLocal, priceFragmentType, priceCoordinate);
		
						PriceCell priceCell = resultPriceConfig.getPriceCell(priceCoordinate, false);
						if (priceCell != null) {
							long amount = priceCell.getPrice().getAmount(priceFragmentType);
							Currency currency = priceCell.getPrice().getCurrency();
//							row.setText(tableX, Long.toString(amount));
							row.setText(tableX, NumberFormatter.formatCurrency(amount, currency, false));
						}
		
						PriceConfigGridCell cell = cells[cellsY][cellsX];
						if (cell == null ||
								formulaPriceConfig != cell.getFormulaPriceConfig() ||
								!priceCoordinate.equals(cell.getPriceCoordinate()) ||
								priceFragmentType != cell.getPriceFragmentType())
						{
							cells[cellsY][cellsX] = new PriceConfigGridCell(
									formulaPriceConfig,
									resultPriceConfig,
									createPriceCoordinate(priceCoordinate),
									priceFragmentType);
							cells[cellsY][cellsX].addPropertyChangeListener(cellChangedListener);
							doResetSelectedCells = true;
						}
		
						cellsX = tableX++;
					}
					++cellsY;
				}
		
				// Make sure we never have illegal selected cells (out of range)
				// but keep the selection if possible.
				if (oldRowCount != gridTable.getItemCount() || oldColCount != gridTable.getColumnCount()) {
					selectedCellCoordinates.clear();
					cursorCellCoordinate = null;
		
		//			Point coordinate = getCursorCellCoordinate();
		//
		//			if (coordinate.y > gridTable.getItemCount() - 1)
		//				coordinate.y = gridTable.getItemCount() - 1;
		//
		//			if (coordinate.x > gridTable.getColumnCount() - 1)
		//				coordinate.x = gridTable.getColumnCount() - 1;
		
		//			gridTableCursor. // .setSelection(null, 0); // coordinate.x, coordinate.y);
		
					doResetSelectedCells = true;
				}
			} // if (formulaPriceConfig != null)
		} // if (productTypeSelector.getSelectedProductTypeItem(false) != null)

		if (doResetSelectedCells) {
			resetSelectedCells();
			fireSelectionChangedEvent();
		}

		gridTable.layout();

		updateTableDisplay();
	}

	protected PriceCoordinate createPriceCoordinate(IPriceCoordinate priceCoordinate)
	{
		return new PriceCoordinate(priceCoordinate);
	}


	
	private PropertyChangeListener cellChangedListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt)
		{
			try {
				if (priceCalculator != null)
					priceCalculator.calculatePrices();				
				propertyChangeSupport.firePropertyChange(PROPERTY_PRICE_CALCULATION_DONE, null, null);
			}
			catch (PriceCalculationException e) {			
				// the event is needed to inform the listener about the wrong formula
				propertyChangeSupport.firePropertyChange(PROPERTY_PRICE_CALCULATION_DONE, null, e);
				return; // since the formula is wrong then simply return and dont update the table.
			}
			updateTableData();

			propertyChangeSupport.firePropertyChange(PROPERTY_CHANGE_KEY_PRICE_CONFIG_CHANGED, null, null);
		}
	};

	/**
	 * This method takes a <tt>PriceCoordinate</tt> (e.g. created by <tt>DimensionValueSelector.preparePriceCoordinate()</tt>)
	 * and adjusts it to match the given cell (defined by gridX &amp; gridY).
	 * <p>
	 * Note, that the <tt>PriceFragmentType</tt> is not part of the <tt>PriceCoordinate</tt>,
	 * because price fragments are - as the name says - fragments within a price cell. The
	 * visualisation in the table is "virtual" and works in conjunction with
	 * <tt>getPriceFragmentType(int gridX, int gridY)</tt>.
	 *
	 * @param priceCoordinate The <tt>PriceCoordinate</tt> to manipulate.
	 * @param gridX The column index. Must be &gt;= than <tt>dataLeft</tt>.
	 * @param gridY The row index. Must be &gt;= than <tt>dataTop</tt>.
	 *
	 * @see #getPriceFragmentType(int, int)
	 */
	public void adjustPriceCoordinate(PriceCoordinate priceCoordinate, int gridX, int gridY)
	{
		int dimensionXIndex = gridX - getDataLeft(); // because of header
		int dimensionYIndex = gridY - getDataTop();
		if (dimensionXIndex < 0)
			throw new IllegalArgumentException("gridX < dataLeft!"); //$NON-NLS-1$
		if (dimensionYIndex < 0)
			throw new IllegalArgumentException("gridY < dataTop!"); //$NON-NLS-1$

		if (!(dimensionXYSelector.getDimensionX() instanceof Dimension.PriceFragmentTypeDimension))
			((DimensionValue)dimensionXYSelector.getDimensionX().getValues()
					.get(dimensionXIndex)).adjustPriceCoordinate(priceCoordinate);

		if (!(dimensionXYSelector.getDimensionY() instanceof Dimension.PriceFragmentTypeDimension))
			((DimensionValue)dimensionXYSelector.getDimensionY().getValues()
					.get(dimensionYIndex)).adjustPriceCoordinate(priceCoordinate);
	}

	/**
	 * @param gridX The column index. Must be &gt;= than <tt>dataLeft</tt>.
	 * @param gridY The row index. Must be &gt;= than <tt>dataTop</tt>.
	 * @return If either the X or the Y axis is spanning <tt>PriceFragmentType</tt> s,
	 *   it returns the <tt>PriceFragmentType</tt> defined by <tt>gridX</tt> or
	 *   <tt>gridY</tt>; otherwise the one coming from the <tt>DimensionValueSelector</tt>.
	 *   Never returns <tt>null</tt>.
	 */
	public PriceFragmentType getPriceFragmentType(int gridX, int gridY)
	{
		int dimensionXIndex = gridX - getDataLeft(); // because of header
		int dimensionYIndex = gridY - getDataTop();
		if (dimensionXIndex < 0)
			throw new IllegalArgumentException("gridX < dataLeft!"); //$NON-NLS-1$
		if (dimensionYIndex < 0)
			throw new IllegalArgumentException("gridY < dataTop!"); //$NON-NLS-1$

		PriceFragmentType priceFragmentType = null;
		if (dimensionXYSelector.getDimensionX() instanceof Dimension.PriceFragmentTypeDimension)
			priceFragmentType = (PriceFragmentType)((DimensionValue)dimensionXYSelector.getDimensionX()
					.getValues().get(dimensionXIndex)).getObject();
	
		if (dimensionXYSelector.getDimensionY() instanceof Dimension.PriceFragmentTypeDimension)
			priceFragmentType = (PriceFragmentType)((DimensionValue)dimensionXYSelector.getDimensionY()
					.getValues().get(dimensionYIndex)).getObject();
	
		if (priceFragmentType == null)
			priceFragmentType = getSelectedPriceFragmentType(true);
	
		return priceFragmentType;
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
		return new PriceConfigGridSelection(
				getCursorCellCoordinate(), getCursorCell(),
				getSelectedCellCoordinates(), getSelectedCells());
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

	/**
	 * @return Returns the productTypeSelector.
	 */
	public ProductTypeSelector getProductTypeSelector()
	{
		return productTypeSelector;
	}
	/**
	 * @return Returns the priceCalculator.
	 */
	public PriceCalculator getPriceCalculator()
	{
		return priceCalculator;
	}
	/**
	 * @param priceCalculator The priceCalculator to set.
	 */
	public void setPriceCalculator(PriceCalculator priceCalculator)
	{
		this.priceCalculator = priceCalculator;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
