package org.nightlabs.jfire.issuetracking.ui.issue.editor;

import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.nightlabs.base.ui.editor.RestorableSectionPart;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.history.IssueHistory;
import org.nightlabs.jfire.issue.history.IssueHistoryDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueHistoryTable;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.security.User;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueHistoryListSection extends RestorableSectionPart{

	private IssueHistoryTable issueHistoryTable;
	private IssueEditorPageController controller;
	
	public IssueHistoryListSection(FormPage page, Composite parent, IssueEditorPageController controller) {
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		this.controller = controller;
		getSection().setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueHistoryListSection.title")); //$NON-NLS-1$
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());
		
		issueHistoryTable = new IssueHistoryTable(getSection(), SWT.NONE);
		issueHistoryTable.getGridData().grabExcessHorizontalSpace = true;
		
		getSection().setClient(issueHistoryTable);
	}
	
	public void setIssue(Issue issue) {
		IssueID issueID = (IssueID)JDOHelper.getObjectId(issue);
		Collection<IssueHistory> issueHistories = IssueHistoryDAO.sharedInstance().getIssueHistories(
				issueID, 
				new String[]{FetchPlan.DEFAULT, IssueHistory.FETCH_GROUP_USER, User.FETCH_GROUP_NAME}, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
		
		issueHistoryTable.setIssueHistories(issueID, issueHistories);
	}
}
