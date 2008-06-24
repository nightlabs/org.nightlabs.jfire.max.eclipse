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

package org.nightlabs.jfire.trade.admin.ui.moneyflow.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.accounting.book.mappingbased.MappingBasedAccountantDelegate;
import org.nightlabs.jfire.accounting.book.mappingbased.MoneyFlowMapping;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateRegistry;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateType;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimension;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimensionRegistry;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimensionWizardPage;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class AddMoneyFlowMappingWizard extends DynamicPathWizard {

	private ProductTypeID productTypeID;
	private MappingBasedAccountantDelegate delegate;
//	private MoneyFlowMappingConfigurator configurator;
	private SelectProductTypeAndPackagePage selectProductTypeAndPackagePage;
	private SelectCurrencyAndAccountPage selectCurrencyAndRevenueAccountPage;
	private SelectCurrencyAndAccountPage selectCurrencyAndExpenseAccountPage;
	private SelectCurrencyAndAccountPage selectCurrencyAndReverseRevenueAccountPage;
	private SelectCurrencyAndAccountPage selectCurrencyAndReverseExpenseAccountPage;
	private List dimensionPages = new LinkedList();
	
	private MoneyFlowMapping createdMapping;
	
	public AddMoneyFlowMappingWizard(MappingBasedAccountantDelegate delegate, ProductTypeID productTypeID) {
		super();
		this.productTypeID = productTypeID;
		this.delegate = delegate;
		setForcePreviousAndNextButtons(true);
		setWindowTitle(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.AddMoneyFlowMappingWizard.windowTitle")); //$NON-NLS-1$
	}

	@Override
	public void addPages()
	{
		selectProductTypeAndPackagePage = new SelectProductTypeAndPackagePage(productTypeID);
		addPage(selectProductTypeAndPackagePage);

		LocalAccountantDelegateType delegateType = LocalAccountantDelegateRegistry.sharedInstance().getType(delegate.getClass());
		if (delegateType == null)
			throw new IllegalStateException("Could not find LocalAccountantDelegateType for delegateClass "+delegate.getClass().getName()); //$NON-NLS-1$
		for (Iterator iter = delegateType.getMoneyFlowDimensionIDs().iterator(); iter.hasNext();) {
			String moneyFlowDimensionID = (String) iter.next();
			MappingDimension dimension = MappingDimensionRegistry.sharedInstance().getDimension(moneyFlowDimensionID);
			if (dimension == null)
				throw new IllegalStateException("Could not find MappingDimension for "+moneyFlowDimensionID); //$NON-NLS-1$
			MappingDimensionWizardPage dimensionPage = dimension.getCreateMappingWizardPage(this);
			addPage(dimensionPage);
			dimensionPages.add(dimensionPage);
		}

		selectCurrencyAndRevenueAccountPage = new SelectCurrencyAndAccountPage(SelectCurrencyAndAccountPage.class.getName()+"#revenue",
				"Revenue account",
				"Select an account as endpoint for this money flow mapping to be used when revenues are booked.",
				true,
				false);
		addPage(selectCurrencyAndRevenueAccountPage);

		selectCurrencyAndExpenseAccountPage = new SelectCurrencyAndAccountPage(SelectCurrencyAndAccountPage.class.getName()+"#expense",
				"Expense account",
				"Select an account as endpoint for this money flow mapping to be used when expenses are booked.",
				false,
				false);
		addPage(selectCurrencyAndExpenseAccountPage);

		selectCurrencyAndReverseRevenueAccountPage = new SelectCurrencyAndAccountPage(SelectCurrencyAndAccountPage.class.getName()+"#reverseRevenue",
				"Reverse revenue account",
				"Select a revenue account for reversing entries. This is optional: If you don't specify it, the normal revenue account will be used.",
				false,
				true);
		addPage(selectCurrencyAndReverseRevenueAccountPage);

		selectCurrencyAndReverseExpenseAccountPage = new SelectCurrencyAndAccountPage(SelectCurrencyAndAccountPage.class.getName()+"#reverseExpense",
				"Reverse expense account",
				"Select an expense account for reversing entries. This is optional: If you don't specify it, the normal expense account will be used.",
				false,
				true);
		addPage(selectCurrencyAndReverseExpenseAccountPage);

		// set the same currency for all pages, if it is changed by the first page.
		selectCurrencyAndRevenueAccountPage.addPropertyChangeListener(SelectCurrencyAndAccountPage.PROPERTY_CURRENCY, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt)
			{
				setSameCurrency();
			}
		});
//		setSameCurrency(); // necessary?
	}

	private void setSameCurrency()
	{
		Currency currency = selectCurrencyAndRevenueAccountPage.getCurrency();
		selectCurrencyAndExpenseAccountPage.setCurrency(currency);
		selectCurrencyAndReverseRevenueAccountPage.setCurrency(currency);
		selectCurrencyAndReverseExpenseAccountPage.setCurrency(currency);
	}

	@Override
	public boolean performFinish() {
		LocalAccountantDelegateType delegateType = LocalAccountantDelegateRegistry.sharedInstance().getType(delegate.getClass());
		if (delegateType == null)
			throw new IllegalStateException("Could not find LocalAccountantDelegateType for delegateClass "+delegate.getClass().getName()); //$NON-NLS-1$
		Map dimensionValues = new HashMap();
		for (Iterator iter = dimensionPages.iterator(); iter.hasNext();) {
			MappingDimensionWizardPage dimensionPage = (MappingDimensionWizardPage) iter.next();
			dimensionValues.put(dimensionPage.getMoneyFlowMappingDimensionID(), dimensionPage.getDimensionValue());
		}
		createdMapping = delegateType.createNewMapping(
				selectProductTypeAndPackagePage.getSelectedProductType(),
				selectProductTypeAndPackagePage.getPackageType(),
				dimensionValues,
				selectCurrencyAndRevenueAccountPage.getCurrency(),
				selectCurrencyAndRevenueAccountPage.getAccount(),
				selectCurrencyAndExpenseAccountPage.getAccount(),
				selectCurrencyAndReverseRevenueAccountPage.getAccount(),
				selectCurrencyAndReverseExpenseAccountPage.getAccount()
			);
		createdMapping.setLocalAccountantDelegate(delegate);
//		try {
//			createdMapping = AccountingUtil.getAccountingManager().storeMoneyFlowMapping(
//					createdMapping,
//					true,
//					new String[] {
//							FetchPlan.DEFAULT,
//							MoneyFlowMapping.FETCH_GROUP_LOCAL_ACCOUNTANT_DELEGATE
//					}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT
//				);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
		delegate.addMoneyFlowMapping(createdMapping);
		return true;
	}
	
	public MoneyFlowMapping getCreatedMapping() {
		return createdMapping;
	}
	
}
