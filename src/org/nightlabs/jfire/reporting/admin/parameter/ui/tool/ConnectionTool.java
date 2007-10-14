package org.nightlabs.jfire.reporting.admin.parameter.ui.tool;

import org.eclipse.gef.Request;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.nightlabs.jfire.reporting.admin.parameter.ui.ModelCreationFactory;
import org.nightlabs.jfire.reporting.admin.parameter.ui.request.ConnectionCreateRequest;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ConnectionTool 
//extends CreationTool 
extends ConnectionCreationTool
{
	public ConnectionTool(ModelCreationFactory factory) {
		super(factory);
	}
 
	protected ModelCreationFactory getModelCreationFactory() {
		return (ModelCreationFactory)getFactory();
	}
	
	@Override
	protected Request createTargetRequest() {
//		return super.createTargetRequest();
		ConnectionCreateRequest request = new ConnectionCreateRequest();
		request.setFactory(getFactory());
		request.setValueAcquisitionSetup(getModelCreationFactory().getSetup());
		return request;
	}	

}
