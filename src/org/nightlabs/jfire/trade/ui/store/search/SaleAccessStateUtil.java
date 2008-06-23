/**
 * 
 */
package org.nightlabs.jfire.trade.ui.store.search;

import org.nightlabs.jfire.store.search.ISaleAccessQuery;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class SaleAccessStateUtil {

	private SaleAccessStateUtil() {}

	public static void applySaleAccessState(SaleAccessState state, ISaleAccessQuery query) 
	{
		Boolean falseValue = null;
		if (state == null) {
			query.setPublished(null);
			query.setConfirmed(null);
			query.setSaleable(null);
			query.setClosed(null);
			return;
		}
		switch (state) {	
			case PUBLISHED:
				query.setPublished(true);
				query.setConfirmed(falseValue);
				query.setSaleable(falseValue);
				query.setClosed(falseValue);
				break;
			case CONFIRMED:
				query.setConfirmed(true);
				query.setPublished(falseValue);
				query.setSaleable(falseValue);
				query.setClosed(falseValue);
				break;
			case SALEABLE:
				query.setSaleable(true);				
				query.setPublished(falseValue);
				query.setConfirmed(falseValue);
				query.setClosed(falseValue);
				break;
			case CLOSED:
				query.setClosed(true);
				query.setPublished(falseValue);
				query.setConfirmed(falseValue);
				query.setSaleable(falseValue);				
				break;
		}
	}
}
