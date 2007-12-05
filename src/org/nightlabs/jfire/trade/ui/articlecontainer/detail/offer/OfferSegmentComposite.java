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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class OfferSegmentComposite extends XComposite
{
	protected OfferSegmentEdit offerSegmentEdit;

	protected XComposite articleAdderArea;
	protected Label articleAdderPlaceholderLabel;
	protected XComposite articleEditArea;

	/**
	 * @param parent
	 * @param style
	 */
	public OfferSegmentComposite(Composite parent, OfferSegmentEdit _orderSegmentEdit)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.offerSegmentEdit = _orderSegmentEdit;

		articleAdderArea = new XComposite(this, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		articleAdderArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		articleAdderPlaceholderLabel = new Label(articleAdderArea, SWT.NONE);
		articleAdderPlaceholderLabel.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.offer.OfferSegmentComposite.articleAdderPlaceholderLabel.text")); //$NON-NLS-1$

		articleEditArea = new XComposite(this, SWT.BORDER, LayoutMode.TIGHT_WRAPPER);
		articleEditArea.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		addDisposeListener(new DisposeListener(){
			public void widgetDisposed(DisposeEvent e)
			{
				offerSegmentEdit.onDispose();
				removeDisposeListener(this);
			}
		});
	}
}
