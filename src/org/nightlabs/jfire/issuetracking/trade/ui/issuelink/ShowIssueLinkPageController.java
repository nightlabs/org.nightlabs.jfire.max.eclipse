package org.nightlabs.jfire.issuetracking.trade.ui.issuelink;

import java.util.Collection;
import java.util.HashSet;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageController;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.dao.IssueLinkDAO;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItem;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat at nightlabs dot de
 *
 */
public class ShowIssueLinkPageController 
extends EntityEditorPageController 
{
	private ArticleContainerID articleContainerID;
	private Collection<IssueLink> issueLinks;
	private Collection<IssueLinkTableItem> issueLinkTableItems;
	
	/**
	 * The fetch groups of issue link data.
	 */
	public static final String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		IssueLink.FETCH_GROUP_LINKED_OBJECT,
		IssueLink.FETCH_GROUP_ISSUE_LINK_TYPE};
	
	/**
	 * @param editor
	 */
	public ShowIssueLinkPageController(EntityEditor editor) {
		super(editor);
		this.articleContainerID = ((ArticleContainerEditorInput) editor.getEditorInput()).getArticleContainerID();
		issueLinkTableItems = new HashSet<IssueLinkTableItem>();
	}

	/**
	 * @param editor
	 * @param startBackgroundLoading
	 */
	public ShowIssueLinkPageController(EntityEditor editor, boolean startBackgroundLoading) {
		super(editor, startBackgroundLoading);
	}
	
	@Override
	public void doLoad(ProgressMonitor monitor) {
		monitor.beginTask("Loading issue links...", 10);
		
		ArticleContainer articleContainer =
			ArticleContainerDAO.sharedInstance().getArticleContainer(articleContainerID, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
		
		ObjectID objectID = (ObjectID)JDOHelper.getObjectId(articleContainer);
		issueLinks = IssueLinkDAO.sharedInstance().getIssueLinksByOrganisationIDAndLinkedObjectID(articleContainerID.getOrganisationID(), 
				objectID.toString(), 
				FETCH_GROUPS, 
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
				monitor);
		
		for (IssueLink issueLink : issueLinks) {
			issueLinkTableItems.add(new IssueLinkTableItem(objectID, issueLink.getIssueLinkType())); 
		}
		
		fireModifyEvent(null, null);
	}

	@Override
	public boolean doSave(ProgressMonitor monitor) {
		return false;
	}
	
	public Collection<IssueLink> getIssueLinks() {
		return issueLinks;
	}
	
	public Collection<IssueLinkTableItem> getIssueLinkTableItems() {
		return issueLinkTableItems;
	}
}