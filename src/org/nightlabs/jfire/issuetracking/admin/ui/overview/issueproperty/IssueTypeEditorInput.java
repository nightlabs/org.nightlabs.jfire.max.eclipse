package org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty;

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
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.jfire.issuetracking.admin.ui.resource.Messages;

/**
 * Editor input for {@link IssueTypeEditor}s.
 * 
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 */
public class IssueTypeEditorInput 
extends JDOObjectEditorInput<IssueTypeID>
{
	/**
	 * Constructor for an existing issue type.
	 * @param issueTypeID The issue type
	 */
	public IssueTypeEditorInput(IssueTypeID issueTypeID)
	{
		super(issueTypeID);
		setName(Messages.getString("org.nightlabs.jfire.issuetracking.admin.ui.overview.issueproperty.IssueTypeEditorInput.name")); //$NON-NLS-1$
	}
}
