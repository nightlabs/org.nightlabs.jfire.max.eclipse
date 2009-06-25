package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.dynamictrade.store.DynamicProduct;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.store.Product;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.Unit;
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
	public static final String TYPE_PRODUCT_TYPE_NAME = "ProductTypeName";
	public static final String TYPE_DYNAMIC_PRODUCT_TYPE_NAME = "ProductName";
	public static final String TYPE_QUANTITY = "Quantity";
	public static final String TYPE_UNIT = "Unit";

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
					ProductType.FETCH_GROUP_NAME, Article.FETCH_GROUP_TARIFF,
					Article.FETCH_GROUP_PRODUCT, DynamicProduct.FETCH_GROUP_NAME,
					DynamicProduct.FETCH_GROUP_UNIT, Unit.FETCH_GROUP_SYMBOL};
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
			else if (types.contains(TYPE_DYNAMIC_PRODUCT_TYPE_NAME)) {
				Product product = element.getProduct();
				if (product instanceof DynamicProduct) {
					DynamicProduct dynamicProduct = (DynamicProduct) product;
					return dynamicProduct.getName().getText();
				}
			}
			else if (types.contains(TYPE_QUANTITY)) {
				Product product = element.getProduct();
				if (product instanceof DynamicProduct) {
					DynamicProduct dynamicProduct = (DynamicProduct) product;
					return String.valueOf(dynamicProduct.getQuantityAsDouble());
				}
			}
			else if (types.contains(TYPE_UNIT)) {
				Product product = element.getProduct();
				if (product instanceof DynamicProduct) {
					DynamicProduct dynamicProduct = (DynamicProduct) product;
					return dynamicProduct.getUnit().getSymbol().getText();
				}
			}
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProvider#getTypeName(java.lang.String)
	 */
	@Override
	public String getTypeName(String type)
	{
		if (type.equals(TYPE_PRODUCT_TYPE_NAME)) {
			return "ProductType Name";
		}
		else if (type.equals(TYPE_PRODUCT_TYPE_NAME)) {
			return "Product Name";
		}
		else if (type.equals(TYPE_QUANTITY)) {
			return "Quantity";
		}
		else if (type.equals(TYPE_UNIT)) {
			return "Unit";
		}

		return type;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProvider#getTypes()
	 */
	@Override
	public String[] getTypes(String scope) {
		if (scope.equals(ArticleTableProviderConstants.SCOPE_PRODUCT_TYPE))
			return new String[] {TYPE_PRODUCT_TYPE_NAME, TYPE_DYNAMIC_PRODUCT_TYPE_NAME, TYPE_QUANTITY, TYPE_UNIT};

		return new String[] {};
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProvider#isCompatible(java.lang.String)
	 */
	@Override
	public boolean isCompatible(Article article, String scope)
	{
		if (scope.equals(ArticleTableProviderConstants.SCOPE_PRODUCT_TYPE)) {
			try {
				return article.getProductType() instanceof DynamicProductType;
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean isGeneric() {
		return false;
	}
}
