package org.nightlabs.jfire.reporting.admin.parameter.ui.action;

import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.jfire.reporting.admin.parameter.ui.ReportParameterEditor;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;
import org.nightlabs.jfire.reporting.admin.parameter.ui.util.ObjectIDProvider;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderProvider;
import org.nightlabs.jfire.reporting.parameter.dao.ValueProviderDAO;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * Creates a default layout for the page-index of the included {@link ValueProviderConfig}s
 * and relayouts the view.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class AutoLayoutPagesAction 
extends AutoLayoutAction 
{
	public static final String ID = AutoLayoutPagesAction.class.getName();
		
	public AutoLayoutPagesAction(ReportParameterEditor part) {
		super(part);
		setId(AutoLayoutPagesAction.ID);
		setText(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.action.AutoLayoutPagesAction.label")); //$NON-NLS-1$
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	private ValueProviderProvider valueProviderProvider = new ValueProviderProvider() {
		public ValueProvider getValueProvider(ValueProviderConfig valueProviderConfig) {
			ValueProviderID providerID = valueProviderConfig.getConfigValueProviderID();
			return ValueProviderDAO.sharedInstance().getValueProvider(
					providerID, 
					new String[] {FetchPlan.DEFAULT, ValueProvider.FETCH_GROUP_INPUT_PARAMETERS},
					new NullProgressMonitor()
				);
		}
	};
	
	@Override
	public void run() 
	{
		getReportParameterEditor().getValueAcquisitionSetup().createAcquisitionSequence(valueProviderProvider);
		getReportParameterEditor().getValueAcquisitionSetup().clearBindingIndexes();
		Set<ValueProviderConfig> configs = getReportParameterEditor().getValueAcquisitionSetup().getValueProviderConfigs();
		for (ValueProviderConfig config : configs) {
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(config), ModelNotificationManager.PROP_PAGE_INDEX, -1, config.getPageIndex()
				);
		}
		super.run();
	}
}
