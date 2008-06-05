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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.nightlabs.annotation.Implement;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleProductTypeClassGroup;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public abstract class AbstractArticleEdit implements ArticleEdit
{
	private ArticleEditFactory articleEditFactory;
	private SegmentEdit segmentEdit;
	private ArticleProductTypeClassGroup articleProductTypeClassGroup;

	/**
	 * Because this ArticleEdit might only manage a subset of the <code>Article</code>s managed
	 * by the {@link #articleProductTypeClassGroup}, we need to store them again. This holds instances
	 * of {@link ArticleCarrier}.
	 */
	private Set<ArticleCarrier> articleCarriers;

	public void init(ArticleEditFactory articleEditFactory,
			SegmentEdit segmentEdit, ArticleProductTypeClassGroup articleProductTypeClassGroup, Set<ArticleCarrier> articleCarriers)
	{
		this.articleEditFactory = articleEditFactory;
		this.segmentEdit = segmentEdit;
		this.articleCarriers = articleCarriers;
		this.articleProductTypeClassGroup = articleProductTypeClassGroup;
		for (ArticleCarrier articleCarrier : articleCarriers) {
			if (this.articleProductTypeClassGroup != articleCarrier.getArticleProductTypeClassGroup()) {
				StringBuffer sb = new StringBuffer("All ArticleCarriers need to belong to the passed ArticleProductTypeClassGroup!"); //$NON-NLS-1$
				for (ArticleCarrier ac : articleCarriers) {
					sb.append("\n    articlePK=\""); //$NON-NLS-1$
					sb.append(ac.getArticle().getPrimaryKey());
					sb.append("\" article.productTypePK=\""); //$NON-NLS-1$
					sb.append(ac.getArticle().getProductType().getPrimaryKey());
					sb.append("\" articleCarrier.articleProductTypeClassGroup=\""); //$NON-NLS-1$
					sb.append(ac.getArticleProductTypeClassGroup());
					sb.append("\" articleSegmentGroup=\""); //$NON-NLS-1$
					sb.append(ac.getArticleProductTypeClassGroup().getArticleSegmentGroup());
					sb.append("\" articleSegmentGroups=\""); //$NON-NLS-1$
					sb.append(ac.getArticleProductTypeClassGroup().getArticleSegmentGroup().getArticleSegmentGroups());
					sb.append("\""); //$NON-NLS-1$
				}
				throw new IllegalArgumentException(sb.toString());
			}
		}
	}

	public ArticleEditFactory getArticleEditFactory()
	{
		return articleEditFactory;
	}

	public SegmentEdit getSegmentEdit()
	{
		return segmentEdit;
	}

	public ArticleProductTypeClassGroup getArticleProductTypeClassGroup()
	{
		return articleProductTypeClassGroup;
	}

	/**
	 * When extending this class and implementing {@link ArticleEdit#addArticles(Set)},
	 * you <b>must</b> call this method with all {@link ArticleCarrier}s you want to accept
	 * and refresh your GUI afterwards.
	 *
	 * @param articles The articles to be added.
	 * @return Returns instances of {@link ArticleCarrier} wrapping the given <code>articles</code>.
	 */
	protected void _addArticleCarriers(Collection<? extends ArticleCarrier> articleCarriers)
	{
//		Collection<ArticleCarrier> res = getArticleProductTypeClassGroup().getArticleCarriers(articles);
//		this.articleCarriers.addAll(res);
//		return res;
		this.articleCarriers.addAll(articleCarriers);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit#removeArticles(java.util.Collection)
	 */
	@Implement
	public Collection<ArticleCarrier> removeArticles(Collection<ArticleCarrier> articleCarriers) {
		for (Iterator<ArticleCarrier> it = articleCarriers.iterator(); it.hasNext(); ) {
			if (this.articleCarriers.remove(it.next()))
				it.remove();
		}
		
		return articleCarriers;
	}
	
//	/**
//	 * Note, that you must call {@link #_removeArticles(Set)} in your implementation
//	 * of this method!
//	 * <br/><br/>
//	 *
//	 * {@inheritDoc}
//	 */
//	public abstract void removeArticles(Set<? extends Article> articles);
//
//	/**
//	 * When extending this class and implementing {@link ArticleEdit#removeArticles(Set)},
//	 * you <b>must</b> call this method and refresh your GUI afterwards.
//	 *
//	 * @param articles
//	 */
//	protected void _removeArticles(Set<? extends Article> articles)
//	{
////		getArticleProductTypeClassGroup().removeArticles(articles);
//		for (Iterator it = articleCarriers.iterator(); it.hasNext();) {
//			ArticleCarrier articleCarrier = (ArticleCarrier) it.next();
//			if (articles.contains(articleCarrier.getArticle()))
//				it.remove();
//		}
//	}

////	public void removeArticles(Collection articles)
////	{
////		// TODO
////		articles = null;
////	}
//
//	/**
//	 * Cache for {@link Article}s. Is created and populated by {@link #getArticles()}.
//	 */
//	private Set articles = null;

	public Set<? extends Article> getArticles()
	{
//		if (articles == null) {
			Set<Article> s = new HashSet<Article>(articleCarriers.size());
			for (ArticleCarrier articleCarrier : articleCarriers) {
				s.add(articleCarrier.getArticle());
			}
			return Collections.unmodifiableSet(s);
//			articles = Collections.unmodifiableSet(s);
//		}
//
//		return articles;
	}

	public Set<? extends ArticleCarrier> getArticleCarriers()
	{
		return Collections.unmodifiableSet(articleCarriers);
	}

	/**
	 * {@link #createComposite(Composite)} initializes this field and
	 * {@link #dispose()} the <tt>Composite</tt> if it is existing.
	 */
	private Composite composite = null;
	
//	/**
//	 * The parent of the composite
//	 */
//	private Composite parent = null;

	private boolean ctrlKeyDown = false;

	/**
	 * Find out whether the "Ctrl" key is currently pressed down.
	 * <p>
	 * It's urgently recommended to take the Ctrl key into account when selecting articles in an <code>ArticleEdit</code>'s composite. If this key is not pressed
	 * down, all other currently selected articles (in all other <code>ArticleEdit</code> s should be deselected.
	 * </p>
	 *
	 * @return <code>true</code>, if the Ctrl key is currently down.
	 */
	public boolean isCtrlKeyDown()
	{
		return ctrlKeyDown;
	}

	private Listener ctrlKeyDownListener = new Listener() {
		public void handleEvent(Event event)
		{
			if (event.keyCode == SWT.CTRL)
				ctrlKeyDown = true;
		}
	};
	private Listener ctrlKeyUpListener = new Listener() {
		public void handleEvent(Event event)
		{
			if (event.keyCode == SWT.CTRL)
				ctrlKeyDown = false;
		}
	};

	/**
	 * Important: Do NOT overwrite/extend this method, but implement {@link #_createComposite(Composite)} instead!
	 *
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleEdit#createComposite(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createComposite(Composite parent)
	{
//		this.parent = parent;
		if (composite != null)
			throw new IllegalStateException("createComposite(...) has already been called! Have already a composite!"); //$NON-NLS-1$

		composite = _createComposite(parent);

		Display.getDefault().addFilter(SWT.KeyDown, ctrlKeyDownListener);
		Display.getDefault().addFilter(SWT.KeyUp, ctrlKeyUpListener);

		composite.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				Display.getDefault().removeFilter(SWT.KeyDown, ctrlKeyDownListener);
				Display.getDefault().removeFilter(SWT.KeyUp, ctrlKeyUpListener);

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

	private ListenerList articleEditArticleSelectionListeners = new ListenerList();

	public void addArticleEditArticleSelectionListener(ArticleEditArticleSelectionListener listener)
	{
		articleEditArticleSelectionListeners.add(listener);
	}

	public void removeArticleEditArticleSelectionListener(ArticleEditArticleSelectionListener listener)
	{
		articleEditArticleSelectionListeners.remove(listener);
	}

	/**
	 * Your implementation must call this method on the SWT GUI thread, whenever the selection of
	 * {@link org.nightlabs.jfire.trade.ui.Article}s
	 * changed.
	 */
	public void fireArticleEditArticleSelectionEvent()
	{
		if (Display.getCurrent().getThread() != Thread.currentThread())
			throw new IllegalStateException("You must call this method on the GUI thread!"); //$NON-NLS-1$

		ArticleEditArticleSelectionEvent event = null;

		Object[] listeners = articleEditArticleSelectionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			ArticleEditArticleSelectionListener listener = (ArticleEditArticleSelectionListener) listeners[i];

			if (event == null)
				event = new ArticleEditArticleSelectionEvent(this);

			listener.selected(event);
		}
	}

//	public boolean isInOrder()
//	{
//		return Order.class.getName().equals(getSegmentEdit().getArticleContainerClass());
//	}
//	public boolean isInOffer()
//	{
//		return Offer.class.equals(getSegmentEdit().getArticleContainerClass());
//	}
//	public boolean isInInvoice()
//	{
//		return Invoice.class.equals(getSegmentEdit().getArticleContainerClass());
//	}
//	public boolean isInDeliveryNote()
//	{
//		return DeliveryNote.class.equals(getSegmentEdit().getArticleContainerClass());
//	}
//	public boolean isInReceptionNote()
//	{
//		return ReceptionNote.class.equals(getSegmentEdit().getArticleContainerClass());
//	}
}
