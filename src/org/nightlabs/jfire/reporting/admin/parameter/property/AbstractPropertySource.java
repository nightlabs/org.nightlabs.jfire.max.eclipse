package org.nightlabs.jfire.reporting.admin.parameter.property;

import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.property.IntPropertyDescriptor;
import org.nightlabs.base.ui.property.XTextPropertyDescriptor;
import org.nightlabs.jfire.reporting.admin.parameter.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.resource.Messages;
import org.nightlabs.jfire.reporting.parameter.config.IGraphicalInfoProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractPropertySource 
implements IPropertySource 
{
	public static final String CATEGORY_GEOM = Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.property.AbstractPropertySource.propertyDescriptorCategoryGeometry.name"); //$NON-NLS-1$
	public static final String CATEGORY_CONSUMER_KEY = Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.property.AbstractPropertySource.propertyDescriptorCategoryConsumerKey.name"); //$NON-NLS-1$
	public static final String CATEGORY_NAME = Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.property.AbstractPropertySource.propertyDescriptorCategoryName.name"); //$NON-NLS-1$
	public static final String CATEGORY_PARAMETER = Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.property.AbstractPropertySource.propertyDescriptorCategoryParameter.name"); //$NON-NLS-1$
	
	public boolean isPropertySet(Object arg0) {
		return false;
	}

	public void resetPropertyValue(Object arg0) {

	}

	protected PropertyDescriptor createXPD(boolean readOnly) 
	{
		PropertyDescriptor desc = new IntPropertyDescriptor(
				IGraphicalInfoProvider.PROP_X,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.property.AbstractPropertySource.propertyDescriptorX.name"), //$NON-NLS-1$
				readOnly);
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}
	
	protected PropertyDescriptor createYPD(boolean readOnly) 
	{
		PropertyDescriptor desc = new IntPropertyDescriptor(
				IGraphicalInfoProvider.PROP_Y,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.property.AbstractPropertySource.propertyDescriptorY.name"), //$NON-NLS-1$
				readOnly);
		desc.setCategory(CATEGORY_GEOM);
		return desc;
	}	
	
	protected PropertyDescriptor createConsumerKeyPD(boolean readOnly)
	{
		PropertyDescriptor desc = new XTextPropertyDescriptor(
				ModelNotificationManager.PROP_CONSUMER_KEY,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.property.AbstractPropertySource.propertyDescriptorConsumerKey.name"), //$NON-NLS-1$
				readOnly);
		desc.setCategory(CATEGORY_CONSUMER_KEY);
		return desc;
	}
	
	protected PropertyDescriptor createParameterIDPD(String suffix) 
	{
		PropertyDescriptor pd = new XTextPropertyDescriptor(
				ModelNotificationManager.PROP_PARAMETER_ID + suffix,
				String.format(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.property.AbstractPropertySource.propertyDescriptorParameterID.name"),  //$NON-NLS-1$
						suffix), 
				true);
		pd.setCategory(CATEGORY_PARAMETER);
		return pd;
	}

	protected PropertyDescriptor createParameterTypePD(String suffix, boolean readOnly) 
	{
		PropertyDescriptor pd = new XTextPropertyDescriptor(
				ModelNotificationManager.PROP_PARAMETER_TYPE + suffix,
				String.format(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.property.AbstractPropertySource.propertyDescriptorParameterType.name"),  //$NON-NLS-1$
						suffix), 
				readOnly);
		pd.setCategory(CATEGORY_PARAMETER);
		return pd;
	}
}
