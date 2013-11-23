package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.l10n.NumberFormatter;

public abstract class InputPriceFragmentTypeTable
extends AbstractTableComposite<InputPriceFragmentType>
{
	private Currency currency;

	private TableLabelProvider labelProvider = new TableLabelProvider()
	{
		public String getColumnText(Object element, int columnIndex)
		{
			if (!(element instanceof InputPriceFragmentType)) {
				if (columnIndex == 0)
					return String.valueOf(element);
				else
					return ""; //$NON-NLS-1$
			}

			InputPriceFragmentType ipft = (InputPriceFragmentType) element;

			switch (columnIndex) {
				case 0:
					return ipft.getPriceFragmentType().getName().getText();
				case 1:
					return NumberFormatter.formatCurrency(ipft.getAmount(), currency);
			}

			return ""; //$NON-NLS-1$
		}
	};

	private static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
	private static final String PROPERTY_AMOUNT = "amount"; //$NON-NLS-1$

	private ICellModifier cellModifier = new ICellModifier() {
		public boolean canModify(Object element, String property)
		{
			return PROPERTY_AMOUNT.equals(property);
		}
		public Object getValue(Object element, String property)
		{
			if (PROPERTY_AMOUNT.equals(property))
				return String.valueOf(((InputPriceFragmentType)element).getAmount());

			return null;
		}
		public void modify(Object element, String property, Object value)
		{
			if (!PROPERTY_AMOUNT.equals(property))
				return;

			InputPriceFragmentType ipft = (InputPriceFragmentType) ((TableItem)element).getData();
			String s = (String) value;
			long newValue = Long.parseLong(s);

			if (newValue != ipft.getAmount()) {
				ipft.setAmount(newValue);
				getTableViewer().refresh(ipft, true);
				inputPriceFragmentTypeModified(ipft);
			}
		}
	};

	protected abstract void inputPriceFragmentTypeModified(InputPriceFragmentType inputPriceFragmentType);

	/**
	 * Create a new {@link InputPriceFragmentTypeTable} for the given parent.
	 * <p>
	 * Note, that {@link #setCurrency(Currency)} still needs to be called 
	 * when using this constructor as the Table won't work otherwise.
	 * </p>
	 * @param parent The parent to use.
	 */
	public InputPriceFragmentTypeTable(Composite parent)
	{
		super(parent, SWT.NONE, false);
		initTable();
		getTable().setHeaderVisible(false);
	}
	/**
	 * Create a new {@link InputPriceFragmentType} for the given parent.
	 * @param parent The parent to use.
	 * @param currency The currency to use.
	 */
	public InputPriceFragmentTypeTable(Composite parent, Currency currency) {
		this(parent);
		setCurrency(currency);
	}
	
	/**
	 * Set the {@link Currency} used by this table. 
	 * @param currency The {@link Currency} to set.
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.InputPriceFragmentTypeTable.inputTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail.InputPriceFragmentTypeTable.amountTableColumn.text")); //$NON-NLS-1$

		TableLayout tl = new TableLayout();
		tl.addColumnData(new ColumnWeightData(1));
		tl.addColumnData(new ColumnPixelData(120));
		table.setLayout(tl);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		setEditable(true);
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(labelProvider);
	}
	
	public void setEditable(boolean editable) {
		CellEditor[] editors = getTableViewer().getCellEditors();
		if (editable) {
			getTableViewer().setColumnProperties(new String[] { PROPERTY_NAME, PROPERTY_AMOUNT });
			getTableViewer().setCellModifier(cellModifier);
			getTableViewer().setCellEditors(new CellEditor[] {
					null,
					new TextCellEditor(getTableViewer().getTable())
			});
		}
		else {
			if (editors != null) {
				for (CellEditor cellEditor : editors) {
					if (cellEditor != null)
						cellEditor.dispose();
				}
			}
			getTableViewer().setCellEditors(new CellEditor[] {null, null});
		}
	}
}
