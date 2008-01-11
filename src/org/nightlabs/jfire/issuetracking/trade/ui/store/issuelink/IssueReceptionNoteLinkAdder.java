/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.store.issuelink;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;
import org.nightlabs.jfire.trade.ui.overview.receptionnote.ReceptionNoteEntryFactory;
import org.nightlabs.jfire.trade.ui.overview.receptionnote.ReceptionNoteEntryViewer;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueReceptionNoteLinkAdder extends AbstractIssueLinkAdder {

	private ReceptionNoteEntryViewer dViewer;
	@Override
	protected Composite doCreateComposite(Composite parent) {
		dViewer = new ReceptionNoteEntryViewer(new ReceptionNoteEntryFactory().createEntry());
		dViewer.createComposite(parent);
		dViewer.getListComposite().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent e) {
				notifyIssueLinkSelectionListeners();
			}
		});
		return dViewer.getComposite();
	}

	public Set<ObjectID> getIssueLinkObjectIds() {
		Set<ObjectID> result = new HashSet<ObjectID>();
		for(Object o : dViewer.getListComposite().getSelectedElements()) {
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
