package org.nightlabs.jfire.issuetracking.ui.projectphase;

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
import org.nightlabs.jfire.issue.project.id.ProjectPhaseID;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;

/**
 * Editor input for {@link ProjectPhaseEditor}s.
 * 
 * @author Chairat Kongarayawetchakun - chairatk[at]nightlabs[dot]de
 */
public class ProjectPhaseEditorInput extends JDOObjectEditorInput<ProjectPhaseID>
{
	/**
	 * Constructor for an existing projectPhase.
	 * @param projectPhaseID The projectPhase
	 */
	public ProjectPhaseEditorInput(ProjectPhaseID projectPhaseID)
	{
		super(projectPhaseID);
		setName("ProjectPhaseID"); //$NON-NLS-1$
	}
}
