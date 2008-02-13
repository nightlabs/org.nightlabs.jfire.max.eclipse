package org.nightlabs.jfire.trade.ui.overview.receptionnote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ReceptionNoteFilterComposite
extends AbstractArticleContainerFilterComposite
{
	/**
	 * @param parent
	 * @param style
	 */
	public ReceptionNoteFilterComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class getQueryClass() {
		return ReceptionNote.class;
	}
	
}
