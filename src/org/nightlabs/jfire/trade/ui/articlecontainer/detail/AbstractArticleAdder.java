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

import javax.jdo.JDOHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.MessageComposite;
import org.nightlabs.base.ui.message.MessageType;
import org.nightlabs.base.ui.notification.NotificationAdapterSWTThreadAsync;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.IArticleEditAction;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.util.NLLocale;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 * @author Fitas Amine <!-- fitas [AT] nightlabs [DOT] de -->
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
		
		composite = createRequirementsNotFulfilledComposite(parent);
		if (composite == null) {
			composite = _createComposite(parent);
		}
		
		JDOLifecycleManager.sharedInstance().addNotificationListener(ArticleContainer.class, articleContainerChangedListener);
		composite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				JDOLifecycleManager.sharedInstance().removeNotificationListener(ArticleContainer.class, articleContainerChangedListener);
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
	
	
	private NotificationAdapterSWTThreadAsync articleContainerChangedListener = new NotificationAdapterSWTThreadAsync() { 
		public void notify(NotificationEvent notificationEvent)
		{
			if(doesNonOrderArticleContainerMeetCriteria() != null)
			{
				if (composite != null)
				{
					Composite parent = composite.getParent();
					composite.dispose();
					createComposite(parent);
				}
			}
		}
	};	

	/**
	 * This method is invoked to check if the requirements
	 * (concerning the current {@link ProductType} or the current {@link ArticleContainer})
	 * are all fulfilled in order to show this {@link ArticleAdder}s Composite.
	 * <p>
	 * A return value of <code>null</code> indicates that all requirements are fulfilled.
	 * When a Composite is returned, the creation of the Composite of this {@link ArticleAdder}
	 * is aborted (i.e. {@link #_createComposite(Composite)} will not be called).
	 * </p>
	 * <p>
	 * By default this method checks if the {@link ProductType} is saleable and if the {@link ArticleContainer}
	 * is not finalized and returns a {@link MessageComposite} if it finds a requirement unfulfilled.
	 * </p>
	 * @return <code>null</code> if all requirements are fulfilled, a Composite which shows a appropriate message which requirements are not fulfilled otherwise.
	 */
	protected Composite createRequirementsNotFulfilledComposite(Composite parent)
	{
		if (!getSegmentEdit().getArticleContainer().getVendor().equals(getProductType().getVendor())) {
			String message = String.format(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder.message.differentVendor"), //$NON-NLS-1$
					getProductType().getName().getText(NLLocale.getDefault())
			);
			return new MessageComposite(parent, SWT.NONE, message, MessageType.WARNING);
		}

		if (!getProductType().isSaleable()) {
			String message = String.format(
					Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder.message.notSaleable"), //$NON-NLS-1$
					getProductType().getName().getText(NLLocale.getDefault()));
			return new MessageComposite(parent, SWT.NONE, message, MessageType.WARNING);
		}
		String errorMessage;
		if ((errorMessage = doesNonOrderArticleContainerMeetCriteria()) != null) {
			ArticleContainer ac = getSegmentEdit().getArticleContainer();
			ArticleContainerID acID = (ArticleContainerID) JDOHelper.getObjectId(ac);
			String message = String.format(
					errorMessage, 
//					TradePlugin.getArticleContainerTypeString(ac.getClass(), false), TradePlugin.getArticleContainerTypeString(ac.getClass(), true),
					TradePlugin.getArticleContainerTypeString(acID), TradePlugin.getArticleContainerTypeString(acID),
					ArticleContainerUtil.getArticleContainerID(ac)
					);
			return new MessageComposite(parent, SWT.NONE, message, MessageType.INFORMATION);
		}
		return null;
	}

	
	protected String doesNonOrderArticleContainerMeetCriteria() {
		if(isNonOrderArticleContainerFinalized())
			return Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder.message.articleContainerFinalized"); //$NON-NLS-1$
		if(isNonOrderArticleContainerAborted())
			return Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleAdder.articleContainerAborted"); //$NON-NLS-1$
		return null;
	}
	
	/**
	 * @return <code>true</code> if the current {@link ArticleContainer} is
	 *         something else than an {@link Order} and is finalized and <code>false</code>
	 *         otherwise.
	 */
	protected boolean isNonOrderArticleContainerFinalized() {
		ArticleContainer ac = getSegmentEdit().getArticleContainer();
		if (ac instanceof Offer) {
			return ((Offer) ac).isFinalized();
		} else if (ac instanceof Invoice) {
			return ((Invoice) ac).isFinalized();
		} else if (ac instanceof DeliveryNote) {
			return ((DeliveryNote) ac).isFinalized();
		} // TODO: Handle ReceptionNotes
		return false;
	}
	
	/** @return <code>true</code> if the current {@link Offer} is aborted
	 */
	protected boolean isNonOrderArticleContainerAborted() {
		ArticleContainer ac = getSegmentEdit().getArticleContainer();
		if (ac instanceof Offer) {
			return ((Offer) ac).isAborted();
		} 
		return false;
	}
}