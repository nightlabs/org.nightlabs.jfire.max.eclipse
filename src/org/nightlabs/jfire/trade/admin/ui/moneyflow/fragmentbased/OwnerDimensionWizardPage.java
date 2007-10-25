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

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.IDynamicPathWizard;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.MappingDimensionWizardPage;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.legalentity.search.LegalEntitySearchComposite;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class OwnerDimensionWizardPage 
extends WizardHopPage
implements MappingDimensionWizardPage
{

	private Button allOwnerButton;
	private LegalEntitySearchComposite searchComposite;
	/**
	 * @param title
	 * @param quickSearchText
	 */
	public OwnerDimensionWizardPage(String quickSearchText) {
		super(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.fragmentbased.OwnerDimensionWizardPage.title"), quickSearchText);		 //$NON-NLS-1$
	}
	
	

	public String getMoneyFlowMappingDimensionID() {
		return org.nightlabs.jfire.accounting.book.mappingbased.OwnerDimension.MONEY_FLOW_DIMENSION_ID;
	}


	public String getDimensionValue() {		
		if (allOwnerButton.getSelection())
			return null;
		return (searchComposite.getResultTable().getFirstSelectedElement() == null)? null: searchComposite.getResultTable().getFirstSelectedElement().getPrimaryKey();
	}

	@Override
	public void onShow() {
		getContainer().updateButtons();
	}


	/**
	 * @see org.nightlabs.jfire.trade.ui.legalentity.search.LegalEntitySearchWizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return allOwnerButton != null && allOwnerButton.getSelection();
	}


	@Override
	public Control createPageContents(Composite parent) {
		XComposite wrapper = new XComposite(parent, SWT.NONE);
		allOwnerButton = new Button(wrapper, SWT.CHECK);
		allOwnerButton.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.fragmentbased.OwnerDimensionWizardPage.allOwnerButton.text")); //$NON-NLS-1$
		allOwnerButton.setSelection(true);
		allOwnerButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				((IDynamicPathWizard)getWizard()).getDynamicWizardDialog().update();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		searchComposite = new LegalEntitySearchComposite(wrapper, SWT.NONE, "");
		searchComposite.getResultTable().getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				getWizard().performFinish();
			}
		});
		return wrapper;
	}
	
	

}
