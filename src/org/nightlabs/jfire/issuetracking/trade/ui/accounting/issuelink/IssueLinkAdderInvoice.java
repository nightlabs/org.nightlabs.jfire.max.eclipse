/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.accounting.issuelink;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.ui.overview.invoice.InvoiceEntryFactory;
import org.nightlabs.jfire.trade.ui.overview.invoice.InvoiceEntryViewer;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkAdderInvoice 
extends AbstractIssueLinkAdder 
{
	private InvoiceEntryViewer iViewer;

	@Override
	protected Composite doCreateComposite(Composite parent) {
		iViewer = new InvoiceEntryViewer(new InvoiceEntryFactory().createEntry()) {
			@Override
			protected void addResultTableListeners(AbstractTableComposite<Invoice> tableComposite) {
				tableComposite.addDoubleClickListener(new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent evt) {
						notifyIssueLinkDoubleClickListeners();
					}
				});
				
				tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						fireSelectionChangedEvent();
					}
				});
			}
		};
		
		iViewer.createComposite(parent);
		return iViewer.getComposite();
	}

	@Override
	protected void doSearch() {
		iViewer.search();
	}
	
	public Set<ObjectID> getLinkedObjectIDs() {
		Set<ObjectID> result = new HashSet<ObjectID>();
		for(Object o : iViewer.getListComposite().getSelectedElements()) {
			result.add((ObjectID)JDOHelper.getObjectId(o));
		}
		return result;
	}

	public boolean isComplete() {
		if (iViewer == null)
			return false;

		return !iViewer.getListComposite().getSelectedElements().isEmpty();
	}
	
	@Override
	public Set<IssueLink> createIssueLinks(
			Issue issue,
			IssueLinkType issueLinkType,
			ProgressMonitor monitor)
	{
		Set<IssueLink> issueLinks = new HashSet<IssueLink>();
		for (Invoice linkedInvoice : iViewer.getListComposite().getSelectedElements()) {
			issueLinks.add(
					issue.createIssueLink(issueLinkType, (ObjectID)JDOHelper.getObjectId(linkedInvoice), Invoice.class));
		}
		return issueLinks;
	}
}
