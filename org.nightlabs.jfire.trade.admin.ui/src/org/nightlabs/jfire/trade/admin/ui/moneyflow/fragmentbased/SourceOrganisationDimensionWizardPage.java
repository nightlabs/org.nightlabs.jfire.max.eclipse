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

package org.nightlabs.jfire.trade.admin.ui.moneyflow.fragmentbased;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimensionWizardPage;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class SourceOrganisationDimensionWizardPage extends WizardHopPage
		implements MappingDimensionWizardPage {

//	/**
//	 * @param pageName
//	 * @param title
//	 */
//	public SourceOrganisationDimensionWizardPage(String title) {
//		super(SourceOrganisationDimensionWizardPage.class.getName(), title);
//	}

	/**
	 * @param pageName
	 * @param title
	 */
	public SourceOrganisationDimensionWizardPage() {
		super(SourceOrganisationDimensionWizardPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.fragmentbased.SourceOrganisationDimensionWizardPage.title")); //$NON-NLS-1$
	}

//	/**
//	 * @param pageName
//	 * @param title
//	 * @param titleImage
//	 */
//	public SourceOrganisationDimensionWizardPage(String pageName, String title,
//			ImageDescriptor titleImage) {
//		super(pageName, title, titleImage);
//	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		Label label = new Label(wrapper, SWT.WRAP);
		label.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.fragmentbased.SourceOrganisationDimensionWizardPage.notYetFullySupportedLabel.text")); //$NON-NLS-1$
		return wrapper;
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimensionWizardPage#getMoneyFlowMappingDimensionID()
	 */
	public String getMoneyFlowMappingDimensionID() {
		return org.nightlabs.jfire.accounting.book.mappingbased.SourceOrganisationDimension.MONEY_FLOW_DIMENSION_ID;
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimensionWizardPage#getDimensionValue()
	 */
	public String getDimensionValue() {
		return null;
	}

}
