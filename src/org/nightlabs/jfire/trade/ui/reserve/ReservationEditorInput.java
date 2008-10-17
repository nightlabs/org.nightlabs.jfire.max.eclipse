package org.nightlabs.jfire.trade.ui.reserve;

import javax.jdo.FetchPlan;

import org.eclipse.ui.internal.part.NullEditorInput;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.dao.ProductTypeDAO;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author daniel
 *
 */
public class ReservationEditorInput extends NullEditorInput {

	private ProductTypeID productTypeID = null;
	private String productTypeName = null;

	public ReservationEditorInput(ProductTypeID productTypeID) {
		this.productTypeID = productTypeID;
	}

	public ProductTypeID getProductTypeID() {
		return productTypeID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.part.NullEditorInput#getName()
	 */
	@Override
	public String getName()
	{
		return "Reservation list "+getProductTypeName();
	}

	public String getProductTypeName()
	{
		if (productTypeName == null)
		{
			ProductType productType = ProductTypeDAO.sharedInstance().getProductType(productTypeID,
					new String[] {FetchPlan.DEFAULT, ProductType.FETCH_GROUP_NAME},
					NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
					new NullProgressMonitor());
			productTypeName = productType.getName().getText();
		}
		return productTypeName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.internal.part.NullEditorInput#getToolTipText()
	 */
	@Override
	public String getToolTipText() {
		return getName();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((productTypeID == null) ? 0 : productTypeID.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ReservationEditorInput other = (ReservationEditorInput) obj;
		if (productTypeID == null) {
			if (other.productTypeID != null)
				return false;
		} else if (!productTypeID.equals(other.productTypeID))
			return false;
		return true;
	}

}
