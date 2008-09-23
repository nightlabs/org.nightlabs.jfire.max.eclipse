package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.eclipse.swt.widgets.Composite;

public class RecurringArticleContainerEdit extends DefaultArticleContainerEdit {

	
	@Override	
	protected ArticleContainerEditComposite createArticleContainerEditComposite(Composite parent, ArticleContainerID articleContainerID) {
		return new RecurringContainerEditComposite(parent, articleContainerID);
	}
	
	
	
	
	
}
