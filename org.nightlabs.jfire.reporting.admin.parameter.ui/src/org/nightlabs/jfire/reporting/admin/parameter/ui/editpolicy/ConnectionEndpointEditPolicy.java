package org.nightlabs.jfire.reporting.admin.parameter.ui.editpolicy;


/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ConnectionEndpointEditPolicy
extends org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy
{
	@Override
	protected void addSelectionHandles(){
		super.addSelectionHandles();
//		getConnectionFigure().setLineWidth(2);
	}

//	protected PolylineConnection getConnectionFigure(){
//		return (PolylineConnection)((GraphicalEditPart)getHost()).getFigure();
//	}

	@Override
	protected void removeSelectionHandles(){
		super.removeSelectionHandles();
//		getConnectionFigure().setLineWidth(0);
	}
}
