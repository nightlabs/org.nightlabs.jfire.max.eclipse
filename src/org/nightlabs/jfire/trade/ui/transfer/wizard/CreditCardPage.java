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

package org.nightlabs.jfire.trade.ui.transfer.wizard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.wizard.IDynamicPathWizard;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.accounting.pay.PaymentDataCreditCard;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.jfire.trade.ui.transfer.pay.ClientPaymentProcessorCreditCardBackend;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CreditCardPage extends WizardHopPage
{
	private PaymentDataCreditCard paymentData = null;

	private Label nameOnCardLabel;
	private Text nameOnCardText;
	private Label cardNumberLabel;
	private Text cardNumberText;
	private Label cvcLabel;
	private Text cvcText;
	private Label expiryLabel;

	/**
	 * Instances of {@link Integer} representing the month of year from 1 to 12
	 */
	private List expiryMonthList = new ArrayList();
	private Combo expiryMonth;

	/**
	 * Instances of {@link Integer} representing the year in full format (e.g. "2005"),
	 * that means no abbreviation (NOT "97", but "1997").
	 */
	private List expiryYearList = new ArrayList();
	private Combo expiryYear;

	private ClientPaymentProcessorCreditCardBackend clientPaymentProcessor;

	public CreditCardPage(ClientPaymentProcessorCreditCardBackend clientPaymentProcessorCreditCard)
	{
		super(CreditCardPage.class.getName(), Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CreditCardPage.title")); //$NON-NLS-1$
		this.clientPaymentProcessor = clientPaymentProcessorCreditCard;
		setMessage(null);
		setDescription(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CreditCardPage.description")); //$NON-NLS-1$
	}

	/**
	 * @see org.nightlabs.base.ui.wizard.DynamicPathWizardPage#createPageContents(org.eclipse.swt.widgets.Composite)
	 */
	public Control createPageContents(Composite parent)
	{
//	 TODO remove test stuff
		getWizardHop().addHopPage(new TestWizardPage1("test1", "Payment - Test1", "This is step 1 of payment")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		getWizardHop().addHopPage(new TestWizardPage1("test2", "Payment - Test2", "This is step 2 of payment")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		TestSubWizardHopEntryPage testSubWizardHopEntryPage =  new TestSubWizardHopEntryPage("sub1.entryPage", "Payment - Sub1 - Entry Page", "This is the entryPage of sub1 of payment"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		getWizardHop().setExitPage(testSubWizardHopEntryPage);

		testSubWizardHopEntryPage.getWizardHop().addHopPage(new TestWizardPage1("sub1.test1", "Payment - Sub1 - Test1", "This is step 1 of sub1 of payment")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		testSubWizardHopEntryPage.getWizardHop().addHopPage(new TestWizardPage1("sub1.test2", "Payment - Sub1 - Test2", "This is step 2 of sub1 of payment")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
// end test stuff
		
		paymentData = (PaymentDataCreditCard)clientPaymentProcessor.getPaymentData();

		XComposite page = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		page.getGridLayout().numColumns = 2;

		(nameOnCardLabel = new Label(page, SWT.NONE)).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CreditCardPage.nameLabel.text")); //$NON-NLS-1$
		(nameOnCardText = new Text(page, SWT.BORDER)).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameOnCardText.setText(paymentData.getNameOnCard());
		nameOnCardText.addModifyListener(new ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				paymentData.setNameOnCard(nameOnCardText.getText());
				((IDynamicPathWizard)getWizard()).updateDialog();
			}
		});

//		(firstNameOnCardLabel = new Label(page, SWT.NONE)).setText("First Name");
//		(firstNameOnCardText = new Text(page, SWT.BORDER)).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		firstNameOnCardText.setText(paymentData.getFirstNameOnCard());
//		firstNameOnCardText.addModifyListener(new ModifyListener() {
//			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
//				paymentData.setFirstNameOnCard(firstNameOnCardText.getText());
//				((IDynamicPathWizard)getWizard()).updateDialog();
//			}
//		});

		(cardNumberLabel = new Label(page, SWT.NONE)).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CreditCardPage.cardNumberLabel.text")); //$NON-NLS-1$
		cardNumberLabel.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CreditCardPage.cardNumberLabel.toolTipText")); //$NON-NLS-1$
		(cardNumberText = new Text(page, SWT.BORDER)).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cardNumberText.setText(paymentData.getCardNumber());
		cardNumberText.setToolTipText(cardNumberLabel.getToolTipText());
		cardNumberText.addModifyListener(new ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				paymentData.setCardNumber(cardNumberText.getText());
				((IDynamicPathWizard)getWizard()).updateDialog();
			}
		});

		(cvcLabel = new Label(page, SWT.NONE)).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CreditCardPage.cvcLabel.text")); //$NON-NLS-1$
		cvcLabel.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CreditCardPage.cvcLabel.toolTipText")); //$NON-NLS-1$
		(cvcText = new Text(page, SWT.BORDER)).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cvcText.setText(paymentData.getCvc());
		cvcText.setToolTipText(cvcLabel.getToolTipText());
		cvcText.addModifyListener(new ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				paymentData.setCvc(cvcText.getText());
				((IDynamicPathWizard)getWizard()).updateDialog();
			}
		});

		(expiryLabel = new Label(page, SWT.NONE)).setText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CreditCardPage.expiryLabel.text")); //$NON-NLS-1$
		expiryLabel.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.transfer.wizard.CreditCardPage.expiryLabel.toolTipText")); //$NON-NLS-1$
		XComposite expiryComposite = new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		expiryComposite.getGridData().grabExcessVerticalSpace = false;
		expiryComposite.getGridLayout().numColumns = 2;
		expiryMonth = new Combo(expiryComposite, SWT.BORDER | SWT.READ_ONLY);
		expiryYear = new Combo(expiryComposite, SWT.BORDER | SWT.READ_ONLY);

		expiryMonth.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				paymentData.setExpiryMonth(
						((Integer)expiryMonthList.get(expiryMonth.getSelectionIndex())).intValue());
			}
		});

		expiryYear.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				paymentData.setExpiryYear(
						((Integer)expiryYearList.get(expiryYear.getSelectionIndex())).intValue());
			}
		});

		expiryMonthList.clear();
		int currMonth = paymentData.getExpiryMonth();
		int currMonthIdx = -1;
		for (int monthIdx = 0; monthIdx < 12; ++monthIdx) {
			Integer monthInt = new Integer(monthIdx + 1);

			if (monthInt.intValue() == currMonth)
				currMonthIdx = monthIdx;

			expiryMonthList.add(monthInt);
			expiryMonth.add(monthInt.toString());
		}
		if (currMonthIdx >= 0)
			expiryMonth.select(currMonthIdx);

		expiryYearList.clear();
		int currYear = paymentData.getExpiryYear();
		int currYearIdx = -1;
		int yearBase = Calendar.getInstance().get(Calendar.YEAR);
		for (int yearIdx = 0; yearIdx < 12; ++yearIdx) {
			Integer yearInt = new Integer(yearBase + yearIdx);

			if (yearInt.intValue() == currYear)
				currYearIdx = yearIdx;

			expiryYearList.add(yearInt);
			expiryYear.add(yearInt.toString());
		}
		if (currYearIdx >= 0)
			expiryYear.select(currYearIdx);

		// fill the remaining space - necessary?
		new XComposite(page, SWT.NONE, LayoutMode.TIGHT_WRAPPER);

		return page;
	}

	/**
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	public boolean isPageComplete()
	{
		if (paymentData == null)
			return false;

		return
				!"".equals(paymentData.getNameOnCard()) && //$NON-NLS-1$
				!"".equals(paymentData.getCardNumber()) && //$NON-NLS-1$
				!"".equals(paymentData.getCvc()); //$NON-NLS-1$
	}

}
