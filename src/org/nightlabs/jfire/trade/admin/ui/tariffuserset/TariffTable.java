package org.nightlabs.jfire.trade.admin.ui.tariffuserset;

import java.util.Map;

import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.notification.IDirtyStateManager;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.entityuserset.ui.AbstractEntityTable;

/**
 * @author Daniel Mazurek - Daniel.Mazurek [dot] nightlabs [dot] de
 *
 */
public class TariffTable extends AbstractEntityTable<Tariff> 
{
	class LabelProvider extends AbstractEntityTableLabelProvider
	{
		/**
		 * @param viewer
		 */
		public LabelProvider(TableViewer viewer) {
			super(viewer);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) 
		{
			if (columnIndex == 1) {
//				Tariff tariff = (Tariff) element;
				Tariff tariff = ((Map.Entry<Tariff, Boolean>) element).getKey();
				return tariff.getName().getText();
			}
			return null;
		}
	}
	
	/**
	 * @param parent
	 * @param style
	 * @param dirtyStateManager
	 */
	public TariffTable(Composite parent, int style, IDirtyStateManager dirtyStateManager) {
		super(parent, style, dirtyStateManager);
	}

	@Override
	protected AbstractEntityTableLabelProvider createEntityTableLabelProvider(TableViewer tableViewer) {
		return new LabelProvider(tableViewer);
	}
	
	@Override
	protected void createAdditionalTableColumns(TableViewer tableViewer, Table table) 
	{
		TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setText("Tariff Name");
		nameColumn.setToolTipText("The name of the tariff");
		
		TableLayout layout = new WeightedTableLayout(new int[] { -1, 100 }, new int[] { 22, -1 });
//		TableLayout layout = new TableLayout();
//		ColumnLayoutData checkBoxData = new ColumnPixelData(20, false);
//		layout.addColumnData(checkBoxData);
//		ColumnLayoutData nameData = new ColumnWeightData(1, true);
//		layout.addColumnData(nameData);
		table.setLayout(layout);		
	}
	
}
