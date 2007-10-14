package org.nightlabs.jfire.dynamictrade.articlecontainer.detail;

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
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.dynamictrade.resource.Messages;
import org.nightlabs.l10n.NumberFormatter;

public abstract class InputPriceFragmentTypeTable
extends AbstractTableComposite
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

	public InputPriceFragmentTypeTable(Composite parent, Currency currency)
	{
		super(parent, SWT.NONE, false);
		this.currency = currency;
		initTable();
		getTable().setHeaderVisible(false);
	}

	@Implement
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;
		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.articlecontainer.detail.InputPriceFragmentTypeTable.inputTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.articlecontainer.detail.InputPriceFragmentTypeTable.amountTableColumn.text")); //$NON-NLS-1$

		TableLayout tl = new TableLayout();
		tl.addColumnData(new ColumnWeightData(1));
		tl.addColumnData(new ColumnPixelData(120));
		table.setLayout(tl);
	}

	@Implement
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setColumnProperties(new String[] { PROPERTY_NAME, PROPERTY_AMOUNT });
		tableViewer.setCellModifier(cellModifier);
		tableViewer.setCellEditors(new CellEditor[] {
				null,
				new TextCellEditor(tableViewer.getTable())
		});
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(labelProvider);
	}
}
