/**
 *
 */
package org.nightlabs.jfire.trade.ui.preference;

import org.nightlabs.base.ui.preference.CategoryPreferencePage;
import org.nightlabs.jfire.trade.ui.resource.Messages;

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
		return Messages.getString("org.nightlabs.jfire.trade.ui.preference.TradePreferencePage.page.text"); //$NON-NLS-1$
	}
}
