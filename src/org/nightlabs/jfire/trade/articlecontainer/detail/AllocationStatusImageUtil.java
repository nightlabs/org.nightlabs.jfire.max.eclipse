package org.nightlabs.jfire.trade.articlecontainer.detail;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.TradePlugin;

public class AllocationStatusImageUtil
{
	public static Image getAllocationStatusImage(Article article)
	{
		if (article.isAllocationAbandoned())
			return SharedImages.getSharedImage(TradePlugin.getDefault(), AllocationStatusImageUtil.class, "allocationAbandoned"); //$NON-NLS-1$
		else if (article.isAllocationPending())
			return SharedImages.getSharedImage(TradePlugin.getDefault(), AllocationStatusImageUtil.class, "allocationPending"); //$NON-NLS-1$
		else if (article.isReleaseAbandoned())
			return SharedImages.getSharedImage(TradePlugin.getDefault(), AllocationStatusImageUtil.class, "releaseAbandoned"); //$NON-NLS-1$
		else if (article.isReleasePending())
			return SharedImages.getSharedImage(TradePlugin.getDefault(), AllocationStatusImageUtil.class, "releasePending"); //$NON-NLS-1$
		else if (article.isAllocated())
			return SharedImages.getSharedImage(TradePlugin.getDefault(), AllocationStatusImageUtil.class, "allocated"); //$NON-NLS-1$
		else
			return SharedImages.getSharedImage(TradePlugin.getDefault(), AllocationStatusImageUtil.class, "notAllocated"); //$NON-NLS-1$
	}
}
