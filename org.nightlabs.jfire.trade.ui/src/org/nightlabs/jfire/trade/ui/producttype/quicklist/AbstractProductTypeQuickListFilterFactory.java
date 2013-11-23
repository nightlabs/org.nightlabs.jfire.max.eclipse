/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public abstract class AbstractProductTypeQuickListFilterFactory implements IProductTypeQuickListFilterFactory {

	private String id;
	private int index;


	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilterFactory#getId()
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.nightlabs.jfire.trade.ui.producttype.quicklist.IProductTypeQuickListFilterFactory#getIndex()
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement element, String propertyName, Object data) throws CoreException {
		id = element.getAttribute("id"); //$NON-NLS-1$
		String iStr = element.getAttribute("index"); //$NON-NLS-1$
		try {
			index = Integer.parseInt(iStr);
		} catch (Exception e) {
			index = 1000;
		}
	}

}
