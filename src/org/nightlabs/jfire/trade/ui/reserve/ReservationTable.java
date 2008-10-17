/**
 *
 */
package org.nightlabs.jfire.trade.ui.reserve;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.ui.overview.offer.OfferListComposite;

/**
 * @author daniel
 *
 */
public class ReservationTable extends OfferListComposite
{
	public static final String[] FETCH_GROUP_RESERVATIONS = OfferListComposite.FETCH_GROUPS_OFFER;

	/**
	 * @param parent
	 * @param style
	 */
	public ReservationTable(Composite parent, int style) {
		super(parent, style);
	}
}
