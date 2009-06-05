package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DeliveryNoteFilterComposite
	extends AbstractArticleContainerFilterComposite<DeliveryNoteQuery>
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
		QueryProvider<? super DeliveryNoteQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public DeliveryNoteFilterComposite(Composite parent, int style,
		QueryProvider<? super DeliveryNoteQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	@Override
	public Class<DeliveryNoteQuery> getQueryClass() {
		return DeliveryNoteQuery.class;
	}

	@Override
	public void resetUI() {
		// TODO Auto-generated method stub
		
	}
			
}
