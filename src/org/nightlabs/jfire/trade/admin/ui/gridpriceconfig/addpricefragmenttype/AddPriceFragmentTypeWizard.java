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

package org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.addpricefragmenttype;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.IDynamicPathWizardPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.dao.PriceFragmentTypeDAO;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.Dimension;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValue;
import org.nightlabs.jfire.trade.admin.ui.gridpriceconfig.DimensionValue.PriceFragmentTypeDimensionValue;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class AddPriceFragmentTypeWizard extends DynamicPathWizard
{
	private Dimension<PriceFragmentTypeDimensionValue> dimension;
	private PriceFragmentTypeSelectionPage priceFragmentTypeSelectionPage;
	private CreatePriceFragmentTypePage createPriceFragmentTypePage;
	private boolean createNewPriceFragmentTypeEnabled = false;

	public AddPriceFragmentTypeWizard(Dimension<PriceFragmentTypeDimensionValue> dimension)
	{
		this.dimension = dimension;
		setForcePreviousAndNextButtons(true);
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizard#createWizardEntryPage()
	 */
	@Override
	public IDynamicPathWizardPage createWizardEntryPage()
	{
		priceFragmentTypeSelectionPage = new PriceFragmentTypeSelectionPage();
		createPriceFragmentTypePage = new CreatePriceFragmentTypePage();
		return priceFragmentTypeSelectionPage;
	}

	@Override
	public boolean performFinish()
	{
		try {
			PriceFragmentType priceFragmentType = null;
			if (createNewPriceFragmentTypeEnabled) {
//				I18nTextBuffer priceFragmentTypeNameBuffer = createPriceFragmentTypePage.getPriceFragmentTypeNameBuffer();
//
//				String priceFragmentTypeID = createPriceFragmentTypePage.getPriceFragmentTypeID().getText();
//				if ("".equals(priceFragmentTypeID)) //$NON-NLS-1$
//					priceFragmentTypeID = ObjectIDUtil.makeValidIDString(
//							priceFragmentTypeNameBuffer.getText(I18nText.DEFAULT_LANGUAGEID), true
//					);
//
//				priceFragmentType = new PriceFragmentType(
//						Login.getLogin().getOrganisationID(),
//						priceFragmentTypeID);
//
////				priceFragmentTypeNameBuffer.store(priceFragmentType.getPriceFragmentTypeName()); // TO DO uncomment

				priceFragmentType = createPriceFragmentTypePage.createPriceFragmentType();
				priceFragmentType = PriceFragmentTypeDAO.sharedInstance().storePriceFragmentType(
						priceFragmentType, true,
						new String[] { FetchPlan.DEFAULT, PriceFragmentType.FETCH_GROUP_NAME },
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor()
				);
			}
			else
				priceFragmentType = priceFragmentTypeSelectionPage.getSelectedPriceFragmentType();

			if (priceFragmentType == null)
				throw new IllegalStateException("customerGroup was neither created nor selected!"); //$NON-NLS-1$

			dimension.guiFeedbackAddDimensionValue(
					new DimensionValue.PriceFragmentTypeDimensionValue(dimension, priceFragmentType));

			return true;
		} catch (Exception x) {
			ExceptionHandlerRegistry.asyncHandleException(x);
			throw new RuntimeException(x);
		}
	}

	/**
	 * @param createPriceFragmentTypeEnabled The createPriceFragmentTypeEnabled to set.
	 */
	public void setCreateNewPriceFragmentTypeEnabled( boolean enabled)
	{
		removeAllDynamicWizardPages();
		if (enabled)
			addDynamicWizardPage(createPriceFragmentTypePage);

		this.createNewPriceFragmentTypeEnabled = enabled;
		updateDialog();
	}
}
