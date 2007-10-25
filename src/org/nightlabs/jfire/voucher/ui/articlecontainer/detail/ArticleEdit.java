package org.nightlabs.jfire.voucher.ui.articlecontainer.detail;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleEdit;

public class ArticleEdit extends AbstractArticleEdit
{
	private ArticleEditComposite articleEditComposite;

	@Override
	@Implement
	protected Composite _createComposite(Composite parent)
	{
		articleEditComposite = new ArticleEditComposite(parent, this);
		return articleEditComposite;
	}

	@Implement
	public Set<? extends ArticleCarrier> addArticles(Set<? extends ArticleCarrier> articleCarriers)
	{
		Class productTypeClass = getArticleProductTypeClassGroup().getProductTypeClass();

		Set<ArticleCarrier> accepted = null;
		for (Iterator<? extends ArticleCarrier> it = articleCarriers.iterator(); it.hasNext(); ) {
			ArticleCarrier articleCarrier = it.next();

			if (productTypeClass.equals(articleCarrier.getArticle().getProductType().getClass())) {
				if (accepted == null)
					accepted = new HashSet<ArticleCarrier>(articleCarriers.size());

				accepted.add(articleCarrier);
				it.remove();
			}
		}

		if (accepted != null) {
			_addArticleCarriers(accepted);
			articleEditComposite.refreshUI();
		}

		return articleCarriers; // we could return null here, if it's empty, but there's no advantage...it doesn't matter
	}

	@Implement
	public Set<? extends Article> getSelectedArticles()
	{
		return articleEditComposite.getSelectedArticles();
	}

//	@Implement
//	public void removeArticles(Set<? extends Article> articles)
//	{
//		_removeArticles(articles);
//		articleEditComposite.refreshUI();
//	}

	@Implement
	public Set<? extends Article> setSelectedArticles(Set<? extends Article> articles)
	{
		return articleEditComposite.setSelectedArticles(articles);
	}

}
