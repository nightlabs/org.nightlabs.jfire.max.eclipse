package org.nightlabs.jfire.voucher.articlecontainer.detail;

import javax.jdo.FetchPlan;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder;
import org.nightlabs.jfire.voucher.dao.VoucherTypeDAO;
import org.nightlabs.jfire.voucher.store.VoucherType;
import org.nightlabs.progress.ProgressMonitor;

public class ArticleAdder
extends AbstractArticleAdder
{
	private VoucherType voucherType;

	@Implement
	protected Composite _createComposite(Composite parent)
	{
		return new ArticleAdderComposite(parent, this);
	}

	public static final String[] FETCH_GROUPS_VOUCHER_TYPE = {
		FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME
	};

	@Implement
	public void setProductTypeID(ProductTypeID productTypeID, ProgressMonitor monitor)
	{
		this.voucherType = VoucherTypeDAO.sharedInstance().getVoucherType(
				productTypeID, FETCH_GROUPS_VOUCHER_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
	}

	@Implement
	public ProductType getProductType()
	{
		return voucherType;
	}
}
