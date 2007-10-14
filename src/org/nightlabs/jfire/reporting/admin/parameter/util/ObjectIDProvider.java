package org.nightlabs.jfire.reporting.admin.parameter.util;

import javax.jdo.JDOHelper;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.ValueAcquisitionSetup;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumerBinding;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.config.id.AcquisitionParameterConfigID;
import org.nightlabs.jfire.reporting.parameter.config.id.ValueAcquisitionSetupID;
import org.nightlabs.jfire.reporting.parameter.config.id.ValueConsumerBindingID;
import org.nightlabs.jfire.reporting.parameter.config.id.ValueProviderConfigID;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ObjectIDProvider 
{
	public static ObjectID getObjectID(Object o) 
	{
		if (o instanceof ValueProviderConfig)
			return ValueProviderConfigID.create((ValueProviderConfig)o);
		else if (o instanceof AcquisitionParameterConfig)
			return AcquisitionParameterConfigID.create((AcquisitionParameterConfig)o);
		else if (o instanceof ValueAcquisitionSetup)
			return ValueAcquisitionSetupID.create((ValueAcquisitionSetup)o);
		else if (o instanceof ValueConsumerBinding)
			return ValueConsumerBindingID.create((ValueConsumerBinding)o);
		
		return (ObjectID) JDOHelper.getObjectId(o);		
	}
}
