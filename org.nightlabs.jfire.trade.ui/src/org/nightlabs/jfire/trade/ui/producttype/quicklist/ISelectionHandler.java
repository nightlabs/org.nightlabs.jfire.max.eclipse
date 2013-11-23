/**
 * 
 */
package org.nightlabs.jfire.trade.ui.producttype.quicklist;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * Interfaces which extends the {@link ISelectionProvider} with one more
 * method which determines if the selectionProvider can handle a certain selection
 *  
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public interface ISelectionHandler
extends ISelectionProvider
{
	boolean canHandleSelection(ISelection selection);
}
