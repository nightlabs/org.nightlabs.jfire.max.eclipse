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


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface SegmentTypeProductTypeDependentFactory
{
	/**
	 * Same as {@link SegmentEditFactory#SEGMENTCONTEXT_ORDER}.
	 */
	public static final String SEGMENTCONTEXT_ORDER = SegmentEditFactory.SEGMENTCONTEXT_ORDER;
	/**
	 * Same as {@link SegmentEditFactory#SEGMENTCONTEXT_OFFER}.
	 */
	public static final String SEGMENTCONTEXT_OFFER = SegmentEditFactory.SEGMENTCONTEXT_OFFER;
	/**
	 * Same as {@link SegmentEditFactory#SEGMENTCONTEXT_INVOICE}.
	 */
	public static final String SEGMENTCONTEXT_INVOICE = SegmentEditFactory.SEGMENTCONTEXT_INVOICE;
	/**
	 * Same as {@link SegmentEditFactory#SEGMENTCONTEXT_DELIVERY_NOTE}.
	 */
	public static final String SEGMENTCONTEXT_DELIVERY = SegmentEditFactory.SEGMENTCONTEXT_DELIVERY_NOTE;

	/**
	 * This method is called by the SegmentEditFactoryRegistry while processing
	 * the declaration. It passes
	 * the name which is declared for the extension in the plugin.xml.
	 *
	 * @param name The name as declared for the extension in the plugin.xml.
	 */
	void setName(String name);

	/**
	 * @return the name which was previously set by {@link #setName(String)}.
	 */
	String getName();

	/**
	 * This method is called by the SegmentEditFactoryRegistry, which passes
	 * the segmentContext that is declared for the extension in the plugin.xml.
	 * <p>
	 * The registration of a <tt>SegmentEditFactory</tt> is dependent on two parameters:
	 * The segmentContext and the class of the SegmentType.
	 *
	 * @param segmentContext The segmentContext declared in the plugin.xml.
	 *
	 * @see #SEGMENTCONTEXT_ORDER
	 * @see #SEGMENTCONTEXT_OFFER
	 * @see #SEGMENTCONTEXT_INVOICE
	 * @see #SEGMENTCONTEXT_DELIVERY_NOTE
	 */
	void setSegmentContext(String segmentContext);

	/**
	 * @return the segmentContext that was previously set by {@link #setSegmentContext(String)}
	 */
	String getSegmentContext();

	/**
	 * This method is called by the SegmentEditFactoryRegistry, which passes
	 * the segmentTypeClass that is declared for the extension in the plugin.xml.
	 * <p>
	 * The registration of a <tt>SegmentEditFactory</tt> is dependent on two parameters:
	 * The segmentContext and the class of the SegmentType.
	 *
	 * @param segmentTypeClass The segmentTypeClass declared in the plugin.xml.
	 */
	void setSegmentTypeClass(String segmentTypeClass);

	/**
	 * @return the segmentTypeClass which was previously set by {@link #setSegmentTypeClass(String)}.
	 */
	String getSegmentTypeClass();

	/**
	 * This method is called by the SegmentEditFactoryRegistry, which passes
	 * the productTypeClass that is declared for the extension in the plugin.xml.
	 * <p>
	 * The registration of a <tt>ArticleEditFactory</tt> is dependent on the class
	 * of the ProductType and within the scope of a {@link SegmentEditFactory}.
	 *
	 * @param productTypeClass The productTypeClass declared in the plugin.xml.
	 */
	void setProductTypeClass(String productTypeClass);

	/**
	 * @return the productTypeClass which was previously set by {@link #setProductTypeClass(String)}.
	 */
	String getProductTypeClass();

}
