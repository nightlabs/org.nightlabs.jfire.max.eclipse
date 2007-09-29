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

package org.nightlabs.jfire.trade.articlecontainer.detail;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class AbstractSegmentTypeProductTypeDependentFactory
implements SegmentTypeProductTypeDependentFactory
{
	private String name;
	private String segmentContext;
	private String segmentTypeClass;
	private String productTypeClass;

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.SegmentTypeProductTypeDependentFactory#setName(java.lang.String)
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.SegmentTypeProductTypeDependentFactory#getName()
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.SegmentTypeProductTypeDependentFactory#setSegmentContext(java.lang.String)
	 */
	public void setSegmentContext(String segmentContext)
	{
		this.segmentContext = segmentContext;
	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.SegmentTypeProductTypeDependentFactory#getSegmentContext()
	 */
	public String getSegmentContext()
	{
		return segmentContext;
	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.SegmentTypeProductTypeDependentFactory#setSegmentTypeClass(java.lang.String)
	 */
	public void setSegmentTypeClass(String segmentTypeClass)
	{
		this.segmentTypeClass = segmentTypeClass;
	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.SegmentTypeProductTypeDependentFactory#getSegmentTypeClass()
	 */
	public String getSegmentTypeClass()
	{
		return segmentTypeClass;
	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.SegmentTypeProductTypeDependentFactory#setProductTypeClass(java.lang.String)
	 */
	public void setProductTypeClass(String productTypeClass)
	{
		this.productTypeClass = productTypeClass;
	}

	/**
	 * @see org.nightlabs.jfire.trade.articlecontainer.detail.SegmentTypeProductTypeDependentFactory#getProductTypeClass()
	 */
	public String getProductTypeClass()
	{
		return productTypeClass;
	}

}
