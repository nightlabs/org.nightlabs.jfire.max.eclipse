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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.jfire.accounting.book.LocalAccountantDelegate;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateTree;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateType;
import org.nightlabs.jfire.trade.admin.ui.moneyflow.LocalAccountantDelegateTypeCombo;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class SelectAccountantDelegateWizardPage extends DynamicPathWizardPage {
	
	private XComposite wrapper;
	
	private XComposite radioWrapper;
	private Button chooseSelected;
	private Button createNew;
	private LocalAccountantDelegateTypeCombo typeCombo;
	private LocalAccountantDelegateTree delegateTree;
	
	private SelectCreateAccountantDelegateWizard wizard;
	
	private SelectionListener radioListener = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			if (chooseSelected.getSelection()) {
				if (wizard.getDynamicWizardPages().contains(wizard.getCreateDelegatePage()))
					wizard.removeDynamicWizardPage(wizard.getCreateDelegatePage());
				wizard.updateDialog();
			}
			else {
				if (!wizard.getDynamicWizardPages().contains(wizard.getCreateDelegatePage())) {
					wizard.addDynamicWizardPage(wizard.getCreateDelegatePage());
					wizard.updateDialog();
				}
			}
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}		
	};
	
	private SelectionListener typeComboListner = new SelectionListener(){
		public void widgetSelected(SelectionEvent e) {
			LocalAccountantDelegateType type = typeCombo.getSelectedType();
			if (type != null)
				delegateTree.setDelegateClass(type.getDelegateClass());
			else
				delegateTree.setDelegateClass(null);
		}
		public void widgetDefaultSelected(SelectionEvent e) {
		}
	};

	/**
	 * @param pageName
	 */
	public SelectAccountantDelegateWizardPage(SelectCreateAccountantDelegateWizard wizard) {
		super(SelectAccountantDelegateWizardPage.class.getName());
		this.wizard = wizard;
		setTitle(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectAccountantDelegateWizardPage.title")); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectAccountantDelegateWizardPage.description")); //$NON-NLS-1$
	}

	@Implement
	@Override
	public Control createPageContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);

		radioWrapper = new XComposite(wrapper, SWT.BORDER);		
		chooseSelected = new Button(radioWrapper, SWT.RADIO);
		chooseSelected.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectAccountantDelegateWizardPage.chooseButton.text")); //$NON-NLS-1$
		chooseSelected.setSelection(true);

		createNew = new Button(radioWrapper, SWT.RADIO);
		createNew.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.moneyflow.edit.SelectAccountantDelegateWizardPage.createButton.text")); //$NON-NLS-1$

		chooseSelected.addSelectionListener(radioListener);
		createNew.addSelectionListener(radioListener);

		typeCombo = new LocalAccountantDelegateTypeCombo(wrapper);
		typeCombo.getCombo().addSelectionListener(typeComboListner);

		delegateTree = new LocalAccountantDelegateTree(
				wrapper, 
				typeCombo.getSelectedType() == null ? null : typeCombo.getSelectedType().getDelegateClass() 
			);

		delegateTree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				getContainer().updateButtons();
			}
		});

		return wrapper;
	}

	@Override
	public boolean isPageComplete() {
		if (wrapper == null)
			return false;
		if (chooseSelected.getSelection())
			return delegateTree.getSelectedDelegate() != null;
		return true;
	}
	
	
	public LocalAccountantDelegateType getSelectedDelegateType() {
		return typeCombo.getSelectedType();
	}
	
	public boolean isCreateNew() {
		return createNew.getSelection();
	}
	
	public LocalAccountantDelegate getSelectedDelegate() {
		return delegateTree.getSelectedDelegate();
	}

}

