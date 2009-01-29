package org.nightlabs.jfire.voucher.admin.ui.tree;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IProgressMonitor;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.jdo.tree.ActiveJDOObjectTreeController;
import org.nightlabs.jfire.jdo.notification.TreeNodeParentResolver;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.notification.VoucherTypeParentResolver;
import org.nightlabs.jfire.voucher.store.VoucherType;

public abstract class ActiveVoucherTypeTreeController
extends ActiveJDOObjectTreeController<ProductTypeID, VoucherType, VoucherTypeTreeNode>
{
	public static final String[] FETCH_GROUPS_VOUCHER_TYPE = {
		FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID
	};

	@Override
	protected VoucherTypeTreeNode createNode()
	{
		return new VoucherTypeTreeNode();
	}

	@Override
	protected Collection<VoucherType> retrieveChildren(ProductTypeID parentID, VoucherType parent, IProgressMonitor monitor)
	{
		Collection<VoucherType> res = VoucherTypeDAO.sharedInstance().getChildVoucherTypes(
				parentID, FETCH_GROUPS_VOUCHER_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
		return res;
	}

	@Override
	protected Collection<VoucherType> retrieveJDOObjects(Set<ProductTypeID> objectIDs, IProgressMonitor monitor)
	{
		Collection<VoucherType> res = VoucherTypeDAO.sharedInstance().getVoucherTypes(
				objectIDs, FETCH_GROUPS_VOUCHER_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new ProgressMonitorWrapper(monitor));
		return res;
	}

	@Override
	protected void sortJDOObjects(List<VoucherType> objects)
	{
		// no need to sort now - later maybe
	}

	@Override
	protected TreeNodeParentResolver createTreeNodeParentResolver()
	{
		return new VoucherTypeParentResolver();
	}

	@Override
	protected Class<VoucherType> getJDOObjectClass()
	{
		return VoucherType.class;
	}
}
