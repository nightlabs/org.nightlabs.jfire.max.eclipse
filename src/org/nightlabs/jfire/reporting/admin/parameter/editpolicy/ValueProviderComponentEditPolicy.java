package org.nightlabs.jfire.reporting.admin.parameter.editpolicy;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.nightlabs.jfire.reporting.admin.parameter.command.DeleteValueProviderCommand;
import org.nightlabs.jfire.reporting.admin.parameter.editpart.ValueAcquisitionSetupEditPart;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueProviderComponentEditPolicy 
extends ComponentEditPolicy 
{

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) 
	{
		DeleteValueProviderCommand cmd = new DeleteValueProviderCommand(
				(ValueProviderConfig) getHost().getModel(), 
				getValueAcquisitionSetup(deleteRequest));
		return cmd;
	}
	
	protected ValueAcquisitionSetup getValueAcquisitionSetup(GroupRequest request) 
	{
		if (!request.getEditParts().isEmpty()) {
			EditPart targetEP = (EditPart) request.getEditParts().iterator().next();
			if (targetEP != null) {
				EditPart parentEP = targetEP.getParent();
				ValueAcquisitionSetupEditPart vasep = (ValueAcquisitionSetupEditPart) parentEP;
				return vasep.getValueAcquisitionSetup();
			}			
		}
		return null;
	}	
}
