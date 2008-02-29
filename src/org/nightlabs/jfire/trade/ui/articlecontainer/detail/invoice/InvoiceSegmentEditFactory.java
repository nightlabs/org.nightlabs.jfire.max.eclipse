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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.invoice;


import org.nightlabs.jfire.trade.ArticleSegmentGroup;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractSegmentEditFactory;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.GeneralEditorComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit;

public class InvoiceSegmentEditFactory extends AbstractSegmentEditFactory
{

	public InvoiceSegmentEditFactory()
	{
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEditFactory#createSegmentEdit(GeneralEditorComposite, java.lang.String, org.nightlabs.jfire.trade.ui.ArticleSegmentGroup)
	 */
	public SegmentEdit createSegmentEdit(GeneralEditorComposite generalEditorComposite,
			String articleContainerClass, ArticleSegmentGroup articleSegmentGroup)
	{
		SegmentEdit segmentEdit = new InvoiceSegmentEdit();
		segmentEdit.init(this, generalEditorComposite, articleContainerClass, articleSegmentGroup);
		return segmentEdit;
	}

}
