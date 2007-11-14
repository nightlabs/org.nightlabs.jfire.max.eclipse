package org.nightlabs.jfire.issuetracking.ui.issue;

import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.Offer;

public enum IssueDocumentType {
	INVOICE (Invoice.class, "Invoice"),
	DELIVERYNOTE   (DeliveryNote.class, "Delivery Note"),
	RECEPTIONNOTE   (ReceptionNote.class, "Reception Note"),
	OFFER    (Offer.class, "Offer");

	private final Class c;   // class
	private final String description; // description
	
	IssueDocumentType(Class c, String description) {
		this.c = c;
		this.description = description;
	}
	public Class c()   { return c; }
	public String description() { return description; }
}
