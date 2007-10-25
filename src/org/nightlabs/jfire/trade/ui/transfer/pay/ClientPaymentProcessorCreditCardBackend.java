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

package org.nightlabs.jfire.trade.ui.transfer.pay;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.wizard.IWizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.pay.PaymentData;
import org.nightlabs.jfire.accounting.pay.PaymentDataCreditCard;
import org.nightlabs.jfire.accounting.pay.PaymentException;
import org.nightlabs.jfire.accounting.pay.PaymentResult;
import org.nightlabs.jfire.accounting.pay.ServerPaymentProcessorCreditCardDummyForClientPayment;
import org.nightlabs.jfire.accounting.pay.id.ServerPaymentProcessorID;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.person.PersonStruct;
import org.nightlabs.jfire.prop.IStruct;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.dao.StructDAO;
import org.nightlabs.jfire.prop.datafield.NumberDataField;
import org.nightlabs.jfire.prop.datafield.TextDataField;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.TradeManager;
import org.nightlabs.jfire.trade.TradeManagerUtil;
import org.nightlabs.jfire.trade.ui.transfer.wizard.CreditCardPage;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * This payment processor gathers data and performs the payment with any backend (i.e.
 * server sided) payment module.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ClientPaymentProcessorCreditCardBackend extends AbstractClientPaymentProcessor
{
	private PaymentDataCreditCard paymentData;

	public ClientPaymentProcessorCreditCardBackend()
	{
	}

	private static Set excludedServerPaymentProcessorIDs = null;

	@Override
	@Implement
	public Set getExcludedServerPaymentProcessorIDs()
	{
		if (excludedServerPaymentProcessorIDs == null) {
			HashSet set = new HashSet();
			set.add(
					ServerPaymentProcessorID.create(
							Organisation.DEVIL_ORGANISATION_ID,
							ServerPaymentProcessorCreditCardDummyForClientPayment.class.getName()));

			excludedServerPaymentProcessorIDs = Collections.unmodifiableSet(set);
		}

		return excludedServerPaymentProcessorIDs;
	}

	@Implement
	public IWizardHopPage createPaymentWizardPage()
	{
		return new CreditCardPage(this);
	}

	@Implement
	public PaymentResult payBegin() throws PaymentException
	{
		return null;
	}
	
	@Implement
	public PaymentResult payDoWork() throws PaymentException
	{
		return null;
	}
	
	@Implement
	public PaymentResult payEnd() throws PaymentException
	{
		return null;
	}

	@Implement
	public PaymentData getPaymentData()
	{
		if (paymentData == null) {
			try {
				paymentData = new PaymentDataCreditCard(getPayment());

				// TODO should use a cache for legal entity / persons
				TradeManager tradeManager = TradeManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
				LegalEntity entity = tradeManager.getLegalEntity(getPartnerID(), new String[] {
					FetchPlan.DEFAULT,
					LegalEntity.FETCH_GROUP_PERSON,
					PropertySet.FETCH_GROUP_FULL_DATA
				}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
				if (!entity.isAnonymous()) {
					Person person = entity.getPerson();
					if (person != null) {
						IStruct struct = StructDAO.sharedInstance().getStruct(Person.class, new NullProgressMonitor());
						person.inflate(struct);

						String nameOnCard = ((TextDataField)person.getDataField(PersonStruct.CREDITCARD_CREDITCARDHOLDER)).getText();
						if (nameOnCard == null) {
							nameOnCard = ((TextDataField)person.getDataField(PersonStruct.PERSONALDATA_FIRSTNAME)).getText() + ' ' + ((TextDataField)person.getDataField(PersonStruct.PERSONALDATA_NAME)).getText();
							nameOnCard = nameOnCard.trim();
//							while (nameOnCard.startsWith(" "))
//								nameOnCard = nameOnCard.substring(1);
//
//							while (nameOnCard.endsWith(" "))
//								nameOnCard = nameOnCard.substring(0, nameOnCard.length() - 1);
						}

						String cardNumber = ((TextDataField)person.getDataField(PersonStruct.CREDITCARD_NUMBER)).getText();

//						int expiryYear = -1;
//						int expiryMonth = -1;
//						// TODO the person should store a special year and month field for expiry - not simply text.
//						String expiryYearStr = ((NumberDataField)person.getDataField(PersonStruct.CREDITCARD_EXPIRYYEAR)).getIntValue();
//						String expiryMonthStr = ((NumberDataField)person.getDataField(PersonStruct.CREDITCARD_EXPIRYMONTH)).getText();
//						
//						try {
//							expiryYear = Integer.parseInt(expiryYearStr);
//						} catch (NumberFormatException x) {
//							// ignore
//						}
//						try {
//							expiryMonth = Integer.parseInt(expiryMonthStr);
//						} catch (NumberFormatException x) {
//							// ignore
//						}

						
						// TODO the person should store a special year and month field for expiry - not simply text.
						int expiryYear = ((NumberDataField)person.getDataField(PersonStruct.CREDITCARD_EXPIRYYEAR)).getIntValue();
						int expiryMonth = ((NumberDataField)person.getDataField(PersonStruct.CREDITCARD_EXPIRYMONTH)).getIntValue();
						
						paymentData.setNameOnCard(nameOnCard);
						paymentData.setCardNumber(cardNumber);
						paymentData.setExpiryYear(expiryYear);
						paymentData.setExpiryMonth(expiryMonth);
					}
				} // if (!entity.isAnonymous()) {

				paymentData.init();
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		return paymentData;
	}

}
