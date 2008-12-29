/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import org.nightlabs.jfire.query.store.id.QueryStoreID;
import org.nightlabs.jfire.transfer.id.AnchorID;
import org.nightlabs.util.Util;

/**
 * This key can be used to identify a stored query-result in the cache. 
 * It references the {@link QueryStoreID} of the query-collection 
 * and the vendorID.
 * 
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public class QuickListFilterQueryResultKey {

	private QueryStoreID queryStoreID;
	private AnchorID vendorID;
	
	public QuickListFilterQueryResultKey(QueryStoreID queryStoreID, AnchorID vendorID) {
		this.queryStoreID = queryStoreID;
		this.vendorID = vendorID;
	}

	@Override
	public int hashCode() {
		return Util.hashCode(this.queryStoreID) ^ Util.hashCode(this.vendorID);  
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (obj.getClass() != QuickListFilterQueryResultKey.class)
			return false;
		return 
			Util.equals(this.queryStoreID, ((QuickListFilterQueryResultKey)obj).queryStoreID) &&
			Util.equals(this.vendorID, ((QuickListFilterQueryResultKey)obj).vendorID);
	}
	
}
