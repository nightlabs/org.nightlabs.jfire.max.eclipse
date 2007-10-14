package org.nightlabs.jfire.reporting.admin.parameter.ui.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.property.XTextPropertyDescriptor;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.ValueProviderConfigEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumer;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.dao.ValueProviderDAO;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueConsumerBindingPropertySource 
extends AbstractPropertySource 
{

	private ValueConsumerBinding binding;
	public ValueConsumerBindingPropertySource(ValueConsumerBinding binding) {
		this.binding = binding;
	}

	public Object getEditableValue() {
		return binding;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() 
	{
		IPropertyDescriptor[] pds = new IPropertyDescriptor[2];
		pds[0] = createConsumerPD();
		pds[1] = createProviderPD();
		return pds;
	}

	public Object getPropertyValue(Object id) 
	{
		if (id.equals(ModelNotificationManager.PROP_PROVIDER)) 
		{
			ValueProvider valueProvider = getValueProvider(
					binding.getProvider().getConfigValueProviderID());
			return valueProvider.getName().getText();
		}
		else if (id.equals(ModelNotificationManager.PROP_CONSUMER)) 
		{
			ValueConsumer valueConsumer = binding.getConsumer();
			if (valueConsumer instanceof ValueProviderConfig) {
				ValueProviderConfig valueProviderConfig = (ValueProviderConfig) valueConsumer;				
				ValueProvider valueProvider = getValueProvider(
						valueProviderConfig.getConfigValueProviderID());
				return valueProvider.getName().getText();
			}
			if (valueConsumer instanceof AcquisitionParameterConfig) {
				AcquisitionParameterConfig acquisitionParameterConfig = (AcquisitionParameterConfig) valueConsumer;
				return acquisitionParameterConfig.getParameterID();
			}
		}
		return null;
	}

	protected ValueProvider getValueProvider(ValueProviderID valueProviderID) 
	{
		ValueProvider valueProvider = ValueProviderDAO.sharedInstance().getValueProvider(
				valueProviderID, 
				ValueProviderConfigEditPart.FETCH_GROUPS, 
				new NullProgressMonitor());
		return valueProvider;
	}
	
	public void setPropertyValue(Object arg0, Object arg1) {

	}

	protected IPropertyDescriptor createProviderPD() 
	{
		PropertyDescriptor pd = new XTextPropertyDescriptor(
				ModelNotificationManager.PROP_PROVIDER,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueConsumerBindingPropertySource.propertyDescriptorProvider.name"), true); //$NON-NLS-1$
		return pd;
	}

	protected IPropertyDescriptor createConsumerPD() 
	{
		PropertyDescriptor pd = new XTextPropertyDescriptor(
				ModelNotificationManager.PROP_CONSUMER,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueConsumerBindingPropertySource.propertyDescriptorConsumer.name"), true); //$NON-NLS-1$
		return pd;
	}
	
}
