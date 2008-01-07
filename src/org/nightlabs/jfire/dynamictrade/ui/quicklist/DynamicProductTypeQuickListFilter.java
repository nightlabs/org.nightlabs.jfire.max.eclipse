package org.nightlabs.jfire.dynamictrade.ui.quicklist;

import java.util.Collection;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTypeDAO;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.StoreManager;
import org.nightlabs.jfire.store.StoreManagerUtil;
import org.nightlabs.jfire.trade.ui.producttype.quicklist.AbstractProductTypeQuickListFilter;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class DynamicProductTypeQuickListFilter
extends AbstractProductTypeQuickListFilter
//extends AbstractProductTypeViewerQuickListFilter
{

	public static String[] FETCH_GROUPS = new String[] {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_NO_LIMIT,
		ProductType.FETCH_GROUP_NAME};

	private DynamicProductTypeTable dynamicProductTypeTable;
	
	public Control createResultViewerControl(Composite parent)
	{
		dynamicProductTypeTable = new DynamicProductTypeTable(parent, this, AbstractTableComposite.DEFAULT_STYLE_SINGLE);
		return dynamicProductTypeTable;
	}
	
//	@Override
//	public StructuredViewer createViewer(Composite parent) {
//		dynamicProductTypeTable = new DynamicProductTypeTable(parent, this);
//		return dynamicProductTypeTable.getTableViewer();
//	}
	
	public String getDisplayName()
	{
		return Messages.getString("org.nightlabs.jfire.dynamictrade.ui.quicklist.DynamicProductTypeQuickListFilter.displayName"); //$NON-NLS-1$
	}

	public void search(ProgressMonitor monitor) {
		new Job(Messages.getString("org.nightlabs.jfire.dynamictrade.ui.quicklist.DynamicProductTypeQuickListFilter.searchJob.name")) { //$NON-NLS-1$
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					StoreManager storeManager = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
					final Collection<DynamicProductType> dynamicProductTypes = DynamicProductTypeDAO.sharedInstance().getDynamicProductTypes(
							ProductType.INHERITANCE_NATURE_LEAF, Boolean.TRUE, FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {			
							dynamicProductTypeTable.setInput(dynamicProductTypes);
						}
					});
					return Status.OK_STATUS;
				} catch (Exception x) {
					throw new RuntimeException(x);
				}
			}
		}.schedule();
		
//		DynamicProductTypeSearchFilter searchFilter = new DynamicProductTypeSearchFilter(SearchFilter.CONJUNCTION_DEFAULT);
//		try {
//			StoreManager storeManager = StoreManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();
//			Collection dynamicProductTypes = storeManager.searchProductTypes(searchFilter, DEFAULT_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
//			dynamicProductTypeTable.setInput(dynamicProductTypes);
//		} catch (Exception x) {
//			throw new RuntimeException(x);
//		}
	}

}
