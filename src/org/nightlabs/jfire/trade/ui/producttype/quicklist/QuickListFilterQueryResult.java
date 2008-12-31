/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;


/**
 * 
 * @author Alexander Bieber
 * @version $Revision$, $Date$
 */
public class QuickListFilterQueryResult<ResultType> {

	/**
	 * This is only here, so this object gets invalidated in the cache 
	 * when the QueryStore changes.  
	 */
	private QuickListFilterQueryResultKey key;
	private ResultType result;
	
	public QuickListFilterQueryResult(QuickListFilterQueryResultKey key, ResultType result) {
		this.key = key;
		this.result = result;
	}
	
	public ResultType getResult() {
		return result;
	}
	
	public QuickListFilterQueryResultKey getKey() {
		return key;
	}
	
	public void setResult(ResultType result) {
		this.result = result;
	}
}
