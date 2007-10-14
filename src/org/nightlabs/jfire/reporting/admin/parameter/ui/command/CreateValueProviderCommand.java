package org.nightlabs.jfire.reporting.admin.parameter.ui.command;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.gef.commands.Command;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.ui.request.ValueProviderCreateRequest;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;
import org.nightlabs.jfire.reporting.admin.parameter.ui.util.ObjectIDProvider;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.config.id.ValueAcquisitionSetupID;
import org.nightlabs.jfire.reporting.parameter.config.id.ValueProviderConfigID;
import org.nightlabs.jfire.reporting.parameter.dao.ValueProviderDAO;
import org.nightlabs.jfire.reporting.parameter.id.ValueProviderID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class CreateValueProviderCommand 
extends Command 
{
	private ValueProviderCreateRequest request;
	public CreateValueProviderCommand(ValueProviderCreateRequest request) 
	{
		super();
		setLabel(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.command.CreateValueProviderCommand.label")); //$NON-NLS-1$
		this.request = request;
	}

	private ValueProviderConfig vpc;
	@Override
	public void execute() 
	{
		ValueAcquisitionSetup setup = request.getValueAcquisitionSetup();
		ValueProvider valueProvider = request.getValueProvider();
		vpc = new ValueProviderConfig(setup, IDGenerator.nextID(ValueProviderConfig.class));
		vpc.setValueProvider(valueProvider);
		vpc.setX(request.getLocation().x);
		vpc.setY(request.getLocation().y);		
		setup.getValueProviderConfigs().add(vpc);
		ValueProvider provider = ValueProviderDAO.sharedInstance().getValueProvider(
				(ValueProviderID) JDOHelper.getObjectId(valueProvider), 
				new String[] {FetchPlan.DEFAULT, ValueProvider.FETCH_GROUP_DEFAULT_MESSAGE}, 
				new NullProgressMonitor()
			);
		vpc.getMessage().copyFrom(provider.getDefaultMessage());
		ValueAcquisitionSetupID setupID = ValueAcquisitionSetupID.create(setup);
		ModelNotificationManager.sharedInstance().notify(
				setupID, ModelNotificationManager.PROP_CREATE, null, vpc);
		
		ValueProviderConfigID valueProviderConfigID = ValueProviderConfigID.create(vpc);
		ModelNotificationManager.sharedInstance().notify(
				valueProviderConfigID, ModelNotificationManager.PROP_CREATE, null, vpc);
	}

	@Override
	public void redo() {
		ValueAcquisitionSetup setup = request.getValueAcquisitionSetup();
		setup.getValueProviderConfigs().add(vpc);
		
		ModelNotificationManager.sharedInstance().notify(
				ObjectIDProvider.getObjectID(vpc), 
				ModelNotificationManager.PROP_DELETE, null, vpc);		
	}

	@Override
	public void undo() {
		ValueAcquisitionSetup setup = request.getValueAcquisitionSetup();
		setup.getValueProviderConfigs().remove(vpc);
		
		ModelNotificationManager.sharedInstance().notify(
				ObjectIDProvider.getObjectID(vpc), 
				ModelNotificationManager.PROP_CREATE, null, vpc);		
	}
	
}
