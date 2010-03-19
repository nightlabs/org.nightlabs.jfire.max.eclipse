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
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.DeliveryNoteEntryFactory;
import org.nightlabs.jfire.trade.ui.overview.deliverynote.DeliveryNoteEntryViewer;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkAdderDeliveryNote
extends AbstractIssueLinkAdder
{
	private DeliveryNoteEntryViewer dViewer;

	@Override
	protected Composite doCreateComposite(Composite parent) {
		dViewer = new DeliveryNoteEntryViewer(new DeliveryNoteEntryFactory().createEntry()){
			@Override
			protected void addResultTableListeners(AbstractTableComposite<DeliveryNote> tableComposite) {
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
		dViewer.setErrorMessageDisplayer(getErrorMessageDisplayer());
		dViewer.createComposite(parent);
		return dViewer.getComposite();
	}

	@Override
	protected void doSearch() {
		dViewer.search();
	}

	public Set<ObjectID> getLinkedObjectIDs() {
		Set<ObjectID> result = new HashSet<ObjectID>();
		for(Object o : dViewer.getListComposite().getSelectedElements()) {
			result.add((ObjectID)JDOHelper.getObjectId(o));
		}
		return result;
	}

	public boolean isComplete() {
		if (dViewer == null)
			return false;

		return !dViewer.getListComposite().getSelectedElements().isEmpty();
	}

	@Override
	public Set<IssueLink> createIssueLinks(
			Issue issue,
			IssueLinkType issueLinkType,
			ProgressMonitor monitor)
	{
		Set<IssueLink> issueLinks = new HashSet<IssueLink>();
		for (DeliveryNote linkedDeliveryNote : dViewer.getListComposite().getSelectedElements()) {
			issueLinks.add(
					issue.createIssueLink(issueLinkType, (ObjectID)JDOHelper.getObjectId(linkedDeliveryNote), DeliveryNote.class));
		}
		return issueLinks;
	}
}
