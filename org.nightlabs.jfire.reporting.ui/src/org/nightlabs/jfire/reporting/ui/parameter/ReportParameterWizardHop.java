/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import org.nightlabs.base.ui.wizard.WizardHop;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.parameter.config.ReportParameterAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.dao.ReportParameterAcquisitionSetupDAO;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.jfire.reporting.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportParameterWizardHop extends WizardHop implements IReportParameterController {

	private Map<String, Object> parameters = new HashMap<String, Object>();
	private Map<ValueProviderID, Object> providerValues = new HashMap<ValueProviderID, Object>();
	private Map<String, Object> initialValues;
	
	public ReportParameterWizardHop(ReportRegistryItemID reportLayoutID, boolean isScheduledReport, Map<String, Object> initialValues) {
		ReportParameterAcquisitionSetup setup = null;
		this.initialValues = initialValues;
		try {
			setup = ReportParameterAcquisitionSetupDAO.sharedInstance().getSetupForReportLayout(
					reportLayoutID, ReportParameterAcquisitionSetupDAO.DEFAULT_FETCH_GROUPS,
					new NullProgressMonitor()
				);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (setup == null)
			return;
		
		if (setup.getValueAcquisitionSetups().size() > 1) {
			ReportParameterAcquisitionUseCaseWizardPage wizardPage = new ReportParameterAcquisitionUseCaseWizardPage(
					Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.ReportParameterWizard.wizardPage.pageName"), //$NON-NLS-1$ 
					reportLayoutID, isScheduledReport);
			setEntryPage(wizardPage);
			wizardPage.getReportParameterWizardHop();
		} else if (setup.getValueAcquisitionSetups().size() == 1){
			ValueAcquisitionSetup acquisitionSetup = setup.getValueAcquisitionSetups().values().iterator().next();
			ReportParameterWizardHop.populateValueProviderSetupPages(acquisitionSetup, this, isScheduledReport, true);
		}
	}
	
	public void setParameterValue(String parameterID, Object value) {
		parameters.put(parameterID, value);
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	
	public Map<ValueProviderID, Object> getProviderValues() {
		return providerValues;
	}

	public static void populateValueProviderSetupPages(
			ValueAcquisitionSetup valueAcquisitionSetup, ReportParameterWizardHop wizardHop,
			boolean isScheduledReport, boolean populateAlsoEntryPage
		)
	{
		wizardHop.removeAllHopPages();
		if (valueAcquisitionSetup == null)
			return;
		SortedMap<Integer, SortedMap<Integer, SortedSet<ValueProviderConfig>>> sortedConfigs = valueAcquisitionSetup.getSortedValueProviderConfigs();
		
		
		int i = 0;
		for (SortedMap<Integer, SortedSet<ValueProviderConfig>> providerPageConfigs : sortedConfigs.values()) {
			ReportParameterValueProviderWizardPage page = new ReportParameterValueProviderWizardPage(
					Messages.getString("org.nightlabs.jfire.reporting.ui.parameter.eportParameterAcquisitionUseCaseWizardPage.pagePrefix")+(++i),  //$NON-NLS-1$
					valueAcquisitionSetup,
					providerPageConfigs,
					wizardHop, 
					isScheduledReport
				);
			if (populateAlsoEntryPage) {
				if (wizardHop.getEntryPage() == null)
					wizardHop.setEntryPage(page);
				else
					wizardHop.addHopPage(page);
			}
			else
				wizardHop.addHopPage(page);
		}
	}

	public boolean hasAcquisitionSetup() {
		return getEntryPage() != null;
	}

	@Override
	public Object getInitialValue(String parameterID) {
		if (initialValues != null)
			return initialValues.get(parameterID);
		return null;	
	}
}
