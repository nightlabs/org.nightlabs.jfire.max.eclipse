package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DeliveryNoteFilterComposite
extends AbstractArticleContainerFilterComposite
{
	/**
	 * @param parent
	 * @param style
	 */
	public DeliveryNoteFilterComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class getQueryClass() {
		return DeliveryNote.class;
	}
			
}
