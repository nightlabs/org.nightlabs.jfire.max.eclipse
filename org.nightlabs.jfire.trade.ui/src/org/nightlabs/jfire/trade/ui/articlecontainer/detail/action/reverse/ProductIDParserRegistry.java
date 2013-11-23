package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.eclipse.extension.EPProcessorException;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ProductIDParserRegistry 
extends AbstractEPProcessor 
{
	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.trade.ui.productIdParser"; //$NON-NLS-1$ 

	public static final String ELEMENT_PRODUCT_ID_PARSER = "productIdParser"; //$NON-NLS-1$
	public static final String ATTRIBUTE_CLASS = "class"; //$NON-NLS-1$
	public static final String ATTRIBUTE_PRIORITY = "priority"; //$NON-NLS-1$
	
	private static ProductIDParserRegistry sharedInstance = null;
	private SortedMap<Integer, IProductIDParser> priority2Parser = new TreeMap<Integer, IProductIDParser>();

	/**
	 * Returns the shared instance (singleton) for this object
	 **/
	public static ProductIDParserRegistry sharedInstance() {
		if (sharedInstance == null) {
			synchronized (ProductIDParserRegistry.class) {
				if (sharedInstance == null)
					sharedInstance = new ProductIDParserRegistry();
			}
		}
		return sharedInstance;
	}
		
	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID() {
		return EXTENSION_POINT_ID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(org.eclipse.core.runtime.IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception 
	{
		if (element.getName().equals(ELEMENT_PRODUCT_ID_PARSER))
		{
			String className = element.getAttribute(ATTRIBUTE_CLASS); 
			if (!checkString(className))
				throw new EPProcessorException("Element "+ELEMENT_PRODUCT_ID_PARSER+" has to define attribute "+ATTRIBUTE_CLASS); //$NON-NLS-1$ //$NON-NLS-2$
						
			String priority = element.getAttribute(ATTRIBUTE_PRIORITY);
			if (!checkString(priority))
				throw new EPProcessorException("Element "+ELEMENT_PRODUCT_ID_PARSER+" has to define attribute "+ATTRIBUTE_PRIORITY); //$NON-NLS-1$ //$NON-NLS-2$
			
			int priorityInt = 500;
			try {
				priorityInt = Integer.parseInt(priority);
			} catch (NumberFormatException e) {
				throw new EPProcessorException("Attribute "+ATTRIBUTE_PRIORITY+" is not a number!");				 //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			Object productIDParser = element.createExecutableExtension(ATTRIBUTE_CLASS);
			if (productIDParser instanceof IProductIDParser) {
				IProductIDParser parser = (IProductIDParser) productIDParser;
				priority2Parser.put(priorityInt, parser);
			} 
			else {
				throw new EPProcessorException("Attribute "+ATTRIBUTE_CLASS+" is of type "+productIDParser.getClass().getName()+" and does not implement "+IProductIDParser.class.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	}

	public Collection<IProductIDParser> getProductIDParser() 
	{
		checkProcessing();
		return priority2Parser.values();
	}
}
