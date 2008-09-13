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

import org.eclipse.ui.IWorkbenchPartSite;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleSegmentGroup;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface SegmentEditFactory
{
//	public static final String SEGMENTCONTEXT_ORDER = "Order"; //$NON-NLS-1$
//	public static final String SEGMENTCONTEXT_OFFER = "Offer"; //$NON-NLS-1$
//	public static final String SEGMENTCONTEXT_INVOICE = "Invoice"; //$NON-NLS-1$
//	public static final String SEGMENTCONTEXT_DELIVERY_NOTE = "DeliveryNote"; //$NON-NLS-1$
//	public static final String SEGMENTCONTEXT_RECEPTION_NOTE = "ReceptionNote"; //$NON-NLS-1$

	/**
	 * This method is called by the SegmentEditFactoryRegistry after creation
	 * of a <code>SegmentEditFactory</code>.
	 * <p>
	 * The registration of a <tt>SegmentEditFactory</tt> is dependent on two parameters:
	 * The articleContainerClass and the class of the SegmentType.
	 * </p>
	 * 
	 * @param name The name as declared for the extension in the plugin.xml.
	 * @param articleContainerClass The articleContainerClass declared in the plugin.xml. This can
	 *		be one of {@link #SEGMENTCONTEXT_ORDER}, {@link #SEGMENTCONTEXT_OFFER},
	 *		{@link #SEGMENTCONTEXT_INVOICE}, {@link #SEGMENTCONTEXT_DELIVERY_NOTE}.
	 * @param segmentTypeClass The segmentTypeClass declared in the plugin.xml.
	 */
	void init(String name, String articleContainerClass, String segmentTypeClass);

	/**
	 * @return the name which was previously set by {@link #setAccountName(String)}.
	 */
	String getName();

	/**
	 * @return the articleContainerClass that was previously set by {@link #setArticleContainerClass(String)}
	 */
	String getArticleContainerClass();

	/**
	 * @return the segmentTypeClass which was previously set by {@link #setSegmentTypeClass(String)}.
	 */
	String getSegmentTypeClass();

	/**
	 * In your implementation of this method, you must create a new instance of
	 * <tt>SegmentEdit</tt> and pass the parameters to it
	 * via {@link SegmentEdit#init(IWorkbenchPartSite, ArticleContainerEdit, ArticleContainer, ArticleSegmentGroup)}.
	 * @param articleContainerEdit TODO
	 * @param articleContainerClass The articleContainerClass - one of
	 *		{@link #SEGMENTCONTEXT_ORDER},
	 *		{@link #SEGMENTCONTEXT_OFFER},
	 *		{@link #SEGMENTCONTEXT_INVOICE} or {@link #SEGMENTCONTEXT_DELIVERY_NOTE}.
	 * @param articleSegmentGroup The group of articles which should be exposed to the user for edit.
	 * @return a new <tt>SegmentEdit</tt> which will be used to render a <tt>Segment</tt>.
	 */
	SegmentEdit createSegmentEdit(
			ArticleContainerEdit articleContainerEdit, String articleContainerClass,
			ArticleSegmentGroup articleSegmentGroup);
}
