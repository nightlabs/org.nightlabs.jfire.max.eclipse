/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.reporting.ui.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportUseCaseRegistry extends AbstractEPProcessor {

	public static final String EXTENSION_POINT_ID = "org.nightlabs.jfire.reporting.ui.reportUseCase"; //$NON-NLS-1$

	private Map<String, ReportUseCase> reportUseCases = new HashMap<String, ReportUseCase>();
	private Map<String, ReportUseCase> reportUseCasesByLayoutType = new HashMap<String, ReportUseCase>();

	/**
	 *
	 */
	public ReportUseCaseRegistry() {
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
	@SuppressWarnings("unchecked")
	@Override
	public void processElement(IExtension extension,
			IConfigurationElement element) throws Exception {

		if (element.getName().equalsIgnoreCase("reportUseCase")) { //$NON-NLS-1$
			String id = element.getAttribute("id"); //$NON-NLS-1$
			if (!checkString(id))
				throw new EPProcessorException("The id - attribute is invalid '"+id+"'", extension); //$NON-NLS-1$ //$NON-NLS-2$
			String name = element.getAttribute("name"); //$NON-NLS-1$
			if (!checkString(name))
				throw new EPProcessorException("The name - attribute is invalid '"+name+"'", extension); //$NON-NLS-1$ //$NON-NLS-2$
			String description = element.getAttribute("description"); //$NON-NLS-1$
			if (description == null)
				description = ""; //$NON-NLS-1$

			String defaultPrinterUseCase = element.getAttribute("defaultPrinterUseCase"); //$NON-NLS-1$
			if (defaultPrinterUseCase == null)
				defaultPrinterUseCase = ""; //$NON-NLS-1$

			String reportLayoutType = element.getAttribute("reportLayoutType"); //$NON-NLS-1$
			ReportUseCase useCase = new ReportUseCase();
			useCase.setId(id);
			useCase.setName(name);
			useCase.setDescription(description);
			if (reportLayoutType != null)
				reportUseCasesByLayoutType.put(reportLayoutType, useCase);
			else
				reportLayoutType = ""; //$NON-NLS-1$
			useCase.setReportLayoutType(reportLayoutType);
			useCase.setDefaultPrinterUseCase(defaultPrinterUseCase);

			String minAdapterClasses = element.getAttribute("minAdaptableClasses"); //$NON-NLS-1$
			if (checkString(minAdapterClasses)) {
				try {
					String[] classes = minAdapterClasses.split(","); //$NON-NLS-1$
					Set<Class<?>> minClasses = new HashSet<Class<?>>();
					for (String className : classes) {
						Class clazz = Class.forName(className);
						minClasses.add(clazz);
					}
					useCase.setMinAdapterClasses(minClasses);
//					Utils.
				} catch (Throwable t) {
					useCase.setMinAdapterClasses(null);
				}
			} else
				useCase.setMinAdapterClasses(null);

			reportUseCases.put(useCase.getId(), useCase);
		}
	}

	/**
	 * Returns the {@link ReportUseCase} with the given id, or
	 * <code>null</code> if it was not found.
	 *
	 * @param id The id of the ReportUseCase to search.
	 * @return The {@link ReportUseCase} with the given id, or
	 * <code>null</code> if it was not found.
	 */
	public ReportUseCase getReportUseCase(String id) {
		checkProcessing();
		return reportUseCases.get(id);
	}

	/**
	 * Returns all registered {@link ReportUseCase}s.
	 *
	 * @return All registered {@link ReportUseCase}s.
	 */
	public List<ReportUseCase> getReportUseCases() {
		checkProcessing();
		ArrayList<ReportUseCase> result = new ArrayList<ReportUseCase>(reportUseCases.size());
		result.addAll(reportUseCases.values());
		return result;
	}

	/**
	 * Returns the {@link ReportUseCase} registered to the given
	 * reportLayoutType or <code>null</code> if none was found.
	 *
	 * @param reportLayoutType The reportLayoutType a registration should be searched for.
	 * @return The {@link ReportUseCase} registered to the given
	 * reportLayoutType or <code>null</code> if none was found.
	 */
	public ReportUseCase getReportUseCaseByLayoutType(String reportLayoutType) {
		checkProcessing();
		return reportUseCasesByLayoutType.get(reportLayoutType);
	}


	private static ReportUseCaseRegistry sharedInstance;

	/**
	 * Returns and layzily creates a static instance of {@link ReportUseCaseRegistry}
	 */
	public static ReportUseCaseRegistry sharedInstance() {
		if (sharedInstance == null)
			sharedInstance = new ReportUseCaseRegistry();
		return sharedInstance;
	}

}
