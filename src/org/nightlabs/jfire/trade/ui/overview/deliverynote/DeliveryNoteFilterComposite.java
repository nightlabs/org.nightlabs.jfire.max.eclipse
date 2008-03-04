package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DeliveryNoteFilterComposite
	extends AbstractArticleContainerFilterComposite<DeliveryNote, DeliveryNoteQuickSearchQuery>
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param queryProvider
	 */
	public DeliveryNoteFilterComposite(
		Composite parent,
		int style,
		LayoutMode layoutMode,
		LayoutDataMode layoutDataMode,
		QueryProvider<DeliveryNote, ? super DeliveryNoteQuickSearchQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public DeliveryNoteFilterComposite(Composite parent, int style,
		QueryProvider<DeliveryNote, ? super DeliveryNoteQuickSearchQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	@Override
	protected Class<DeliveryNoteQuickSearchQuery> getQueryClass() {
		return DeliveryNoteQuickSearchQuery.class;
	}
			
}
