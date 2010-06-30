package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.jdo.FetchPlan;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssueLinkDAO;
import org.nightlabs.jfire.issue.issuemarker.IssueMarker;
import org.nightlabs.jfire.issuetracking.trade.ui.resource.Messages;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueTable;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.progress.ProgressMonitor;

/**
 * This is meant to be used as a direct control for the {@link IssueTable} located inside the {@link ShowLinkedIssueSection}.
 * See comments in the constructor below on strategy.
 *
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 * @author Khaireel Mohamed - khaireel at nightlabs dot de
 */
public class ShowLinkedIssuePageController
extends EntityEditorPageController
{
	private ArticleContainerID articleContainerID;
	private Collection<Issue> linkedIssues;

	/**
	 * The fetch groups of linked issue data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		IssueLink.FETCH_GROUP_ISSUE,
		Issue.FETCH_GROUP_ISSUE_TYPE,
		Issue.FETCH_GROUP_ISSUE_ASSIGNEE,
		Issue.FETCH_GROUP_ISSUE_REPORTER,
		Issue.FETCH_GROUP_SUBJECT,
		Issue.FETCH_GROUP_DESCRIPTION,
		Issue.FETCH_GROUP_ISSUE_SEVERITY_TYPE,
		Issue.FETCH_GROUP_ISSUE_PRIORITY,
		Statable.FETCH_GROUP_STATE,
		Issue.FETCH_GROUP_ISSUE_LOCAL,
		StatableLocal.FETCH_GROUP_STATE,
		State.FETCH_GROUP_STATE_DEFINITION,
		IssueType.FETCH_GROUP_NAME,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssuePriority.FETCH_GROUP_NAME,
		StateDefinition.FETCH_GROUP_NAME,
		Issue.FETCH_GROUP_ISSUE_MARKERS,          // <-- Since 14.05.2009
		IssueMarker.FETCH_GROUP_NAME,             // <-- Since 14.05.2009
		IssueMarker.FETCH_GROUP_ICON_16X16_DATA,  // <-- Since 14.05.2009
	};

	/**
	 * @param editor
	 */
	public ShowLinkedIssuePageController(EntityEditor editor) {
		super(editor);
		this.articleContainerID = ((ArticleContainerEditorInput) editor.getEditorInput()).getArticleContainerID();
		linkedIssues = new HashSet<Issue>();
		issueLinks = new ArrayList<IssueLink>();

		// [Observation and strategy, 23.06.2009]: Kai.
		//   ~ Let <issues> be all the Issues listed in the IssueTable.
		//   ~ Let <linkedObject> be the common Entity of which all of the <issues> in the IssueTable is 'related' to (via valid IssueLinks).
		//
		// Since all IssueLinks are uni-directional, then it follows that the <linkedObject> of all <issues> does not have the knowledge
		// that it is 'related' to them all. However, the reverse is true.
		//
		// Thus, in order to ensure that the IssueTable correctly displays all <issues> related to the <linkedObject>, without having
		// to create an additional field in the <linkedObject>, we need:
		//
		//   1. A listener on the data store, listening to the events that (i) a new IssueLink has been added, and (ii) an existing
		//      IssueLink has been deleted. In both events, we should check to see if the linked object in the IssueLink is exactly
		//      the same <linkedObject> (and that, additionally (?), the IssueLinkType == Related). If so, then we update the IssueTable.
		//      --> Furthermore, we know that an IssueLinks are never modified, so the above should be sufficient.
		//
		//  2. A further set of listeners, for every single <issue> that is already in the IssueTable, listening to modification events.
		//     That is, we should update modifications in the IssueTable whenever there is a subject change, or description change, etc.
		//     --> Other kinds of listeners, such as add and delete, for the <issues> are not necessary, since add and delete events for
		//         an Issue, will automatically trigger its related IssueLink to be added or removed, and this is already handled
		//         in 1.
		//
		// All these should be implemented somewhere where there is a chance to related the creation and disposal of the listeners
		// directly to the IssueTable (as is clearly defined by the ground rules in the Wiki: https://www.jfire.org/modules/phpwiki/index.php/ClientSide%20JDO%20Lifecycle%20Listeners).
		//
		// Logically, we should place the listeners in the IssueTable class itself, as is dictated above. However, the IssueTable appears in several situations,
		// and not all of them require the active functionality described thus far. Hence, the closest we can get to the IssueTable, whose
		// behaviour we want to be portrayed like so, is to place the JDOLifecycleListener in ShowLinkedIssueSection; and then making sure
		// that the disposeListener is attached to the IssueTable's instance.
	}





	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public ShowLinkedIssuePageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}

	@Override
	public void doLoad(ProgressMonitor monitor) {
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.issuetracking.trade.ui.issuelink.ShowLinkedIssuePageController.monitor.loadIssues.text"), 10); //$NON-NLS-1$

		this.articleContainer =
			ArticleContainerDAO.sharedInstance().getArticleContainer(articleContainerID, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);

		issueLinks = IssueLinkDAO.sharedInstance().getIssueLinksByOrganisationIDAndLinkedObjectID(
				null, // This must be the local organisationID! The backend now chooses this automatically, when passing null. Marco.
				articleContainerID,
				FETCH_GROUPS,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);


		// In the case that existing IssueLinks are removed, we need to make sure that the current Collection of IssueLinks are
		// also updated. And hence, we clear the current colletion before adding new things.
		linkedIssues.clear();
		for (IssueLink issueLink : issueLinks)
			linkedIssues.add(issueLink.getIssue());

		fireModifyEvent(null, null);
	}

	@Override
	public boolean doSave(ProgressMonitor monitor) {
		return false;
	}

	public Collection<Issue> getLinkedIssues() {
		return linkedIssues;
	}

	public ArticleContainerID getArticleContainerID() {
		return articleContainerID;
	}

	private ArticleContainer articleContainer;
	public ArticleContainer getArticleContainer() {
		return articleContainer;
	}


	// ---[ Helper methods to manage the IssueLinks ]-------------------------------------------------------------------------------------|
	private Collection<IssueLink> issueLinks;
//	public Collection<IssueLink> getIssueLinks() {
//		return issueLinks;
//	}

	/**
	 * @return the IssueLink from the issueLinks collection matching the srcIssue. Null if no matching Issue is found.
	 */
	public IssueLink getRelatedIssueLink(Issue srcIssue) {
		for (IssueLink issueLink : issueLinks)
			if (issueLink.getIssue().equals(srcIssue)) {
				return issueLink;
			}

		return null;
	}

	/**
	 * Removes an IssueLink based on the given reference source Issue.
	 */
	public IssueLink removeRelatedIssueLink(Issue srcIssue) {
		IssueLink issueLink = getRelatedIssueLink(srcIssue);
		if (issueLink != null)
			issueLinks.remove(issueLink);

		return issueLink;
	}
	// -----------------------------------------------------------------------------------------------------------------------------------|


}