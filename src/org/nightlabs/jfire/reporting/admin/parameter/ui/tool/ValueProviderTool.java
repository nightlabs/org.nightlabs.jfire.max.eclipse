package org.nightlabs.jfire.reporting.admin.parameter.ui.tool;

import org.eclipse.gef.Request;
import org.eclipse.gef.tools.CreationTool;
import org.nightlabs.jfire.reporting.admin.parameter.ui.ModelCreationFactory;
import org.nightlabs.jfire.reporting.admin.parameter.ui.request.ValueProviderCreateRequest;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.ui.parameter.ValueProviderDialog;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueProviderTool
extends CreationTool
{
	public ValueProviderTool(ModelCreationFactory factory) {
		super(factory);
		valueAcquisitionSetup = factory.getSetup();
	}

	@Override
	protected Request createTargetRequest()
	{
		ValueProviderCreateRequest request = new ValueProviderCreateRequest();
		request.setFactory(getFactory());
		return request;
	}

	protected ValueAcquisitionSetup valueAcquisitionSetup;
	
	public ValueProviderCreateRequest getValueProviderCreateRequest() {
		return (ValueProviderCreateRequest) getTargetRequest();
	}
	
	private boolean creationInProgress = false;
	@Override
	protected void performCreation(int button) {
		creationInProgress = true;
		try {
			ValueProvider valueProvider = ValueProviderDialog.openDialog();
			if (valueProvider != null) {
				getValueProviderCreateRequest().setValueProvider(valueProvider);
				getValueProviderCreateRequest().setValueAcquisitionSetup(valueAcquisitionSetup);
				super.performCreation(button);
			}
		} finally {
			creationInProgress = false;
		}		
	}

	@Override
	protected boolean handleFocusLost() {
		if (creationInProgress)
			return false;
		return super.handleFocusLost();
	}
}
