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

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.LabeledText;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.reporting.admin.ui.resource.Messages;
import org.nightlabs.jfire.reporting.oda.Connection;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class JDODriverConnectionWizardPage extends DynamicPathWizardPage {

	private XComposite wrapper;
	private LabeledText organisationID;
	
	public JDODriverConnectionWizardPage(String title) {
		super(JDODriverConnectionWizardPage.class.getName(), title);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPageContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		organisationID = new LabeledText(wrapper, Messages.getString("org.nightlabs.jfire.reporting.admin.ui.oda.JDODriverConnectionWizardPage.organisationIDLabel.text")); //$NON-NLS-1$
		return wrapper;
	}
	
	public String getOrganisationID() {
		if (wrapper == null || wrapper.isDisposed())
			throw new IllegalStateException("Wrapper of "+this.getClass().getName()+" is either null or disposed"); //$NON-NLS-1$ //$NON-NLS-2$
		return organisationID.getTextControl().getText();
	}
	
	public Map getConnectionProperties() {
		return Connection.createJDOConnectonProperties();
	}

}
