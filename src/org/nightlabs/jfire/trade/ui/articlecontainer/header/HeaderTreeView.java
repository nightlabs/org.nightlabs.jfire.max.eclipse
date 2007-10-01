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

package org.nightlabs.jfire.trade.ui.articlecontainer.header;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.selection.SelectionProviderProxy;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class HeaderTreeView 
extends LSDViewPart
{
	public static final String ID_VIEW = HeaderTreeView.class.getName();

	private HeaderTreeComposite headerTreeComposite;
	private SelectionProviderProxy selectionProviderProxy = new SelectionProviderProxy();

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		getSite().setSelectionProvider(selectionProviderProxy); // this *must* be done here, because it is too late in createPartContents(...)
	}

	public void createPartContents(Composite parent)
	{
		try {
//			Login.getLogin();
			// give the parent a GridLayout
//			parent.setLayout(new GridLayout(1, true));
			headerTreeComposite = new HeaderTreeComposite(parent, SWT.NONE, getSite());
			headerTreeComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			selectionProviderProxy.addRealSelectionProvider(headerTreeComposite);
		} catch (Exception x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
		}
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	public void setFocus()
	{
	}

	public HeaderTreeComposite getHeaderTreeComposite()
	{
		return headerTreeComposite;
	}
}
