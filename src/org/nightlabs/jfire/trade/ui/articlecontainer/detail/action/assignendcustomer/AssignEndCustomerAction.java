package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.assignendcustomer;

import java.util.Set;

import org.nightlabs.base.ui.wizard.DynamicPathWizardDialog;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.GenericArticleEditAction;

public class AssignEndCustomerAction extends GenericArticleEditAction
{
	@Override
	public boolean calculateVisible() {
		return true;
	}

	@Override
	protected boolean excludeArticle(Article article) {
		return false;
	}

	@Override
	public void run() {
		Set<ArticleID> articleIDs = NLJDOHelper.getObjectIDSet(getArticles());
		AssignEndCustomerWizard wizard = new AssignEndCustomerWizard(articleIDs);
		DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
		dialog.open();
	}
}
