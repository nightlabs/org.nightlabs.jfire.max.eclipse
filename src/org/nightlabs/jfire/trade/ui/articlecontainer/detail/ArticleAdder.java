/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://opensource.org/licenses/lgpl-license.php                         *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.progress.ProgressMonitor;


/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface ArticleAdder
{
	void init(SegmentEdit segmentEdit);

	/**
	 * @param productTypeID The currently selected productType for which to expose the add-it-feature
	 * @param monitor TODO
	 */
	void setProductTypeID(ProductTypeID productTypeID, ProgressMonitor monitor);

	ProductType getProductType();

	/**
	 * @return the instance of <tt>SegmentEdit</tt> that has previously been
	 * passed to {@link #init(SegmentEdit)}
	 */
	SegmentEdit getSegmentEdit();

	/**
	 * Create the <tt>Composite</tt> which serves the functionality to add one or
	 * more {@link org.nightlabs.jfire.trade.ui.Article}s to the
	 * order/offer/invoice/delivery.
	 * <p>
	 * Note, that you must call the method {@link #onDispose()} when the <tt>Composite</tt>
	 * which you created here gets disposed!
	 * <p>
	 * <b>It is recommended to extend {@link AbstractArticleAdder} and implement
	 * {@link AbstractArticleAdder#_createComposite(Composite)} instead of overriding/extending
	 * {@link #createComposite(Composite)}!</b> You do not need to take care about dispose then!
	 *
	 * @param parent The parent composite into all GUI elements should be created.
	 * @return The newly created <tt>Composite</tt>.
	 */
	Composite createComposite(Composite parent);

	/**
	 * @return the <tt>Composite</tt> which has been created by {@link #createComposite(Composite)}.
	 */
	Composite getComposite();

	/**
	 * You must call this method when the <tt>Composite</tt> which you created in
	 * {@link #createComposite(Composite)} is disposed. You should do this by
	 * adding a {@link org.eclipse.swt.events.DisposeListener}:
	 * <code><pre>
	 *  addDisposeListener(new DisposeListener() {
	 *    public void widgetDisposed(DisposeEvent e)
	 *    {
	 *      removeDisposeListener(this);
	 *      articleAdder.dispose();
	 *    }
	 *  });
	 * </pre></code>
	 * <p>
	 * <b>It is recommended to extend {@link AbstractArticleAdder} and implement
	 * {@link AbstractArticleAdder#_createComposite(Composite)} instead of overriding/extending
	 * {@link #createComposite(Composite)}!</b> You do not need to take care about dispose then!
	 */
	void onDispose();

	/**
	 * This method is called by the framework in order to remove this <tt>ArticleAdder</tt> and
	 * the <tt>Composite</tt> it has created. If you extend {@link AbstractArticleAdder}
	 * correctly, you don't need to care about this.
	 */
	void dispose();
}
