/**
 *
 */
package org.nightlabs.jfire.trade.ui.preference;

import org.nightlabs.base.ui.preference.CategoryPreferencePage;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class TradePreferencePage
//extends PreferencePage
extends CategoryPreferencePage
{
	/**
	 *
	 */
	public TradePreferencePage() {
		super();
	}

	@Override
	protected String getText() {
		return "Trade Settings";
	}
}
