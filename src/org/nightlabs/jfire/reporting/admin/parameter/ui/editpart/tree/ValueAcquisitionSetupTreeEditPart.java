package org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.tree;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.reporting.admin.parameter.ui.ReportingAdminParameterPlugin;
import org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueAcquistionSetupPropertySource;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueAcquisitionSetupTreeEditPart
extends AbstractReportTreeEditPart
{

	/**
	 * @param model
	 */
	public ValueAcquisitionSetupTreeEditPart(ValueAcquisitionSetup model) {
		super(model);
	}

	public ValueAcquisitionSetup getValueAcquisitionSetup() {
		return (ValueAcquisitionSetup) getModel();
	}
	
	@Override
	protected IPropertySource createPropertySource() {
		return new ValueAcquistionSetupPropertySource(getValueAcquisitionSetup());
	}

  @Override
  protected List getModelChildren() {
  	List children = new ArrayList();
  	children.addAll(getValueAcquisitionSetup().getParameterConfigs());
  	children.addAll(getValueAcquisitionSetup().getValueProviderConfigs());
    return children;
  }

	public static final Image IMAGE = SharedImages.getSharedImage(
			ReportingAdminParameterPlugin.getDefault(),
			ValueAcquisitionSetupTreeEditPart.class);
	
	@Override
	protected Image getImage() {
		return IMAGE;
	}

	@Override
	protected String getText() {
		return getValueAcquisitionSetup().getOrganisationID();
	}
}
