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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageSettings;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.trade.SegmentType;

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
	 * key: String articleContainerClass<br/>
	 * value: Map {<br/>
	 *   key: String segmentTypeClass<br/>
	 *   value: SegmentEditFactory editFactory<br/>
	 * }
	 */
	protected Map<String, Map<String, SegmentEditFactory>> segmentEditFactoriesByArticleContainerClass = new HashMap<String, Map<String,SegmentEditFactory>>();

//	protected static boolean isValidArticleContainerClass(String articleContainerClass)
//	{
//		return
//				Order.class.getName().equals(articleContainerClass) ||
//				Offer.class.getName().equals(articleContainerClass) ||
//				SegmentEditFactory.SEGMENTCONTEXT_INVOICE.equals(articleContainerClass) ||
//				SegmentEditFactory.SEGMENTCONTEXT_DELIVERY_NOTE.equals(articleContainerClass) ||
//				SegmentEditFactory.SEGMENTCONTEXT_RECEPTION_NOTE.equals(articleContainerClass);
//	}
//
//	protected static void assertValidArticleContainerClass(String articleContainerClass)
//	{
//		if (!isValidArticleContainerClass(articleContainerClass))
//			throw new IllegalArgumentException("articleContainerClass \""+articleContainerClass+"\" is not valid!"); //$NON-NLS-1$ //$NON-NLS-2$
//	}

	protected Map<String, SegmentEditFactory> getSegmentEditFactories(String articleContainerClass)
	{
//		assertValidArticleContainerClass(articleContainerClass);

		Map<String, SegmentEditFactory> res = segmentEditFactoriesByArticleContainerClass.get(articleContainerClass);
		if (res == null) {
			res = new HashMap<String, SegmentEditFactory>();
			segmentEditFactoriesByArticleContainerClass.put(articleContainerClass, res);
		}
		return res;
	}

	/**
	 * @param articleContainerClass
	 * @param segmentTypeClass This class will be resolved recursively. Means you can
	 *		subclass a <tt>SegmentType</tt> and it will use the parent's factory, if you
	 *		don't override it for your child.
	 * @param throwExceptionIfNotFound If <tt>false</tt> return <tt>null</tt>, if
	 * <tt>true</tt> throw an {@link IllegalStateException}, in case nothing can be found.
	 *
	 * @return
	 */
	public SegmentEditFactory getSegmentEditFactory(
			Class<?> articleContainerClass, Class<? extends SegmentType> segmentTypeClass,
			boolean throwExceptionIfNotFound)
	{
		Class<?> searchClass = articleContainerClass;
		SegmentEditFactory factory = null;
		while (searchClass != null) {
			factory = getSegmentEditFactory(searchClass.getName(), segmentTypeClass);
			if (factory != null)
				return factory;
			Class<?>[] interfaces = searchClass.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				factory = getSegmentEditFactory(interfaces[i].getName(), segmentTypeClass);
				if (factory != null)
					return factory;
			}
			searchClass = searchClass.getSuperclass();
		}
		if (throwExceptionIfNotFound && factory == null)
			throw new IllegalStateException("Nothing registered for articleContainerClass=\""+articleContainerClass+"\", segmentTypeClass=\""+segmentTypeClass.getName()+"\" (or a super-class)!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return factory;
		
	}

	private SegmentEditFactory getSegmentEditFactory(String articleContainerClass, Class<?> segmentTypeClass) {
		Map<String, SegmentEditFactory> m = getSegmentEditFactories(articleContainerClass);
		SegmentEditFactory factory = null;
		Class<?> clazz = segmentTypeClass;
		do {
			factory = m.get(clazz.getName());
			clazz = clazz.getSuperclass();
		} while (factory == null && clazz != Object.class);
		return factory;
	}

	protected void addSegmentEditFactory(SegmentEditFactory sef)
	{
		Map<String, SegmentEditFactory> sefMap = getSegmentEditFactories(sef.getArticleContainerClass());
		sefMap.put(sef.getSegmentTypeClass(), sef);
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.IEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID()
	{
		return "org.nightlabs.jfire.trade.ui.segmentEditFactory"; //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.IEPProcessor#processElement(IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
			throws Exception
	{
		try {
			SegmentEditFactory segmentEditFactory = (SegmentEditFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			String segmentTypeClass = element.getAttribute("segmentTypeClass"); //$NON-NLS-1$
			String articleContainerClass = element.getAttribute("articleContainerClass"); //$NON-NLS-1$
			String segmentEditFactoryName = element.getAttribute("name"); //$NON-NLS-1$

//			assertValidArticleContainerClass(articleContainerClass);
			if (segmentTypeClass == null || "".equals(segmentTypeClass)) //$NON-NLS-1$
				throw new EPProcessorException("segmentTypeClass undefined!"); //$NON-NLS-1$

			segmentEditFactory.init(segmentEditFactoryName, articleContainerClass, segmentTypeClass);

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
