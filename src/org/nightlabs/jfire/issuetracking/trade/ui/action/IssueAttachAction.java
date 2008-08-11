package org.nightlabs.jfire.issuetracking.trade.ui.action;

import javax.jdo.JDOHelper;

import org.eclipse.swt.graphics.Point;
import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.ObjectID;
import org.nightlabs.jfire.issuetracking.ui.issuelink.attach.AttachIssueToObjectWizard;
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

		AttachIssueToObjectWizard attachIssueToObjectWizard = new AttachIssueToObjectWizard(articleContainer);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(attachIssueToObjectWizard)
		{
			@Override
			protected Point getInitialSize()
			{
				return new Point(convertHorizontalDLUsToPixels(600), convertVerticalDLUsToPixels(450));
			}
		};
		dialog.open();
	}
}

