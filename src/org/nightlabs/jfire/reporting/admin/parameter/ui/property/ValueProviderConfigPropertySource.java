package org.nightlabs.jfire.reporting.admin.parameter.ui.property;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.nightlabs.base.ui.property.CheckboxPropertyDescriptor;
import org.nightlabs.base.ui.property.IntPropertyDescriptor;
import org.nightlabs.base.ui.property.XI18nTextPropertyDescriptor;
import org.nightlabs.base.ui.property.XTextPropertyDescriptor;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.jfire.reporting.admin.parameter.ui.editpart.ValueProviderConfigEditPart;
import org.nightlabs.jfire.reporting.admin.parameter.ui.notification.ModelNotificationManager;
import org.nightlabs.jfire.reporting.admin.parameter.ui.resource.Messages;
import org.nightlabs.jfire.reporting.admin.parameter.ui.util.ObjectIDProvider;
import org.nightlabs.jfire.reporting.parameter.ValueProvider;
import org.nightlabs.jfire.reporting.parameter.config.IGraphicalInfoProvider;
import org.nightlabs.jfire.reporting.parameter.config.ValueProviderConfig;
import org.nightlabs.jfire.reporting.parameter.dao.ValueProviderDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ValueProviderConfigPropertySource 
extends AbstractPropertySource 
{

	public ValueProviderConfigPropertySource(ValueProviderConfig valueProviderConfig) {
		super();
		this.valueProviderConfig = valueProviderConfig;
	}
	
	private ValueProviderConfig valueProviderConfig = null;
	
	public ValueProviderConfig getValueProviderConfig() {
		return valueProviderConfig;
	}
	
	private ValueProvider valueProvider = null;
	protected ValueProvider getValueProvider() {
		if (valueProvider == null) {
			valueProvider = ValueProviderDAO.sharedInstance().getValueProvider(
					valueProviderConfig.getConfigValueProviderID(),
					ValueProviderConfigEditPart.FETCH_GROUPS,
					new NullProgressMonitor());
		}
		return valueProvider;
	}
	
//protected ValueProvider getValueProvider() 
//{
//	return ValueProviderDAO.sharedInstance().getValueProvider(
//			valueProviderConfig.getConfigValueProviderID(),
//			ValueProviderConfigEditPart.FETCH_GROUPS,
//			new NullProgressMonitor());
//}
	
	public Object getEditableValue() {
		return valueProviderConfig;
	}

	protected IGraphicalInfoProvider getGraphicalInfoProvider() {
		return valueProviderConfig;
	}
	
	private int staticPropertyDescriptorSize = 11;
	private int getInpuParamterSize() {
		return getValueProvider().getInputParameters().size();
	}
	public IPropertyDescriptor[] getPropertyDescriptors() 
	{
		int inputParameters = getInpuParamterSize();
		int finalSize = staticPropertyDescriptorSize + inputParameters * 2;
		IPropertyDescriptor[] propertyDescriptors = new IPropertyDescriptor[finalSize];
		propertyDescriptors[0] = createXPD(false);
		propertyDescriptors[1] = createYPD(false);
		propertyDescriptors[2] = createNamePD(true);
		propertyDescriptors[3] = createDescriptionPD(true);
		propertyDescriptors[4] = createMessagePD(false);
		propertyDescriptors[5] = createShowMessageInHeaderPD();
		propertyDescriptors[6] = createPageIndexPD();
		propertyDescriptors[7] = createPageRowPD();
		propertyDescriptors[8] = createPageColumnPD();
		propertyDescriptors[9] = createOutputTypePD();
		propertyDescriptors[10] = createAllowOutputNullPD();
		for (int i=0; i<inputParameters; i++) 
		{
			int suffix = (i + 1);
			String suffixID = String.valueOf(suffix);
			int baseIdx = staticPropertyDescriptorSize + (i*2);
			propertyDescriptors[baseIdx] = createParameterIDPD(suffixID);
			String suffixType = String.valueOf(suffix);
			propertyDescriptors[baseIdx+1] = createParameterTypePD(suffixType, true);
		}
		return propertyDescriptors;
	}

	protected PropertyDescriptor createNamePD(boolean readOnly)
	{
		PropertyDescriptor desc = new XTextPropertyDescriptor(
				ModelNotificationManager.PROP_NAME,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorName.name"), //$NON-NLS-1$
				readOnly);
		desc.setCategory(CATEGORY_NAME);
		return desc;
	}

	protected PropertyDescriptor createDescriptionPD(boolean readOnly)
	{
		PropertyDescriptor desc = new XTextPropertyDescriptor(
				ModelNotificationManager.PROP_DESCRIPTION,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorDescription.name"), //$NON-NLS-1$
				readOnly);
		desc.setCategory(CATEGORY_NAME);
		return desc;
	}
	
	protected PropertyDescriptor createMessagePD(boolean readOnly)
	{
//		PropertyDescriptor desc = new XTextPropertyDescriptor(
		PropertyDescriptor desc = new XI18nTextPropertyDescriptor(
				ModelNotificationManager.PROP_MESSAGE,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorMessage.name"), //$NON-NLS-1$
				readOnly);
		desc.setCategory(CATEGORY_NAME);
		return desc;
	}	
	
	public static final String CATEGORY_PAGE = Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorCategoryPage.name"); //$NON-NLS-1$
	
	protected PropertyDescriptor createPageIndexPD() 
	{
		PropertyDescriptor desc = new IntPropertyDescriptor(
				ModelNotificationManager.PROP_PAGE_INDEX,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorPageIndex.name"), //$NON-NLS-1$
				false);
		desc.setCategory(CATEGORY_PAGE);
		return desc;		
	}

	protected PropertyDescriptor createPageRowPD() 
	{
		PropertyDescriptor desc = new IntPropertyDescriptor(
				ModelNotificationManager.PROP_PAGE_ROW,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorPageRow.name"), //$NON-NLS-1$
				false);
		desc.setCategory(CATEGORY_PAGE);
		return desc;		
	}	
	
	protected PropertyDescriptor createPageColumnPD() 
	{
		PropertyDescriptor desc = new IntPropertyDescriptor(
				ModelNotificationManager.PROP_PAGE_COLUMN,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorPageColumn.name"), //$NON-NLS-1$
				false);
		desc.setCategory(CATEGORY_PAGE);
		return desc;		
	}
	
	protected PropertyDescriptor createOutputTypePD() 
	{
		PropertyDescriptor pd = new XTextPropertyDescriptor(
				ModelNotificationManager.PROP_OUTPUT_TYPE,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorOutputType.name"), //$NON-NLS-1$
				true);
		return pd;
	}
	
	protected PropertyDescriptor createAllowOutputNullPD() 
	{
		List<Boolean> trueFalse = new ArrayList<Boolean>();
		trueFalse.add(true);
		trueFalse.add(false);		
		PropertyDescriptor pd = new CheckboxPropertyDescriptor(
				ModelNotificationManager.PROP_ALLOW_OUTPUT_NULL_VALUE,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorAllowNull.name"), false //$NON-NLS-1$
			);
		return pd;
	}
	
	protected PropertyDescriptor createShowMessageInHeaderPD() 
	{
		List<Boolean> trueFalse = new ArrayList<Boolean>();
		trueFalse.add(true);
		trueFalse.add(false);		
		PropertyDescriptor pd = new CheckboxPropertyDescriptor(
				ModelNotificationManager.PROP_SHOW_MESSAGE_IN_HEADER,
				Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorShowMessage.name"), false //$NON-NLS-1$
			);
		pd.setCategory(Messages.getString("org.nightlabs.jfire.reporting.admin.parameter.ui.property.ValueProviderConfigPropertySource.propertyDescriptorCategoryName.name")); //$NON-NLS-1$
		return pd;
	}
	
	public Object getPropertyValue(Object id) 
	{
		if (id.equals(IGraphicalInfoProvider.PROP_X)) {
			return getGraphicalInfoProvider().getX();
		}
		else if (id.equals(IGraphicalInfoProvider.PROP_Y)) {
			return getGraphicalInfoProvider().getY();
		}
		else if (id.equals(ModelNotificationManager.PROP_NAME)) {
			return getValueProvider().getName().getText();
		}
		else if (id.equals(ModelNotificationManager.PROP_DESCRIPTION)) {
			return getValueProvider().getDescription().getText();
		}
		else if (id.equals(ModelNotificationManager.PROP_PAGE_INDEX)) {
			return valueProviderConfig.getPageIndex();
		}						
		else if (id.equals(ModelNotificationManager.PROP_PAGE_ROW)) {
			return valueProviderConfig.getPageRow();
		}						
		else if (id.equals(ModelNotificationManager.PROP_PAGE_COLUMN)) {
			return valueProviderConfig.getPageColumn();
		}						
		else if (id.equals(ModelNotificationManager.PROP_OUTPUT_TYPE)) {
			return getValueProvider().getOutputType();
		}
		else if (id.equals(ModelNotificationManager.PROP_MESSAGE)) {
//			return getValueProviderConfig().getMessage().getText();
			return getValueProviderConfig().getMessage();
		}
		else if (id.equals(ModelNotificationManager.PROP_ALLOW_OUTPUT_NULL_VALUE)) {
			return getValueProviderConfig().isAllowNullOutputValue();
		}
		else if (id.equals(ModelNotificationManager.PROP_SHOW_MESSAGE_IN_HEADER)) {
			return getValueProviderConfig().isShowMessageInHeader();
		}
		for (int i=staticPropertyDescriptorSize; i<staticPropertyDescriptorSize+getInpuParamterSize(); i++) 
		{
			int index = (i - staticPropertyDescriptorSize);
			int suffix = (i + 1 - staticPropertyDescriptorSize);
			String suffixID = String.valueOf(suffix);
			String suffixType = String.valueOf(suffix);
			if (id.equals(ModelNotificationManager.PROP_PARAMETER_ID + suffixID))
				return getValueProvider().getInputParameters().get(index).getParameterID();
			if (id.equals(ModelNotificationManager.PROP_PARAMETER_TYPE + suffixType))
					return getValueProvider().getInputParameters().get(index).getParameterType();
		}
		return null;
	}

	public void setPropertyValue(Object id, Object value) 
	{
		if (id.equals(IGraphicalInfoProvider.PROP_X)) 
		{
			int x = ((Integer)value).intValue();
			getGraphicalInfoProvider().setX(x);
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(valueProviderConfig), 
					IGraphicalInfoProvider.PROP_X, 
					-1, 
					x);
			return;
		}
		else if (id.equals(IGraphicalInfoProvider.PROP_Y)) 
		{
			int y = ((Integer)value).intValue();
			getGraphicalInfoProvider().setY(y);
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(valueProviderConfig), 
					IGraphicalInfoProvider.PROP_Y, 
					-1, 
					y);			
			return;
		}
		else if (id.equals(ModelNotificationManager.PROP_PAGE_INDEX)) 
		{
			int pageIndex = ((Integer)value).intValue();			
			valueProviderConfig.setPageIndex(pageIndex);
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(valueProviderConfig),
					ModelNotificationManager.PROP_PAGE_INDEX,
					-1, 
					pageIndex);
			return;
		}						
		else if (id.equals(ModelNotificationManager.PROP_PAGE_ROW)) {
			int pageRow = ((Integer)value).intValue();			
			valueProviderConfig.setPageRow(pageRow);
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(valueProviderConfig),
					ModelNotificationManager.PROP_PAGE_ROW,
					-1, 
					pageRow);
			return;
		}		
		else if (id.equals(ModelNotificationManager.PROP_PAGE_COLUMN)) {
			int pageColumn = ((Integer)value).intValue();			
			valueProviderConfig.setPageColumn(pageColumn);
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(valueProviderConfig),
					ModelNotificationManager.PROP_PAGE_COLUMN,
					-1, 
					pageColumn);
			return;
		}		
		else if (id.equals(ModelNotificationManager.PROP_ALLOW_OUTPUT_NULL_VALUE)) {
			boolean oldVal = valueProviderConfig.isAllowNullOutputValue();
			valueProviderConfig.setAllowNullOutputValue(((Boolean)value).booleanValue());
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(valueProviderConfig),
					ModelNotificationManager.PROP_ALLOW_OUTPUT_NULL_VALUE,
					oldVal, 
					((Boolean)value).booleanValue());
			return;
		}
		else if (id.equals(ModelNotificationManager.PROP_SHOW_MESSAGE_IN_HEADER)) {
			boolean oldVal = valueProviderConfig.isShowMessageInHeader();
			valueProviderConfig.setShowMessageInHeader(((Boolean)value).booleanValue());
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(valueProviderConfig),
					ModelNotificationManager.PROP_SHOW_MESSAGE_IN_HEADER,
					oldVal, 
					((Boolean)value).booleanValue());
			return;
		}
		else if (id.equals(ModelNotificationManager.PROP_MESSAGE)) {
			I18nText text = (I18nText) value;
			valueProviderConfig.getMessage().copyFrom(text);
			ModelNotificationManager.sharedInstance().notify(
					ObjectIDProvider.getObjectID(valueProviderConfig),
					ModelNotificationManager.PROP_MESSAGE,
					-1, 
					text);
			return;
		}				
	}

}
