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

package org.nightlabs.jfire.trade.admin.ui.account;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.AbstractListComposite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.DynamicPathWizardPage;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.Currency;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.admin.ui.TradeAdminPlugin;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.accounting.CurrencyLabelProvider;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class CreateAccountEntryWizardPage 
extends DynamicPathWizardPage 
{
	private XComposite wrapper = null;
	private I18nTextEditor accountNameEditor;
	private Text anchorIDText;
	private XComboComposite<String> comboOwner;
	private XComboComposite<Currency> comboCurrency;
	private Group accountTypeGroup;
	private Button normalAccountRadio;
	private Button shadowAccountRadio;
	private AccountingManager accountingManager;
	private List<Currency> currencies;
	
	public CreateAccountEntryWizardPage() {
		this(null);
	}
	/**
	 * @param productTypeForAccount The productType this account is for (just for ID-suggestion)
	 */
	public CreateAccountEntryWizardPage(ProductType productTypeForAccount) {
		super(
				CreateAccountEntryWizardPage.class.getName(),  
				Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountEntryWizardPage.title"), //$NON-NLS-1$
				SharedImages.getWizardPageImageDescriptor(TradeAdminPlugin.getDefault(), CreateAccountEntryWizardPage.class)
			);
	}
	
	private void updateIDText() {
		if ( "".equals(accountNameEditor.getEditText()) ) //$NON-NLS-1$
				anchorIDText.setText(""); //$NON-NLS-1$
		else
			anchorIDText.setText(ObjectIDUtil.makeValidIDString(accountNameEditor.getEditText()));
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control createPageContents(Composite parent) {
		wrapper = new XComposite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		wrapper.setLayout(layout);
		getContainer().getShell().setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountEntryWizardPage.windowTitle")); //$NON-NLS-1$
		
		anchorIDText = new Text(wrapper, wrapper.getBorderStyle());
		anchorIDText.setText(""); //$NON-NLS-1$
		anchorIDText.setEnabled(false);
		GridData anchorIDTextLData = new GridData(GridData.FILL_HORIZONTAL);
		anchorIDTextLData.horizontalSpan = 3;
		anchorIDText.setLayoutData(anchorIDTextLData);
		
		accountNameEditor = new I18nTextEditor(wrapper);
		GridData accountNameEditorGD = new GridData(GridData.FILL_HORIZONTAL);
		accountNameEditorGD.horizontalSpan = 2;
		accountNameEditor.setLayoutData(accountNameEditorGD);
		accountNameEditor.setI18nText(new I18nTextBuffer());
		accountNameEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updatePage();
				updateIDText();
			}
		});

		comboOwner = new XComboComposite<String>(wrapper, AbstractListComposite.getDefaultWidgetStyle(wrapper), 
			Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountEntryWizardPage.ownerCombo.caption")); //$NON-NLS-1$
		
		comboOwner.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboOwner.setEnabled(false);
		
		comboCurrency = new XComboComposite<Currency>(wrapper, AbstractListComposite.getDefaultWidgetStyle(wrapper),	
				Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountEntryWizardPage.currencyCombo.caption"), new CurrencyLabelProvider()); //$NON-NLS-1$
		comboCurrency.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		try {
			Collection<Currency> tmpCurrenies = accountingManager.getCurrencies(new String[]{FetchPlan.DEFAULT}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			currencies = new ArrayList<Currency>(tmpCurrenies.size());
			currencies.addAll(tmpCurrenies);
		} catch (Exception e) {
			ExceptionHandlerRegistry.asyncHandleException(e);
		}
		comboCurrency.setInput(currencies);
		if (! currencies.isEmpty())
			comboCurrency.selectElementByIndex(0);
//		getCombo().add(currency.getCurrencyID()+" ("+currency.getCurrencySymbol()+")"); //$NON-NLS-1$ //$NON-NLS-2$
		
		accountTypeGroup = new Group(wrapper, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.verticalAlignment = GridData.CENTER;
		accountTypeGroup.setLayoutData(gd);
		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		accountTypeGroup.setLayout(gl);
		
		normalAccountRadio = new Button(accountTypeGroup, SWT.RADIO | SWT.WRAP);
		normalAccountRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		normalAccountRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountEntryWizardPage.radioNormalAccount.text")); //$NON-NLS-1$
		normalAccountRadio.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updatePage();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		normalAccountRadio.setSelection(true);
		updatePage();
		
		shadowAccountRadio = new Button(accountTypeGroup, SWT.RADIO);
		shadowAccountRadio.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		shadowAccountRadio.setText(Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountEntryWizardPage.radioSummaryAccount.text")); //$NON-NLS-1$
		shadowAccountRadio.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				updatePage();
			}
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		return wrapper;
	}
	
	/**
	 * Checks page status
	 */
	protected void updatePage() {
		if ( "".equals(accountNameEditor.getEditText()) ) { //$NON-NLS-1$
			updateStatus(Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountEntryWizardPage.errorMessageNoAnchorID")); //$NON-NLS-1$
			return;
		}
		if (comboCurrency.getSelectionIndex() < 0) {
			updateStatus(Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountEntryWizardPage.errorMessageNoCurrency")); //$NON-NLS-1$
			return;
		}
		updateStatus(null);
	}
	
	/**
	 * Returns true. Can be last page, when user just creates 
	 * an account, and does not perform any configuration. 
	 *  
	 */
	@Override
	public boolean canBeLastPage() {
		return true;
	}
	
	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#getDefaultPageMessage()
	 */
	@Override
	protected String getDefaultPageMessage() {
		return Messages.getString("org.nightlabs.jfire.trade.admin.ui.account.CreateAccountEntryWizardPage.defaultPageMessage"); //$NON-NLS-1$
	}
	
	@Override
	public IWizardPage getNextPage() {
		if (!isPageComplete())
			return null;
		else {			 
			return null;
		}
	}
	
	public boolean isCreateSummaryAccount() {
		return shadowAccountRadio.getSelection();
	}
	
	public String getAnchorID() {
		return anchorIDText.getText();
	}
	
	public Currency getCurrency() {
		if (comboCurrency.getSelectionIndex() < 0)
			throw new IllegalStateException("No currency selected. Can't return one."); //$NON-NLS-1$
		return currencies.get(comboCurrency.getSelectionIndex());
	}
	
	public I18nTextEditor getAccountNameEditor() {
		return accountNameEditor;
	}
	
}
