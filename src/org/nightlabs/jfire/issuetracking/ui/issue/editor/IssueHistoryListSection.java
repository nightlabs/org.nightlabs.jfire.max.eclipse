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
import org.nightlabs.jfire.issue.history.FetchGroupsIssueHistoryItem;
import org.nightlabs.jfire.issue.history.IssueHistoryItem;
import org.nightlabs.jfire.issue.history.IssueHistoryItemDAO;
import org.nightlabs.jfire.issue.id.IssueID;
import org.nightlabs.jfire.issuetracking.ui.issuehistory.IssueHistoryTable;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * A {@link RestorableSectionPart} that contains an {@link IssueHistoryTable} listing down all historical
 * contents pertaining to an {@link Issue}.
 *
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class IssueHistoryListSection extends RestorableSectionPart{
	private IssueHistoryTable issueHistoryTable;

	/**
	 * Creates a new instance of an IssueHistoryListSection.
	 */
	public IssueHistoryListSection(FormPage page, Composite parent) { //, IssueEditorPageController controller) { // No controllers needed here. Kai.
		super(parent, page.getEditor().getToolkit(), ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		getSection().setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.issue.editor.IssueHistoryListSection.title")); //$NON-NLS-1$
		getSection().setLayoutData(new GridData(GridData.FILL_BOTH));
		getSection().setLayout(new GridLayout());

		issueHistoryTable = new IssueHistoryTable(getSection(), SWT.NONE);
		issueHistoryTable.getGridData().grabExcessHorizontalSpace = true;

		getSection().setClient(issueHistoryTable);
	}

	/**
	 * Given an {@link Issue}, all {@link IssueHistoryItem}s related to it are loaded and subsequently displayed
	 * inside the {@link IssueHistoryTable}.
	 */
	public void setIssue(Issue issue) {
		IssueID issueID = (IssueID)JDOHelper.getObjectId(issue);
		Collection<IssueHistoryItem> issueHistoryItems = IssueHistoryItemDAO.sharedInstance().getIssueHistoryItems(
				issueID,
				new String[]{FetchPlan.DEFAULT, FetchGroupsIssueHistoryItem.FETCH_GROUP_LIST},	// Since 28 May 2009.
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new NullProgressMonitor());

//		issueHistoryTable.setIssueHistoryItems(issueID, issueHistoryItems);
		issueHistoryTable.setInput( issueHistoryItems );
	}
}
