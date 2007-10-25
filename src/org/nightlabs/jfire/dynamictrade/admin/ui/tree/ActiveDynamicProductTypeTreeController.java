package org.nightlabs.jfire.dynamictrade.admin.ui.tree;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.annotation.Implement;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.dynamictrade.dao.DynamicProductTypeDAO;
import org.nightlabs.jfire.dynamictrade.notification.DynamicProductTypeParentResolver;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

public abstract class ActiveDynamicProductTypeTreeController
extends ActiveJDOObjectTreeController<ProductTypeID, DynamicProductType, DynamicProductTypeTreeNode>
{
	@Override
	@Implement
	protected DynamicProductTypeTreeNode createNode()
	{
		return new DynamicProductTypeTreeNode();
	}

	public static final String[] FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE = {
		FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID,
//		DynamicProductType.FETCH_GROUP_OWNER, DynamicProductType.FETCH_GROUP_DELIVERY_CONFIGURATION,
//		DynamicProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE
	};

	@Override
	@Implement
	protected Collection<DynamicProductType> retrieveChildren(ProductTypeID parentID, DynamicProductType parent, IProgressMonitor monitor)
	{
		Collection<DynamicProductType> res = DynamicProductTypeDAO.sharedInstance().getChildDynamicProductTypes(
				parentID, FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new ProgressMonitorWrapper(monitor));
		return res;
	}

	@Override
	@Implement
	protected Collection<DynamicProductType> retrieveJDOObjects(Set<ProductTypeID> objectIDs, IProgressMonitor monitor)
	{
		Collection<DynamicProductType> res = DynamicProductTypeDAO.sharedInstance().getDynamicProductTypes(
				objectIDs, FETCH_GROUPS_DYNAMIC_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
				new ProgressMonitorWrapper(monitor));
		return res;
	}

	@Override
	@Implement
	protected void sortJDOObjects(List<DynamicProductType> objects)
	{
		// no need to sort now - later maybe
	}

	@Override
	@Implement
	protected TreeNodeParentResolver createTreeNodeParentResolver()
	{
		return new DynamicProductTypeParentResolver();
	}

	@Override
	@Implement
	protected Class getJDOObjectClass()
	{
		return DynamicProductType.class;
	}

}
