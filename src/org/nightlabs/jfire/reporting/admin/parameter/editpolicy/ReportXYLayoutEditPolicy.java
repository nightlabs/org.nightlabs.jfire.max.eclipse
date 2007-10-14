package org.nightlabs.jfire.reporting.admin.parameter.editpolicy;

import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.nightlabs.jfire.reporting.admin.parameter.command.ConnectionCommand;
import org.nightlabs.jfire.reporting.admin.parameter.command.CreateValueProviderCommand;
import org.nightlabs.jfire.reporting.admin.parameter.command.SetConstraintCommand;
import org.nightlabs.jfire.reporting.admin.parameter.request.ConnectionCreateRequest;
import org.nightlabs.jfire.reporting.admin.parameter.request.ValueProviderCreateRequest;
import org.nightlabs.jfire.reporting.parameter.config.IGraphicalInfoProvider;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ReportXYLayoutEditPolicy 
extends XYLayoutEditPolicy 
{

	public ReportXYLayoutEditPolicy(XYLayout layout) {
		super();
		if (layout == null)
			throw new IllegalArgumentException("Param layout must not be null!"); //$NON-NLS-1$
		setXyLayout(layout);
	}

	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) 
	{
//		if (child instanceof ValueProviderConfigEditPart) {
			IGraphicalInfoProvider model = (IGraphicalInfoProvider) child.getModel();
			Rectangle rect = (Rectangle) constraint;		
			SetConstraintCommand setConstraint = new SetConstraintCommand();
			setConstraint.setLocation(rect);
			setConstraint.setModel(model);
			return setConstraint;			
//		}
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) 
	{
		if (request instanceof ValueProviderCreateRequest) {
			return new CreateValueProviderCommand((ValueProviderCreateRequest)request);
		}
		if (request instanceof ConnectionCreateRequest) {
			return new ConnectionCommand();
		}
		
		return null;
	}

//	@Override
//	protected Command createChangeConstraintCommand(ChangeBoundsRequest request,
//			EditPart child, Object constraint) 
//	{
////		return super.createChangeConstraintCommand(request, child, constraint);
//		IGraphicalInfoProvider model = (IGraphicalInfoProvider) child.getModel();
//		Rectangle rect = (Rectangle) constraint;		
//		SetConstraintCommand setConstraint = new SetConstraintCommand();
//		setConstraint.setLocation(rect);
//		setConstraint.setModel(model);
//	}
	
}
