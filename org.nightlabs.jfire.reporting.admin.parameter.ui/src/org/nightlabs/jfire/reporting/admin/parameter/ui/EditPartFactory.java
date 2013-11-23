package org.nightlabs.jfire.reporting.admin.parameter.ui;

import org.eclipse.gef.EditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.AcquisitionParameterConfigEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.ValueAcquisitionSetupEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.ValueConsumerBindingEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.ValueProviderConfigEditPart;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class EditPartFactory
implements org.eclipse.gef.EditPartFactory
{
	private ReportParameterEditor editor;
	public EditPartFactory(ReportParameterEditor editor)
	{
		this.editor = editor;
	}
		
	public EditPart createEditPart(EditPart context, Object model)
	{
		if (model instanceof ValueProviderConfig)
			return new ValueProviderConfigEditPart((ValueProviderConfig)model, editor.getValueAcquisitionSetup());

		if (model instanceof ValueAcquisitionSetup)
			return new ValueAcquisitionSetupEditPart((ValueAcquisitionSetup)model, editor.getReportHandle());

		if (model instanceof AcquisitionParameterConfig)
			return new AcquisitionParameterConfigEditPart((AcquisitionParameterConfig)model, editor.getValueAcquisitionSetup());
		
		if (model instanceof ValueConsumerBinding)
			return new ValueConsumerBindingEditPart((ValueConsumerBinding)model, editor.getValueAcquisitionSetup());
		
		return null;
	}

}
