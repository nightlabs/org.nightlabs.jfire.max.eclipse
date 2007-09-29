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

package org.nightlabs.jfire.trade.admin.ui.tariff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.jdo.FetchPlan;
import javax.security.auth.login.LoginException;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.accounting.dao.TariffDAO;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class TariffTableContentProvider
	implements IStructuredContentProvider
{
	/**
	 * Contains items of type CategorySetCarrier
	 */
	protected List tariffCarriers;

	public static final String[] FETCH_GROUPS_TARIFF = {
		FetchPlan.DEFAULT, Tariff.FETCH_GROUP_NAME
	};

	public TariffTableContentProvider(String filterOrganisationID, boolean filterOrganisationIDInverse)
	{
		try {
			Collection<Tariff> tariffCollection = TariffDAO.sharedInstance().getTariffs(
					filterOrganisationID, filterOrganisationIDInverse, FETCH_GROUPS_TARIFF, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()); // TODO use a job for non-blocking UI!

			tariffCarriers = new ArrayList();
			int i = 0;
			for (Iterator it = tariffCollection.iterator(); it.hasNext(); ) {
				Tariff tariff = (Tariff)it.next();
//				tariff.localize(Locale.getDefault().getLanguage());
				tariffCarriers.add(new TariffCarrier(tariff));
			}
		} catch (RuntimeException x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
			throw x;
		} catch (Exception x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
			throw new RuntimeException(x);
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement)
	{
		return tariffCarriers.toArray();
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose()
	{
	}

	/**
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}

	public void createTariff()
	{
		try {
			Tariff tariff = new Tariff(Login.getLogin().getOrganisationID(), Tariff.createTariffID());
			TariffCarrier tc = new TariffCarrier(tariff);
			tc.setDirty(true);
			tariffCarriers.add(tc);
		} catch (LoginException x) {
			throw new RuntimeException(x);
		}
	}

}
