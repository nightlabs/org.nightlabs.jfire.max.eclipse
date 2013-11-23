package org.nightlabs.jfire.reporting.admin.parameter.ui.editpart;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpolicy.AcquisitionParameterConfigEditPolicy;
import org.nightlabs.jfire.reporting.admin.parameter.ui.figure.AcquistionParameterConfigFigure;
import org.nightlabs.jfire.reporting.admin.parameter.ui.property.AcquisitionParameterConfigPropertySource;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.id.AcquisitionParameterConfigID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class AcquisitionParameterConfigEditPart
extends AbstractNodeReportEditPart
{
	public AcquisitionParameterConfigEditPart(AcquisitionParameterConfig model,
			ValueAcquisitionSetup setup) {
		super(setup);
		setModel(model);
	}

	public AcquisitionParameterConfig getAcquisitionParameterConfig() {
		return (AcquisitionParameterConfig) getModel();
	}
	
	@Override
	protected IFigure createFigure()
	{
		AcquistionParameterConfigFigure nodeFigure = new AcquistionParameterConfigFigure(
				this,
				getAcquisitionParameterConfig());
//		nodeFigure.setText(getFigureText());
		return nodeFigure;
	}
	
	protected String getFigureText()
	{
//		return "ParameterID " + getAcquisitionParameterConfig().getParameterID() + ", " +
//			"ParameterType " + getAcquisitionParameterConfig().getParameterType() + ", " +
//			"ConsumerKey " + getAcquisitionParameterConfig().getConsumerKey();
		return Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.AcquisitionParameterConfigEditPart.figureText.prefix") + getAcquisitionParameterConfig().getParameterID(); //$NON-NLS-1$
	}
	
	@Override
	protected ObjectID getObjectID() {
		return AcquisitionParameterConfigID.create(getAcquisitionParameterConfig());
	}
	
	@Override
	protected IPropertySource createPropertySource() {
		return new AcquisitionParameterConfigPropertySource(getAcquisitionParameterConfig());
	}
	
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new AcquisitionParameterConfigEditPolicy());
	}
		
	@SuppressWarnings("unchecked")
	@Override
	protected List getModelTargetConnections()
	{
		ValueConsumerBinding binding = getValueAcquisitionSetup().getValueConsumerBinding(
				getAcquisitionParameterConfig(), getAcquisitionParameterConfig().getParameterID());
		
		List bindings = new ArrayList<ValueConsumerBinding>(1);
		if (binding != null) {
			bindings.add(binding);
		}
		return bindings;
	}

	@Override
	protected List<INodeAnchorItem> getNodeTargetItems() {
		List<INodeAnchorItem> result = new ArrayList<INodeAnchorItem>(1);
		result.add(new INodeAnchorItem() {
			public String getAnchorName() {
				return getAcquisitionParameterConfig().getParameterID();
			}

			public String getAnchorType() {
				return getAcquisitionParameterConfig().getParameterType();
			}
		});
		return result;
	}

	@Override
	protected INodeAnchorItem getNodeSourceItem() {
		return null;
	}
	
}
