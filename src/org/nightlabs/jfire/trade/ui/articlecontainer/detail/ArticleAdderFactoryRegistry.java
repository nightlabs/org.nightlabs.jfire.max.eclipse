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

import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.SegmentType;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ArticleAdderFactoryRegistry
extends SegmentTypeProductTypeDependentFactoryRegistry
{
	protected static ArticleAdderFactoryRegistry _sharedInstance = null;

	public static synchronized ArticleAdderFactoryRegistry sharedInstance()
	throws EPProcessorException
	{
		if (_sharedInstance == null) {
			_sharedInstance = new ArticleAdderFactoryRegistry();
			_sharedInstance.process();
		}

		return _sharedInstance;
	}

	/**
	 * This method finds a <tt>ArticleAdderFactory</tt> according to the given
	 * parameters. For <tt>segmentTypeClass</tt> and <tt>productTypeClass</tt>,
	 * the inheritence tree will be iterated in case no direct match exists.
	 * The inheritance search will first try to find a closer match for the
	 * <tt>productTypeClass</tt>, then for <tt>segmentTypeClass</tt>.
	 * This behaviour might change!!!
	 *
	 * @param articleContainerClass
	 * @param segmentTypeClass
	 * @param productTypeClass
	 * @param throwExceptionIfNotFound Whether or not to throw an {@link IllegalStateException}. If <tt>false</tt>, <tt>null</tt> will be returned instead.
	 * @return An instance of <tt>ArticleAdderFactory</tt> or <tt>null</tt> (if allowed).
	 */
	public ArticleAdderFactory getArticleAdderFactory(
			String articleContainerClass, Class<? extends SegmentType> segmentTypeClass,
			Class<? extends ProductType> productTypeClass, boolean throwExceptionIfNotFound)
	{
		return (ArticleAdderFactory) super.getFactory(articleContainerClass, segmentTypeClass, productTypeClass, throwExceptionIfNotFound);
	}

	@Override
	protected void addFactory(SegmentTypeProductTypeDependentFactory factory)
	{
		if (!(factory instanceof ArticleAdderFactory))
			throw new ClassCastException("Factory is an instance of \""+(factory == null ? "null" : factory.getClass().getName())+"\", but expected is \""+ArticleAdderFactory.class.getName()+"\"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		if (!Order.class.getName().equals(factory.getArticleContainerClass()) &&
				!Offer.class.getName().equals(factory.getArticleContainerClass()))
			throw new IllegalArgumentException("Unsupported ArticleContainer class! Can only add Articles to Orders and Offers!"); //$NON-NLS-1$
		super.addFactory(factory);
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.IEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID()
	{
		return "org.nightlabs.jfire.trade.ui.articleAdderFactory"; //$NON-NLS-1$
	}
}
