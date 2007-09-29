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

package org.nightlabs.jfire.trade.articlecontainer.detail;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class SegmentEditFactoryRegistry extends AbstractEPProcessor
{
	protected static SegmentEditFactoryRegistry _sharedInstance = null;

	private static boolean initializingSharedInstance = false;
	public static synchronized SegmentEditFactoryRegistry sharedInstance()
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!"); //$NON-NLS-1$

		if (_sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				_sharedInstance = new SegmentEditFactoryRegistry();
				_sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return _sharedInstance;
	}

	/**
	 * key: String segmentContext<br/>
	 * value: Map {<br/>
	 *   key: String segmentTypeClass<br/>
	 *   value: SegmentEditFactory editFactory<br/>
	 * }
	 */
	protected Map segmentEditFactoriesBySegmentContext = new HashMap();

	protected static boolean isValidSegmentContext(String segmentContext)
	{
		return
				SegmentEditFactory.SEGMENTCONTEXT_ORDER.equals(segmentContext) ||
				SegmentEditFactory.SEGMENTCONTEXT_OFFER.equals(segmentContext) ||
				SegmentEditFactory.SEGMENTCONTEXT_INVOICE.equals(segmentContext) ||
				SegmentEditFactory.SEGMENTCONTEXT_DELIVERY_NOTE.equals(segmentContext) ||
				SegmentEditFactory.SEGMENTCONTEXT_RECEPTION_NOTE.equals(segmentContext);
	}

	protected static void assertValidSegmentContext(String segmentContext)
	{
		if (!isValidSegmentContext(segmentContext))
			throw new IllegalArgumentException("segmentContext \""+segmentContext+"\" is not valid!"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected Map getSegmentEditFactories(String segmentContext)
	{
		assertValidSegmentContext(segmentContext);

		Map res = (Map) segmentEditFactoriesBySegmentContext.get(segmentContext);
		if (res == null) {
			res = new HashMap();
			segmentEditFactoriesBySegmentContext.put(segmentContext, res);
		}
		return res;
	}

	/**
	 * @param segmentContext
	 * @param segmentTypeClass This class will be resolved recursively. Means you can
	 *		subclass a <tt>SegmentType</tt> and it will use the parent's factory, if you
	 *		don't override it for your child.
	 * @param throwExceptionIfNotFound If <tt>false</tt> return <tt>null</tt>, if
	 * <tt>true</tt> throw an {@link IllegalStateException}, in case nothing can be found.
	 *
	 * @return
	 */
	public SegmentEditFactory getSegmentEditFactory(
			String segmentContext, Class segmentTypeClass,
			boolean throwExceptionIfNotFound)
	{
		Map m = getSegmentEditFactories(segmentContext);
		SegmentEditFactory factory = null;
		Class clazz = segmentTypeClass;
		do {
			factory = (SegmentEditFactory) m.get(clazz.getName());
			clazz = clazz.getSuperclass();
		} while (factory == null && clazz != Object.class);

		if (throwExceptionIfNotFound && factory == null)
			throw new IllegalStateException("Nothing registered for segmentContext=\""+segmentContext+"\", segmentTypeClass=\""+segmentTypeClass.getName()+"\" (or a super-class)!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		return factory;
	}

	protected void addSegmentEditFactory(SegmentEditFactory sef)
	{
		Map sefMap = getSegmentEditFactories(sef.getSegmentContext());
		sefMap.put(sef.getSegmentTypeClass(), sef);
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.IEPProcessor#getExtensionPointID()
	 */
	public String getExtensionPointID()
	{
		return "org.nightlabs.jfire.trade.segmentEditFactory"; //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.IEPProcessor#processElement(IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	public void processElement(IExtension extension, IConfigurationElement element)
			throws Exception
	{
		try {
			SegmentEditFactory segmentEditFactory = (SegmentEditFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			String segmentTypeClass = element.getAttribute("segmentTypeClass"); //$NON-NLS-1$
			String segmentContext = element.getAttribute("segmentContext"); //$NON-NLS-1$
			String segmentEditFactoryName = element.getAttribute("name"); //$NON-NLS-1$

			assertValidSegmentContext(segmentContext);
			if (segmentTypeClass == null || "".equals(segmentTypeClass)) //$NON-NLS-1$
				throw new EPProcessorException("segmentTypeClass undefined!"); //$NON-NLS-1$

			segmentEditFactory.init(segmentEditFactoryName, segmentContext, segmentTypeClass);

//			ArticleEditFactoryRegistry articleEditFactoryRegistry = new ArticleEditFactoryRegistry();
//			SegmentEditFactoryRegistryEntry sefre = new SegmentEditFactoryRegistryEntry(
//					segmentEditFactory, articleEditFactoryRegistry);

//			IConfigurationElement[] children = element.getChildren("articleEditFactory");
//			for (int i = 0; i < children.length; ++i) {
//				IConfigurationElement child = children[i];
//
//				ArticleEditFactory articleEditFactory = (ArticleEditFactory) child.createExecutableExtension("class");
//				String productTypeClass = child.getAttribute("productTypeClass");
//				String articleEditFactoryName = child.getAttribute("name");
//
//				articleEditFactory.setSegmentEditFactory(segmentEditFactory);
//				articleEditFactory.setProductTypeClass(productTypeClass);
//				articleEditFactory.setName(articleEditFactoryName);
//
//				articleEditFactoryRegistry.addArticleEditFactory(articleEditFactory);
//			}

			addSegmentEditFactory(segmentEditFactory);
		} catch (Throwable t) {
			throw new EPProcessorException("Extension to "+getExtensionPointID()+" with class "+element.getAttribute("class")+" has errors!", t); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}

}
