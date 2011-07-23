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

package org.nightlabs.jfire.trade.admin.ui.moneyflow;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.part.ControllablePart;
import org.nightlabs.base.ui.part.PartVisibilityListener;
import org.nightlabs.base.ui.part.PartVisibilityTracker;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.base.login.ui.part.LSDPartController;
import org.nightlabs.jfire.store.ProductType;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public abstract class MoneyFlowConfigView
extends ViewPart
implements ControllablePart, PartVisibilityListener
{
	/**
	 * Should return the zone the listener to selections of ProductTypes
	 * should be registered
	 */
	protected abstract String getListenerZone();
	
	public MoneyFlowConfigView() {
		super();
		LSDPartController.sharedInstance().registerPart(this);
	}

	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		LSDPartController.sharedInstance().createPartControl(this, parent);
		PartVisibilityTracker.sharedInstance().addVisibilityListener(this, this);
	}

	private MoneyFlowConfigComposite moneyFlowConfigComposite = null;
	public MoneyFlowConfigComposite getMoneyFlowConfigComposite() {
		return moneyFlowConfigComposite;
	}
	
	/**
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartContents(Composite parent) {
		moneyFlowConfigComposite = new MoneyFlowConfigComposite(parent, SWT.NONE, null, true);
	}
	
	/**
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
//		moneyFlowConfigComposite.setFocus();
	}
		
	/**
	 * @see org.nightlabs.base.ui.part.ControllablePart#canDisplayPart()
	 */
	public boolean canDisplayPart() {
		return Login.isLoggedIn();
	}

	/**
	 * Removes the NotificationListener.
	 * 
	 * @see org.nightlabs.base.ui.part.PartVisibilityListener#partHidden(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partHidden(IWorkbenchPartReference partRef) {
		SelectionManager.sharedInstance().removeNotificationListener(
				getListenerZone(),
				ProductType.class,
				getMoneyFlowConfigComposite().getNotificationListener()
			);
	}

	/**
	 * Adds the NotificationListener.
	 * 
	 * @see org.nightlabs.base.ui.part.PartVisibilityListener#partVisible(org.eclipse.ui.IWorkbenchPartReference)
	 */
	public void partVisible(IWorkbenchPartReference partRef) {
		SelectionManager.sharedInstance().addNotificationListener(
				getListenerZone(),
				ProductType.class,
				getMoneyFlowConfigComposite().getNotificationListener()
			);
	}
	
}
