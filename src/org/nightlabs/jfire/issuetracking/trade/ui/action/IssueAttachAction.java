package org.nightlabs.jfire.issuetracking.trade.ui.action;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueNewWizard;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.dao.ArticleContainerDAO;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;


public class IssueAttachAction extends ArticleContainerAction
{
//	@Override
	public boolean calculateVisible()
	{
		return true;
	}

	@Override
	public boolean calculateEnabled()
	{
		return true; // super.calculateEnabled();
	}

	@Override
	public void run()
	{
		ArticleContainer articleContainer = this.getArticleContainer();
		Set<ObjectID> objectIDs = new HashSet<ObjectID>();
		objectIDs.add((ObjectID) JDOHelper.getObjectId(articleContainer));
		IssueNewWizard issueNewWizard = new IssueNewWizard(objectIDs);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(issueNewWizard);
		dialog.open();
	}
}

