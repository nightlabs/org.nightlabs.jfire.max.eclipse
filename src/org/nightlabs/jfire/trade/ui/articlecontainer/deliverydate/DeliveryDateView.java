package org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.Offer;

/**
 * {@link ViewPart} which offer the functionality to search for {@link Offer}s and/or {@link DeliveryNote}s
 * which contain {@link Article}s with a certain kind of delivery date.
 *
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DeliveryDateView extends LSDViewPart
{
	public static final String ID_VIEW = DeliveryDateView.class.getName();

	private DeliveryDateComposite deliveryDateComposite;

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.part.ControllablePart#createPartContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartContents(Composite parent)
	{
		deliveryDateComposite = new DeliveryDateComposite(parent, SWT.NONE);
	}

}
