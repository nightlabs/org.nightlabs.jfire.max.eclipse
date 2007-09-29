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

package org.nightlabs.jfire.trade.ui.transfer.deliver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;

import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.store.deliver.ModeOfDelivery;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour;
import org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory.ModeOfDeliveryFlavourRef;
import org.nightlabs.jfire.trade.ui.transfer.deliver.ClientDeliveryProcessorFactory.ModeOfDeliveryRef;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ClientDeliveryProcessorFactoryRegistry extends AbstractEPProcessor
{
	protected static ClientDeliveryProcessorFactoryRegistry _sharedInstance = null;

	public static synchronized ClientDeliveryProcessorFactoryRegistry sharedInstance()
	throws EPProcessorException
	{
		if (_sharedInstance == null) {
			_sharedInstance = new ClientDeliveryProcessorFactoryRegistry();
			_sharedInstance.process();
		}

		return _sharedInstance;
	}

	/**
	 * key: String id (the id of the extension)<br/>
	 * value: ClientDeliveryProcessorFactory clientDeliveryProcessorFactory
	 */
	protected Map<String, ClientDeliveryProcessorFactory> clientDeliveryProcessorFactoriesByID = new HashMap<String, ClientDeliveryProcessorFactory>();

	/**
	 * key: {@link ClientDeliveryProcessorFactory.ModeOfDeliveryRef} modeOfDeliveryRef<br/>
	 * value: List of ClientDeliveryProcessorFactory clientDeliveryProcessorFactory
	 */
	protected Map<ModeOfDeliveryRef, List<ClientDeliveryProcessorFactory>> clientDeliveryProcessorFactoriesByModeOfDeliveryRef = new HashMap<ModeOfDeliveryRef, List<ClientDeliveryProcessorFactory>>();

	/**
	 * key: {@link ClientDeliveryProcessorFactory.ModeOfDeliveryFlavourRef} modeOfDeliveryFlavourRef<br/>
	 * value: List of ClientDeliveryProcessorFactory clientDeliveryProcessorFactory
	 */
	protected Map<ModeOfDeliveryFlavourRef, List<ClientDeliveryProcessorFactory>> clientDeliveryProcessorFactoriesByModeOfDeliveryFlavourRef = new HashMap<ModeOfDeliveryFlavourRef, List<ClientDeliveryProcessorFactory>>();

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.IEPProcessor#getExtensionPointID()
	 */
	public String getExtensionPointID()
	{
		return "org.nightlabs.jfire.trade.ui.clientDeliveryProcessorFactory"; //$NON-NLS-1$
	}

	/**
	 * @return Returns an instance of <tt>List</tt> with items of
	 * type {@link ClientDeliveryProcessorFactory}. This method never returns <tt>null</tt>.
	 * If no factory is found, the resulting <tt>List</tt> is empty.
	 */
	public List<ClientDeliveryProcessorFactory> getClientDeliveryProcessorFactories(ModeOfDeliveryFlavour modeOfDeliveryFlavour)
	{
		ModeOfDeliveryFlavourRef modeOfDeliveryFlavourRef = new ModeOfDeliveryFlavourRef(
				modeOfDeliveryFlavour.getOrganisationID(),
				modeOfDeliveryFlavour.getModeOfDeliveryFlavourID());

		ModeOfDelivery modeOfDelivery = modeOfDeliveryFlavour.getModeOfDelivery();
		ModeOfDeliveryRef modeOfDeliveryRef = new ModeOfDeliveryRef(
				modeOfDelivery.getOrganisationID(),
				modeOfDelivery.getModeOfDeliveryID());

		ArrayList<ClientDeliveryProcessorFactory> res = new ArrayList<ClientDeliveryProcessorFactory>();

		List<ClientDeliveryProcessorFactory> factoriesForModeOfDeliveryFlavour = clientDeliveryProcessorFactoriesByModeOfDeliveryFlavourRef.get(modeOfDeliveryFlavourRef);
		if (factoriesForModeOfDeliveryFlavour != null)
			res.addAll(factoriesForModeOfDeliveryFlavour);

		List<ClientDeliveryProcessorFactory> factoriesForModeOfDelivery = clientDeliveryProcessorFactoriesByModeOfDeliveryRef.get(modeOfDeliveryRef);
		if (factoriesForModeOfDelivery != null)
			res.addAll(factoriesForModeOfDelivery);

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
			if (clientDeliveryProcessorFactoriesByID.containsKey(id))
				throw new IllegalStateException("Duplicate registration with same id!"); //$NON-NLS-1$

			ClientDeliveryProcessorFactory clientDeliveryProcessorFactory = (ClientDeliveryProcessorFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			clientDeliveryProcessorFactory.setID(id);
			clientDeliveryProcessorFactory.setName(element.getAttribute("name")); //$NON-NLS-1$
			clientDeliveryProcessorFactoriesByID.put(id, clientDeliveryProcessorFactory);

			IConfigurationElement[] children = element.getChildren();
			for (int i = 0; i < children.length; ++i) {
				IConfigurationElement child = children[i];
				String childName = child.getName();
				
				if ("modeOfDelivery".equals(childName)) { //$NON-NLS-1$
					String organisationID = child.getAttribute("organisationID"); //$NON-NLS-1$
					String modeOfDeliveryID = child.getAttribute("modeOfDeliveryID"); //$NON-NLS-1$
					ModeOfDeliveryRef key = new ModeOfDeliveryRef(organisationID, modeOfDeliveryID);

					List<ClientDeliveryProcessorFactory> list = clientDeliveryProcessorFactoriesByModeOfDeliveryRef.get(key);
					if (list == null) {
						list = new ArrayList<ClientDeliveryProcessorFactory>();
						clientDeliveryProcessorFactoriesByModeOfDeliveryRef.put(key, list);
					}
					list.add(clientDeliveryProcessorFactory);
				}
				else if ("modeOfDeliveryFlavour".equals(childName)) { //$NON-NLS-1$
					String organisationID = child.getAttribute("organisationID"); //$NON-NLS-1$
					String modeOfDeliveryFlavourID = child.getAttribute("modeOfDeliveryFlavourID"); //$NON-NLS-1$
					ModeOfDeliveryFlavourRef key = new ModeOfDeliveryFlavourRef(organisationID, modeOfDeliveryFlavourID);

					List<ClientDeliveryProcessorFactory> list = clientDeliveryProcessorFactoriesByModeOfDeliveryFlavourRef.get(key);
					if (list == null) {
						list = new ArrayList<ClientDeliveryProcessorFactory>();
						clientDeliveryProcessorFactoriesByModeOfDeliveryFlavourRef.put(key, list);
					}
					list.add(clientDeliveryProcessorFactory);
				}
				else
					throw new IllegalStateException("unknown child \""+childName+"\"!"); //$NON-NLS-1$ //$NON-NLS-2$
			} // for (int i = 0; i < children.length; ++i) {

			clientDeliveryProcessorFactory.init();

		} catch (Throwable t) {
			throw new EPProcessorException("Extension to "+getExtensionPointID()+" with class "+element.getAttribute("class")+" and id \""+element.getAttribute("id")+"\" has errors!", t); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		}
	}

}
