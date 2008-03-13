package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Collection;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryMap;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.dao.IssueDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditor;
import org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueEditorInput;
import org.nightlabs.progress.ProgressMonitor;

/**
 * 
 * @author Chairat Kongarayawetchakun
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class IssueEntryListViewer 
	extends JDOQuerySearchEntryViewer<Issue, IssueQuery>
{
	public IssueEntryListViewer(Entry entry) {
		super(entry);
	}

	private IssueTable issueTable;
	
//	@Override
//	public AbstractQueryFilterComposite createFilterComposite(Composite parent) {
//		return new IssueFilterCompositeIssueRelated(parent, SWT.NONE);
//	}

	@Override
	public AbstractTableComposite<Issue> createListComposite(Composite parent) {
//		TODO we should pass the QueryMap obtained via this.getQueryMap() to the IssueTable so that it can filter new Issues agains it.
		issueTable = new IssueTable(parent, SWT.NONE);
		return issueTable;
	}

	@Override
	protected void addResultTableListeners(AbstractTableComposite<Issue> tableComposite) {
		super.addResultTableListeners(tableComposite);
		issueTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				StructuredSelection s = (StructuredSelection)e.getSelection();
				if (s.isEmpty())
					return;

				Issue issue = (Issue)s.getFirstElement();

				IssueEditorInput issueEditorInput = new IssueEditorInput(IssueID.create(issue.getOrganisationID(), issue.getIssueID()));
				try {
					RCPUtil.openEditor(issueEditorInput, IssueEditor.EDITOR_ID);
				} catch (PartInitException e1) {
					throw new RuntimeException(e1);
				}
			}
		});

	}
	
//	private static final String[] FETCH_GROUPS_ISSUES = { 
//		Issue.FETCH_GROUP_THIS_ISSUE,
//		IssueType.FETCH_GROUP_THIS_ISSUE_TYPE,
//		IssueSeverityType.FETCH_GROUP_THIS_ISSUE_SEVERITY_TYPE,
////		IssuePriority.FETCH_GROUP_THIS_ISSUE_PRIORITY,
////		IssueDescription.FETCH_GROUP_THIS_DESCRIPTION,
////		IssueSubject.FETCH_GROUP_THIS_ISSUE_SUBJECT,
//		FetchPlan.DEFAULT
//		};
	
//	@Override
//	protected Object getQueryResult(Collection<? extends AbstractJDOQuery> queries,
//			ProgressMonitor monitor) {
//		try {
//			return IssueDAO.sharedInstance().getIssuesForQueries(
//					queries,
//					FETCH_GROUPS_ISSUES, 
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
//					monitor);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
	
	public IssueTable getIssueTable() {
		return issueTable;
	}

	@Override
	protected Collection<Issue> doSearch(QueryMap<Issue, ? extends IssueQuery> queryMap,
		ProgressMonitor monitor)
	{
		return IssueDAO.sharedInstance().getIssuesForQueries(
			queryMap,
			IssueTable.FETCH_GROUPS_ISSUE,
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
			monitor);
	}

	@Override
	protected Class<Issue> getResultType()
	{
		return Issue.class;
	}
}