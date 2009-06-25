/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.accounting.modeofpayment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.dao.ModeOfPaymentDAO;
import org.nightlabs.jfire.accounting.pay.ModeOfPayment;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavour;
import org.nightlabs.jfire.accounting.pay.id.ModeOfPaymentFlavourID;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.trade.ui.resource.Messages;
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.trade.ui.modeofpayment.ModeOfPaymentTable;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUIModeOfPaymentFlavoursByModeOfPayment
extends AbstractValueProviderGUI<Collection<ModeOfPaymentFlavourID>>
{
	
	public static class Factory implements IValueProviderGUIFactory {
		
		/* (non-Javadoc)
		 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory#createValueProviderGUI()
		 */
		public IValueProviderGUI<Collection<ModeOfPaymentFlavourID>> createValueProviderGUI(ValueProviderConfig valueProviderConfig, boolean isScheduledReportParameterConfig) {
			return new ValueProviderGUIModeOfPaymentFlavoursByModeOfPayment(valueProviderConfig);
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory#getValueProviderID()
		 */
		public ValueProviderID getValueProviderID() {
			return ReportingTradeConstants.VALUE_PROVIDER_ID_ACCOUNTING_MODE_OF_PAYMENT_FLAVOURS_BY_MODE_OF_PAYMENT;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
		 */
		public void setInitializationData(IConfigurationElement arg0, String arg1,
				Object arg2) throws CoreException {
		}
	}
	
	private ModeOfPaymentTable modeOfPaymentTable;
	
	public ValueProviderGUIModeOfPaymentFlavoursByModeOfPayment(ValueProviderConfig valueProviderConfig) {
		super(valueProviderConfig);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#createGUI(org.eclipse.swt.widgets.Composite)
	 */
	public Control createGUI(Composite wrapper) {
		Group group = new Group(wrapper, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setLayout(new GridLayout());
		group.setText(getValueProviderConfig().getMessage().getText());
		
		modeOfPaymentTable = new ModeOfPaymentTable(group);
		modeOfPaymentTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				notifyOutputChanged();
			}
		});
		
		modeOfPaymentTable.setLoadingMessage(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.accounting.modeofpayment.ValueProviderGUIModeOfPaymentFlavoursByModeOfPayment.loadingMessage")); //$NON-NLS-1$
		Job job = new Job(Messages.getString("org.nightlabs.jfire.reporting.trade.ui.accounting.modeofpayment.ValueProviderGUIModeOfPaymentFlavoursByModeOfPayment.jobName")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final List<ModeOfPayment> mops = ModeOfPaymentDAO.sharedInstance().getModeOfPayments(
						new String[] {
								FetchPlan.DEFAULT, ModeOfPayment.FETCH_GROUP_NAME, ModeOfPayment.FETCH_GROUP_FLAVOURS
						}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						modeOfPaymentTable.setInput(mops);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.schedule();
		return group;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#getOutputValue()
	 */
	public Collection<ModeOfPaymentFlavourID> getOutputValue() {
		Collection<ModeOfPayment> mops = modeOfPaymentTable.getSelectedElements();
		if (mops.size() == 0)
			return null;
		List<ModeOfPaymentFlavourID> mopfIDs = new ArrayList<ModeOfPaymentFlavourID>();
		for (ModeOfPayment modeOfPayment : mops) {
			for (ModeOfPaymentFlavour mopf : modeOfPayment.getFlavours()) {
				mopfIDs.add((ModeOfPaymentFlavourID) JDOHelper.getObjectId(mopf));
			}
		}
		return mopfIDs;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#isAcquisitionComplete()
	 */
	public boolean isAcquisitionComplete() {
		return getOutputValue() != null || getValueProviderConfig().isAllowNullOutputValue();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI#setInputParameterValue(java.lang.String, java.lang.Object)
	 */
	public void setInputParameterValue(String parameterID, final Object value) {
	}

}
