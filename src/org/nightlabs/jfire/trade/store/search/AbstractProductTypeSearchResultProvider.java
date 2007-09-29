package org.nightlabs.jfire.trade.store.search;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.search.ISearchResultProvider;
import org.nightlabs.base.ui.search.ISearchResultProviderFactory;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.store.ProductType;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public abstract class AbstractProductTypeSearchResultProvider 
implements ISearchResultProvider<ProductType>
{
	public AbstractProductTypeSearchResultProvider(ISearchResultProviderFactory factory) {
		this.factory = factory;
	}
	
	public Collection<ProductType> getSelectedObjects() 
	{
		AbstractProductTypeSearchDialog dialog = createProductTypeSearchDialog(
				RCPUtil.getActiveWorkbenchShell());
		dialog.setSearchText(searchText);
		int returnCode = dialog.open();
		if (returnCode == Dialog.OK) {
			Collection<ProductType> productTypes = new ArrayList<ProductType>(1);
			productTypes.add(dialog.getProductType());
			return productTypes;
		}
		return null;
	}

	private String searchText = null;
	public void setSearchText(String text) {
		this.searchText = text;
	}
	
	private ISearchResultProviderFactory factory = null;
	public ISearchResultProviderFactory getFactory() {
		return factory;
	}
	
	protected abstract AbstractProductTypeSearchDialog createProductTypeSearchDialog(Shell shell); 
}
