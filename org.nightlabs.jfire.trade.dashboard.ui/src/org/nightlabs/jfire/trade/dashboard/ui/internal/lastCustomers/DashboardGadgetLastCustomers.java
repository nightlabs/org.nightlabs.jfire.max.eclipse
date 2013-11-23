package org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.GlobalJFireEjb3Provider;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadget;
import org.nightlabs.jfire.base.dashboard.ui.action.DashboardTableActionManager;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.trade.dashboard.DashboardGadgetLastCustomersConfig;
import org.nightlabs.jfire.trade.dashboard.DashboardManagerRemote;
import org.nightlabs.jfire.trade.dashboard.LastCustomerTransaction;
import org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.action.SelectCustomerAction;
import org.nightlabs.jfire.trade.dashboard.ui.resource.Messages;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * "My Last Customers" dashboard gadget. Configuration of this gadget is performed via 
 * {@link DashboardGadgetLastCustomersConfigPage}. 
 * @author abieber
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class DashboardGadgetLastCustomers extends AbstractDashboardGadget {

	TransactionInfoTable transactionInfoTable;
	private DashboardTableActionManager<TransactionInfoTableItem> actionManager;
	
	@Override
	public Composite createControl(Composite parent) {
		XComposite wrapper = createDefaultWrapper(parent);
		transactionInfoTable = new TransactionInfoTable(wrapper, SWT.NONE);
		actionManager = new DashboardTableActionManager<TransactionInfoTableItem>(transactionInfoTable);
		createActions();
		return wrapper;
	}

	@Override
	public void refresh() {
		Job loadCustomersJob = new Job(Messages.getString(
			"org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.DashboardGadgetLastCustomers.loadCustomers.job.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) {
				monitor.beginTask(Messages.getString(
					"org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.DashboardGadgetLastCustomers.loadCustomers.task.name"), 100); //$NON-NLS-1$
				try {
					if (!transactionInfoTable.isDisposed()) {
						transactionInfoTable.getDisplay().syncExec(new Runnable() {
							public void run() {
								transactionInfoTable.setLoadingMessage(Messages.getString(
									"org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers.DashboardGadgetLastCustomers.loadCustomers.message")); //$NON-NLS-1$
							}
						});
					}
					DashboardManagerRemote dashboardManager = GlobalJFireEjb3Provider.sharedInstance().getRemoteBean(DashboardManagerRemote.class);
					List<LastCustomerTransaction> lastCustomers = dashboardManager.searchLastCustomerTransactions(
						(DashboardGadgetLastCustomersConfig) getGadgetContainer().getLayoutEntry().getConfig());
					monitor.worked(50);
					
					Map<AnchorID, LegalEntity> anchorIDToLegalEntity = new HashMap<AnchorID, LegalEntity>();
					for (LastCustomerTransaction trans : lastCustomers)
						anchorIDToLegalEntity.put(trans.getCustomerID(), null);
					
					Collection<LegalEntity> legalEntities = LegalEntityDAO.sharedInstance().getLegalEntities(
						anchorIDToLegalEntity.keySet(), 
						new String[] {FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_PERSON, PropertySet.FETCH_GROUP_FULL_DATA}, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
						new SubProgressMonitor(monitor, 50)
					);
	
					for (LegalEntity legalEntity : legalEntities) {
						anchorIDToLegalEntity.put((AnchorID) JDOHelper.getObjectId(legalEntity), legalEntity);
					}
					
					final List<TransactionInfoTableItem> customerTransactions = new LinkedList<TransactionInfoTableItem>();
					
					for (LastCustomerTransaction lastCustomerTransaction : lastCustomers) {
						TransactionInfoTableItem customerTransaction = new TransactionInfoTableItem();
						customerTransaction.setTransactionInfo(lastCustomerTransaction);
						customerTransaction.setLegalEntityName(getLegalEntityName(anchorIDToLegalEntity.get(lastCustomerTransaction.getCustomerID())));
						customerTransactions.add(customerTransaction);
					}
					
					if (!transactionInfoTable.isDisposed()) {
						transactionInfoTable.getDisplay().syncExec(new Runnable() {
							public void run() {
								if (!transactionInfoTable.isDisposed()) {
									transactionInfoTable.setInput(customerTransactions);
								}
							}
						});
					}
					
				} finally {
					monitor.done();
				}
				
				return Status.OK_STATUS;
			}

			private String getLegalEntityName(LegalEntity legalEntity) {
				String displayName = legalEntity.getPerson().getDisplayName();
				if (displayName == null || displayName.equals("")) { //$NON-NLS-1$
					IStruct structure = legalEntity.getPerson().getStructure();
					displayName = structure.createDisplayName(legalEntity.getPerson());
				}
				return displayName;
			}
		};
			
		loadCustomersJob.schedule();	
	}
	
	private void createActions() {
		SelectCustomerAction selectCustomerAction = new SelectCustomerAction();
		actionManager.addAction(selectCustomerAction);
	}	
}
