package org.nightlabs.jfire.reporting.ui.parameter;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionUseCase;
import org.nightlabs.jfire.reporting.ui.resource.Messages;

/**
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportParameterAcquisitionUseCaseTable extends AbstractTableComposite<ReportParameterAcquisitionUseCase> {

	private class LabelProvider extends TableLabelProvider {
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ReportParameterAcquisitionUseCase)
				return ((ReportParameterAcquisitionUseCase)element).getName().getText();
			return ""; //$NON-NLS-1$
		}
	}
	
	public ReportParameterAcquisitionUseCaseTable(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText(Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.ReportParameterAcquisitionUseCaseTable.useCaseColumn.text")); //$NON-NLS-1$
		col.setToolTipText(Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.ReportParameterAcquisitionUseCaseTable.useCaseColumn.toolTipText")); //$NON-NLS-1$
//		col.setL
		TableLayout l = new TableLayout();
		l.addColumnData(new ColumnWeightData(1));
		table.setLayout(l);
		
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}
	
	public void setReportParameterAcquisitionSetup(ReportParameterAcquisitionSetup reportParameterAcquisitionSetup) {
		setInput(reportParameterAcquisitionSetup.getValueAcquisitionSetups().keySet());
	}

}
