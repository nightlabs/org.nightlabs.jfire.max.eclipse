package org.nightlabs.jfire.reporting.admin.parameter.editpolicy;


/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ConnectionEndpointEditPolicy 
extends org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy 
{
	protected void addSelectionHandles(){
		super.addSelectionHandles();
//		getConnectionFigure().setLineWidth(2);
	}

//	protected PolylineConnection getConnectionFigure(){
//		return (PolylineConnection)((GraphicalEditPart)getHost()).getFigure();
//	}

	protected void removeSelectionHandles(){
		super.removeSelectionHandles();
//		getConnectionFigure().setLineWidth(0);
	}
}
