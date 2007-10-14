package org.nightlabs.jfire.reporting.admin.parameter.ui.command;

import org.apache.log4j.Logger;
import org.eclipse.gef.commands.Command;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;
import org.nightlabs.jfire.reporting.admin.parameter.ui.util.ObjectIDProvider;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumer;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ConnectionCommand 
extends Command 
{
	private static final Logger logger = Logger.getLogger(ConnectionCommand.class);
	
//	private ValueConsumer oldTarget;
//	private ValueProviderConfig oldSource;
//	private String oldSourceParameterID;
//	private String oldParameterType;
//	private String oldTargetParameterID;
	
	private ValueConsumer target;
	private ValueProviderConfig source;
	private String sourceParameterID;
	private String sourceParameterType;
	private String targetParameterID;
	private String targetParameterType;	
		
	private ValueConsumerBinding binding;
	private ValueAcquisitionSetup setup;
	
	public ConnectionCommand() {
		super();
		setLabel(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.command.ConnectionCommand.label")); //$NON-NLS-1$
	}

	@Override
	public boolean canExecute() 
	{
		if (sourceParameterType != null && targetParameterType != null) {
			if (sourceParameterType.equals(targetParameterType))
				return true;
		}
		return false;
	}

	@Override
	public void execute() 
	{
		if (binding != null) {
			binding.setParameterID(targetParameterID);
			binding.setConsumer(target);
			binding.setProvider(source);
			if (setup != null) {
				setup.getValueConsumerBindings().add(binding);
				setup.clearBindingIndexes();
				ModelNotificationManager.sharedInstance().notify(
						ObjectIDProvider.getObjectID(source), 
						ModelNotificationManager.PROP_CONNECT, 
						null, 
						binding);			
				ModelNotificationManager.sharedInstance().notify(
						ObjectIDProvider.getObjectID(target), 
						ModelNotificationManager.PROP_CONNECT, 
						null, 
						binding);			
			} else {
				logger.warn("ValueAcquisitionSetup is null!"); //$NON-NLS-1$
			}			
		} else {
			logger.warn("binding == null!"); //$NON-NLS-1$
		}
	}

	@Override
	public void redo() {
		execute();
	}

	@Override
	public void undo() 
	{
		if (setup != null) {
			setup.getValueConsumerBindings().remove(binding);
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(setup), 
					ModelNotificationManager.PROP_CONNECT, 
					null, 
					binding);			
		} else {
			logger.warn("ValueAcquisitionSetup is null!"); //$NON-NLS-1$
		}
	}
	 
	public void setSource(ValueProviderConfig source) {
		this.source = source;
	}
	
	public void setSourceParameterID(String parameterID) {
		this.sourceParameterID = parameterID;
	}
	
	public void setTarget(ValueConsumer valueConsumer) {
		this.target = valueConsumer;
	}
	
	public void setTargetParameterID(String parameterID) {
		this.targetParameterID = parameterID;
	}
	
	public void setBinding(ValueConsumerBinding binding) {
		this.binding = binding;
	}
	
	public void setValueAcquisitionSetup(ValueAcquisitionSetup setup) {
		this.setup = setup;
	}
	
	public void setSourceParameterType(String parameterType) {
		this.sourceParameterType = parameterType;
	}
	
	public void setTargetParameterType(String parameterType) {
		this.targetParameterType = parameterType;
	}
}
