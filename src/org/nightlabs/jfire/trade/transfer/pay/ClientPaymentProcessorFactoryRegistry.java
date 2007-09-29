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

package org.nightlabs.jfire.trade.transfer.pay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.accounting.pay.ModeOfPayment;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavour;
import org.nightlabs.jfire.trade.transfer.pay.ClientPaymentProcessorFactory.ModeOfPaymentFlavourRef;
import org.nightlabs.jfire.trade.transfer.pay.ClientPaymentProcessorFactory.ModeOfPaymentRef;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ClientPaymentProcessorFactoryRegistry extends AbstractEPProcessor
{
	protected static ClientPaymentProcessorFactoryRegistry _sharedInstance = null;

	public static synchronized ClientPaymentProcessorFactoryRegistry sharedInstance()
	throws EPProcessorException
	{
		if (_sharedInstance == null) {
			_sharedInstance = new ClientPaymentProcessorFactoryRegistry();
			_sharedInstance.process();
		}

		return _sharedInstance;
	}

	/**
	 * key: String id (the id of the extension)<br/>
	 * value: ClientPaymentProcessorFactory clientPaymentProcessorFactory
	 */
	protected Map clientPaymentProcessorFactoriesByID = new HashMap();

	/**
	 * key: {@link ClientPaymentProcessorFactory.ModeOfPaymentRef} modeOfPaymentRef<br/>
	 * value: List of ClientPaymentProcessorFactory clientPaymentProcessorFactory
	 */
	protected Map clientPaymentProcessorFactoriesByModeOfPaymentRef = new HashMap();

	/**
	 * key: {@link ClientPaymentProcessorFactory.ModeOfPaymentFlavourRef} modeOfPaymentFlavourRef<br/>
	 * value: List of ClientPaymentProcessorFactory clientPaymentProcessorFactory
	 */
	protected Map clientPaymentProcessorFactoriesByModeOfPaymentFlavourRef = new HashMap();

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.IEPProcessor#getExtensionPointID()
	 */
	public String getExtensionPointID()
	{
		return "org.nightlabs.jfire.trade.clientPaymentProcessorFactory"; //$NON-NLS-1$
	}

	/**
	 * @return Returns an instance of <tt>List</tt> with items of
	 * type {@link ClientPaymentProcessorFactory}. This method never returns <tt>null</tt>.
	 * If no factory is found, the resulting <tt>List</tt> is empty.
	 */
	public List getClientPaymentProcessorFactories(ModeOfPaymentFlavour modeOfPaymentFlavour)
	{
		ModeOfPaymentFlavourRef modeOfPaymentFlavourRef = new ModeOfPaymentFlavourRef(
				modeOfPaymentFlavour.getOrganisationID(),
				modeOfPaymentFlavour.getModeOfPaymentFlavourID());

		ModeOfPayment modeOfPayment = modeOfPaymentFlavour.getModeOfPayment();
		ModeOfPaymentRef modeOfPaymentRef = new ModeOfPaymentRef(
				modeOfPayment.getOrganisationID(),
				modeOfPayment.getModeOfPaymentID());

		ArrayList res = new ArrayList();

		List factoriesForModeOfPaymentFlavour = (List) clientPaymentProcessorFactoriesByModeOfPaymentFlavourRef.get(modeOfPaymentFlavourRef);
		if (factoriesForModeOfPaymentFlavour != null)
			res.addAll(factoriesForModeOfPaymentFlavour);

		List factoriesForModeOfPayment = (List) clientPaymentProcessorFactoriesByModeOfPaymentRef.get(modeOfPaymentRef);
		if (factoriesForModeOfPayment != null)
			res.addAll(factoriesForModeOfPayment);

		return res;
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.IEPProcessor#processElement(IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	public void processElement(IExtension extension, IConfigurationElement element)
			throws Exception
	{
		try {
			String id = element.getAttribute("id"); //$NON-NLS-1$
			if (clientPaymentProcessorFactoriesByID.containsKey(id))
				throw new IllegalStateException("Duplicate registration with same id!"); //$NON-NLS-1$

			ClientPaymentProcessorFactory clientPaymentProcessorFactory = (ClientPaymentProcessorFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			clientPaymentProcessorFactory.setID(id);
			clientPaymentProcessorFactory.setName(element.getAttribute("name")); //$NON-NLS-1$
			clientPaymentProcessorFactoriesByID.put(id, clientPaymentProcessorFactory);

			IConfigurationElement[] children = element.getChildren();
			for (int i = 0; i < children.length; ++i) {
				IConfigurationElement child = children[i];
				String childName = child.getName();
				
				Object key;
				Map map;

				if ("modeOfPayment".equals(childName)) { //$NON-NLS-1$
					String organisationID = child.getAttribute("organisationID"); //$NON-NLS-1$
					String modeOfPaymentID = child.getAttribute("modeOfPaymentID"); //$NON-NLS-1$
					key = new ModeOfPaymentRef(organisationID, modeOfPaymentID);
					map = clientPaymentProcessorFactoriesByModeOfPaymentRef;
				}
				else if ("modeOfPaymentFlavour".equals(childName)) { //$NON-NLS-1$
					String organisationID = child.getAttribute("organisationID"); //$NON-NLS-1$
					String modeOfPaymentFlavourID = child.getAttribute("modeOfPaymentFlavourID"); //$NON-NLS-1$
					key = new ModeOfPaymentFlavourRef(organisationID, modeOfPaymentFlavourID);
					map = clientPaymentProcessorFactoriesByModeOfPaymentFlavourRef;
				}
				else
					throw new IllegalStateException("unknown child \""+childName+"\"!"); //$NON-NLS-1$ //$NON-NLS-2$

				List list = (List) map.get(key);
				if (list == null) {
					list = new ArrayList();
					map.put(key, list);
				}
				list.add(clientPaymentProcessorFactory);
			} // for (int i = 0; i < children.length; ++i) {

			clientPaymentProcessorFactory.init();

		} catch (Throwable t) {
			throw new EPProcessorException("Extension to "+getExtensionPointID()+" with class "+element.getAttribute("class")+" and id \""+element.getAttribute("id")+"\" has errors!", t); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		}
	}

}
