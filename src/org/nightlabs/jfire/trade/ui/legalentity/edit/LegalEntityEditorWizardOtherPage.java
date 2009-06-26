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

package org.nightlabs.jfire.trade.ui.legalentity.edit;

import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.base.ui.prop.edit.blockbased.FullDataBlockCoverageWizardPage;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class LegalEntityEditorWizardOtherPage extends
		FullDataBlockCoverageWizardPage {

	/**
	 * @param pageName
	 * @param person
	 * @param structBlockIDs
	 */
	public LegalEntityEditorWizardOtherPage(Person person) {
		super(
			LegalEntityEditorWizardOtherPage.class.getName(),
			Messages.getString("org.nightlabs.jfire.trade.ui.legalentity.edit.LegalEntityEditorWizardOtherPage.title"), //$NON-NLS-1$
			person,
			true,
			null
		);

		setImageDescriptor(
				SharedImages.getSharedImageDescriptor(
						TradePlugin.getDefault(),
						LegalEntityEditorWizardOtherPage.class, null, SharedImages.ImageDimension._75x70));
	}
	
	
//	create

}
