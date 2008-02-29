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

import org.eclipse.core.runtime.IConfigurationElement;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditActionDelegate;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ArticleEditFactoryRegistry
extends SegmentTypeProductTypeDependentFactoryRegistry
{
	protected static ArticleEditFactoryRegistry _sharedInstance = null;

	private static boolean initializingSharedInstance = false;
	public static synchronized ArticleEditFactoryRegistry sharedInstance()
	throws EPProcessorException
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!"); //$NON-NLS-1$

		if (_sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				_sharedInstance = new ArticleEditFactoryRegistry();
				_sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return _sharedInstance;
	}

	/**
	 * This method finds a <tt>ArticleEditFactory</tt> according to the given
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
	 * @return An instance of <tt>ArticleEditFactory</tt> or <tt>null</tt> (if allowed).
	 */
	public ArticleEditFactory getArticleEditFactory(
			String articleContainerClass, Class segmentTypeClass,
			Class productTypeClass, boolean throwExceptionIfNotFound)
	{
		return (ArticleEditFactory) super.getFactory(articleContainerClass, segmentTypeClass,
			productTypeClass, throwExceptionIfNotFound);
	}

	@Override
	protected void addFactory(SegmentTypeProductTypeDependentFactory factory)
	{
		if (!(factory instanceof ArticleEditFactory))
			throw new ClassCastException("Factory is an instance of \""+(factory == null ? "null" : factory.getClass().getName())+"\", but expected is \""+ArticleEditFactory.class.getName()+"\"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

		super.addFactory(factory);
	}

	/**
	 * @see org.nightlabs.base.ui.extensionpoint.AbstractEPProcessor#getExtensionPointID()
	 */
	@Override
	public String getExtensionPointID()
	{
		return "org.nightlabs.jfire.trade.ui.articleEditFactory"; //$NON-NLS-1$
	}

	@Override
	protected void processChildElements(SegmentTypeProductTypeDependentFactory _factory, IConfigurationElement[] children)
	throws EPProcessorException
	{
		try {
			if (!(_factory instanceof ArticleEditFactory))
				throw new ClassCastException("Factory is an instance of \""+(_factory == null ? "null" : _factory.getClass().getName())+"\", but expected is \""+ArticleEditFactory.class.getName()+"\"!"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

			ArticleEditFactory factory = (ArticleEditFactory) _factory;

			for (int i = 0; i < children.length; ++i) {
				IConfigurationElement child = children[i];
				if (!"articleEditActionDelegate".equals(child.getName())) //$NON-NLS-1$
					throw new EPProcessorException("child element is unknown! name=" + child.getName()); //$NON-NLS-1$

				String articleEditActionID = child.getAttribute("articleEditActionID"); //$NON-NLS-1$
				IArticleEditActionDelegate delegate = (IArticleEditActionDelegate) child.createExecutableExtension("class"); //$NON-NLS-1$
				String name = child.getAttribute("name"); //$NON-NLS-1$
				delegate.init(articleEditActionID, name);
				factory.addArticleEditActionDelegate(delegate);
			}
		} catch (EPProcessorException x) {
			throw x;
		} catch (Exception x) {
			throw new EPProcessorException(x);
		}
	}
}
