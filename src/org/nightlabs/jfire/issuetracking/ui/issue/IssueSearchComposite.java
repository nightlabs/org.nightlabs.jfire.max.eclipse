package org.nightlabs.jfire.issuetracking.ui.issue;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListFactory;
import org.nightlabs.jfire.issuetracking.ui.overview.IssueEntryListViewer;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueSearchComposite extends XComposite
{
	private IssueEntryListViewer issueEntryListViewer;
	private Composite issueEntryListViewerComposite;

	private Collection<Issue> selectedIssues = new HashSet<Issue>();

	public IssueSearchComposite(Composite parent, int style) {
		super(parent, style);

		issueEntryListViewer = new IssueEntryListViewer(new IssueEntryListFactory().createEntry()) {
			@Override
			protected void addResultTableListeners(AbstractTableComposite<Issue> tableComposite) {
				tableComposite.addDoubleClickListener(new IDoubleClickListener() {
					@Override
					public void doubleClick(DoubleClickEvent evt) {
						// Do nothing!!!
					}
				});

				tableComposite.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent e) {
						selectedIssues = issueEntryListViewer.getResultTable().getSelectedElements();
					}
				});
			}
		};

		issueEntryListViewerComposite = issueEntryListViewer.createComposite(parent);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		issueEntryListViewerComposite.setLayoutData(gridData);

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getShell().layout(true, true);
				issueEntryListViewer.search();
			}
		});

		issueEntryListViewer.getResultTable().setIsTableInWizard(true);
	}

	public Collection<Issue> getSelectedIssues() {
		return selectedIssues;
	}

	public IssueEntryListViewer getIssueEntryListViewer() {
		return issueEntryListViewer;
	}
}