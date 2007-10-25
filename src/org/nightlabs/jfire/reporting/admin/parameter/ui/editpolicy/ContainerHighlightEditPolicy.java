package org.nightlabs.jfire.reporting.admin.parameter.ui.editpolicy;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.AbstractNodeReportEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.figure.AbstractInputNodeFigure;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ContainerHighlightEditPolicy 
extends GraphicalEditPolicy 
{
	private AbstractNodeReportEditPart reportEditPart;

	public ContainerHighlightEditPolicy(AbstractNodeReportEditPart reportEditPart) {
		this.reportEditPart = reportEditPart;
	}
	
	@Override
	public void eraseTargetFeedback(Request request){
		if (getContainerFigure() instanceof AbstractInputNodeFigure) {
			((AbstractInputNodeFigure)getContainerFigure()).setInputHighlight(null);
		}
	}

	private IFigure getContainerFigure(){
		return ((GraphicalEditPart)getHost()).getFigure();
	}

	@Override
	public EditPart getTargetEditPart(Request request){
		return request.getType().equals(RequestConstants.REQ_SELECTION_HOVER) ?
			getHost() : null;
	}

	protected void showHighlight(Request request){
		if (getContainerFigure() instanceof AbstractInputNodeFigure) {
			ConnectionAnchor anchor = reportEditPart.getTargetConnectionAnchor(request);
			if (anchor != null)
				((AbstractInputNodeFigure)getContainerFigure()).setInputHighlight(reportEditPart.mapConnectionAnchorToParameterID(anchor));
			else
				((AbstractInputNodeFigure)getContainerFigure()).setInputHighlight(reportEditPart.mapConnectionAnchorToParameterID(null));
		}
	}

	@Override
	public void showTargetFeedback(Request request){
		if(request.getType().equals(RequestConstants.REQ_MOVE) ||
			request.getType().equals(RequestConstants.REQ_ADD) ||
			request.getType().equals(RequestConstants.REQ_CLONE) ||
			request.getType().equals(RequestConstants.REQ_CONNECTION_START) ||
			request.getType().equals(RequestConstants.REQ_CONNECTION_END) ||
			request.getType().equals(RequestConstants.REQ_CREATE)
		)
			showHighlight(request);
	}
}
