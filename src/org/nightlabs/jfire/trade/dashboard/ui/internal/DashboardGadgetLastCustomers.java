package org.nightlabs.jfire.trade.dashboard.ui.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

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
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.GlobalJFireEjb3Provider;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadget;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.dashboard.DashboardGadgetLastCustomersConfig;
import org.nightlabs.jfire.trade.dashboard.DashboardManagerRemote;
import org.nightlabs.jfire.trade.dashboard.LastCustomerTransaction;
import org.nightlabs.jfire.trade.dashboard.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * 
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class DashboardGadgetLastCustomers extends AbstractDashboardGadget {

	TransactionInfoTable transactionInfoTable;
	
	static class CustomerTransaction {
		public String legalEntityName;
		public LastCustomerTransaction transactionInfo;
	}
	
	
	static class TransactionInfoTable extends AbstractTableComposite<CustomerTransaction> {

		public TransactionInfoTable(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		protected void createTableColumns(final TableViewer tableViewer, final Table table) {
			TableColumn col1 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
			col1.setText("Business partner");
			TableColumn col2 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
			col2.setText("Last transaction");
			final TableLayout tableLayout = new TableLayout();
			tableLayout.addColumnData(new ColumnWeightData(100));
			tableLayout.addColumnData(new ColumnWeightData(50));
			table.getDisplay().asyncExec(new Runnable() {
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
						return ((CustomerTransaction) element).legalEntityName;
					if (columnIndex == 1) {
						String type = ((CustomerTransaction) element).transactionInfo.getTransactionType();
						return Messages.getString(
							"org.nightlabs.jfire.trade.dashboard.ui.internal.DashboardGadgetLastCustomers.TransactionInfoTable." + type);
					}
					return "";
				}
			});
		}
		
	}
	
	@Override
	public Composite createControl(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);

		transactionInfoTable = new TransactionInfoTable(wrapper, SWT.NONE);
		
		configureTableViewer();

		return wrapper;
	}

	private void configureTableViewer() {
	}

	@Override
	public void refresh() {
		Job loadCustomersJob = new Job("Load Customers Job") {
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				monitor.beginTask("Retrieving customers", 100);
				try {
					transactionInfoTable.getDisplay().syncExec(new Runnable() {
						public void run() {
							transactionInfoTable.setLoadingMessage("Loading...");
						}
					});
					DashboardManagerRemote dashboardManager = GlobalJFireEjb3Provider.sharedInstance().getRemoteBean(DashboardManagerRemote.class);
					List<LastCustomerTransaction> lastCustomers = dashboardManager.searchLastCustomerTransactions(
						(DashboardGadgetLastCustomersConfig) getGadgetContainer().getLayoutEntry().getConfig());
					monitor.worked(50);
					
					Map<AnchorID, LegalEntity> anchorIDToLegalEntity = new HashMap<AnchorID, LegalEntity>();
					for (LastCustomerTransaction trans : lastCustomers)
						anchorIDToLegalEntity.put(trans.getCustomerID(), null);
					
					Collection<LegalEntity> legalEntities = LegalEntityDAO.sharedInstance().getLegalEntities(
						anchorIDToLegalEntity.keySet(), 
						new String[] {FetchPlan.ALL, "TODO"}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new SubProgressMonitor(monitor, 50)
					);
	
					for (LegalEntity legalEntity : legalEntities) {
						anchorIDToLegalEntity.put((AnchorID) JDOHelper.getObjectId(legalEntity), legalEntity);
					}
					
					final List<CustomerTransaction> customerTransactions = new LinkedList<CustomerTransaction>();
					
					for (LastCustomerTransaction lastCustomerTransaction : lastCustomers) {
						CustomerTransaction customerTransaction = new CustomerTransaction();
						customerTransaction.transactionInfo = lastCustomerTransaction;
						customerTransaction.legalEntityName = getLegalEntityName(anchorIDToLegalEntity.get(lastCustomerTransaction.getCustomerID()));
						customerTransactions.add(customerTransaction);
					}
					
					transactionInfoTable.getDisplay().syncExec(new Runnable() {
						public void run() {
							transactionInfoTable.setInput(customerTransactions);
						}
					});
					
				} finally {
					monitor.done();
				}
				
				return Status.OK_STATUS;
			}

			private String getLegalEntityName(LegalEntity legalEntity) {
				// TODO
				// Look for displayName, or Name and FirstName or Company....
				
				String displayName = legalEntity.getPerson().getDisplayName();

				if (displayName == null || displayName.equals("")) {
					IStruct structure = legalEntity.getPerson().getStructure();
					
					
					
				}
				
				
				
				
				
				
				
				
				
				
				
				return displayName;
			}
		};
			
		loadCustomersJob.setUser(true);
		loadCustomersJob.schedule();	
	}
}
