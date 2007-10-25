package org.nightlabs.jfire.reporting.admin.parameter.ui.editpolicy;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.admin.parameter.ui.command.ConnectionCommand;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.AbstractNodeReportEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.ValueAcquisitionSetupEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.ValueProviderConfigEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.request.ConnectionCreateRequest;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumer;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ReportNodeEditPolicy 
extends GraphicalNodeEditPolicy 
{
	private static final Logger logger = Logger.getLogger(ReportNodeEditPolicy.class);
	
	public ReportNodeEditPolicy() {
	}

	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		ConnectionCommand command = (ConnectionCommand)request.getStartCommand();
		command.setTarget(getValueConsumer());
		ConnectionAnchor anchor = getNodeEditPart().getTargetConnectionAnchor(request);
		if (anchor == null) {
			logger.warn("anchor == null for request "+request); //$NON-NLS-1$
			return null;			
		}
		command.setTargetParameterID(getNodeEditPart().mapConnectionAnchorToParameterID(anchor));
		command.setTargetParameterType(getNodeEditPart().mapConnectionAnchorToParameterType(anchor));
		return command;
	}

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) 
	{
		ConnectionCommand command = new ConnectionCommand();
		ValueAcquisitionSetup setup = null;
		if (request instanceof ConnectionCreateRequest) {
			ConnectionCreateRequest connectionCreateRequest = (ConnectionCreateRequest) request;
			setup = connectionCreateRequest.getValueAcquisitionSetup();
		}
		else {
			setup = getValueAcquisitionSetup(request);			
		}
		
		if (setup == null)
			logger.warn("ValueAcquisitionSetup == null!"); //$NON-NLS-1$
		
		// only allow source anchors for ValueProviderConfigs
		if (getValueProviderConfig() == null)
			return null;
		
		command.setValueAcquisitionSetup(setup);
		command.setBinding(new ValueConsumerBinding(
				setup.getOrganisationID(),
				IDGenerator.nextID(ValueConsumerBinding.class),
				setup));					
		command.setSource(getValueProviderConfig());
		ConnectionAnchor anchor = getNodeEditPart().getSourceConnectionAnchor(request);
		command.setSourceParameterID(getNodeEditPart().mapConnectionAnchorToParameterID(anchor));
		command.setSourceParameterType(getNodeEditPart().mapConnectionAnchorToParameterType(anchor));		
		request.setStartCommand(command);
		return command;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) 
	{
		ConnectionCommand cmd = new ConnectionCommand();
		cmd.setBinding((ValueConsumerBinding)request.getConnectionEditPart().getModel());		
		ConnectionAnchor anchor = getNodeEditPart().getSourceConnectionAnchor(request);
		cmd.setSource(getValueProviderConfig());
		cmd.setSourceParameterID(getNodeEditPart().mapConnectionAnchorToParameterID(anchor));
		cmd.setSourceParameterType(getNodeEditPart().mapConnectionAnchorToParameterType(anchor));
		return cmd;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) 
	{
//		if (getValueConsumer() instanceof AcquisitionParameterConfig)
//			return null;			
		ConnectionCommand cmd = new ConnectionCommand();
		cmd.setBinding((ValueConsumerBinding)request.getConnectionEditPart().getModel());
		ConnectionAnchor anchor = getNodeEditPart().getTargetConnectionAnchor(request);
		cmd.setTarget(getValueConsumer());
		cmd.setTargetParameterID(getNodeEditPart().mapConnectionAnchorToParameterID(anchor));
		cmd.setTargetParameterType(getNodeEditPart().mapConnectionAnchorToParameterType(anchor));
		return cmd;
	}

	public ValueProviderConfig getValueProviderConfig() 
	{
		if (getHost().getModel() instanceof ValueProviderConfig)
			return (ValueProviderConfig) getHost().getModel();
		
		return null;
	}
	
	public ValueProviderConfigEditPart getValueProviderConfigEditPart() 
	{
		if (getHost() instanceof ValueProviderConfigEditPart)
			return (ValueProviderConfigEditPart) getHost();
		
		return null;
	}

	public ValueConsumer getValueConsumer() {
		return (ValueConsumer) getHost().getModel();
	}
	
	public AbstractNodeReportEditPart getNodeEditPart() {
		return (AbstractNodeReportEditPart) getHost(); 
	}
	
	@Override
	protected IFigure getFeedbackLayer() 
	{
		/*
		 * Fix for Bug# 66590
		 * Feedback needs to be added to the scaled feedback layer
		 */
		return getLayer(LayerConstants.SCALED_FEEDBACK_LAYER);
	}	
	
	protected ValueAcquisitionSetup getValueAcquisitionSetup(CreateConnectionRequest request) 
	{
		EditPart targetEP = request.getTargetEditPart();
		if (targetEP != null) {
			EditPart parentEP = targetEP.getParent();
			ValueAcquisitionSetupEditPart vasep = (ValueAcquisitionSetupEditPart) parentEP;
			return vasep.getValueAcquisitionSetup();
		}
		return null;
	}
}
