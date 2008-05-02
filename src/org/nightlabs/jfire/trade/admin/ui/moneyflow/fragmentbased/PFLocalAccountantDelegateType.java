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

import java.util.List;
import java.util.Map;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Account;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.PriceFragmentType;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.accounting.book.mappingbased.MappingBasedAccountantDelegate;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.accounting.book.mappingbased.PFMoneyFlowMapping;
import org.nightlabs.jfire.accounting.dao.PriceFragmentTypeDAO;
import org.nightlabs.jfire.accounting.id.PriceFragmentTypeID;
import org.nightlabs.jfire.idgenerator.IDGenerator;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateType;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.dao.LegalEntityDAO;
import org.nightlabs.jfire.transfer.Anchor;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.NLLocale;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class PFLocalAccountantDelegateType 
implements LocalAccountantDelegateType 
{
	/**
	 * 
	 */
	public PFLocalAccountantDelegateType() {
		super();
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateType#getDelegateClass()
	 */
	public Class getDelegateClass() {
		return MappingBasedAccountantDelegate.class;
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateType#getName()
	 */
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.fragmentbased.PFLocalAccountantDelegateType.name"); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateType#getDescription()
	 */
	public String getDescription() {
		return getName();
	}

	/**
	 * @see org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateType#createNewDelegate(java.lang.String, java.lang.String)
	 */
	public LocalAccountantDelegate createNewDelegate(
			LocalAccountantDelegate extendedDelegate,
			String organisationID,
			String localAccountantDelegateID
		)
	{
		if ((extendedDelegate != null) && !(extendedDelegate instanceof MappingBasedAccountantDelegate))
			throw new IllegalArgumentException("Can not extend a new MappingBasedAccountantDelegate from the given delegate: "+extendedDelegate.getClass().getName()); //$NON-NLS-1$
		return new MappingBasedAccountantDelegate(extendedDelegate, organisationID, localAccountantDelegateID);
	}

	public MoneyFlowMapping createNewMapping(
			ProductType productType,
			String packageType,
			Map dimensionValues,
			Currency currency,
			Account revenueAccount,
			Account expenseAccount, Account reverseRevenueAccount, Account reverseExpenseAccount
		)
	{
		String pTypeStr = (String) dimensionValues.get(org.nightlabs.jfire.accounting.book.mappingbased.PriceFragmentDimension.MONEY_FLOW_DIMENSION_ID);
		PriceFragmentType pType = null;
		if (pTypeStr != null) {
			PriceFragmentTypeID pTypeID = PriceFragmentType.primaryKeyToPriceFragmentTypeID(pTypeStr);
			pType = PriceFragmentTypeDAO.sharedInstance().getPriceFragmentType(
					pTypeID, 
					new String[] {FetchPlan.DEFAULT}, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
		}
		PFMoneyFlowMapping mapping = new PFMoneyFlowMapping(
				IDGenerator.getOrganisationID(),
				IDGenerator.nextID(MoneyFlowMapping.class),
				productType,
				packageType,
				pType,
				currency
		);
		LegalEntity owner = null;
		String leStr = (String) dimensionValues.get(org.nightlabs.jfire.accounting.book.mappingbased.OwnerDimension.MONEY_FLOW_DIMENSION_ID);
		if (leStr != null) {
			AnchorID leID = Anchor.primaryKeyToAnchorID((String) dimensionValues.get(org.nightlabs.jfire.accounting.book.mappingbased.OwnerDimension.MONEY_FLOW_DIMENSION_ID));
			owner = LegalEntityDAO.sharedInstance().getLegalEntity(
					leID,
					new String[] {FetchPlan.DEFAULT},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor()
			);
		}
		mapping.setOwner(owner);
		mapping.setSourceOrganisationID((String) dimensionValues.get(org.nightlabs.jfire.accounting.book.mappingbased.SourceOrganisationDimension.MONEY_FLOW_DIMENSION_ID));
		mapping.setRevenueAccount(revenueAccount);
		mapping.setExpenseAccount(expenseAccount);
		mapping.setReverseRevenueAccount(reverseRevenueAccount);
		mapping.setReverseExpenseAccount(reverseExpenseAccount);
		return mapping;
	}

	public List getMoneyFlowDimensionIDs() {
		return MappingBasedAccountantDelegate.DIMENSION_IDS;
	}

	public String getMappingDescription(MoneyFlowMapping mapping) {
		if (mapping == null)
			throw new IllegalArgumentException("mapping must not be null!"); //$NON-NLS-1$

		if (!canHandleMappingType(mapping.getClass()))
			return "Unknown mapping type! PFLocalAccountantDelegateType can only handle " + PFMoneyFlowMapping.class.getName() + " - not " + mapping.getClass().getName(); //$NON-NLS-1$ //$NON-NLS-2$
		PFMoneyFlowMapping pfMapping = (PFMoneyFlowMapping)mapping;
//		AnchorID anchorID = Anchor.primaryKeyToAnchorID(pfMapping.getOwnerPK());
		PriceFragmentTypeID priceFragmentTypeID = (PriceFragmentTypeID) JDOHelper.getObjectId(pfMapping.getPriceFragmentType());
		PriceFragmentType priceFragmentType = PriceFragmentTypeDAO.sharedInstance().getPriceFragmentType(
				priceFragmentTypeID, 
				new String[] {FetchPlan.DEFAULT, PriceFragmentType.FETCH_GROUP_NAME},
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor()
			);
//		LegalEntityProvider.sharedInstance().getLegalEntity(anchorID,
//				new String[] {FetchPlan.DEFAULT, LegalEntity.FETCH_GROUP_PERSON}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
//			);
//		return pfMapping.getSourceOrganisationID()+" "+priceFragmentType.getName().getText(NLLocale.getDefault().getLanguage());
		return priceFragmentType.getName().getText(NLLocale.getDefault().getLanguage());
//		return pfMapping.getPriceFragmentTypePK();
	}

	public boolean canHandleMappingType(Class mappingType) {
		return PFMoneyFlowMapping.class.isAssignableFrom(mappingType);
	}

}
