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
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.reporting.admin.ui.oda.jfs.client.ui;

import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.OdaDesignSession;
import org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSetWizardPageCore;
import org.eclipse.datatools.connectivity.oda.design.ui.manifest.DataSetUIElement;
import org.eclipse.datatools.connectivity.oda.design.ui.wizards.DataSetWizard;

/**
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 *
 */
public class JFSDataSetWizard extends DataSetWizard {

	/**
	 * 
	 */
	public JFSDataSetWizard() {
		super();
	}

	public JFSDataSetWizard(String dummyString) {
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSetWizardBase#createWizardPage(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	protected DataSetWizardPageCore createWizardPage(String arg0, String arg1, String arg2) throws OdaException {
		// TODO Auto-generated method stub
		return super.createWizardPage(arg0, arg1, arg2);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.datatools.connectivity.oda.design.internal.ui.DataSetWizardBase#initialize(org.eclipse.datatools.connectivity.oda.design.OdaDesignSession, org.eclipse.datatools.connectivity.oda.design.ui.manifest.DataSetUIElement)
	 */
	@Override
	public void initialize(OdaDesignSession arg0, DataSetUIElement arg1) throws OdaException {
		// TODO Auto-generated method stub
		super.initialize(arg0, arg1);
	}
	
	
}
