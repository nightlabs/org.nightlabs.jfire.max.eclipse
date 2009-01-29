package org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.CheckboxCellEditorHelper;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.dynamictrade.accounting.priceconfig.DynamicTradePriceConfig;
import org.nightlabs.jfire.dynamictrade.admin.ui.resource.Messages;

public class PriceFragmentTypeTable
extends AbstractTableComposite<InputPriceFragmentType>
{
	private ICellModifier cellModifier = new ICellModifier() {
		public boolean canModify(Object element, String property)
		{
			return PROPERTY_INPUT.equals(property);
		}
		public Object getValue(Object element, String property)
		{
			if (!PROPERTY_INPUT.equals(property))
				return null;

			InputPriceFragmentType ipft = (InputPriceFragmentType) element;
			return new Boolean(ipft.isInput());
		}
		public void modify(Object element, String property, Object value)
		{
			if (!PROPERTY_INPUT.equals(property))
				return;

			InputPriceFragmentType ipft = (InputPriceFragmentType) ((TableItem) element).getData();
			ipft.setInput((Boolean)value);
			getTableViewer().refresh(ipft, true);

			if (ipft.isInput())
				dynamicTradePriceConfig.addInputPriceFragmentType(ipft.getPriceFragmentType());
			else
				dynamicTradePriceConfig.removeInputPriceFragmentType(ipft.getPriceFragmentType());
		}
	};

	private TableLabelProvider labelProvider = new TableLabelProvider()
	{
		@Override
		public Image getColumnImage(Object element, int columnIndex)
		{
			if (!(element instanceof InputPriceFragmentType))
				return null;

			if (columnIndex == 1)
				return CheckboxCellEditorHelper.getCellEditorImage(cellModifier, element, PROPERTY_INPUT);
			else
				return super.getImage(element);
		}

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
				default:
					return ""; //$NON-NLS-1$
			}
		}
	};

	private static final String PROPERTY_NAME = "name"; //$NON-NLS-1$
	private static final String PROPERTY_INPUT = "input"; //$NON-NLS-1$

	public PriceFragmentTypeTable(Composite parent)
	{
		super(parent, SWT.NONE, false, DEFAULT_STYLE_SINGLE_BORDER);
		initTable();
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn tc;

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig.PriceFragmentTypeTable.priceFragmentTypeTableColumn.text")); //$NON-NLS-1$

		tc = new TableColumn(table, SWT.CENTER);
		tc.setText(Messages.getString("org.nightlabs.jfire.dynamictrade.admin.ui.priceconfig.PriceFragmentTypeTable.useAsInputTableColumn.text")); //$NON-NLS-1$

		TableLayout tl = new TableLayout();
		tl.addColumnData(new ColumnWeightData(1));
		tl.addColumnData(new ColumnPixelData(80));
		table.setLayout(tl);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer)
	{
		tableViewer.setCellModifier(cellModifier);
		tableViewer.setColumnProperties(new String[] {
				PROPERTY_NAME,
				PROPERTY_INPUT
		});
		tableViewer.setCellEditors(new CellEditor[] {
				null,
				new CheckboxCellEditor(tableViewer.getTable())
		});
		tableViewer.setLabelProvider(labelProvider);
		tableViewer.setContentProvider(new TableContentProvider());
	}

	@Override
	public void setInput(Object input)
	{
		throw new UnsupportedOperationException("Call setInput(DynamicTradePriceConfig, List) instead!"); //$NON-NLS-1$
	}

	private DynamicTradePriceConfig dynamicTradePriceConfig;

	public void setInput(DynamicTradePriceConfig dynamicTradePriceConfig, List<InputPriceFragmentType> inputPriceFragmentTypes)
	{
		this.dynamicTradePriceConfig = dynamicTradePriceConfig;
		super.setInput(inputPriceFragmentTypes);
	}
}
