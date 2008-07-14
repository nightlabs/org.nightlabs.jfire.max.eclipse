package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse;

import org.nightlabs.jdo.ObjectIDUtil;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.jfire.store.id.ProductID;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Default implementation of {@link IProductIDParser}, which expects an
 * "normal" organisationID as string (e.g. yourorganisationname.host.topleveldomain)
 * and the productID as Base36 encoded string.
 * 
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DefaultProductIDParser 
implements IProductIDParser 
{
	private static final String SEPARATOR = "/";
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.reverse.IProductIDParser#isValid(java.lang.String, org.nightlabs.progress.ProgressMonitor)
	 */
	@Override
	public ProductID getProductID(String productIDString, ProgressMonitor monitor) 
	{
		String organisationID = getOrganisationID(productIDString);
		if (organisationID == null) {
			return null;
		}
		
		Long productID = getProductID(productIDString);
		if (productID == null) {
			return null;
		}
		
		if (organisationID != null && productID != null) {
			return ProductID.create(organisationID, productID);
		}
		
		return null;
	}

	public static String getOrganisationID(String productIDString) 
	{
		if (productIDString == null)
			return null;
		
		int index = productIDString.lastIndexOf(SEPARATOR);
		if (index != -1) {
			String organisationID = productIDString.substring(0, index);
			try {
				Organisation.assertValidOrganisationID(organisationID);
			} catch (IllegalArgumentException e) {
				return null;
			}
			return organisationID;
		}
		return null;
	}

	public static Long getProductID(String productIDString) 
	{
		if (productIDString == null)
			return null;
		
		int index = productIDString.lastIndexOf(SEPARATOR);
		if (index != -1) {
			String productID = productIDString.substring(index+1);
			try {
				return ObjectIDUtil.parseLongObjectIDField(productID);				
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}
	
}
