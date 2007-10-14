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
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.reporting.ui.layout;

/**
 * Simple interface to let listeners react on changes of the
 * ReportRegistry (category structure). Register instances
 * of this class to {@link org.nightlabs.jfire.reporting.ui.layout.ReportRegistryItemProvider}
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[ÐOT]de>
 *
 */
public interface ReportRegistryListener {
	
	/**
	 * Will be called when the ReportRegistry has changed.
	 * E.g. a new item was created, or an item was moved
	 * or deleted. This will not be called if an items
	 * name or other properties changed, only if it affects
	 * the structure of the ReportRegistry.
	 * This method is likely to be called from other threads
	 * than the SWT GUI thread.
	 */
	public void reportRegistryChanged();	
}
