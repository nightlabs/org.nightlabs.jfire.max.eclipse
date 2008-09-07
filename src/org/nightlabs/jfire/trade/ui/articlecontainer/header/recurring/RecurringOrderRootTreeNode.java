package org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring;

import java.util.List;
import java.util.Set;

import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.ArticleContainerRootTreeNode;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

public class RecurringOrderRootTreeNode extends ArticleContainerRootTreeNode {


	public RecurringOrderRootTreeNode(HeaderTreeNode parent, boolean customerSide)
	{
		super(parent, "Order", parent.getHeaderTreeComposite().getImageOrderRootTreeNode(), customerSide); //$NON-NLS-1$
		init();
	}


	@Override
	protected HeaderTreeNode createArticleContainerNode(byte position,
			ArticleContainer articleContainer) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Object> doLoadChildElements(AnchorID vendorID,
			AnchorID customerID, long rangeBeginIdx, long rangeEndIdx,
			ProgressMonitor monitor) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<ArticleContainer> doLoadNewArticleContainers(
			Set<ArticleContainerID> articleContainerIDs, ProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<? extends ArticleContainerID> getArticleContainerIDClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
