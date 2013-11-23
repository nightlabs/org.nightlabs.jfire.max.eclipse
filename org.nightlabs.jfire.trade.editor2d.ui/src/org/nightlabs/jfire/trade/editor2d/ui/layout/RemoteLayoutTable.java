package org.nightlabs.jfire.trade.editor2d.ui.layout;

import java.text.DateFormat;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectTableComposite;
import org.nightlabs.jfire.trade.editor2d.ILayout;
import org.nightlabs.jfire.trade.editor2d.ui.resource.Messages;

public class RemoteLayoutTable<ID, L extends ILayout>
extends ActiveJDOObjectTableComposite<ID, L>
{
	private static final Logger logger = Logger.getLogger(RemoteLayoutTable.class);
	private static final String LOADING_MESSAGE = Messages.getString("org.nightlabs.jfire.trade.editor2d.ui.layout.RemoteLayoutTable.loadingMessage"); //$NON-NLS-1$

	private ActiveJDOObjectController<ID, L> controller;

	public RemoteLayoutTable(Composite parent, int style, ActiveJDOObjectController<ID, L> controller) {
		super(parent, style, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);

		this.controller = controller;
	}

	public void displayLoadingMessage() {
		setInput(new String[] {LOADING_MESSAGE});
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, final Table table)
	{
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.editor2d.ui.layout.RemoteLayoutTable.fileNameColumnText")); //$NON-NLS-1$
				new TableColumn(table, SWT.LEFT).setText(Messages.getString("org.nightlabs.jfire.trade.editor2d.ui.layout.RemoteLayoutTable.timeStampColumnText")); //$NON-NLS-1$

				TableLayout tl = new TableLayout();
				tl.addColumnData(new ColumnWeightData(1, true));
				tl.addColumnData(new ColumnWeightData(1, true));
				table.setLayout(tl);

				table.setHeaderVisible(true);
			}
		});
	}

	private static final ITableLabelProvider LABEL_PROVIDER = new TableLabelProvider() {
		@Override
		public String getColumnText(Object obj, int col) {
			if (obj == LOADING_MESSAGE) {
				if (col == 0)
					return LOADING_MESSAGE;

				return ""; //$NON-NLS-1$
			}

			if (obj instanceof ILayout) {
				ILayout layout = (ILayout) obj;
				if (col == 0)
					return layout.getFileName();
				else if (col == 1)
					return DateFormat.getDateTimeInstance().format(layout.getFileTimestamp());
			}
			else {
				if (logger.isDebugEnabled()) {
					logger.debug("obj.getClass().getClassLoader() = "+obj.getClass().getClassLoader());
					logger.debug("ILayout.class.getClassLoader() = "+ILayout.class.getClassLoader());
					logger.debug("obj instanceof ILayout = "+(obj instanceof ILayout));
					logger.debug("ILayout.class.isAssignableFrom(obj.getClass()) = "+ILayout.class.isAssignableFrom(obj.getClass()));
				}
			}
			return ""; //$NON-NLS-1$
		}
	};

	@Override
	protected ITableLabelProvider createLabelProvider() {
		return LABEL_PROVIDER;
	}

	@Override
	protected ActiveJDOObjectController<ID, L> createActiveJDOObjectController() {
		return controller;
	}
}
