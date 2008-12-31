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
import org.nightlabs.jfire.base.jdo.cache.Cache;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.dynamictrade.store.search.DynamicProductTypeQuery;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.search.VendorDependentQuery;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.QuickListFilterQueryResult;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.QuickListFilterQueryResultKey;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class DynamicProductTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
{
	public static String[] FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_NO_LIMIT,
		ProductType.FETCH_GROUP_NAME};

	private DynamicProductTypeTable dynamicProductTypeTable =null;

	@Override
	public Control doCreateResultViewerControl(Composite parent)
	{
		dynamicProductTypeTable = new DynamicProductTypeTable(parent);
		return dynamicProductTypeTable;
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

	@SuppressWarnings("unchecked")
	@Override
	protected void search(ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.quicklist.DynamicProductTypeQuickListFilter.search.monitor.task.name"), 100); //$NON-NLS-1$
		final QueryCollection<VendorDependentQuery> productTypeQueries = getQueryCollection(new SubProgressMonitor(monitor, 50));
		try {
			QuickListFilterQueryResultKey cacheKey = createQueryResultCacheKey(new SubProgressMonitor(monitor, 10));
			QuickListFilterQueryResult<Collection<ProductType>> cacheResult = (QuickListFilterQueryResult<Collection<ProductType>>) Cache.sharedInstance().get(
					null, cacheKey, 
					FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			if (cacheResult == null) {

				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						dynamicProductTypeTable.setLoadingMessage(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.quicklist.DynamicProductTypeQuickListFilter.search.monitor.task.name")); //$NON-NLS-1$
					}
				});
				Collection<ProductType> queryResult = ProductTypeDAO.sharedInstance().getProductTypes(
						productTypeQueries,
						FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 50));
				cacheResult = new QuickListFilterQueryResult<Collection<ProductType>>(cacheKey, queryResult);
				Cache.sharedInstance().put(
						null, cacheKey, cacheResult, 
						FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
			}
			final Collection<ProductType> productTypes = cacheResult.getResult(); 
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					if (dynamicProductTypeTable.isDisposed())
						return;

					dynamicProductTypeTable.setInput(productTypes);
				}
			});
			monitor.done();
		} catch (Exception e) {
			monitor.setCanceled(true);
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter#getQueryClass()
	 */
	@Override
	protected Class<? extends VendorDependentQuery> getQueryClass() {
		return DynamicProductTypeQuery.class;
	}

}
