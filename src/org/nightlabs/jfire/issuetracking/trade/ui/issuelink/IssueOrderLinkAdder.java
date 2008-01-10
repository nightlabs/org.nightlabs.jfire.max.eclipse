/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.JDOHelper;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;
import org.nightlabs.jfire.trade.ui.overview.order.OrderEntryFactory;
import org.nightlabs.jfire.trade.ui.overview.order.OrderEntryViewer;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueOrderLinkAdder extends AbstractIssueLinkAdder {

	private OrderEntryViewer oViewer;
	@Override
	protected Composite doCreateComposite(Composite parent) {
		oViewer = new OrderEntryViewer(new OrderEntryFactory().createEntry());
		oViewer.createComposite(parent);
		return oViewer.getComposite();
	}

	public Collection<String> getIssueLinkObjectIds() {
		Collection<String> result = new HashSet<String>();
		Collection<Object> elements = oViewer.getListComposite().getSelectedElements();
		for(Object o : elements) {
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
