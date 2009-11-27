package org.nightlabs.jfire.trade.ui.articlecontainer.detail.recurring;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditComposite;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditFactory;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.DefaultArticleContainerEdit;

public class RecurringArticleContainerEdit extends DefaultArticleContainerEdit {

	/**
	 * This factory creates instances of {@link DefaultArticleContainerEdit}.
	 */
	public static class RecurringFactory implements ArticleContainerEditFactory {
		@Override
		public ArticleContainerEdit createArticleContainerEdit() {
			return new RecurringArticleContainerEdit();
		}
	}
	
	public RecurringArticleContainerEdit() {
	}
	
		
	@Override	
	protected ArticleContainerEditComposite createArticleContainerEditComposite(Composite parent, ArticleContainerID articleContainerID) {
		return new RecurringArticleContainerEditComposite(parent, articleContainerID);
	}
	
	
	
	
	
}
