/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.layout.scheduled;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectController;
import org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectTableComposite;
import org.nightlabs.jfire.reporting.scheduled.ScheduledReport;
import org.nightlabs.jfire.reporting.scheduled.id.ScheduledReportID;

/**
 * Active table showing {@link ScheduledReport}s of the current user.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ScheduledReportsTable extends ActiveJDOObjectTableComposite<ScheduledReportID, ScheduledReport> {

	class LabelProvider extends TableLabelProvider {
		@Override
		public String getColumnText(Object elment, int columnIdx) {
			if (elment instanceof ScheduledReport) {
				return ((ScheduledReport) elment).getName().getText();
			}
			return "";
		}
	}

	public ScheduledReportsTable(Composite parent, int style) {
		super(parent, style);
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectTableComposite#createActiveJDOObjectController()
	 */
	@Override
	protected ActiveJDOObjectController<ScheduledReportID, ScheduledReport> createActiveJDOObjectController() {
		return new ActiveScheduledReportsJDOObjectController();
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.jdo.ActiveJDOObjectTableComposite#createLabelProvider()
	 */
	@Override
	protected ITableLabelProvider createLabelProvider() {
		return new LabelProvider();
	}

	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		new TableColumn(table, SWT.LEFT).setText("Scheduled report");
		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(1));
		table.setLayout(tableLayout);
	}

}
