/**
 * 
 */
package org.nightlabs.jfire.trade.ui.currency;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class CurrencyTable extends AbstractTableComposite<Currency> {

	/**
	 * @param parent
	 * @param style
	 */
	public CurrencyTable(Composite parent) {
		this(parent, SWT.NONE, DEFAULT_STYLE_MULTI_BORDER);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public CurrencyTable(Composite parent, int style, int viewerStyle) {
		super(parent, style, true, viewerStyle);
		getTable().setHeaderVisible(false);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.ui.currency.CurrencyTable.column.currency")); //$NON-NLS-1$
		table.setLayout(new WeightedTableLayout(new int[] {1}));
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new CurrencyLabelProvider());
		tableViewer.setContentProvider(new TableContentProvider());
	}

}
