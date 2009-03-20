package org.nightlabs.jfire.trade.admin.ui.layout;

import java.text.DateFormat;
import java.util.Collection;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.trade.ILayout;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

public class LayoutTable<L extends ILayout> extends AbstractTableComposite<L> {
	
	private static final String LOADING_MESSAGE = Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.LayoutTable.loadingMessage"); //$NON-NLS-1$

	public LayoutTable(Composite parent, int style) {
		super(parent, style, true, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.LayoutTable.table.column.fileName")); //$NON-NLS-1$
		new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.layout.LayoutTable.table.column.timeStamp")); //$NON-NLS-1$
		
		table.setLayout(new WeightedTableLayout(new int[] {1,1}));
		table.setHeaderVisible(true);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider() {
			@Override
			public String getColumnText(Object obj, int col) {
				if (obj == LOADING_MESSAGE) {
					if (col == 0)
						return LOADING_MESSAGE;

					return ""; //$NON-NLS-1$
				}

				ILayout layout = (ILayout) obj;
				if (col == 0)
					return layout.getFileName();
				else if (col == 1)
					return DateFormat.getDateTimeInstance().format(layout.getFileTimestamp());
				else
					return ""; //$NON-NLS-1$
			}
		});
	}
	
	public void displayLoadingMessage() {
		setInput(new String[] {LOADING_MESSAGE});
	}
	
	public void setTicketLayouts(Collection<L> layouts) {
		setInput(layouts);
	}
}
