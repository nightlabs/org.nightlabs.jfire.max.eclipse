/**
 * 
 */
package org.nightlabs.jfire.reporting.trade.ui.accounting.currency;

import java.util.List;

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
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.dao.CurrencyDAO;
import org.nightlabs.jfire.accounting.id.CurrencyID;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.trade.ReportingTradeConstants;
import org.nightlabs.jfire.reporting.ui.parameter.AbstractValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUI;
import org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory;
import org.nightlabs.jfire.trade.ui.currency.CurrencyTable;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ValueProviderGUICurrency
extends AbstractValueProviderGUI<CurrencyID>
{
	public static class Factory implements IValueProviderGUIFactory {
		
		/* (non-Javadoc)
		 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory#createValueProviderGUI()
		 */
		public IValueProviderGUI<CurrencyID> createValueProviderGUI(ValueProviderConfig valueProviderConfig, boolean isScheduledReportParameterConfig) {
			return new ValueProviderGUICurrency(valueProviderConfig);
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.jfire.reporting.ui.parameter.IValueProviderGUIFactory#getValueProviderID()
		 */
		public ValueProviderID getValueProviderID() {
			return ReportingTradeConstants.VALUE_PROVIDER_ID_ACCOUNTING_MODE_OF_PAYMENT_FLAVOUR;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
		 */
		public void setInitializationData(IConfigurationElement arg0, String arg1,
				Object arg2) throws CoreException {
		}
	}
	
	private CurrencyTable currencyTable;
	
	public ValueProviderGUICurrency(ValueProviderConfig valueProviderConfig) {
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
		
		currencyTable = new CurrencyTable(group);
		currencyTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				notifyOutputChanged();
			}
		});
		
		currencyTable.setLoadingMessage("Loading...");
		Job job = new Job("Loading currencies") {
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception {
				final List<Currency> currencies = CurrencyDAO.sharedInstance().getCurrencies(monitor);
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						currencyTable.setInput(currencies);
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
	public CurrencyID getOutputValue() {
		Currency currency = currencyTable.getFirstSelectedElement();
		if (currency != null)
			return (CurrencyID) JDOHelper.getObjectId(currency);
		return null;
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
