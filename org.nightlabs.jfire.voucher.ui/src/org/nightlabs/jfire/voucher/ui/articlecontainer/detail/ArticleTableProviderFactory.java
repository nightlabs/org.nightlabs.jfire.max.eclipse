package org.nightlabs.jfire.voucher.ui.articlecontainer.detail;

import org.nightlabs.tableprovider.ui.AbstractTableProviderFactory;
import org.nightlabs.tableprovider.ui.TableProvider;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleTableProviderFactory extends AbstractTableProviderFactory {

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProviderFactory#createTableProvider()
	 */
	@Override
	public TableProvider createTableProvider() {
		return new ArticleTableProvider();
	}

}
