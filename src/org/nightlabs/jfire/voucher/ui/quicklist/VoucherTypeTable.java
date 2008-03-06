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

package org.nightlabs.jfire.voucher.ui.quicklist;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeTable;
import org.nightlabs.jfire.voucher.store.VoucherType;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public class VoucherTypeTable 
extends AbstractProductTypeTable<VoucherType> 
{
	public VoucherTypeTable(Composite parent, VoucherTypeQuickListFilter filter) {
		super(parent, filter);
	}
	
	public VoucherTypeTable(Composite parent, VoucherTypeQuickListFilter filter, int viewerStyle) {
		super(parent, filter, viewerStyle);
	}	
}

