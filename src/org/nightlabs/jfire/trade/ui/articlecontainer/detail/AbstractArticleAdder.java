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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.MessageComposite;
import org.nightlabs.base.ui.composite.MessageComposite.MessageType;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditAction;
import org.nightlabs.util.NLLocale;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class AbstractArticleAdder implements ArticleAdder
{
	private SegmentEdit segmentEdit;

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder#init(IArticleEditAction, String)
	 */
	public void init(SegmentEdit segmentEdit)
	{
		this.segmentEdit = segmentEdit;
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder#getSegmentEdit()
	 */
	public SegmentEdit getSegmentEdit()
	{
		return segmentEdit;
	}

	/**
	 * {@link #createComposite(Composite)} initializes this field and
	 * {@link #dispose()} the <tt>Composite</tt> if it is existing.
	 */
	private Composite composite = null;

	/**
	 * Important: Do NOT overwrite/extend this method, but implement {@link #_createComposite(Composite)} instead!
	 *
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleAdder#createComposite(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createComposite(Composite parent)
	{
		if (composite != null)
			throw new IllegalStateException("createComposite(...) has already been called! Have already a composite!"); //$NON-NLS-1$

		boolean requirementsFulFilled = checkRequirements();
		if (!requirementsFulFilled) {
			composite = createRequirementsNotFulFilledComposite(parent);
		}
		if (composite == null) {
			composite = _createComposite(parent);
		}

		composite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				((Composite)e.getSource()).removeDisposeListener(this);
				onDispose();
			}
		});

		return composite;
	}

	public Composite getComposite()
	{
		return composite;
	}

	/**
	 * This method is called by {@link #createComposite(Composite)}. Implement it and return a new instance
	 * of <tt>Composite</tt>.
	 *
	 * @param parent The parent <tt>Composite</tt> for the new <tt>Composite</tt>.
	 * @return The newly created <tt>Composite</tt>.
	 */
	protected abstract Composite _createComposite(Composite parent);

	public void onDispose()
	{
		composite = null;
	}

	public void dispose()
	{
		if (composite != null)
			composite.dispose();
	}

	/**
	 * If the method {@link #checkRequirements()} returns false 
	 * this method should return a corresponding Composite should which shows a appropriate
	 * message 
	 * 
	 * @return a Composite which shows a appropriate message which requirements are not fulfilled.
	 */
	protected Composite createRequirementsNotFulFilledComposite(Composite parent) 
	{
		if (!getProductType().isSaleable()) {
			String message = String.format("The ProductType %s is not saleable!", getProductType().getName().getText(NLLocale.getDefault())); 
			return new MessageComposite(parent, SWT.NONE, message, MessageType.WARNING);
		}
		return null;
	}

	/**
	 * Subclasses can override this method to check if all prerequisites are fulfilled 
	 * for selling the current productType.
	 * By default this method checks if the ProductType is saleable.  
	 * 
	 * @return true if all requirements are fulfilled or false if not
	 */	
	protected boolean checkRequirements() {
		return getProductType().isSaleable();
	}
	
}
