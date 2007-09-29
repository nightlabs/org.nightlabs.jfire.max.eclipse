package org.nightlabs.jfire.trade.account.editor;

/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
 ******************************************************************************/

import org.nightlabs.base.ui.editor.JDOObjectEditorInput;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.jfire.transfer.Anchor;
import org.nightlabs.jfire.transfer.id.AnchorID;

/**
 * Editor input for {@link AccountEditor}s.
 * 
 * @author Chairat Kongarayawetchakun - chairatk[at]nightlabs[dot]de
 */
public class AccountEditorInput extends JDOObjectEditorInput<AnchorID>
{
	/**
	 * Constructor for an existing account.
	 * @param anchorID The account
	 */
	public AccountEditorInput(AnchorID accountID)
	{
		super(accountID);
		setName(String.format(Messages.getString("org.nightlabs.jfire.trade.account.editor.AccountEditorInput.name"), Anchor.getPrimaryKey(accountID.organisationID, accountID.anchorTypeID, accountID.anchorID))); //$NON-NLS-1$
	}
}
