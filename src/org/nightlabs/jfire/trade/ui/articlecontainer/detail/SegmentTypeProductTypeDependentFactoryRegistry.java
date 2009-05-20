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
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.SegmentType;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class SegmentTypeProductTypeDependentFactoryRegistry
extends AbstractEPProcessor
{
	/**
	 * key: String articleContainerClass<br/>
	 * value: Map {<br/>
	 *		key: String segmentTypeClass<br/>
	 *		value: Map {<br/>
	 *			key: String productTypeClass<br/>
	 *			value: SegmentTypeProductTypeDependentFactory factory
	 *		}
	 * }
	 */
	protected Map<String, Map<String, Map<String, SegmentTypeProductTypeDependentFactory>>> factories = new HashMap<String, Map<String,Map<String,SegmentTypeProductTypeDependentFactory>>>();

	/**
	 * This method finds a <tt>SegmentTypeProductTypeDependentFactory</tt> according to the given
	 * parameters. For <tt>segmentTypeClass</tt> and <tt>productTypeClass</tt>,
	 * the inheritence tree will be iterated in case no direct match exists.
	 * The inheritance search will first try to find a closer match for the
	 * <tt>segmentTypeClass</tt>, then for the <tt>productTypeClass</tt>.
	 * Note, that this behaviour might still change!!!
	 *
	 * @param articleContainerClass
	 * @param segmentTypeClass
	 * @param productTypeClass
	 * @param throwExceptionIfNotFound Whether or not to throw an {@link IllegalStateException}. If <tt>false</tt>, <tt>null</tt> will be returned instead.
	 * @return An instance of <tt>SegmentTypeProductTypeDependentFactory</tt> or <tt>null</tt> (if allowed).
	 */
	protected SegmentTypeProductTypeDependentFactory getFactory(
			Class<?> articleContainerClass, Class<? extends SegmentType> segmentTypeClass,
			Class<? extends ProductType> productTypeClass, boolean throwExceptionIfNotFound)
	{
		Class<?> articleContainerC = articleContainerClass;
		SegmentTypeProductTypeDependentFactory factory = null;
		while (articleContainerC != null) {
			factory = getFactory(articleContainerC.getName(), segmentTypeClass, productTypeClass, false);
			if (factory != null)
				return factory;
			Class<?>[] interfaces = articleContainerC.getInterfaces();
			for (int i = 0; i < interfaces.length; i++) {
				factory = getFactory(interfaces[i].getName(), segmentTypeClass, productTypeClass, false);
				if (factory != null)
					return factory;
			}
			articleContainerC = articleContainerC.getSuperclass();
		}
		if (throwExceptionIfNotFound && factory == null)
			throw new IllegalStateException(
					"Nothing registered for articleContainerClass=\"" //$NON-NLS-1$
					+ articleContainerClass.getName() + "\", segmentTypeClass=\"" //$NON-NLS-1$
					+ segmentTypeClass.getName() + "\" " //$NON-NLS-1$
					+ " productTypeClass=\"" + productTypeClass.getName() + "\" (or a super-class)!" //$NON-NLS-1$  //$NON-NLS-2$
			);
		return factory;

	}

	private SegmentTypeProductTypeDependentFactory getFactory(
			String articleContainerClass, Class<? extends SegmentType> segmentTypeClass,
			Class<? extends ProductType> productTypeClass, boolean throwExceptionIfNotFound)
	{

		Map<String, Map<String, SegmentTypeProductTypeDependentFactory>> aefsBySegmentTypeClass = factories.get(articleContainerClass);
		if (aefsBySegmentTypeClass == null) {
			if (throwExceptionIfNotFound)
				throw new IllegalStateException("No SegmentTypeProductTypeDependentFactory registered for articleContainerClass=\""+articleContainerClass+"\"!!!"); //$NON-NLS-1$ //$NON-NLS-2$
			else
				return null;
		}

		SegmentTypeProductTypeDependentFactory factory = null;

		// We iterate first through the segmentType classes and second through
		// the productType classes.
		Class<?> segmentTypeSearchC = segmentTypeClass;
		do {

			Class<?> productTypeSearchC = productTypeClass;
			do {

				Map<String, SegmentTypeProductTypeDependentFactory> aefsByProductTypeClass = aefsBySegmentTypeClass.get(segmentTypeSearchC.getName());
				if (aefsByProductTypeClass != null) {

					factory = aefsByProductTypeClass.get(productTypeSearchC.getName());

				} // if (aefsByProductTypeClass != null) {

				productTypeSearchC = productTypeSearchC.getSuperclass();
			} while (factory == null && productTypeSearchC != Object.class);

			segmentTypeSearchC = segmentTypeSearchC.getSuperclass();
		} while (factory == null && segmentTypeSearchC != Object.class);

		if (throwExceptionIfNotFound && factory == null)
			throw new IllegalStateException("No SegmentTypeProductTypeDependentFactory registered for segmentTypeClass=\""+segmentTypeClass.getName()+"\" & productTypeClass=\""+productTypeClass.getName()+"\" within articleContainerClass=\""+articleContainerClass+"\"!!!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		return factory;
	}

	protected void addFactory(SegmentTypeProductTypeDependentFactory factory)
	{
		Map<String, Map<String, SegmentTypeProductTypeDependentFactory>> map1 = factories.get(factory.getArticleContainerClass());
		if (map1 == null) {
			map1 = new HashMap<String, Map<String,SegmentTypeProductTypeDependentFactory>>();
			factories.put(factory.getArticleContainerClass(), map1);
		}

		Map<String, SegmentTypeProductTypeDependentFactory> map2 = map1.get(factory.getSegmentTypeClass());
		if (map2 == null) {
			map2 = new HashMap<String, SegmentTypeProductTypeDependentFactory>();
			map1.put(factory.getSegmentTypeClass(), map2);
		}

		map2.put(factory.getProductTypeClass(), factory);
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#processElement(IExtension, org.eclipse.core.runtime.IConfigurationElement)
	 */
	@Override
	public void processElement(IExtension extension, IConfigurationElement element)
	throws Exception
	{
		try {
			SegmentTypeProductTypeDependentFactory factory = (SegmentTypeProductTypeDependentFactory) element.createExecutableExtension("class"); //$NON-NLS-1$
			String segmentTypeClass = element.getAttribute("segmentTypeClass"); //$NON-NLS-1$
			String articleContainerClass = element.getAttribute("articleContainerClass"); //$NON-NLS-1$
			String productTypeClass = element.getAttribute("productTypeClass"); //$NON-NLS-1$
			String factoryName = element.getAttribute("name"); //$NON-NLS-1$

//			SegmentEditFactoryRegistry.assertValidArticleContainerClass(articleContainerClass);
			if (segmentTypeClass == null || "".equals(segmentTypeClass)) //$NON-NLS-1$
				throw new EPProcessorException("segmentTypeClass undefined!"); //$NON-NLS-1$

			if (productTypeClass == null || "".equals(productTypeClass)) //$NON-NLS-1$
				throw new EPProcessorException("productTypeClass undefined!"); //$NON-NLS-1$

			factory.setSegmentTypeClass(segmentTypeClass);
			factory.setArticleContainerClass(articleContainerClass);
			factory.setProductTypeClass(productTypeClass);
			factory.setName(factoryName);

			processChildElements(factory, element.getChildren());

			addFactory(factory);
		} catch (Throwable t) {
			throw new EPProcessorException("Extension to "+getExtensionPointID()+" with class "+element.getAttribute("class")+" has errors!", t); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}

	protected void processChildElements(SegmentTypeProductTypeDependentFactory factory, IConfigurationElement[] children)
	throws EPProcessorException
	{
	}
}
