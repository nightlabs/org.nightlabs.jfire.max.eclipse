package org.nightlabs.jfire.trade.ui.transfer.print;

import org.nightlabs.jfire.trade.id.ArticleContainerID;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface IArticleContainerPrinter
{
	void printArticleContainer(ArticleContainerID articleContainerId)
//	throws PrinterException
	;
}
