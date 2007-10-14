package org.nightlabs.jfire.reporting.admin.parameter.ui.editpolicy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class AcquisitionParameterConfigEditPolicy 
extends ComponentEditPolicy 
{
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) 
	{
		return null;
	}
}
