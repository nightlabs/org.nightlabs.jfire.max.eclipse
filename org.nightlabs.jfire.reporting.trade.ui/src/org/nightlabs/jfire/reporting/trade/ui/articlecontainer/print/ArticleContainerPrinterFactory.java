package org.nightlabs.jfire.reporting.trade.ui.articlecontainer.print;

import org.nightlabs.jfire.trade.ui.transfer.print.IArticleContainerPrinter;
import org.nightlabs.jfire.trade.ui.transfer.print.IArticleContainerPrinterFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleContainerPrinterFactory
implements IArticleContainerPrinterFactory
{
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.transfer.print.IArticleContainerPrinterFactory#createArticleContainerPrinter()
	 */
	@Override
	public IArticleContainerPrinter createArticleContainerPrinter() {
		return new ArticleContainerPrinter();
	}

}
