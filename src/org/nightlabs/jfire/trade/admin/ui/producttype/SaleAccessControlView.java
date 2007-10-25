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

package org.nightlabs.jfire.trade.admin.ui.producttype;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.ModuleException;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;

public abstract class SaleAccessControlView extends ViewPart
{
	private SaleAccessControlComposite saleAccessControlComposite;

	/**
	 * @return In your descendant of SaleAccessControlView, you must implement this method
	 *		and return the zone in which you want to be notified about selections.
	 */
	public abstract String getZone();

	public SaleAccessControlView()
	{
	}

	protected abstract SaleAccessControlHelper getSaleAccessControlHelper();

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent)
	{
		saleAccessControlComposite = new SaleAccessControlComposite(
				parent, SWT.NONE, getSaleAccessControlHelper());

		SelectionManager.sharedInstance().addNotificationListener(
				getZone(),
				ProductType.class, notificationListenerProductTypeSelected);

		saleAccessControlComposite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				SelectionManager.sharedInstance().removeNotificationListener(
						getZone(),
						ProductType.class, notificationListenerProductTypeSelected);
			}
		});
	}

	private NotificationListener notificationListenerProductTypeSelected = new NotificationAdapterJob(Messages.getString("org.nightlabs.jfire.trade.admin.ui.producttype.SaleAccessControlView.loadProductTypeJob.name")) { //$NON-NLS-1$
		public void notify(NotificationEvent event) {
			try {
				if (event.getSubjects().isEmpty())
					setProductTypeID(null);
				else
					setProductTypeID((ProductTypeID)event.getFirstSubject());
			} catch (ModuleException x) {
				throw new RuntimeException(x);
			}
		}
	};

	public void setProductTypeID(ProductTypeID productTypeID)
	throws ModuleException
	{
		saleAccessControlComposite.setProductTypeID(productTypeID);
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus()
	{
	}

	/**
	 * Submit all the settings to the server.
	 */
	public void submit()
	{
		saleAccessControlComposite.submit();
	}
}
