package org.nightlabs.jfire.trade.ui.store.search;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.search.ISearchResultProvider;
import org.nightlabs.base.ui.search.ISearchResultProviderFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.store.ProductType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractProductTypeSearchResultProvider<P extends ProductType>
implements ISearchResultProvider<P>
{
	public AbstractProductTypeSearchResultProvider(ISearchResultProviderFactory<P> factory) {
		this.factory = factory;
	}
	
	public Collection<P> getSelectedObjects()
	{
		AbstractProductTypeSearchDialog dialog = createProductTypeSearchDialog(
				RCPUtil.getActiveShell());
		dialog.setSearchText(searchText);
		int returnCode = dialog.open();
		if (returnCode == Window.OK) {
			Collection<P> productTypes = new ArrayList<P>(1);
			productTypes.add((P) dialog.getProductType());
			return productTypes;
		}
		return null;
	}

	private String searchText = null;
	public void setSearchText(String text) {
		this.searchText = text;
	}
	
	private ISearchResultProviderFactory<P> factory = null;
	public ISearchResultProviderFactory<P> getFactory() {
		return factory;
	}
	
	protected abstract AbstractProductTypeSearchDialog createProductTypeSearchDialog(Shell shell);
}
