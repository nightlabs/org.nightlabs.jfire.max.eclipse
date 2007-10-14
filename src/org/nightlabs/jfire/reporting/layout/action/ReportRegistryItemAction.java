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

package org.nightlabs.jfire.reporting.layout.action;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.reporting.layout.ReportRegistryItem;

/**
 * Base class for all actions that manipulate <code>ReportRegistryItem</code>s.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class ReportRegistryItemAction extends Action implements IReportRegistryItemAction {

	/**
	 * 
	 */
	public ReportRegistryItemAction() {
		super();
	}

	/**
	 * @param text
	 */
	public ReportRegistryItemAction(String text) {
		super(text);
	}

	/**
	 * @param text
	 * @param image
	 */
	public ReportRegistryItemAction(String text, ImageDescriptor image) {
		super(text, image);
	}

	/**
	 * @param text
	 * @param style
	 */
	public ReportRegistryItemAction(String text, int style) {
		super(text, style);
	}
	
	
	private Collection<ReportRegistryItem> reportRegistryItems = new HashSet<ReportRegistryItem>();
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.layout.action.IReportRegistryItemAction#setReportRegistryItem(org.nightlabs.jfire.reporting.layout.ReportRegistryItem)
	 */
	public void setReportRegistryItems(Collection<ReportRegistryItem> reportRegistryItem) {
		this.reportRegistryItems = reportRegistryItem;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.layout.action.IReportRegistryItemAction#getReportRegistryItem()
	 */
	public Collection<ReportRegistryItem> getReportRegistryItems() {
		return reportRegistryItems;
	}

	
	/**
	 * All <code>ReportRegistryItemAction</code>s should do their work
	 * in this method as they can be passed a <code>ReportRegistryItem</code>
	 * to interact with.
	 * 
	 * @param reportRegistryItems The <code>ReportRegistryItem</code>s this action was invoked on
	 * @see #setReportRegistryItem(ReportRegistryItem)
	 */
	public abstract void run(Collection<ReportRegistryItem> reportRegistryItems);
	

	/**
	 * Runs the action with {@link #run(ReportRegistryItem)} passing
	 * the current <code>ReportRegistryItem</code>. After the action 
	 * was performed the current <code>ReportRegistryItem</code> is
	 * set back to <code>null</code> so {@link #setReportRegistryItem(ReportRegistryItem)}
	 * has to be invoked again before rerunning the action.
	 * 
	 */
	@Override
	public void run() {
		run(reportRegistryItems);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.layout.action.IReportRegistryItemAction#calculateEnabled(java.util.Collection)
	 */
	public boolean calculateEnabled(Collection<ReportRegistryItem> registryItems) {
		return true;
	}

	/*
	 *  (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.admin.layout.action.IReportRegistryItemAction#calculateVisible(java.util.Collection)
	 */
	public boolean calculateVisible(Collection<ReportRegistryItem> registryItems) {
		return true;
	}

	
	
	private String scope;
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.layout.action.IReportRegistryItemAction#getScope()
	 */
	public String getScope() {
		return scope;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.reporting.layout.action.IReportRegistryItemAction#setScope(java.lang.String)
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

}
