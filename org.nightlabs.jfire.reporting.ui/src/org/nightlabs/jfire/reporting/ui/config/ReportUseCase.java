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

import java.util.Set;

import org.nightlabs.base.ui.print.PrinterUseCase;
import org.nightlabs.jfire.reporting.layout.id.ReportRegistryItemID;

/**
 * {@link ReportUseCase}s are used to declare the different use-cases where 
 * reports are shown/printed in JFire. {@link ReportUseCase}s are purely 
 * declarative and are registered using the <code>org.nightlabs.jfire.reporting.ui.reportUseCase</code> 
 * extension-point. 
 * <p>
 * {@link ReportUseCase}s are not intended to be use instantiated by client
 * code, they are managed by the {@link ReportUseCaseRegistry}.
 * </p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ReportUseCase {

	private String id;
	private String name;
	private String description;
	private String reportLayoutType;
	private String defaultPrinterUseCase;
	private Set<Class<?>> minAdapterClasses;


	/**
	 * Create a new {@link ReportUseCase}.
	 */
	public ReportUseCase() {
	}


	/**
	 * @return A human readable description of this report use-case.
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return The unique id of this report use-case.
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return The rather short name of this use-case.
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The reportLayoutType (part of {@link ReportRegistryItemID}) this use-case represents the usage of.
	 */
	public String getReportLayoutType() {
		return reportLayoutType;
	}


	/**
	 * @param reportLayoutType The reportLayoutType to set.
	 */
	public void setReportLayoutType(String reportLayoutType) {
		this.reportLayoutType = reportLayoutType;
	}

	/**
	 * @return The id of the default {@link PrinterUseCase} that should be used for this report use-case.
	 */
	public String getDefaultPrinterUseCase() {
		return defaultPrinterUseCase;
	}

	/**
	 * @param defaultPrinterUseCase The defaultPrinterUseCase to set.
	 */
	public void setDefaultPrinterUseCase(String defaultPrinterUseCase) {
		this.defaultPrinterUseCase = defaultPrinterUseCase;
	}

	/**
	 * @return The (minimum/all) classes this {@link ReportUseCase} consumers of this use-case
	 * should be adaptable to. (e.g. Composite). 
	 */
	public Set<Class<?>> getMinAdapterClasses() {
		return minAdapterClasses;
	}

	/**
	 * @param minAdapterClasses The minAdapterClasses to set.
	 */
	public void setMinAdapterClasses(Set<Class<?>> minAdapterClasses) {
		this.minAdapterClasses = minAdapterClasses;
	}
}
