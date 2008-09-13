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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.order;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.HeaderComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class OrderHeaderComposite
extends HeaderComposite
{
	private Order order;
	private Label createdLabel;
	private Label timestamp;
	private Label userName;
	
	public OrderHeaderComposite(ArticleContainerEditComposite articleContainerEditComposite, Order _order)
	{
		super(articleContainerEditComposite, articleContainerEditComposite, _order);
		this.order = _order;
		
		setLayout(new GridLayout(4, false));
		
		createdLabel = new Label(this, SWT.NONE);
		createdLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.order.OrderHeaderComposite.createLabel.text")); //$NON-NLS-1$
		timestamp = new Label(this, SWT.NONE);
		timestamp.setText(DateFormatter.formatDateShortTimeHMS(order.getCreateDT(), false));
		userName = new Label(this, SWT.NONE);
		userName.setText(order.getCreateUser().getName());
		
		createArticleContainerContextMenu();
	}

}
