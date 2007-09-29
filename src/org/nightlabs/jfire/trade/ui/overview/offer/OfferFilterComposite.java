package org.nightlabs.jfire.trade.ui.overview.offer;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class OfferFilterComposite 
extends AbstractArticleContainerFilterComposite 
{
	/**
	 * @param parent
	 * @param style
	 */
	public OfferFilterComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class getQueryClass() {
		return Offer.class;
	}
	
}
