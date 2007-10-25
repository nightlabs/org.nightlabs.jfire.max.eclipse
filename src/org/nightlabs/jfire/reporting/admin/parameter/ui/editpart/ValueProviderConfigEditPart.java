package org.nightlabs.jfire.reporting.admin.parameter.ui.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpolicy.ValueProviderComponentEditPolicy;
import org.nightlabs.jfire.reporting.admin.parameter.ui.figure.ValueProviderConfigFigure;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.ValueProviderInputParameter;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.config.id.ValueProviderConfigID;
import org.nightlabs.jfire.reporting.parameter.dao.ValueProviderDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueProviderConfigEditPart 
extends AbstractNodeReportEditPart
{
	public ValueProviderConfigEditPart(ValueProviderConfig valueProviderConfig,
			ValueAcquisitionSetup setup) {
		super(setup);
		setModel(valueProviderConfig);
	}
	
	protected ValueProviderConfig getValueProviderConfig() {
		return (ValueProviderConfig) getModel();
	}
	
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ValueProvider.FETCH_GROUP_INPUT_PARAMETERS,
		ValueProvider.FETCH_GROUP_DESCRIPTION,
		ValueProvider.FETCH_GROUP_NAME
	};
	
	@Override
	protected IFigure createFigure() 
	{
		ValueProvider valueProvider = ValueProviderDAO.sharedInstance().getValueProvider(
				getValueProviderConfig().getConfigValueProviderID(),
				FETCH_GROUPS,
				new NullProgressMonitor());
		ValueProviderConfigFigure nodeFigure = new ValueProviderConfigFigure(this, valueProvider);
		nodeFigure.setColorIndex(getValueProviderConfig().getPageIndex());
		return nodeFigure; 
	}
	
	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ValueProviderComponentEditPolicy());
	}

	@Override
	protected ObjectID getObjectID() {
		return ValueProviderConfigID.create(getValueProviderConfig());
	}
	
	@Override
	protected IPropertySource createPropertySource() {
		return new ValueProviderConfigPropertySource(getValueProviderConfig());
	}

	@Override
	protected List getModelSourceConnections() 
	{
		ValueConsumerBinding binding = getValueAcquisitionSetup().getValueProviderBinding(getValueProviderConfig());
		List<ValueConsumerBinding> bindings = new ArrayList<ValueConsumerBinding>(1);		
		if (binding != null) {
			bindings.add(binding);
		}
		return bindings;
	}

	@Override
	protected List getModelTargetConnections() 
	{
		Map<String, ValueConsumerBinding> bindings = getValueAcquisitionSetup().getValueConsumerBindings(getValueProviderConfig());
		if (bindings != null) {
			return new ArrayList<ValueConsumerBinding>(bindings.values());
		}
		return super.getModelTargetConnections();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) 
	{
		if (evt.getPropertyName().equals(ModelNotificationManager.PROP_PAGE_INDEX)) {
			int newPageIndex = (Integer)evt.getNewValue();
			((ValueProviderConfigFigure)getFigure()).setColorIndex(newPageIndex);
			getFigure().repaint();
		}
		super.propertyChange(evt);
	}
	
	private static class InputParameterAchorItem implements INodeAnchorItem {
		private ValueProviderInputParameter inputParameter;
		
		public InputParameterAchorItem(ValueProviderInputParameter inputParameter) {
			this.inputParameter = inputParameter;
		}
		
		public String getAnchorName() {
			return inputParameter.getParameterID();
		}

		public String getAnchorType() {
			return inputParameter.getParameterType();
		}
		
	}
	
	@Override
	protected List<INodeAnchorItem> getNodeTargetItems() {
		ValueProvider valueProvider = ValueProviderDAO.sharedInstance().getValueProvider(
				getValueProviderConfig().getConfigValueProviderID(),
				FETCH_GROUPS,
				new NullProgressMonitor()
			);
		List<INodeAnchorItem> result = new ArrayList<INodeAnchorItem>(valueProvider.getInputParameters().size());
		for (ValueProviderInputParameter parameter : valueProvider.getInputParameters()) {
			result.add(new InputParameterAchorItem(parameter));
		}
		return result;
	}

	@Override
	protected INodeAnchorItem getNodeSourceItem() {
		ValueProvider valueProvider = ValueProviderDAO.sharedInstance().getValueProvider(
				getValueProviderConfig().getConfigValueProviderID(),
				FETCH_GROUPS,
				new NullProgressMonitor()
			);
		final String name = valueProvider.getName().getText();
		final String type = valueProvider.getOutputType();
		return new INodeAnchorItem() {
			public String getAnchorName() {
				return name;
			}

			public String getAnchorType() {
				return type;
			}
		};
	}
	
}
