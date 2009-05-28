package org.nightlabs.jfire.trade.ui.store;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.tree.lazy.ActiveJDOObjectLazyTreeController;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.ProductTypeParentResolver;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;



/**
 * @author Fitas Amine - fitas [dot] nightlabs [dot] de
 *
 */
public class ActiveProductTypeLazyTreeController extends ActiveJDOObjectLazyTreeController<ProductTypeID, ProductType, ProductTypeLazyTreeNode>
{
	public static final String[] FETCH_GROUPS_PRODUCT_TYPE = {
		FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME, ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL, ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID
	};

	@Override
	protected ProductTypeLazyTreeNode createNode()
	{
		return new ProductTypeLazyTreeNode();
	}

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver()
	{
		return new ProductTypeParentResolver();
	}

	@Override
	protected Class<ProductType> getJDOObjectClass()
	{
		return ProductType.class;
	}

	@Override
	protected Collection<ProductTypeID> retrieveChildObjectIDs(ProductTypeID parentID, IProgressMonitor monitor)
	{
		return ProductTypeDAO.sharedInstance().getChildProductTypesIDs(parentID, FETCH_GROUPS_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
	}

	@Override
	protected Collection<ProductType> retrieveJDOObjects(Set<ProductTypeID> objectIDs, IProgressMonitor monitor)
	{
		return ProductTypeDAO.sharedInstance().getProductTypes(objectIDs, FETCH_GROUPS_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
	}


	@Override
	protected Map<ProductTypeID, Long> retrieveChildCount(
			Set<ProductTypeID> parentIDs, IProgressMonitor monitor) {
		return ProductTypeDAO.sharedInstance().getChildProductTypeCounts(parentIDs, new ProgressMonitorWrapper(monitor));
	}

}
