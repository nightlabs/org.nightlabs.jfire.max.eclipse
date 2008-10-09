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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.IAction;
import org.nightlabs.base.ui.action.IXContributionItem;
import org.nightlabs.base.ui.action.registry.AbstractActionRegistry;
import org.nightlabs.base.ui.action.registry.ActionDescriptor;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;

public class ArticleContainerActionRegistry extends AbstractActionRegistry
{
	public static final String ATTRIBUTE_ARTICLE_CONTAINER_CLASS = "articleContainerClass"; //$NON-NLS-1$

//	private static ArticleContainerActionRegistry _sharedInstance;
	private static Map<Class<? extends ArticleContainer>, ArticleContainerActionRegistry> articleContainerClass2sharedInstance = new HashMap<Class<? extends ArticleContainer>, ArticleContainerActionRegistry>();

	public static ArticleContainerActionRegistry sharedInstance(ArticleContainer articleContainer)
	throws EPProcessorException
	{
		if (articleContainer == null)
			throw new IllegalArgumentException("articleContainer == null");

		return sharedInstance(articleContainer.getClass());
	}

	private static synchronized ArticleContainerActionRegistry sharedInstance(Class<? extends ArticleContainer> articleContainerClass)
	throws EPProcessorException
	{
		if (articleContainerClass == null)
			throw new IllegalArgumentException("articleContainerClass == null");

		ArticleContainerActionRegistry _sharedInstance = articleContainerClass2sharedInstance.get(articleContainerClass);
		if (_sharedInstance == null) {
			_sharedInstance = new ArticleContainerActionRegistry(articleContainerClass);
			_sharedInstance.process();
			articleContainerClass2sharedInstance.put(articleContainerClass, _sharedInstance);
		}

		return _sharedInstance;
	}

	private Class<? extends ArticleContainer> articleContainerClass;

	protected ArticleContainerActionRegistry(Class<? extends ArticleContainer> articleContainerClass) {
		if (articleContainerClass == null)
			throw new IllegalArgumentException("articleContainerClass == null");

		this.articleContainerClass = articleContainerClass;
	}

	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return articleContainerClass;
	}

	private static final String ATTRIBUTE_NAME_ACTION_CLASS = "class"; //$NON-NLS-1$

	@Override
	protected ActionDescriptor createActionDescriptor() {
		return new ArticleContainerActionDescriptor();
	}

	@Override
	protected boolean initActionDescriptor(ActionDescriptor _actionDescriptor, IExtension extension, IConfigurationElement element)
	throws EPProcessorException
	{
		ArticleContainerActionDescriptor actionDescriptor = (ArticleContainerActionDescriptor) _actionDescriptor;
		String actionID = actionDescriptor.getID();

		String actionDescriptorArticleContainerClass = element.getAttribute(ATTRIBUTE_ARTICLE_CONTAINER_CLASS);
		if (actionDescriptorArticleContainerClass == null || "".equals(actionDescriptorArticleContainerClass))
			actionDescriptorArticleContainerClass = ArticleContainer.class.getName();

		actionDescriptor.setArticleContainerClass(actionDescriptorArticleContainerClass);

		int matchDistanceNewActionDescriptor = actionDescriptor.calculateArticleContainerClassMatchDistance(this.articleContainerClass);
		if (matchDistanceNewActionDescriptor < 0)
			return false;

		ArticleContainerActionDescriptor oldActionDescriptor = (ArticleContainerActionDescriptor) getActionDescriptor(actionID, false);
		if (oldActionDescriptor != null) {
			int matchDistanceOldActionDescriptor = oldActionDescriptor.calculateArticleContainerClassMatchDistance(this.articleContainerClass);
			if (matchDistanceOldActionDescriptor < matchDistanceNewActionDescriptor)
				return false; // keep the old one - it's more specific than the new one
		}

		return true;
	}

	@Override
	protected Object createActionOrContributionItem(IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		try {
			return element.createExecutableExtension(ATTRIBUTE_NAME_ACTION_CLASS);
		} catch (CoreException e) {
			throw new EPProcessorException(e);
		}
//		IArticleContainerAction res;
//		try {
//			res = (IArticleContainerAction) element.createExecutableExtension(ATTRIBUTE_NAME_ACTION_CLASS);
//		} catch (CoreException e) {
//			throw new EPProcessorException(e);
//		}
//		return res;
	}

	@Override
	protected void initAction(IAction _action, IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		IArticleContainerAction action = (IArticleContainerAction) _action;
		action.init(this);
	}

	@Override
	protected void initContributionItem(IXContributionItem contributionItem, IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		if (contributionItem instanceof IArticleContainerContributionItem)
			((IArticleContainerContributionItem)contributionItem).init(this);
	}

	@Override
	public String getExtensionPointID()
	{
		return "org.nightlabs.jfire.trade.ui.articleContainerAction"; //$NON-NLS-1$
	}

	@Override
	protected String getActionElementName()
	{
		return "articleContainerAction"; //$NON-NLS-1$
	}

	private ArticleContainerEditorActionBarContributor activeArticleContainerEditorActionBarContributor = null;

	public ArticleContainerEdit getActiveArticleContainerEdit() {
		return activeArticleContainerEditorActionBarContributor != null ? activeArticleContainerEditorActionBarContributor.getActiveArticleContainerEdit() : null;
	}

	protected void setActiveArticleContainerEditorActionBarContributor(
			ArticleContainerEditorActionBarContributor activeArticleContainerEditorActionBarContributor)
	{
		this.activeArticleContainerEditorActionBarContributor = activeArticleContainerEditorActionBarContributor;
	}
}
