package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadget;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author sschefczyk
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class DashboardGadgetInvoice extends AbstractDashboardGadget {

	private InvoiceTable invoiceTable;

	static class InvoiceTableItem {
		String invoiceId;
		String invoiceDate;
		String invoiceTitle;
		//TODO incoming shows source, outgoing shows destination
	}
	
	static class InvoiceTable extends AbstractTableComposite<InvoiceTableItem> {
		
		public InvoiceTable(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		protected void createTableColumns(TableViewer tableViewer, final Table table) 
		{
			TableColumn col0 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
			col0.setText("ID");
			TableColumn col1 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
			col1.setText("Date");
			TableColumn col2 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
			col2.setText("Title");
			
			final TableLayout tableLayout = new TableLayout();
			tableLayout.addColumnData(new ColumnWeightData(10));
			tableLayout.addColumnData(new ColumnWeightData(20));
			tableLayout.addColumnData(new ColumnWeightData(70));
			table.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!table.isDisposed()) {
						table.setLayout(tableLayout);
						table.layout(true, true);
					}
				}
			});
		}

		@Override
		protected void setTableProvider(TableViewer tableViewer) {
			tableViewer.setContentProvider(new ArrayContentProvider());
			tableViewer.setLabelProvider(new TableLabelProvider() {
				
				@Override
				public String getColumnText(Object element, int columnIndex) {
					if (columnIndex == 0)
						return ((InvoiceTableItem) element).invoiceId;
					if (columnIndex == 1)
						return ((InvoiceTableItem) element).invoiceDate;
					if (columnIndex == 2)
						return ((InvoiceTableItem) element).invoiceTitle;
					return "";
				}
			});
		}
		
	}
	
	
	public DashboardGadgetInvoice() {}

	@Override
	public Composite createControl(Composite parent) {
		XComposite invoiceGadget = createDefaultWrapper(parent);
		
		invoiceTable = new InvoiceTable(invoiceGadget, SWT.NONE);
		
		return invoiceGadget;
	}
	
	@Override
	public void refresh() {
		getGadgetContainer().setTitle(getGadgetContainer().getLayoutEntry().getName());
		Job loadJob = new Job("Load invoices Job") {
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				monitor.beginTask("Retrieving invoices", 100);
				try {
					invoiceTable.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							invoiceTable.setLoadingMessage("Loading...");
							//TODO no foreground display, only background-load-display in the right bottom corner o.s..
						}
					});
					
					//TODO get the desired query
					//TODO load from bean 
					monitor.worked(50);
					
					//TODO fill into list of InvoiceTableItems
					
					final List<InvoiceTableItem> invoiceList = new LinkedList<InvoiceTableItem>();
					
					
					//TODO set input 
					invoiceTable.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							invoiceTable.setInput(invoiceList);
						}
					});
					
				} finally {
					monitor.done();
				}
				
				return Status.OK_STATUS;
			}

		};
			
		loadJob.setUser(true);
		loadJob.schedule();	
	}
	
}
