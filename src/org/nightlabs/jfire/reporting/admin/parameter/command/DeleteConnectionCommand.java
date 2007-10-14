package org.nightlabs.jfire.reporting.admin.parameter.command;

import org.eclipse.gef.commands.Command;
import org.nightlabs.jfire.reporting.admin.parameter.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.resource.Messages;
import org.nightlabs.jfire.reporting.admin.parameter.util.ObjectIDProvider;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DeleteConnectionCommand 
extends Command 
{
	public DeleteConnectionCommand(ValueConsumerBinding binding, ValueAcquisitionSetup setup) 
	{		
		super();
		if (binding == null)
			throw new IllegalArgumentException("Param binding must NOT be null!"); //$NON-NLS-1$

		setLabel(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.command.DeleteConnectionCommand.label")); //$NON-NLS-1$
		this.valueConsumerBinding = binding;
		this.setup = setup;
	}

	private ValueConsumerBinding valueConsumerBinding;
	private ValueAcquisitionSetup setup;

	@Override
	public void execute() {
//		valueConsumerBinding.getSetup().getValueConsumerBindings().remove(valueConsumerBinding);		
		setup.getValueConsumerBindings().remove(valueConsumerBinding);
		notifyEditParts();
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() {
//		valueConsumerBinding.getSetup().getValueConsumerBindings().add(valueConsumerBinding);
		setup.getValueConsumerBindings().add(valueConsumerBinding);
		notifyEditParts();
	}

	protected void notifyEditParts() 
	{
		ModelNotificationManager.sharedInstance().notify(
				ObjectIDProvider.getObjectID(setup), 
				ModelNotificationManager.PROP_CREATE_CONNECTION, 
				null,
				valueConsumerBinding);
		
		ModelNotificationManager.sharedInstance().notify(
			ObjectIDProvider.getObjectID(valueConsumerBinding), 
			ModelNotificationManager.PROP_CREATE_CONNECTION, 
			null,
			valueConsumerBinding);
		ModelNotificationManager.sharedInstance().notify(
				ObjectIDProvider.getObjectID(valueConsumerBinding.getConsumer()), 
				ModelNotificationManager.PROP_DELETE_CONNECTION, 
				null, 
				valueConsumerBinding);
		ModelNotificationManager.sharedInstance().notify(
				ObjectIDProvider.getObjectID(valueConsumerBinding.getProvider()), 
				ModelNotificationManager.PROP_DELETE_CONNECTION, 
				null, 
				valueConsumerBinding);				
	}
}
