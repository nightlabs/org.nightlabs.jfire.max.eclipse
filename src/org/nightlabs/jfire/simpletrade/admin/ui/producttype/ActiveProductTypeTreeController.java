package org.nightlabs.jfire.simpletrade.admin.ui.producttype;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.simpletrade.dao.SimpleProductTypeDAO;
import org.nightlabs.jfire.simpletrade.notification.SimpleProductTypeParentResolver;
import org.nightlabs.jfire.simpletrade.store.SimpleProductType;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;

public abstract class ActiveProductTypeTreeController
extends ActiveJDOObjectTreeController<ProductTypeID, SimpleProductType, ProductTypeTreeNode>
{
	@Override
	protected ProductTypeTreeNode createNode()
	{
		return new ProductTypeTreeNode();
	}

	public static final String[] FETCH_GROUPS_PRODUCT_TYPE = {
		FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME, ProductType.FETCH_GROUP_PRODUCT_TYPE_LOCAL, ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID
	};

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver()
	{
		return new SimpleProductTypeParentResolver();
	}

	@Override
	protected Class<SimpleProductType> getJDOObjectClass()
	{
		return SimpleProductType.class;
	}

	@Override
	protected Collection<SimpleProductType> retrieveChildren(ProductTypeID parentID, SimpleProductType parent, IProgressMonitor monitor)
	{
		return SimpleProductTypeDAO.sharedInstance().getChildSimpleProductTypes(parentID, FETCH_GROUPS_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
	}

	@Override
	protected Collection<SimpleProductType> retrieveJDOObjects(Set<ProductTypeID> objectIDs, IProgressMonitor monitor)
	{
		return SimpleProductTypeDAO.sharedInstance().getSimpleProductTypes(objectIDs, FETCH_GROUPS_PRODUCT_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
	}

	@Override
	protected void sortJDOObjects(List<SimpleProductType> objects)
	{
		// no need to sort - maybe later
	}
}
