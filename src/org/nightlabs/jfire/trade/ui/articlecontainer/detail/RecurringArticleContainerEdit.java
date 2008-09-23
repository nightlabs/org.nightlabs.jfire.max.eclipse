package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.eclipse.swt.widgets.Composite;

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
