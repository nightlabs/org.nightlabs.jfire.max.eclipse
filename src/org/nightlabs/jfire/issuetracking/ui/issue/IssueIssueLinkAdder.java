/**
 * 
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListFactory;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueIssueLinkAdder extends AbstractIssueLinkAdder {

	private IssueEntryListViewer iViewer;
	@Override
	protected Composite doCreateComposite(Composite parent) {
		iViewer = new IssueEntryListViewer(new IssueEntryListFactory().createEntry());
		iViewer.createComposite(parent);
		iViewer.getListComposite().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				notifyIssueLinkSelectionListeners();
			}
		});
		return iViewer.getComposite();
	}

	public Set<ObjectID> getIssueLinkObjectIds() {
		Set<ObjectID> result = new HashSet<ObjectID>();
		Collection<Object> elements = iViewer.getListComposite().getSelectedElements();
		for(Object o : elements) {
			result.add((ObjectID)JDOHelper.getObjectId(o));
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
