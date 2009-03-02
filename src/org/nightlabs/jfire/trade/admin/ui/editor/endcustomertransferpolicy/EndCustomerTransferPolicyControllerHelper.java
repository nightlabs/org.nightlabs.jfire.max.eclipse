package org.nightlabs.jfire.trade.admin.ui.editor.endcustomertransferpolicy;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.prop.StructField;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.dao.EndCustomerTransferPolicyDAO;
import org.nightlabs.jfire.trade.endcustomer.EndCustomerTransferPolicy;
import org.nightlabs.jfire.trade.endcustomer.id.EndCustomerTransferPolicyID;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

public class EndCustomerTransferPolicyControllerHelper
{
	private ProductTypeID productTypeID;
	private ProductType productType;
	private EndCustomerTransferPolicyID endCustomerTransferPolicyID;
	private EndCustomerTransferPolicy endCustomerTransferPolicy;

	private static final String[] FETCH_GROUPS_PRODUCT_TYPE = {
		FetchPlan.DEFAULT,
		ProductType.FETCH_GROUP_EXTENDED_PRODUCT_TYPE_ID,
		ProductType.FETCH_GROUP_FIELD_METADATA_MAP,
		ProductType.FETCH_GROUP_END_CUSTOMER_TRANSFER_POLICY
	};

	private static final String[] FETCH_GROUPS_END_CUSTOMER_TRANSFER_POLICY = {
		FetchPlan.DEFAULT,
		EndCustomerTransferPolicy.FETCH_GROUP_NAME,
		EndCustomerTransferPolicy.FETCH_GROUP_DESCRIPTION,
		EndCustomerTransferPolicy.FETCH_GROUP_STRUCT_FIELDS,
		StructField.FETCH_GROUP_NAME
	};

	public void load(ProductTypeID productTypeID, ProgressMonitor monitor)
	{
		monitor.beginTask("Loading end-customer transfer policy", 100);
		try {
			this.productTypeID = productTypeID;
			productType = null;
			endCustomerTransferPolicyID = null;
			endCustomerTransferPolicy = null;

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

			if (productType.getEndCustomerTransferPolicy() != null) {
				endCustomerTransferPolicyID = (EndCustomerTransferPolicyID) JDOHelper.getObjectId(productType.getEndCustomerTransferPolicy());

				endCustomerTransferPolicy = EndCustomerTransferPolicyDAO.sharedInstance().getEndCustomerTransferPolicy(
						endCustomerTransferPolicyID,
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

	public ProductType getProductType() {
		return productType;
	}

	public EndCustomerTransferPolicy getEndCustomerTransferPolicy() {
		return endCustomerTransferPolicy;
	}
}
