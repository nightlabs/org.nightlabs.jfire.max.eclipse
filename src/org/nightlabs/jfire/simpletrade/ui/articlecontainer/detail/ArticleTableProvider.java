package org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Tariff;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.simpletrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.dao.ArticleDAO;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleTableProviderConstants;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.tableprovider.ui.TableProvider;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleTableProvider
implements TableProvider<ArticleID, Article>
{
	private static final long serialVersionUID = 5382512257907547221L;

	public static final String TYPE_PRODUCT_TYPE_NAME = "ProductTypeName"; //$NON-NLS-1$
	public static final String TYPE_TARIFF = "Tariff"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProvider#getObjects(java.util.Collection, org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	public Map<ArticleID, Article> getObjects(Collection<ArticleID> objectIDs, String scope, ProgressMonitor monitor)
	{
		String[] fetchGroups = getFetchGroups(scope);
		if (fetchGroups == null) {
			return Collections.emptyMap();
		}
		Collection<Article> articles = ArticleDAO.sharedInstance().getArticles(
				objectIDs,
				fetchGroups,
				NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				monitor);
		Map<ArticleID, Article> articleID2Article = new HashMap<ArticleID, Article>();
		for (Article article : articles) {
			if (isCompatible(article, scope)) {
				ArticleID articleID = (ArticleID) JDOHelper.getObjectId(article);
				articleID2Article.put(articleID, article);
			}
		}
		return articleID2Article;
	}

	protected String[] getFetchGroups(String scope) {
		if (scope.equals(ArticleTableProviderConstants.SCOPE_PRODUCT_TYPE)) {
			return new String[] {FetchPlan.DEFAULT, Article.FETCH_GROUP_PRODUCT_TYPE,
					ProductType.FETCH_GROUP_NAME, Article.FETCH_GROUP_TARIFF, Tariff.FETCH_GROUP_NAME};
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProvider#getText(java.util.Set, java.lang.Object)
	 */
	@Override
	public String getText(Set<String> types, Article element, String scope)
	{
		if (scope.equals(ArticleTableProviderConstants.SCOPE_PRODUCT_TYPE)) {
			if (types.contains(TYPE_PRODUCT_TYPE_NAME)) {
				return element.getProductType().getName().getText();
			}
			else if (types.contains(TYPE_TARIFF)) {
				Tariff tariff = element.getTariff();
				return tariff.getName().getText();
			}
		}
		return ""; //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProvider#getTypeName(java.lang.String)
	 */
	@Override
	public String getTypeName(String type)
	{
		if (type.equals(TYPE_PRODUCT_TYPE_NAME)) {
			return Messages.getString("org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail.ArticleTableProvider.column.productTypeName"); //$NON-NLS-1$
		}
		else if (type.equals(TYPE_TARIFF)) {
			return Messages.getString("org.nightlabs.jfire.simpletrade.ui.articlecontainer.detail.ArticleTableProvider.column.tariff"); //$NON-NLS-1$
		}
		return type;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProvider#getTypes()
	 */
	@Override
	public String[] getTypes(String scope) {
		if (scope.equals(ArticleTableProviderConstants.SCOPE_PRODUCT_TYPE)) {
			return new String[] {TYPE_PRODUCT_TYPE_NAME, TYPE_TARIFF};
		}
		return new String[] {};
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProvider#isCompatible(java.lang.String)
	 */
	@Override
	public boolean isCompatible(Article article, String scope) {
		if (scope.equals(ArticleTableProviderConstants.SCOPE_PRODUCT_TYPE)) {
			try {
				return article.getProductType() instanceof SimpleProductType;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean isGeneric() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Image getColumnImage(Set<String> types, Article element, String scope) {
		// TODO Auto-generated method stub
		return null;
	}
}
