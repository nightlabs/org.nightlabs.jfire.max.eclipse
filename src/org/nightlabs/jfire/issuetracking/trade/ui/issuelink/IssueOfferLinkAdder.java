/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;
import org.nightlabs.jfire.trade.ui.overview.offer.OfferEntryFactory;
import org.nightlabs.jfire.trade.ui.overview.offer.OfferEntryViewer;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueOfferLinkAdder extends AbstractIssueLinkAdder {

	private OfferEntryViewer oViewer;
	@Override
	protected Composite doCreateComposite(Composite parent) {
		oViewer = new OfferEntryViewer(new OfferEntryFactory().createEntry());
		oViewer.createComposite(parent);
		oViewer.getListComposite().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				notifyIssueLinkSelectionListeners();
			}
		});
		return oViewer.getComposite();
	}

	public Set<String> getIssueLinkObjectIds() {
		Set<String> result = new HashSet<String>();
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
