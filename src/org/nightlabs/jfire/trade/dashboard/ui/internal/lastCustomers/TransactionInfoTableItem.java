package org.nightlabs.jfire.trade.dashboard.ui.internal.lastCustomers;

import org.nightlabs.jfire.trade.dashboard.LastCustomerTransaction;

public class TransactionInfoTableItem {
	private String legalEntityName;
	private LastCustomerTransaction transactionInfo;
	
	public String getLegalEntityName() {
		return legalEntityName;
	}
	public void setLegalEntityName(String legalEntityName) {
		this.legalEntityName = legalEntityName;
	}
	public LastCustomerTransaction getTransactionInfo() {
		return transactionInfo;
	}
	public void setTransactionInfo(LastCustomerTransaction transactionInfo) {
		this.transactionInfo = transactionInfo;
	}
	
	
}