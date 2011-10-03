/**
 * 
 */
package org.nightlabs.jfire.reporting.ui.viewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.eclipse.extension.AbstractEPProcessor;
import org.nightlabs.eclipse.extension.EPProcessorException;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;
import org.nightlabs.jfire.reporting.ui.config.ReportUseCase;
import org.nightlabs.jfire.reporting.ui.config.ReportUseCaseRegistry;
import org.nightlabs.jfire.reporting.ui.config.ReportViewPrintConfigModule;
import org.nightlabs.jfire.reporting.ui.config.ReportViewPrintConfigModule.UseCaseConfig;
import org.nightlabs.jfire.reporting.ui.viewer.editor.DefaultReportViewer;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportViewerRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.reporting.ui.reportViewer"; //$NON-NLS-1$
	
	public static final String DEFAULT_REPORT_VIEWER_ID = DefaultReportViewer.ID_REPORT_VIEWER;
	
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(ReportViewerRegistry.class);

	public static class ReportViewerEntry {
		private String id;
		private ReportViewerFactory reportViewerFactory;
		private String name;
		private String description;
		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}
		/**
		 * @param description the description to set
		 */
		public void setDescription(String description) {
			this.description = description;
		}
		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}
		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @return the reportViewerFactory
		 */
		public ReportViewerFactory getReportViewerFactory() {
			return reportViewerFactory;
		}
		/**
		 * @param reportViewerFactory the reportViewerFactory to set
		 */
		public void setReportViewerFactory(ReportViewerFactory reportViewerFactory) {
			this.reportViewerFactory = reportViewerFactory;
		}
		
	}
	
	/**
	 * Key: ReportViewer - id
	 * Value: ReportViewerEntry
	 */
	private Map<String, ReportViewerEntry> reportViewerEntries = new HashMap<String, ReportViewerEntry>();
	
	/**
	 * 
	 */
	public ReportViewerRegistry() {
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
	public void processElement(IExtension extension,
			IConfigurationElement element) throws Exception {
		
		if (element.getName().equalsIgnoreCase("reportViewerFactory")) { //$NON-NLS-1$
			String id = element.getAttribute("id"); //$NON-NLS-1$
			if (!(id != null && !id.trim().isEmpty()))
				throw new EPProcessorException("The id - attribute is not valid: '"+id+"'.", extension); //$NON-NLS-1$ //$NON-NLS-2$
			ReportViewerFactory factory;
			try {
				factory = (ReportViewerFactory)element.createExecutableExtension("class"); //$NON-NLS-1$
			} catch (CoreException e) {
				throw new EPProcessorException("Could not create ReportViewerFactory '"+element.getAttribute("class")+"'", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			String name = element.getAttribute("name"); //$NON-NLS-1$
			if (!(name != null && !name.trim().isEmpty()))
				throw new EPProcessorException("The name - attribute is not valid: '"+id+"'.", extension); //$NON-NLS-1$ //$NON-NLS-2$
			String description = element.getAttribute("description"); //$NON-NLS-1$
			if (description == null)
				description = ""; //$NON-NLS-1$
			ReportViewerEntry entry = new ReportViewerEntry();
			entry.setId(id);
			entry.setReportViewerFactory(factory);
			entry.setName(name);
			entry.setDescription(description);
			reportViewerEntries.put(id, entry);
		}
	}
	
	protected void validate() {
		if (reportViewerEntries.get(DEFAULT_REPORT_VIEWER_ID) == null)
			if (reportViewerEntries.size() < 1)
				throw new IllegalStateException("The default ReportViewer was not registered and no other as well."); //$NON-NLS-1$
			else
				logger.warn("The default ReportViewer was not registered. As there are some other registrations, this caused no error here, but might soon."); //$NON-NLS-1$
	}
	
	@Override
	public synchronized void process() {
		super.process();
		validate();
	}

	/**
	 * Returns the {@link ReportViewerFactory} with the given id,
	 * or <code>null</code> if none was found for this id.
	 * 
	 * @param id The id of the factory to search.
	 * @return the {@link ReportViewerFactory} with the given id,
	 * or <code>null</code> if none was found for this id.
	 */
	public ReportViewerFactory getReportViewerFactory(String id) {
		checkProcessing();
		ReportViewerEntry entry = reportViewerEntries.get(id);
		return (entry != null) ? entry.getReportViewerFactory() : null;
	}
	
	/**
	 * Returns the {@link ReportViewerEntry} with the given id,
	 * or <code>null</code> if none was found for this id.
	 * 
	 * @param id The id of the factory-entry to search.
	 * @return the {@link ReportViewerEntry} with the given id,
	 * or <code>null</code> if none was found for this id.
	 */
	public ReportViewerEntry getReportViewerEntry(String id) {
		checkProcessing();
		return reportViewerEntries.get(id);
	}

	/**
	 * Returns all registered {@link ReportViewerFactory}s wrapped
	 * into their {@link ReportViewerEntry} objects.
	 * 
	 * @return All registered {@link ReportViewerFactory}s wrapped
	 * into their {@link ReportViewerEntry} objects.
	 */
	public List<ReportViewerEntry> getReportViewerEntries() {
		checkProcessing();
		return new ArrayList<ReportViewerEntry>(reportViewerEntries.values());
	}
	
	public ReportViewerFactory getReportViewerFactoryByLayoutType(ReportRegistryItemID itemID) throws NoReportViewerFoundException
	{
		// Try to lookup the UseCase by the reportLayoutType
		ReportUseCase useCase = ReportUseCaseRegistry.sharedInstance().getReportUseCaseByLayoutType(itemID.reportRegistryItemType);
		if (useCase == null)
			throw new NoReportViewerFoundException("Could not find an usecase for report item type "+itemID.reportRegistryItemType); //$NON-NLS-1$
		return getReportViewerFactory(useCase);
	}
	
	public ReportViewerFactory getReportViewerFactory(ReportUseCase useCase)
	throws NoReportViewerFoundException
	{
		String useCaseID = useCase.getId();
		
		ReportViewPrintConfigModule cfMod = ReportViewPrintConfigModule.sharedInstance();
		UseCaseConfig useCaseConfig = cfMod.getReportUseCaseConfigs().get(useCaseID);
		
		String reportViewerID = null;
		if (useCaseConfig != null)
			reportViewerID = useCaseConfig.getReportViewerID();
		if (reportViewerID == null)
			reportViewerID = ReportViewerRegistry.DEFAULT_REPORT_VIEWER_ID;
		
		ReportViewerFactory viewerFactory = ReportViewerRegistry.sharedInstance().getReportViewerFactory(reportViewerID);
		if (viewerFactory == null)
			viewerFactory = ReportViewerRegistry.sharedInstance().getReportViewerFactory(ReportViewerRegistry.DEFAULT_REPORT_VIEWER_ID);

		if (viewerFactory == null)
			throw new NoReportViewerFoundException("Could not find an report viewer for the given report layout id (not event the default one)"); //$NON-NLS-1$
			
		return viewerFactory;
	}
	
	private static ReportViewerRegistry sharedInstance;
	
	public static ReportViewerRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new ReportViewerRegistry();
		return sharedInstance;
	}

}
