package org.nightlabs.jfire.reporting.admin.parameter.editpart.tree;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertySource;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.reporting.admin.parameter.ReportingAdminParameterPlugin;
import org.nightlabs.jfire.reporting.admin.parameter.property.AcquisitionParameterConfigPropertySource;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class AcquistionParameterTreeEditPart 
extends AbstractReportTreeEditPart 
{

	/**
	 * @param model
	 */
	public AcquistionParameterTreeEditPart(AcquisitionParameterConfig model) {
		super(model);
	}

	public AcquisitionParameterConfig getAcquisitionParameterConfig() {
		return (AcquisitionParameterConfig) getModel();
	}
	
	@Override
	protected IPropertySource createPropertySource() {
		return new AcquisitionParameterConfigPropertySource(getAcquisitionParameterConfig());
	}

	public static final Image IMAGE = SharedImages.getSharedImage(
			ReportingAdminParameterPlugin.getDefault(),
			AcquistionParameterTreeEditPart.class);
	
	@Override
	protected Image getImage() {
		return IMAGE;
	}

	@Override
	protected String getText() {
		return getAcquisitionParameterConfig().getConsumerKey();
	}

}
