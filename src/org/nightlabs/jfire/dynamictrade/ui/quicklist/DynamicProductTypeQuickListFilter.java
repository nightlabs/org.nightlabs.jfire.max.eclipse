package org.nightlabs.jfire.dynamictrade.ui.quicklist;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jdo.search.SearchFilter;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductTypeSearchFilter;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.progress.ProgressMonitor;

public class DynamicProductTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
{
	public static String[] FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_NO_LIMIT,
		ProductType.FETCH_GROUP_NAME};

	private DynamicProductTypeTable dynamicProductTypeTable =null;

	public DynamicProductTypeQuickListFilter() {
		super();

	}

	@Override
	public Control doCreateResultViewerControl(Composite parent)
	{
		dynamicProductTypeTable = new DynamicProductTypeTable(parent);
		return dynamicProductTypeTable;
	}



	// TODO temporary workaround - this should come from the query store. 
	private DynamicProductTypeSearchFilter dynamicProductTypeSearchFilter;
	@Override
	public DynamicProductTypeSearchFilter getProductTypeSearchFilter() {
		if (dynamicProductTypeSearchFilter == null)
			dynamicProductTypeSearchFilter = new DynamicProductTypeSearchFilter(SearchFilter.CONJUNCTION_DEFAULT);

		return dynamicProductTypeSearchFilter;
	}

	public void setVendorID(AnchorID vendorID) 
	{
		getProductTypeSearchFilter().setVendorID(vendorID);
	}

	public String getDisplayName()
	{
		return Messages.getString("org.nightlabs.jfire.dynamictrade.ui.quicklist.DynamicProductTypeQuickListFilter.displayName"); //$NON-NLS-1$
	}

	@Override
	public Control getResultViewerControl() {
		return dynamicProductTypeTable;
	}

	@Override
	public Set<Class<? extends Object>> getClasses() {
		Set<Class<? extends Object>> classes = new HashSet<Class<? extends Object>>();
		classes.add(DynamicProductType.class);
		return classes;
	}

	@Override
	protected void search(ProgressMonitor monitor) {

		final DynamicProductTypeSearchFilter searchFilter = getProductTypeSearchFilter();
		try {
			QueryCollection<DynamicProductTypeSearchFilter> productTypeQueries = new QueryCollection<DynamicProductTypeSearchFilter>(ProductType.class);
			productTypeQueries.add(searchFilter);
			final Collection<ProductType> productTypes = ProductTypeDAO.sharedInstance().getProductTypes(productTypeQueries,
					FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE, 
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					monitor);

			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					dynamicProductTypeTable.setInput(productTypes);
				}
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
