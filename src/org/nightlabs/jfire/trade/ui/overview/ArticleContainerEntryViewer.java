package org.nightlabs.jfire.trade.ui.overview;

import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.base.ui.overview.search.JDOQuerySearchEntryViewer;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuickSearchQuery;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marco Schulze - Marco at NightLabs dot de
 */
public abstract class ArticleContainerEntryViewer<R extends ArticleContainer, Q extends AbstractArticleContainerQuickSearchQuery<R>>
	extends JDOQuerySearchEntryViewer<R, Q>
{
	public ArticleContainerEntryViewer(Entry entry) {
		super(entry);
	}

//	@Override
//	protected void optimizeSearchResults(Object result)
//	{
//		if (result instanceof Collection) {
//			Collection articleContainers = (Collection) result;
//			Set<AnchorID> anchorIDs = new HashSet<AnchorID>(articleContainers.size() * 2);
//			for (Object object : articleContainers) {
//				if (object instanceof ArticleContainer) {
//					ArticleContainer articleContainer = (ArticleContainer) object;
//					anchorIDs.add(articleContainer.getVendorID());
//					anchorIDs.add(articleContainer.getCustomerID());
//				}
//			}
//			String[] FETCH_GROUP_ARTICLE_CONTAINER_ANCHORS = new String[] {
//					LegalEntity.FETCH_GROUP_PERSON,
//					FetchPlan.DEFAULT
//			};
//			// fetch the name of the customer and the vendor at once to add them to the cache
//			// what avoids afterwards multiple connections to the server in labelProvider
//			LegalEntityProvider.sharedInstance().getLegalEntities(
//					anchorIDs.toArray(new AnchorID[anchorIDs.size()]),
//					FETCH_GROUP_ARTICLE_CONTAINER_ANCHORS,
//					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//		}
//	}

}
