/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.accounting.issuelink;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;
import org.nightlabs.jfire.trade.ui.overview.invoice.InvoiceEntryFactory;
import org.nightlabs.jfire.trade.ui.overview.invoice.InvoiceEntryViewer;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueInvoiceLinkAdder extends AbstractIssueLinkAdder {

	private InvoiceEntryViewer iViewer;
	@Override
	protected Composite doCreateComposite(Composite parent) {
		iViewer = new InvoiceEntryViewer(new InvoiceEntryFactory().createEntry());
		iViewer.createComposite(parent);
		iViewer.getListComposite().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				notifyIssueLinkSelectionListeners();
			}
		});
		return iViewer.getComposite();
	}

	public Set<String> getIssueLinkObjectIds() {
		Set<String> result = new HashSet<String>();
		for(Object o : iViewer.getListComposite().getSelectedElements()) {
			result.add(JDOHelper.getObjectId(o).toString());
		}
		return result;
	}

	public boolean isComplete() {
		if(getIssueLinkObjectIds() == null || getIssueLinkObjectIds().size() <= 0) {
			return false;
		}
		return true; 
	}
}
