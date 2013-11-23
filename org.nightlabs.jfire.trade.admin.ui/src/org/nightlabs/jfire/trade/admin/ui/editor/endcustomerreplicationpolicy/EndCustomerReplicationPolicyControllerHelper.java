package org.nightlabs.jfire.trade.admin.ui.editor.endcustomerreplicationpolicy;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.prop.StructField;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.admin.ui.resource.Messages;
import org.nightlabs.jfire.trade.dao.EndCustomerReplicationPolicyDAO;
import org.nightlabs.jfire.trade.endcustomer.EndCustomerReplicationPolicy;
import org.nightlabs.jfire.trade.endcustomer.id.EndCustomerReplicationPolicyID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class EndCustomerReplicationPolicyControllerHelper
{
	private ProductTypeID productTypeID;
	private ProductType productType;
	private EndCustomerReplicationPolicyID endCustomerReplicationPolicyID;
	private EndCustomerReplicationPolicy endCustomerReplicationPolicy;

	private static final String[] FETCH_GROUPS_PRODUCT_TYPE = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID,
		ProductType.FETCH_GROUP_FIELD_METADATA_MAP,
		ProductType.FETCH_GROUP_END_CUSTOMER_TRANSFER_POLICY
	};

	private static final String[] FETCH_GROUPS_END_CUSTOMER_TRANSFER_POLICY = {
		FetchPlan.DEFAULT,
		EndCustomerReplicationPolicy.FETCH_GROUP_NAME,
		EndCustomerReplicationPolicy.FETCH_GROUP_DESCRIPTION,
		EndCustomerReplicationPolicy.FETCH_GROUP_STRUCT_FIELDS,
		StructField.FETCH_GROUP_NAME
	};

	public void load(ProductTypeID productTypeID, ProgressMonitor monitor)
	{
		monitor.beginTask(Messages.getString("org.nightlabs.jfire.trade.admin.ui.editor.endcustomerreplicationpolicy.EndCustomerReplicationPolicyControllerHelper.job.name"), 100); //$NON-NLS-1$
		try {
			this.productTypeID = productTypeID;
			productType = null;
			endCustomerReplicationPolicyID = null;
			endCustomerReplicationPolicy = null;

			if (productTypeID == null) {
				monitor.worked(100);
				return;
			}

			productType = ProductTypeDAO.sharedInstance().getProductType(
					productTypeID,
					FETCH_GROUPS_PRODUCT_TYPE,
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new SubProgressMonitor(monitor, 50)
			);

			if (productType.getEndCustomerReplicationPolicy() != null) {
				endCustomerReplicationPolicyID = (EndCustomerReplicationPolicyID) JDOHelper.getObjectId(productType.getEndCustomerReplicationPolicy());

				endCustomerReplicationPolicy = EndCustomerReplicationPolicyDAO.sharedInstance().getEndCustomerReplicationPolicy(
						endCustomerReplicationPolicyID,
						FETCH_GROUPS_END_CUSTOMER_TRANSFER_POLICY,
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new SubProgressMonitor(monitor, 50)
				);
			}
			else
				monitor.worked(50);

		} finally {
			monitor.done();
		}
	}

	public ProductTypeID getProductTypeID() {
		return productTypeID;
	}

	public ProductType getProductType() {
		return productType;
	}

	public EndCustomerReplicationPolicy getEndCustomerReplicationPolicy() {
		return endCustomerReplicationPolicy;
	}
}
