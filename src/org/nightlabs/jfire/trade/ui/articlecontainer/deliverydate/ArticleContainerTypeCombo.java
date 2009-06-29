package org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.Offer;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleContainerTypeCombo extends XComboComposite<Class<? extends ArticleContainer>>
{
	class LabelProvider extends org.eclipse.jface.viewers.LabelProvider {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
		 */
		@Override
		public String getText(Object element)
		{
			if (Offer.class.equals(element))
				return "Offer";
			else if (DeliveryNote.class.equals(element))
				return "Delivery Note";

			return "";
		}
	}

	/**
	 * @param parent
	 * @param comboStyle
	 */
	public ArticleContainerTypeCombo(Composite parent, int comboStyle) {
		super(parent, comboStyle, "Type");
		setLabelProvider(new LabelProvider());
		Collection<Class<? extends ArticleContainer>> articleContainerClasses = new ArrayList<Class<? extends ArticleContainer>>();
		articleContainerClasses.add(Offer.class);
		articleContainerClasses.add(DeliveryNote.class);
		setInput(articleContainerClasses);
	}

}
