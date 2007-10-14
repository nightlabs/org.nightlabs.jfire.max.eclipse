package org.nightlabs.jfire.reporting.admin.parameter.util;

import java.util.ArrayList;
import java.util.List;

import org.nightlabs.jfire.reporting.admin.parameter.editpart.ValueProviderConfigEditPart;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.ValueProviderInputParameter;
import org.nightlabs.jfire.reporting.parameter.config.AcquisitionParameterConfig;
import org.nightlabs.jfire.reporting.parameter.config.ValueConsumer;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.dao.ValueProviderDAO;
import org.nightlabs.progress.NullProgressMonitor;

public class ValueConsumerUtil 
{
	public List<String> getParameterIDs(ValueConsumer valueConsumer) 
	{
		List<String> parameterIDs = new ArrayList<String>();		
		if (valueConsumer instanceof AcquisitionParameterConfig) {
			AcquisitionParameterConfig acquisitionParameterConfig = (AcquisitionParameterConfig) valueConsumer;
			parameterIDs.add(acquisitionParameterConfig.getParameterID());
		}
		if (valueConsumer instanceof ValueProviderConfig) {
			ValueProviderConfig valueProviderConfig = (ValueProviderConfig) valueConsumer;
			ValueProvider valueProvider = ValueProviderDAO.sharedInstance().getValueProvider(
					valueProviderConfig.getConfigValueProviderID(),
					ValueProviderConfigEditPart.FETCH_GROUPS,
					new NullProgressMonitor());
			for (ValueProviderInputParameter inputParameter : valueProvider.getInputParameters()) {
				parameterIDs.add(inputParameter.getParameterID());
			}
		}
		return parameterIDs;		
	}
	
	public List<String> getParameterTypes(ValueConsumer valueConsumer) 
	{
		List<String> parameterTypes = new ArrayList<String>();		
		if (valueConsumer instanceof AcquisitionParameterConfig) {
			AcquisitionParameterConfig acquisitionParameterConfig = (AcquisitionParameterConfig) valueConsumer;
			parameterTypes.add(acquisitionParameterConfig.getParameterType());
		}
		if (valueConsumer instanceof ValueProviderConfig) {
			ValueProviderConfig valueProviderConfig = (ValueProviderConfig) valueConsumer;
			ValueProvider valueProvider = ValueProviderDAO.sharedInstance().getValueProvider(
					valueProviderConfig.getConfigValueProviderID(),
					ValueProviderConfigEditPart.FETCH_GROUPS,
					new NullProgressMonitor());
			for (ValueProviderInputParameter inputParameter : valueProvider.getInputParameters()) {
				parameterTypes.add(inputParameter.getParameterType());
			}
		}
		return parameterTypes;				
	}
}
