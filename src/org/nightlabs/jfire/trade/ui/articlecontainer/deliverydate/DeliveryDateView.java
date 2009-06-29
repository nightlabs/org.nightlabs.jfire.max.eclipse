package org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.login.part.LSDViewPart;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DeliveryDateView extends LSDViewPart
{
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
