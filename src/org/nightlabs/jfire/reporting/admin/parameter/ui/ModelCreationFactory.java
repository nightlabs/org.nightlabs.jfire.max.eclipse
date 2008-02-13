package org.nightlabs.jfire.reporting.admin.parameter.ui;

import org.eclipse.gef.requests.CreationFactory;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ModelCreationFactory
implements CreationFactory
{
	private Class targetClass;
	private IValueAcquisitionSetupProvider setupProvider;
	public ValueAcquisitionSetup getSetup() {
		return setupProvider.getValueAcquisitionSetup();
	}
	
	public ModelCreationFactory(Class targetClass, IValueAcquisitionSetupProvider setupProvider) {
		this.targetClass = targetClass;
		this.setupProvider = setupProvider;
	}
	
	public Object getObjectType() {
		return targetClass;
	}

	public Object getNewObject()
	{
		Object result = null;
		
		if( targetClass.equals(ValueProviderConfig.class)) {
			result = new ValueProviderConfig(setupProvider.getValueAcquisitionSetup(),
					IDGenerator.nextID(ValueProviderConfig.class));
		}
		
		return result;
	}
	
}
