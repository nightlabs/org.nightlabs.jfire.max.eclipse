/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.viewer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.eclipse.extension.EPProcessorException;
import org.nightlabs.jfire.reporting.Birt.OutputFormat;

/**
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 *
 */
public class RenderedReportHandlerRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.reporting.ui.renderedReportHandler"; //$NON-NLS-1$
	
	
	private Map<String, RenderedReportHandler> renderedReportHandlers = new HashMap<String, RenderedReportHandler>();
	
	
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
	public void processElement(
			IExtension extension,
			IConfigurationElement element
		)
	throws Exception
	{
		if (element.getName().equalsIgnoreCase("renderedReportHandler")) { //$NON-NLS-1$
			String format = element.getAttribute("format"); //$NON-NLS-1$
			if (format == null || "".equals(format)) //$NON-NLS-1$
				throw new EPProcessorException("Element renderedReportHandler has an invalid/missing format attribute."); //$NON-NLS-1$
			RenderedReportHandler handler = null;
			try {
				handler = (RenderedReportHandler)element.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				throw new EPProcessorException("Could not create RenderedReportHandler", e); //$NON-NLS-1$
			}
			renderedReportHandlers.put(format, handler);
		}
	}
	
	
	public RenderedReportHandler getHandler(String format) {
		checkProcessing();
		return renderedReportHandlers.get(format);
	}
	
	public RenderedReportHandler getHandler(OutputFormat format) {
		return getHandler(format.toString());
	}
	
	private static RenderedReportHandlerRegistry sharedInstance;
	
	public static RenderedReportHandlerRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new RenderedReportHandlerRegistry();
		return sharedInstance;
	}

}
