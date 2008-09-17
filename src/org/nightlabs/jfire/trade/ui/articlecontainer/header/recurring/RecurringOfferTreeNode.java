package org.nightlabs.jfire.trade.ui.articlecontainer.header.recurring;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.recurring.RecurringOffer;
import org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode;
import org.nightlabs.progress.ProgressMonitor;

public class RecurringOfferTreeNode extends HeaderTreeNode{



	private RecurringOffer recurringOffer;

	public RecurringOfferTreeNode(HeaderTreeNode parent, byte position, RecurringOffer offer)
	{
		super(parent, position);
		this.recurringOffer = offer;
		init();
	}


	@Override
	protected List<HeaderTreeNode> createChildNodes(List<Object> childData) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Image getColumnImage(int columnIndex)
	{
		switch (columnIndex) {
			case 0:
				return getHeaderTreeComposite().getImageOfferTreeNode();
			default:
				return null;
		}
	}

	@Override
	@Implement
	public String getColumnText(int columnIndex)
	{
		switch (columnIndex) {
			case 0: return ArticleContainerUtil.getArticleContainerID(recurringOffer);
			default:
				return ""; //$NON-NLS-1$
		}
	}

	@Override
	protected List<Object> loadChildData(ProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}


	public RecurringOffer getRecurringOffer() {
		return recurringOffer;
	}


	/**
	 * This method returns always <tt>false</tt>.
	 *
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.header.HeaderTreeNode#hasChildren()
	 */
	@Override
	public boolean hasChildren()
	{
		return false;
	}






}
