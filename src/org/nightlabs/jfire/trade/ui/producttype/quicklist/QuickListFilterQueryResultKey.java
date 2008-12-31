/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import org.nightlabs.jfire.query.store.QueryStore;
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

	/**
	 * This is not an id 
	 */
	private QueryStore queryStore;
	private AnchorID vendorID;
	
	public QuickListFilterQueryResultKey(QueryStore queryStore, AnchorID vendorID) {
		this.queryStore = queryStore;
		this.vendorID = vendorID;
	}
	
	public QueryStore getQueryStore() {
		return queryStore;
	}
	
	public AnchorID getVendorID() {
		return vendorID;
	}

	@Override
	public int hashCode() {
		return Util.hashCode(this.queryStore) ^ Util.hashCode(this.vendorID);  
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
			Util.equals(this.queryStore, ((QuickListFilterQueryResultKey)obj).queryStore) &&
			Util.equals(this.vendorID, ((QuickListFilterQueryResultKey)obj).vendorID);
	}
	
}
