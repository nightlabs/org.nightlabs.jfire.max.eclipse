package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice;

import java.util.Date;

import org.nightlabs.jfire.trade.id.ArticleContainerID;

public class InvoiceTableItem {
	private ArticleContainerID invoiceID;
	private String invoiceId;
	private String businessPartnerName;
	private String invoiceAmount;
	private Date invoiceCreationDate;
	private long amountToPay;
	
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public String getBusinessPartnerName() {
		return businessPartnerName;
	}
	public void setBusinessPartnerName(String customerName) {
		this.businessPartnerName = customerName;
	}
	public String getInvoiceAmount() {
		return invoiceAmount;
	}
	public void setInvoiceAmount(String invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}
	public ArticleContainerID getInvoiceID() {
		return invoiceID;
	}
	public void setInvoiceID(ArticleContainerID invoiceID) {
		this.invoiceID = invoiceID;
	}
	public long getAmountToPay() {
		return amountToPay;
	}
	public void setAmountToPay(long amountToPay) {
		this.amountToPay = amountToPay;
	}
	public Date getInvoiceCreationDate() {
		return invoiceCreationDate;
	}
	public void setInvoiceCreationDate(Date invoiceCreationDate) {
		this.invoiceCreationDate = invoiceCreationDate;
	}
	
}