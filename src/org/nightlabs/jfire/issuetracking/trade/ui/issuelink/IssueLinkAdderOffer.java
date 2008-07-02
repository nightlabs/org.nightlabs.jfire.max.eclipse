package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;
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
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.ui.overview.offer.OfferEntryFactory;
import org.nightlabs.jfire.trade.ui.overview.offer.OfferEntryViewer;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkAdderOffer 
extends AbstractIssueLinkAdder 
{
	private OfferEntryViewer oViewer;

	@Override
	protected Composite doCreateComposite(Composite parent) {
		oViewer = new OfferEntryViewer(new OfferEntryFactory().createEntry()) {
			@Override
			protected void addResultTableListeners(AbstractTableComposite<Offer> tableComposite) {
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
		
		oViewer.createComposite(parent);
		return oViewer.getComposite();
	}

	@Override
	protected void doSearch() {
		oViewer.search();
	}
	
	public Set<ObjectID> getLinkedObjectIDs() {
		Set<ObjectID> result = new HashSet<ObjectID>();
		Collection<Offer> elements = oViewer.getListComposite().getSelectedElements();
		for(Object o : elements) {
			result.add((ObjectID)JDOHelper.getObjectId(o));
		}
		return result;
	}

	public boolean isComplete() {
		if (oViewer == null)
			return false;

		return !oViewer.getListComposite().getSelectedElements().isEmpty();
	}
	
	@Override
	public Set<IssueLink> createIssueLinks(
			Issue issue,
			IssueLinkType issueLinkType,
			ProgressMonitor monitor)
	{
		Set<IssueLink> issueLinks = new HashSet<IssueLink>();
		for (Offer linkedOffer : oViewer.getListComposite().getSelectedElements()) {
			issueLinks.add(
					issue.createIssueLink(issueLinkType, (ObjectID)JDOHelper.getObjectId(linkedOffer), Offer.class));
		}
		return issueLinks;
	}
}
