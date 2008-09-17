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
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleProductTypeClassGroup;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface ArticleEdit
{
	/**
	 * This method must be called by your implementation of
	 * {@link ArticleEditFactory#createArticleEdits(SegmentEdit, ArticleProductTypeClassGroup, Collection)}.
	 * @param articleEditFactory The factory that has created this <code>ArticleEdit</code>.
	 * @param segmentEdit The <tt>SegmentEdit</tt> into which this <tt>ArticleEdit</tt>
	 *		has been created.
	 * @param articleProductTypeClassGroup TODO
	 * @param articleCarriers A subset of the {@link org.nightlabs.jfire.trade.ui.Article}s
	 *		defined in <tt>articleProductTypeGroup</tt>. This instance of <tt>ArticleEdit</tt>
	 *		must only display the <tt>Article</tt>s in this <tt>Collection</tt>.
	 */
	void init(ArticleEditFactory articleEditFactory, SegmentEdit segmentEdit, ArticleProductTypeClassGroup articleProductTypeClassGroup, Set<ArticleCarrier> articleCarriers);

	/**
	 * @return Returns the factory that has created this <code>ArticleEdit</code> and was passed to
	 *		{@link #init(ArticleEditFactory, SegmentEdit, ArticleProductTypeClassGroup, Set)}
	 */
	ArticleEditFactory getArticleEditFactory();

	/**
	 * @return the instance of <tt>SegmentEdit</tt> that has previously been
	 * passed to {@link #init(List, ArticleContainerEdit, Class, Set)}.
	 */
	SegmentEdit getSegmentEdit();

	/**
	 * @return the instance of <tt>ArticleProductTypeClassGroup</tt> that has previously been
	 * passed to {@link #init(List, ArticleContainerEdit, Class, Set)}.
	 */
	ArticleProductTypeClassGroup getArticleProductTypeClassGroup();

	/**
	 * @return Returns all {@link org.nightlabs.jfire.trade.ui.Article}s managed by this <code>ArticleEdit</code>.
	 *		The result of this method is READ-ONLY!
	 */
	Set<? extends Article> getArticles();

	/**
	 * @return Returns all {@link ArticleCarrier}s that wrap the {@link Article}s managed by this <code>ArticleEdit</code>.
	 *		The result of this method is READ-ONLY!
	 */
	Set<? extends ArticleCarrier> getArticleCarriers();

//	/**
//	 * @return the <tt>List articles</tt> which has previously been
//	 * passed to {@link #init(IWorkbenchPartSite, SegmentEdit, ArticleProductTypeGroup, List)}.
//	 */
//	List getArticles();

	/**
	 * In your implementation of this method, you should create a GUI element that
	 * makes the previously defined articleProductTypeGroup editable. Whenever you
	 * add or remove a child control (e.g. add a new line for a new Article), you
	 * must call the method {@link #fireControlContentChangeEvent()}.
	 * <p>
	 * Note, that you must call the method {@link #onDispose()} when the <tt>Composite</tt>
	 * which you created here gets disposed!
	 * <p>
	 * <b>It is recommended to extend {@link AbstractArticleEdit} and implement
	 * {@link AbstractArticleEdit#_createComposite(Composite)} instead of overriding/extending
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
	 * This method is called by {@link SegmentEdit#getArticleSelections()}.
	 *
	 * @return Returns a collection (which might be empty) with
	 *		all selected {@link org.nightlabs.jfire.trade.ui.Article}s.
	 *		Must <b>not</b> return <code>null</code>.
	 */
	Set<? extends Article> getSelectedArticles();

	/**
	 * Do <b>not</b> call this method! Probably, you want to call
	 * {@link SegmentEdit#setSelectedArticles(Collection)} instead.
	 * <p>
	 * This method is called by {@link SegmentEdit#setSelectedArticles(Collection)}.
	 * In your implementation of <code>ArticleEdit</code>, you must deselect all
	 * {@link org.nightlabs.jfire.trade.ui.Article}s that are not contained in the given
	 * collection and select all that are contained. If you don't know an <code>Article</code>,
	 * you ignore it and put it into the result.
	 * </p>
	 *
	 * @param articles The {@link org.nightlabs.jfire.trade.ui.Article}s that shall be selected. Never <code>null</code>.
	 * @return Returns all {@link org.nightlabs.jfire.trade.ui.Article}s that are unknown
	 *		to this {@link ArticleEdit}. Never return <code>null</code>.
	 */
	Set<? extends Article> setSelectedArticles(Set<? extends Article> articles);

	/**
	 * When new {@link org.nightlabs.jfire.trade.ui.Article}s  have been created, the method
	 * {@link SegmentEdit#addArticles(Collection)}
	 * iterates all <tt>ArticleEdit</tt>s and tries to add them. Every
	 * <tt>ArticleEdit</tt> returns the <tt>Article</tt>s, which it did <b>not</b> add.
	 * Hence, the iteration is complete, once the returned <tt>Collection</tt> is empty.
	 * If, at the end, it still contains items, the <tt>ArticleEditFactory</tt> will be
	 * asked to create a new <tt>ArticleEdit</tt>.
	 * <p>
	 * <b>Important API change!!!</b>
	 * </p>
	 * 
	 * <p>
	 * <u>Old behaviour:</u><br/>
	 * If your implementation of this method decides to accept an <tt>Article</tt>, it must
	 * add it to the {@link ArticleProductTypeClassGroup} (passed by
	 * {@link #init(ArticleEditFactory, SegmentEdit, ArticleProductTypeClassGroup, Set)}). Note,
	 * that {@link AbstractArticleEdit} provides the method {@link AbstractArticleEdit#_removeArticles(Set)},
	 * which you should call, if you extend this abstract class (recommended).
	 * </p>
	 * @param articleCarriers The {@link org.nightlabs.jfire.trade.ui.Article}s which need to be added.
	 *		It is OK to manipulate this collection (e.g. removing all processed articles).
	 * @return Returns those instances of {@link org.nightlabs.jfire.trade.ui.Article} that have
	 *		<b>not</b> been added. You can return either <code>null</code> or an empty collection,
	 *		if you've added all articles.
	 */
	Set<? extends ArticleCarrier> addArticles(Set<? extends ArticleCarrier> articleCarriers);

//	/**
//	 * This method is called by {@link SegmentEdit#canRemoveArticles(Collection)} with all
//	 * {@link org.nightlabs.jfire.trade.ui.Article}s that shall be removed. Therefore,
//	 * some of the articles might not be known to this <code>ArticleEdit</code> and
//	 * must be ignored.
//	 *
//	 * @param articles All instances of {@link org.nightlabs.jfire.trade.ui.Article}
//	 *		that have to be checked.
//	 * @return Returns <code>true</code> if all articles known to this <code>SegmentEdit</code>
//	 *		can be removed, <code>false</code> if at least one cannot be removed. <code>Article</code>s
//	 *		not known to this <code>SegmentEdit</code> do not affect the result.
//	 */
//	boolean canRemoveArticles(Collection articles);
//
//	/**
//	 * This method is called by {@link SegmentEdit#removeArticles(Collection)}. It must remove
//	 * all <code>Article</code>s known to this <code>ArticleEdit</code>. Those, not known to it
//	 * must be returned. They will be passed to the next <code>ArticleEdit</code>.
//	 * <p>
//	 * You <b>must</b> remove the articles from the {@link ArticleProductTypeClassGroup} (which was
//	 * passed to you by {@link #init(SegmentEdit, ArticleProductTypeClassGroup, Set)}.
//	 * </p>
//	 * @param articles Instances of {@link org.nightlabs.jfire.trade.ui.Article} that shall be removed.
//	 * @return Returns all those articles, that are not known to this {@link ArticleEdit} and therefore
//	 *		could not be removed.
//	 *
//	 * @throws IllegalStateException Thrown if one of the <code>articles</code> cannot be removed (and should
//	 *		therefore never be passed, because {@link #canRemoveArticles(Collection)} is called before).
//	 */
//	Collection removeArticles(Collection articles);
//	/**
//	 * This method is called in order to remove the given articles. If an article is not allowed to be removed
//	 * or not known, this method should throw an {@link IllegalArgumentException}. It should already be
//	 * checked before by the implementation of {@link RemoveActionDelegate}, whether removing is possible.
//	 */
//	void removeArticles(Set<? extends Article> articles);

	/**
	 * This method is called by the default implementation of {@link SegmentEdit#removeArticles(Collection)}
	 * (in {@link AbstractSegmentEdit#removeArticles(Collection)}) in order to update the UI after
	 * {@link Article}s have been removed.
	 *
	 * @param articleCarriers The {@link ArticleCarrier}s referencing those {@link Article}s which have been removed.
	 * @return all those {@link ArticleCarrier}s which where unknown to this instance of {@link ArticleEdit}. <code>null</code>
	 *		or an empty <code>Collection</code>, if all given <code>articleCarriers</code> where known and processed by
	 *		this <code>ArticleEdit</code>.
	 */
	Collection<ArticleCarrier> removeArticles(Collection<ArticleCarrier> articleCarriers);

	/**
	 * You must call this method when the <tt>Composite</tt> which you created in
	 * {@link #createComposite(Composite)} is disposed. You should do this by
	 * adding a {@link org.eclipse.swt.events.DisposeListener}:
	 * <code><pre>
	 *  addDisposeListener(new DisposeListener() {
	 *    public void widgetDisposed(DisposeEvent e)
	 *    {
	 *      articleEdit.dispose();
	 *      removeDisposeListener(this);
	 *    }
	 *  });
	 * </pre></code>
	 * <p>
	 * <b>It is recommended to extend {@link AbstractArticleEdit} and implement
	 * {@link AbstractArticleEdit#_createComposite(Composite)} instead of overriding/extending
	 * {@link #createComposite(Composite)}!</b> You do not need to take care about dispose then!
	 */
	void onDispose();

	/**
	 * This method is called by the framework in order to remove this <tt>ArticleEdit</tt> and
	 * the <tt>Composite</tt> it has created. If you extend {@link AbstractArticleEdit}
	 * correctly, you don't need to care about this.
	 */
	void dispose();

	/**
	 * This method adds a listener that will be notified (on the GUI thread) whenever the selection
	 * of {@link org.nightlabs.jfire.trade.ui.Article}s changes.
	 *
	 * @param listener The listener to add.
	 */
	void addArticleEditArticleSelectionListener(ArticleEditArticleSelectionListener listener);

	/**
	 * @param listener The listener to remove.
	 * @see #addArticleEditArticleSelectionListener(ArticleEditArticleSelectionListener)
	 */
	void removeArticleEditArticleSelectionListener(ArticleEditArticleSelectionListener listener);

	void fireArticleEditArticleSelectionEvent();

//	/**
//	 * This method is called by {@link SegmentEdit#populateArticleEditContextMenu(IMenuManager)}.
//	 * You can populate the manager with specialized {@link org.eclipse.jface.action.Action}s.
//	 * The default {@link org.eclipse.jface.action.Action}s (e.g. remove) should be added by the
//	 * {@link SegmentEdit}.
//	 *
//	 * @param manager
//	 */
//	void populateArticleEditContextMenu(IMenuManager manager);
	
	void changeTariffForSelectedArticles();
}
