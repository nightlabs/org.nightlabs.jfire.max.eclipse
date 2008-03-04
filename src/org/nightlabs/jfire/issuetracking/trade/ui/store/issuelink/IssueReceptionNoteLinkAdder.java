/**
 * 
 */
package org.nightlabs.jfire.issuetracking.trade.ui.store.issuelink;

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
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.ui.overview.receptionnote.ReceptionNoteEntryFactory;
import org.nightlabs.jfire.trade.ui.overview.receptionnote.ReceptionNoteEntryViewer;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueReceptionNoteLinkAdder extends AbstractIssueLinkAdder {

	private ReceptionNoteEntryViewer rViewer;
	@Override
	protected Composite doCreateComposite(Composite parent) {
		rViewer = new ReceptionNoteEntryViewer(new ReceptionNoteEntryFactory().createEntry()) {
			@Override
			protected void addResultTableListeners(AbstractTableComposite<ReceptionNote> tableComposite) {
				tableComposite.getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent evt) {
						notifyIssueLinkDoubleClickListeners();
					}
				});
				
				tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						notifyIssueLinkSelectionListeners();
					}
				});
			}
		};
		
		rViewer.createComposite(parent);
		return rViewer.getComposite();
	}

	public Set<ObjectID> getIssueLinkObjectIds() {
		Set<ObjectID> result = new HashSet<ObjectID>();
		for(Object o : rViewer.getListComposite().getSelectedElements()) {
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
