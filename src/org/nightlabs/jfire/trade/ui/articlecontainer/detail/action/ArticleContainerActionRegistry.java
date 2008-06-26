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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.jface.action.IAction;
import org.nightlabs.base.ui.action.IXContributionItem;
import org.nightlabs.base.ui.action.registry.AbstractActionRegistry;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;

public class ArticleContainerActionRegistry extends AbstractActionRegistry
{
	private static ArticleContainerActionRegistry _sharedInstance;

	public static synchronized ArticleContainerActionRegistry sharedInstance()
	throws EPProcessorException
	{
		if (_sharedInstance == null) {
			_sharedInstance = new ArticleContainerActionRegistry();
			_sharedInstance.process();
		}

		return _sharedInstance;
	}

	protected ArticleContainerActionRegistry() { }

	private static final String ATTRIBUTE_NAME_ACTION_CLASS = "class"; //$NON-NLS-1$

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

	public ArticleContainerEditorActionBarContributor getActiveArticleContainerEditorActionBarContributor()
	{
		return activeArticleContainerEditorActionBarContributor;
	}
	protected void setActiveArticleContainerEditorActionBarContributor(
			ArticleContainerEditorActionBarContributor activeGeneralEditorActionBarContributor)
	{
		this.activeArticleContainerEditorActionBarContributor = activeGeneralEditorActionBarContributor;
	}
}
