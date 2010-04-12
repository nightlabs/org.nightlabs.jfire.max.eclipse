package org.nightlabs.jfire.trade.ui.articlecontainer.detail.info;

import org.eclipse.jface.resource.ImageDescriptor;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DefaultArticleContainerInfoDelegate
implements ArticleContainerInfoDelegate
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.info.ArticleContainerInfoDelegate#getImage(org.nightlabs.jfire.trade.id.ArticleContainerID)
	 */
	@Override
	public ImageDescriptor getImageDescriptor(ArticleContainerID articleContainerID, ProgressMonitor monitor)
	{
		if (articleContainerID == null)
			return null;

		Class<? extends ArticleContainer> acClass = ArticleContainerUtil.getArticleContainerClassByID(articleContainerID);
		return TradePlugin.getArticleContainerImageDescriptor(acClass);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.info.ArticleContainerInfoDelegate#getText(org.nightlabs.jfire.trade.id.ArticleContainerID)
	 */
	@Override
	public String getText(ArticleContainerID articleContainerID, ProgressMonitor monitor)
	{
		if (articleContainerID == null)
			return "";

		Class<? extends ArticleContainer> acClass = ArticleContainerUtil.getArticleContainerClassByID(articleContainerID);
		return TradePlugin.getArticleContainerTypeString(acClass, true);
	}

}
