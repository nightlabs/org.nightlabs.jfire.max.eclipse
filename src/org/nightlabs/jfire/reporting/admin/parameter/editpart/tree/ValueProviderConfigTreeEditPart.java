package org.nightlabs.jfire.reporting.admin.parameter.editpart.tree;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.reporting.admin.parameter.ReportingAdminParameterPlugin;
import org.nightlabs.jfire.reporting.admin.parameter.editpart.ValueProviderConfigEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.property.ValueProviderConfigPropertySource;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.dao.ValueProviderDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueProviderConfigTreeEditPart 
extends AbstractReportTreeEditPart 
{

	/**
	 * @param model
	 */
	public ValueProviderConfigTreeEditPart(ValueProviderConfig model) {
		super(model);
	}

	public ValueProviderConfig getValueProviderConfig() {
		return (ValueProviderConfig) getModel();
	}
	
	@Override
	protected IPropertySource createPropertySource() {
		return new ValueProviderConfigPropertySource(getValueProviderConfig());
	}

	public static final Image IMAGE = SharedImages.getSharedImage(
			ReportingAdminParameterPlugin.getDefault(),
			ValueProviderConfigTreeEditPart.class);
	
	@Override
	protected Image getImage() {
		return IMAGE;
	}

	@Override
	protected String getText() {
//		return getValueProviderConfig().getConsumerKey();
		return getValueProvider().getName().getText();
	}
		
	protected ValueProvider getValueProvider() 
	{
		return ValueProviderDAO.sharedInstance().getValueProvider(
				getValueProviderConfig().getConfigValueProviderID(),
				ValueProviderConfigEditPart.FETCH_GROUPS,
				new NullProgressMonitor());
	}
}
