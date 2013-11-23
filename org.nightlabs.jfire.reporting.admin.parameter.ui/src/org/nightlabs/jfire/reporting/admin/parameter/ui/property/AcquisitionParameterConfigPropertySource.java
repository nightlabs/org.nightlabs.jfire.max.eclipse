package org.nightlabs.jfire.reporting.admin.parameter.ui.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.ui.util.ObjectIDProvider;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.IGraphicalInfoProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class AcquisitionParameterConfigPropertySource
extends AbstractPropertySource
{

	public AcquisitionParameterConfigPropertySource(AcquisitionParameterConfig acquisitionParameterConfig) {
		super();
		this.acquisitionParameterConfig = acquisitionParameterConfig;
	}

	private AcquisitionParameterConfig acquisitionParameterConfig;
	
	public Object getEditableValue() {
		return acquisitionParameterConfig;
	}

	protected IGraphicalInfoProvider getGraphicalInfoProvider() {
		return acquisitionParameterConfig;
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[4];
		propertyDescriptors[0] = createXPD(true);
		propertyDescriptors[1] = createYPD(true);
		propertyDescriptors[2] = createParameterIDPD(""); //$NON-NLS-1$
		propertyDescriptors[3] = createParameterTypePD("", false); //$NON-NLS-1$
//		propertyDescriptors[4] = createConsumerKeyPD(true);
		return propertyDescriptors;
	}
	
	public Object getPropertyValue(Object id)
	{
		if (id.equals(IGraphicalInfoProvider.PROP_X)) {
			return getGraphicalInfoProvider().getX();
		}
		else if (id.equals(IGraphicalInfoProvider.PROP_Y)) {
			return getGraphicalInfoProvider().getY();
		}
//		else if (id.equals(ModelNotificationManager.PROP_CONSUMER_KEY)) {
//			return acquisitionParameterConfig.getConsumerKey();
//		}
		else if (id.equals(ModelNotificationManager.PROP_PARAMETER_ID)) {
			return acquisitionParameterConfig.getParameterID();
		}
		else if (id.equals(ModelNotificationManager.PROP_PARAMETER_TYPE)) {
			return acquisitionParameterConfig.getParameterType();
		}
		return null;
	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(IGraphicalInfoProvider.PROP_X)) {
			int x = ((Integer)value).intValue();
			getGraphicalInfoProvider().setX(x);
			return;
		}
		else if (id.equals(IGraphicalInfoProvider.PROP_Y)) {
			int y = ((Integer)value).intValue();
			getGraphicalInfoProvider().setY(y);
			return;
		}
		else if (id.equals(ModelNotificationManager.PROP_PARAMETER_TYPE)) {
			String oldVal = acquisitionParameterConfig.getParameterType();
			acquisitionParameterConfig.setParameterType((String)value);
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(acquisitionParameterConfig),
					ModelNotificationManager.PROP_PARAMETER_TYPE,
					oldVal,
					value
				);
			return;
		}
	}

}
