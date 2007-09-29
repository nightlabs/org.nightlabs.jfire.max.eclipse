/**
 * 
 */
package org.nightlabs.jfire.trade.ui.accounting;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.nightlabs.jfire.accounting.Currency;

/**
 * Base {@link Currency} label provider, which returns null as the image and 
 * <!-- <pre> getCurrencyID() + " (" + getCurrencySymbol() +") " </pre> -->
 * the {@link Currency#getCurrencySymbol()}.
 * 
 * 
 * @author Marius Heinzmann [marius<at>NightLabs<dot>de]
 */
public class CurrencyLabelProvider extends LabelProvider implements ILabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		if (! (element instanceof Currency))
			throw new IllegalArgumentException("The CurrencyLabelProvider seems to be used in another " + //$NON-NLS-1$
					"	context, since the given object is no Currency! object="+element); //$NON-NLS-1$
		Currency currency = (Currency) element;
//		return currency.getCurrencyID() + " (" + currency.getCurrencySymbol() +") ";
		return currency.getCurrencySymbol(); // why should we list the currencyID?! There's no need!
	}

}
