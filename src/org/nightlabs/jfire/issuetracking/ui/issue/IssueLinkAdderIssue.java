/**
 *
 */
package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssueLinkType;
import org.nightlabs.jfire.issuetracking.ui.issuelink.AbstractIssueLinkAdder;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListFactory;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer;
import org.nightlabs.progress.ProgressMonitor;

/**
 *
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class IssueLinkAdderIssue
extends AbstractIssueLinkAdder
{
	private IssueEntryListViewer iViewer;
	private Issue issue;

	/**
	 * Constructs an issue-issue link adder.
	 * @param issue - the {@link Issue} used in adding process
	 */
	public IssueLinkAdderIssue(Issue issue) {
		this.issue = issue;
	}

	@Override
	protected Composite doCreateComposite(Composite parent) {
		iViewer = new IssueEntryListViewer(new IssueEntryListFactory().createEntry()) {
			@Override
			protected void addResultTableListeners(AbstractTableComposite tableComposite) {
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
		iViewer.getIssueTable().setIsTableInWizard(true);

		iViewer.getIssueTable().getTableViewer().addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return !element.equals(issue);
			}
		});


		return iViewer.getComposite();
	}

	@Override
	protected void doSearch() {
		iViewer.search();
	}

	/**
	 *
	 */
	public Set<ObjectID> getLinkedObjectIDs() {
		Collection<Issue> elements = iViewer.getListComposite().getSelectedElements();
		return NLJDOHelper.getObjectIDSet(elements);
	}

	/**
	 *
	 * @return
	 */
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
		for (Issue linkedIssue : iViewer.getListComposite().getSelectedElements()) {
			issueLinks.add(
					issue.createIssueLink(issueLinkType, linkedIssue));
		}
		return issueLinks;
	}
}