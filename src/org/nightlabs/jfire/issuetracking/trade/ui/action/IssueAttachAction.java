package org.nightlabs.jfire.issuetracking.trade.ui.action;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.JDOHelper;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueNewWizard;
import org.nightlabs.jfire.trade.ArticleContainer;
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
		Set<String> objectIDs = new HashSet<String>();
		objectIDs.add(JDOHelper.getObjectId(articleContainer).toString());
		IssueNewWizard issueNewWizard = new IssueNewWizard(objectIDs);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(issueNewWizard);
		dialog.open();
	}
}

