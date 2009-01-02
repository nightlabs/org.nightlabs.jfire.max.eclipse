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

public class ArticleEditActionRegistry extends AbstractActionRegistry
{
	public static final String ATTRIBUTE_ARTICLE_CONTAINER_CLASS = "articleContainerClass"; //$NON-NLS-1$

//	private static ArticleEditActionRegistry _sharedInstance;
	private static Map<Class<? extends ArticleContainer>, ArticleEditActionRegistry> articleContainerClass2sharedInstance = new HashMap<Class<? extends ArticleContainer>, ArticleEditActionRegistry>();

	private static boolean initializingSharedInstance = false;

	public static ArticleEditActionRegistry sharedInstance(ArticleContainer articleContainer)
	throws EPProcessorException
	{
		if (articleContainer == null)
			throw new IllegalArgumentException("articleContainer == null"); //$NON-NLS-1$

		return sharedInstance(articleContainer.getClass());
	}

	private static synchronized ArticleEditActionRegistry sharedInstance(Class<? extends ArticleContainer> articleContainerClass)
	throws EPProcessorException
	{
		if (articleContainerClass == null)
			throw new IllegalArgumentException("articleContainerClass == null"); //$NON-NLS-1$

		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!"); //$NON-NLS-1$

		ArticleEditActionRegistry _sharedInstance = articleContainerClass2sharedInstance.get(articleContainerClass);
		if (_sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				_sharedInstance = new ArticleEditActionRegistry(articleContainerClass);
				_sharedInstance.process();
				articleContainerClass2sharedInstance.put(articleContainerClass, _sharedInstance);
			} finally {
				initializingSharedInstance = false;
			}
		}

		return _sharedInstance;
	}

	private Class<? extends ArticleContainer> articleContainerClass;

	protected ArticleEditActionRegistry(Class<? extends ArticleContainer> articleContainerClass) {
		if (articleContainerClass == null)
			throw new IllegalArgumentException("articleContainerClass == null"); //$NON-NLS-1$

		this.articleContainerClass = articleContainerClass;
	}

	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return articleContainerClass;
	}

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
		if (actionDescriptorArticleContainerClass == null || "".equals(actionDescriptorArticleContainerClass)) //$NON-NLS-1$
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
	public String getExtensionPointID()
	{
		return "org.nightlabs.jfire.trade.ui.articleEditAction"; //$NON-NLS-1$
	}

	@Override
	protected String getActionElementName()
	{
		return "articleEditAction"; //$NON-NLS-1$
	}

	private ArticleContainerEditorActionBarContributor activeArticleContainerEditorActionBarContributor = null;

//	public ArticleContainerEditorActionBarContributor getActiveArticleContainerEditorActionBarContributor()
//	{
//		return activeArticleContainerEditorActionBarContributor;
//	}

	public ArticleContainerEdit getActiveArticleContainerEdit()
	{
		return activeArticleContainerEditorActionBarContributor.getActiveArticleContainerEdit();
	}

	protected void setActiveArticleContainerEditorActionBarContributor(
			ArticleContainerEditorActionBarContributor activeArticleContainerEditorActionBarContributor)
	{
		this.activeArticleContainerEditorActionBarContributor = activeArticleContainerEditorActionBarContributor;
	}

	private static final String ATTRIBUTE_NAME_ACTION_CLASS = "class"; //$NON-NLS-1$

	@Override
	protected Object createActionOrContributionItem(IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		String className = element.getAttribute(ATTRIBUTE_NAME_ACTION_CLASS);
		if (className == null || "".equals(className)) //$NON-NLS-1$
			return new ArticleEditAction();

		IArticleEditAction res;
		try {
			res = (IArticleEditAction) element.createExecutableExtension(ATTRIBUTE_NAME_ACTION_CLASS);
		} catch (CoreException e) {
			throw new EPProcessorException(e);
		}
		return res;
	}

	@Override
	protected void initAction(IAction _action, IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		IArticleEditAction action = (IArticleEditAction) _action;
		action.init(this);
	}

	@Override
	protected void initContributionItem(IXContributionItem contributionItem, IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		if (contributionItem instanceof IArticleEditContributionItem)
			((IArticleEditContributionItem)contributionItem).init(this);
	}
}
