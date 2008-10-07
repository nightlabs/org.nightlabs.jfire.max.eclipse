package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.MessageComposite;
import org.nightlabs.base.ui.composite.MessageComposite.MessageType;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.ui.TradePlugin;


public class RecurringArticleAdder extends ArticleAdder {
	
	@Override
	protected Composite createRequirementsNotFulfilledComposite(Composite parent) 
	{
			ArticleContainer ac = getSegmentEdit().getArticleContainer();
			String message = String.format(
					"Recurring Trade is currently not supported on Dynamic Trade", 
					TradePlugin.getArticleContainerTypeString(ac.getClass(), false), TradePlugin.getArticleContainerTypeString(ac.getClass(), true),
					ArticleContainerUtil.getArticleContainerID(ac)
					);
			return new MessageComposite(parent, SWT.NONE, message, MessageType.INFO);
		}

}
