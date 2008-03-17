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

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class ReportUseCase {

	private String id;
	private String name;
	private String description;
	private String reportLayoutType;
	private String defaultPrinterUseCase;
	private Set<Class<?>> minAdapterClasses;


	/**
	 *
	 */
	public ReportUseCase() {
		// TODO Auto-generated constructor stub
	}


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
	 * @return the reportLayoutType
	 */
	public String getReportLayoutType() {
		return reportLayoutType;
	}


	/**
	 * @param reportLayoutType the reportLayoutType to set
	 */
	public void setReportLayoutType(String reportLayoutType) {
		this.reportLayoutType = reportLayoutType;
	}

	/**
	 * @return the defaultPrinterUseCase
	 */
	public String getDefaultPrinterUseCase() {
		return defaultPrinterUseCase;
	}

	/**
	 * @param defaultPrinterUseCase the defaultPrinterUseCase to set
	 */
	public void setDefaultPrinterUseCase(String defaultPrinterUseCase) {
		this.defaultPrinterUseCase = defaultPrinterUseCase;
	}

	public Set<Class<?>> getMinAdapterClasses() {
		return minAdapterClasses;
	}

	public void setMinAdapterClasses(Set<Class<?>> minAdapterClasses) {
		this.minAdapterClasses = minAdapterClasses;
	}
}
