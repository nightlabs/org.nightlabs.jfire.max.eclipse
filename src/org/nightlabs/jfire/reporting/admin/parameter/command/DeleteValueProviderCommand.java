package org.nightlabs.jfire.reporting.admin.parameter.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.nightlabs.jfire.reporting.admin.parameter.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.resource.Messages;
import org.nightlabs.jfire.reporting.admin.parameter.util.ObjectIDProvider;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DeleteValueProviderCommand 
extends Command 
{
	private ValueProviderConfig valueProviderConfig;
	private ValueAcquisitionSetup setup;
	
	public DeleteValueProviderCommand(ValueProviderConfig valueProviderConfig, 
			ValueAcquisitionSetup setup) {
		super();
		setLabel(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.command.DeleteValueProviderCommand.label")); //$NON-NLS-1$
		this.valueProviderConfig = valueProviderConfig;
		this.setup = setup;
	}
	
	@Override
	public void execute() {
		setup.getValueProviderConfigs().remove(valueProviderConfig);
		deleteConnections();
		notifyEditParts();
	}

	public void redo() {
		execute();
	}

	public void undo() {
		setup.getValueProviderConfigs().add(valueProviderConfig);
		restoreConnections();
		notifyEditParts();
	}	
	
	protected void notifyEditParts() 
	{
		ModelNotificationManager.sharedInstance().notify(
				ObjectIDProvider.getObjectID(valueProviderConfig), 
				ModelNotificationManager.PROP_DELETE, 
				null, 
				valueProviderConfig);		
		ModelNotificationManager.sharedInstance().notify(
				ObjectIDProvider.getObjectID(setup), 
				ModelNotificationManager.PROP_DELETE, 
				null, 
				valueProviderConfig);
		for (ValueConsumerBinding binding : deletedConnections) {
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(binding), 
					ModelNotificationManager.PROP_DELETE, 
					null, 
					valueProviderConfig);
			if (binding != null && binding.getConsumer() != null) {
				ModelNotificationManager.sharedInstance().notify(
						ObjectIDProvider.getObjectID(binding.getConsumer()), 
						ModelNotificationManager.PROP_DELETE, 
						null, 
						valueProviderConfig);				
			}
			if (binding != null && binding.getProvider() != null) {
				ModelNotificationManager.sharedInstance().notify(
						ObjectIDProvider.getObjectID(binding.getProvider()), 
						ModelNotificationManager.PROP_DELETE, 
						null, 
						valueProviderConfig);													
			}
		}
	}
	
	private List<ValueConsumerBinding> deletedConnections = new ArrayList<ValueConsumerBinding>();
	
	protected void deleteConnections() 
	{
		ValueConsumerBinding providerBinding = setup.getValueProviderBinding(valueProviderConfig);
		setup.getValueConsumerBindings().remove(providerBinding);
		deletedConnections.add(providerBinding);		
		Map<String, ValueConsumerBinding> consumerBindings = setup.getValueConsumerBindings(valueProviderConfig);
		if (consumerBindings != null) {
			for (Map.Entry<String, ValueConsumerBinding> entry : consumerBindings.entrySet()) {
				setup.getValueConsumerBindings().remove(entry.getValue());
				deletedConnections.add(entry.getValue());
			}			
		}		
		setup.clearBindingIndexes();
	}
	
	protected void restoreConnections() 
	{
		for (ValueConsumerBinding binding : deletedConnections) {
			setup.getValueConsumerBindings().add(binding);
		}
		setup.clearBindingIndexes();		
	}
}
