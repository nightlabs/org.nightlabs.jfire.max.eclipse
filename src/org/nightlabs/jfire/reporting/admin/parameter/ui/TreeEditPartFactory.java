package org.nightlabs.jfire.reporting.admin.parameter.ui;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.tree.AcquistionParameterTreeEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.tree.ValueAcquisitionSetupTreeEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.tree.ValueProviderConfigTreeEditPart;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class TreeEditPartFactory
implements EditPartFactory
{

	public TreeEditPartFactory() {
		super();
	}

	public EditPart createEditPart(EditPart context, Object model)
	{
		if (model instanceof ValueAcquisitionSetup)
			return new ValueAcquisitionSetupTreeEditPart((ValueAcquisitionSetup)model);
		
		if (model instanceof ValueProviderConfig)
			return new ValueProviderConfigTreeEditPart((ValueProviderConfig)model);
		
		if (model instanceof AcquisitionParameterConfig)
			return new AcquistionParameterTreeEditPart((AcquisitionParameterConfig)model);
		
		return null;
	}

}
