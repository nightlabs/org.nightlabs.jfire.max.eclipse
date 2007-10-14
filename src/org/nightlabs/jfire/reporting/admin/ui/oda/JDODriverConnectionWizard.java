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

package org.nightlabs.jfire.reporting.admin.ui.oda;

import org.eclipse.birt.report.designer.ui.odadatasource.wizards.DefaultExtendedDataSourceWizard;
import org.eclipse.birt.report.model.api.DataSourceHandle;
import org.eclipse.birt.report.model.api.ModuleHandle;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class JDODriverConnectionWizard extends DefaultExtendedDataSourceWizard {
	
	private JDODriverConnectionWizardPage connectionWizardPage;
	private DataSourceHandle dataSourceHandle;
	
	
	public DataSourceHandle createDataSource(ModuleHandle handle) {
		this.dataSourceHandle = super.createDataSource(handle); 
		return dataSourceHandle;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.odadatasource.wizards.AbstractDataSourceConnectionWizard#doFinish()
	 */
	public boolean doFinish() {
//		try {
//			dataSourceHandle.setProperties(connectionWizardPage.getConnectionProperties());
//		} catch (SemanticException e) {
//			throw new RuntimeException(e);
//		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.birt.report.designer.ui.odadatasource.wizards.AbstractDataSourceConnectionWizard#doCancel()
	 */
	public boolean doCancel() {
		// TODO Auto-generated method stub
		return true;
	}

	public void addPages() {
		connectionWizardPage = new JDODriverConnectionWizardPage(Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.JDODriverConnectionWizard.title")); //$NON-NLS-1$
		addPage(connectionWizardPage);
	}

	
	

}
