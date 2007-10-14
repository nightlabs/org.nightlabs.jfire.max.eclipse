package org.nightlabs.jfire.reporting.admin.parameter.ui.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.birt.report.model.api.ModuleHandle;
import org.eclipse.birt.report.model.api.ScalarParameterHandle;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpolicy.ReportXYLayoutEditPolicy;
import org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueAcquistionSetupPropertySource;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.id.ValueAcquisitionSetupID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueAcquisitionSetupEditPart 
extends AbstractReportParameterEditPart 
{

	public ValueAcquisitionSetupEditPart(ValueAcquisitionSetup setup,
			ModuleHandle reportHandle) {
		super();
		setModel(setup);
		this.reportHandle = reportHandle;
	}

	private ModuleHandle reportHandle;
	
	public ValueAcquisitionSetup getValueAcquisitionSetup() {
		return (ValueAcquisitionSetup) getModel();
	}
	
	/**
	 * Returns a Figure to represent this. 
	 *
	 * @return  Figure.
	 */
	@Override
	protected IFigure createFigure() {
		Figure f = new FreeformLayer();
		f.setLayoutManager(new FreeformLayout());
		f.setBorder(new MarginBorder(5));
		return f;
	}

	/**
	 * Installs EditPolicies specific to this. 
	 */
	protected void createEditPolicies(){
		super.createEditPolicies();

		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new RootComponentEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ReportXYLayoutEditPolicy(
				(XYLayout)getContentPane().getLayoutManager()));

		installEditPolicy("Snap Feedback", new SnapFeedbackPolicy()); //$NON-NLS-1$
	}

	@Override
	protected ObjectID getObjectID() {
		return ValueAcquisitionSetupID.create(getValueAcquisitionSetup());
	}	
		
  @SuppressWarnings("unchecked") //$NON-NLS-1$
	@Override  
  protected List getModelChildren() 
  {
  	List children = new ArrayList();
  	validateValueAcquisitionSetupParameterConfigs();
  	children.addAll(getValueAcquisitionSetup().getParameterConfigs());
  	children.addAll(getValueAcquisitionSetup().getValueProviderConfigs());
//  	children.addAll(getValueAcquisitionSetup().getValueConsumerBindings());
    return children;
  }
  
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		clearAndRefresh();
	} 
	
	public void clearAndRefresh() {
		getValueAcquisitionSetup().clearBindingIndexes();
		refreshChildren();		
	}
    
	@Override
	protected IPropertySource createPropertySource() {
		return new ValueAcquistionSetupPropertySource(getValueAcquisitionSetup());
	}
	
	@SuppressWarnings("unchecked") //$NON-NLS-1$
	protected void validateValueAcquisitionSetupParameterConfigs() 
	{
		List params = reportHandle.getAllParameters();		
		List<ScalarParameterHandle> reportParams = new ArrayList<ScalarParameterHandle>(params.size());
		for (Object param : params) {
			if (param instanceof ScalarParameterHandle)
				reportParams.add((ScalarParameterHandle) param);
		}
		Map<String, AcquisitionParameterConfig> setupConfigs = new HashMap<String, AcquisitionParameterConfig>();
		Set<AcquisitionParameterConfig> unlinkedParamConfigs = new HashSet<AcquisitionParameterConfig>();
		for (AcquisitionParameterConfig config : getValueAcquisitionSetup().getParameterConfigs()) {
			setupConfigs.put(config.getParameterID(), config);
			unlinkedParamConfigs.add(config);
		}
		for (ScalarParameterHandle reportParam : reportParams) {
			if (!setupConfigs.containsKey(reportParam.getName())) {
				AcquisitionParameterConfig newConfig = new AcquisitionParameterConfig(
						getValueAcquisitionSetup(), 
						reportParam.getName(), 
						reportParam.getDataType()
					);
				getValueAcquisitionSetup().getParameterConfigs().add(newConfig);
			} else {
				unlinkedParamConfigs.remove(setupConfigs.get(reportParam.getName()));
			}
		}
		for (AcquisitionParameterConfig config : unlinkedParamConfigs) {
			getValueAcquisitionSetup().getParameterConfigs().remove(config);
			Map<String, ValueConsumerBinding> bindings = getValueAcquisitionSetup().getValueConsumerBindings(config);
			if (bindings != null) {
				for (ValueConsumerBinding binding : bindings.values()) {
					getValueAcquisitionSetup().getValueConsumerBindings().remove(binding);
				}
			}
		}
	}
	
	public IPropertySource getPropertySource() {
		return null;
	}
	
}
