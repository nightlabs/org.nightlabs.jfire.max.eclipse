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

public class ArticleEditActionRegistry extends AbstractActionRegistry
{
	private static ArticleEditActionRegistry _sharedInstance;

	private static boolean initializingSharedInstance = false;
	public static synchronized ArticleEditActionRegistry sharedInstance()
	throws EPProcessorException
	{
		if (initializingSharedInstance)
			throw new IllegalStateException("Circular call to the method sharedInstance() during initialization!"); //$NON-NLS-1$

		if (_sharedInstance == null) {
			initializingSharedInstance = true;
			try {
				_sharedInstance = new ArticleEditActionRegistry();
				_sharedInstance.process();
			} finally {
				initializingSharedInstance = false;
			}
		}

		return _sharedInstance;
	}

	protected ArticleEditActionRegistry() { }

	public String getExtensionPointID()
	{
		return "org.nightlabs.jfire.trade.ui.articleEditAction"; //$NON-NLS-1$
	}

	protected String getActionElementName()
	{
		return "articleEditAction"; //$NON-NLS-1$
	}

	private GeneralEditorActionBarContributor activeGeneralEditorActionBarContributor = null;

	public GeneralEditorActionBarContributor getActiveGeneralEditorActionBarContributor()
	{
		return activeGeneralEditorActionBarContributor;
	}
	protected void setActiveGeneralEditorActionBarContributor(
			GeneralEditorActionBarContributor activeGeneralEditorActionBarContributor)
	{
		this.activeGeneralEditorActionBarContributor = activeGeneralEditorActionBarContributor;
	}

	private static final String ATTRIBUTE_NAME_ACTION_CLASS = "class"; //$NON-NLS-1$

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

	protected void initAction(IAction _action, IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		IArticleEditAction action = (IArticleEditAction) _action;
		action.init(this);
	}

	protected void initContributionItem(IXContributionItem contributionItem, IExtension extension, IConfigurationElement element) throws EPProcessorException
	{
		if (contributionItem instanceof IArticleEditContributionItem)
			((IArticleEditContributionItem)contributionItem).init(this);
	}
}
