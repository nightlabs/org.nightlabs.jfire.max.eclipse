/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.accounting.issuelink;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.JDOHelper;

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
		return iViewer.getComposite();
	}

	public Collection<String> getIssueLinkObjectIds() {
		Collection<String> result = new HashSet<String>();
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
