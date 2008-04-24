package org.nightlabs.jfire.issuetracking.trade.ui.action;

import javax.jdo.JDOHelper;

import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleContainerAction;


public class IssueAttachAction extends ArticleContainerAction
{
	public boolean calculateVisible()
	{
		return true;
	}

	@Override
	public boolean calculateEnabled()
	{
		return true;
	}

	@Override
	public void run()
	{
		ArticleContainer articleContainer = this.getArticleContainer();
		ObjectID objectID = (ObjectID)JDOHelper.getObjectId(articleContainer);
		
//		IssueCreateWizard issueCreateWizard = new IssueCreateWizard();
//		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(issueCreateWizard);
//		dialog.open();
	}
}

