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
import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleSegmentGroup;
import org.nightlabs.jfire.trade.id.ArticleContainerID;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public interface SegmentEdit
{
	/**
	 * This method is called by
	 * {@link SegmentEditFactory#createSegmentEdit(GeneralEditorComposite, String, ArticleSegmentGroup)}.
	 * @param segmentEditFactory The factory which has created this <code>SegmentEdit</code>.
	 * @param generalEditorComposite TODO
	 * @param articleContainerClass The articleContainerClass - one of
	 *		{@link SegmentEditFactory#SEGMENTCONTEXT_ORDER},
	 *		{@link SegmentEditFactory#SEGMENTCONTEXT_OFFER},
	 *		{@link SegmentEditFactory#SEGMENTCONTEXT_INVOICE} or
	 *		{@link SegmentEditFactory#SEGMENTCONTEXT_DELIVERY_NOTE}.
	 * @param articleSegmentGroup The group of articles which should be exposed to
	 *		the user for edit.
	 */
	void init(
			SegmentEditFactory segmentEditFactory,
			GeneralEditorComposite generalEditorComposite,
			String articleContainerClass,
			ArticleSegmentGroup articleSegmentGroup);


	/**
	 * @return Returns the factory which has created this <code>SegmentEdit</code> and
	 *		was passed to
	 *		{@link #init(SegmentEditFactory, GeneralEditorComposite, String, ArticleSegmentGroup)}.
	 */
	SegmentEditFactory getSegmentEditFactory();

	GeneralEditorComposite getGeneralEditorComposite();

	/**
	 * This method is a convenience method and shortcuts
	 * <code>getGeneralEditorComposite().getArticleContainer()</code>.
	 *
	 * @return Returns the ArticleContainer
	 *		(either {@link org.nightlabs.jfire.trade.ui.Order}, {@link org.nightlabs.jfire.trade.ui.Offer},
	 *		{@link org.nightlabs.jfire.accounting.Invoice} or {@link org.nightlabs.jfire.store.DeliveryNote}).
	 */
	ArticleContainer getArticleContainer();

	/**
	 * This method is a convenience method and shortcuts
	 * <code>getGeneralEditorComposite().getArticleContainerID()</code>.
	 *
	 * @return Returns the ArticleContainerID
	 *		(either {@link org.nightlabs.jfire.trade.ui.id.OrderID}, {@link org.nightlabs.jfire.trade.ui.id.OfferID},
	 *		{@link org.nightlabs.jfire.accounting.id.InvoiceID} or {@link org.nightlabs.jfire.store.id.DeliveryNoteID}).
	 */
	ArticleContainerID getArticleContainerID();

	/**
	 * @return the <tt>ArticleSegmentGroup</tt> for which this <tt>SegmentEdit</tt> was created.
	 *
	 * @see #setArticleSegmentGroup(ArticleSegmentGroup)
	 */
	ArticleSegmentGroup getArticleSegmentGroup();

	/**
	 * This is a convenience method to avoid casting and shorten method calls. It
	 * does the following:
	 * <code>
	 * return (ClientArticleSegmentGroups)getArticleSegmentGroup().getArticleSegmentGroups();
	 * </code>
	 *
	 * @return Returns the {@link ClientArticleSegmentGroups} instance which is the main container
	 *		holding all {@link Article}s in a grouped tree.
	 */
	ClientArticleSegmentGroups getClientArticleSegmentGroups();

	/**
	 * @return Returns all {@link ArticleEdit}s of this <code>SegmentEdit</code>.
	 */
	List<ArticleEdit> getArticleEdits();

	/**
	 * In your implementation of this method, you should create a GUI element that
	 * makes the previously defined segment editable. Whenever you add or remove
	 * a child control (e.g. add a new line for a new Article), you must call
	 * the method {@link #fireCompositeContentChangeEvent()}.
	 * <p>
	 * Note, that you must call {@link #onDispose()} when this composite is disposed.
	 * <p>
	 * <b>It is recommended to extend {@link AbstractSegmentEdit} and implement
	 * {@link AbstractSegmentEdit#_createComposite(Composite)} instead of overriding/extending
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

	void addCompositeContentChangeListener(CompositeContentChangeListener listener);

	void removeCompositeContentChangeListener(CompositeContentChangeListener listener);

	/**
	 * This method must be called whenever the <tt>Control</tt> returned by
	 * {@link #createComposite(Composite)} has added or removed a child control.
	 * This will cause the main container to layout all its children (and allocate
	 * the necessary space within the scrolling area).
	 */
	void fireCompositeContentChangeEvent();

	String getArticleContainerClass();

	/**
	 * This method iterates all {@link ArticleEdit}s, calls {@link ArticleEdit#getSelectedArticles()}
	 * and forms a <code>Set</code> of {@link ArticleSelection}s.
	 * <p>
	 * Note, that only {@link ArticleSelection}s with at least one selected {@link org.nightlabs.jfire.trade.ui.Article}
	 * are returned.
	 * </p>
	 *
	 * @return Returns a <code>Set</code> of {@link ArticleSelection}s.
	 *		Never returns <code>null</code>.
	 */
	Set<ArticleSelection> getArticleSelections();
	
	/**
	 * This method selects the given <code>articles</code> (and deselects all others).
	 * Therefore, it iterates all
	 * {@link ArticleEdit}s and calls {@link ArticleEdit#setSelectedArticles(Set)}.
	 *
	 * @param articles The {@link org.nightlabs.jfire.trade.ui.Article}s that shall be selected. Never <code>null</code>.
	 */
	void setSelectedArticles(Collection<? extends Article> articles);

	/**
	 * <b>Important API change!!!</b>
	 * <p>
	 * The {@link ArticleAdder} does not need to add articles locally anymore. It only needs
	 * to add them on the server side. The client is automatically notified about this via
	 * a {@link JDOLifecycleListener} which is registered by the {@link ClientArticleSegmentGroups}.
	 * </p>
	 * <p>
	 * Hence, this method is now called by the {@link GeneralEditorComposite} (which is notified
	 * by the {@link ClientArticleSegmentGroups}) and it needs to update its UI (i.e. its {@link Composite}).
	 * </p>
	 * <p>
	 * If you do not override the default implementation in {@link AbstractSegmentEdit}, this method
	 * will iterate all {@link ArticleEdit}s and call {@link ArticleEdit#addArticles(Set)}. If necessary,
	 * new {@link ArticleEdit}s will be created.
	 * </p>
	 *
	 * <p>
	 * <u>Old behaviour:</u><br/>
	 * This method must be called by the {@link ArticleAdder} (or its composite) after
	 * it has added {@link org.nightlabs.jfire.trade.ui.Article}s to the
	 * {@link org.nightlabs.jfire.trade.ui.Offer}/{@link org.nightlabs.jfire.trade.ui.Order}.
	 * The <code>SegmentEdit</code> will then first add the articles to the {@link ArticleContainer}
	 * and then iterate all its {@link ArticleEdit}s and call {@link ArticleEdit#addArticles(Set)}.
	 * </p>
	 * @param articleCarriers All the newly added {@link org.nightlabs.jfire.trade.ui.Article}s wrapped in {@link ArticleCarrier}s.
	 */
	void addArticles(Collection<ArticleCarrier> articleCarriers);

	/**
	 * <p>
	 * This method is called by the {@link GeneralEditorComposite} (which is notified
	 * by the {@link ClientArticleSegmentGroups}), whenever articles are removed from an {@link ArticleContainer}.
	 * Note, that the {@link ArticleCarrier}s passed to this method have already been removed from
	 * the {@link ClientArticleSegmentGroups} when this method is called.
	 * </p>
	 * <p>
	 * The client is notified about this kind of change by the server. Therefore, the UI does not need
	 * to call this method. It is, however, still possible to call this method locally in the client,
	 * in order to have a faster UI-update for local changes.
	 * </p>
	 * <p>
	 * The default implementation of this method in {@link AbstractSegmentEdit} iterates all its
	 * {@link ArticleEdit}s and calls their
	 * </p>
	 *
	 * @param articleCarriers The {@link ArticleCarrier}s referencing {@link Article}s which already have
	 *		been deleted.
	 */
	void removeArticles(Collection<ArticleCarrier> articleCarriers);

//	void removeArticles(Collection<ArticleCarrier> articleCarriers, Collection<Article> articles);

//	/**
//	 * This method checks whether all of the given <code>articles</code> can be removed.
//	 * It iterates all {@link ArticleEdit}s and calls {@link ArticleEdit#canRemoveArticles(Collection)}.
//	 * If one of these calls returns <code>false</code>, this method immediately returns
//	 * <code>false</code> without iterating further.
//	 *
//	 * @param articles Instances of {@link org.nightlabs.jfire.trade.ui.Article}.
//	 * @return Returns <code>true</code>, if all articles can be removed and <code>false</code>,
//	 *		if at least one of the articles cannot be removed.
//	 */
//	boolean canRemoveArticles(Collection articles);
//
//	/**
//	 * This method is the entry point for removing {@link org.nightlabs.jfire.trade.ui.Article}s out
//	 * of an Order/Offer/Invoice/DeliveryNote. It will iterate all {@link ArticleEdit}s and
//	 * call {@link ArticleEdit#removeArticles(Collection)}.
//	 *
//	 * @param articlesToRemove
//	 * @param nonRemovableArticles
//	 */
//	void removeArticles(Collection articles);

	/**
	 * Note, that you must call this method when the <tt>Composite</tt> which you created
	 * in {@link #createComposite(Composite)} is disposed. You should do this by
	 * adding a {@link org.eclipse.swt.events.DisposeListener}:
	 * <code><pre>
	 *  addDisposeListener(new DisposeListener() {
	 *    public void widgetDisposed(DisposeEvent e)
	 *    {
	 *      removeDisposeListener(this);
	 *      segmentEdit.dispose();
	 *    }
	 *  });
	 * </pre></code>
	 * <p>
	 * <b>It is recommended to extend {@link AbstractSegmentEdit} and implement
	 * {@link AbstractSegmentEdit#_createComposite(Composite)} instead of overriding/extending
	 * {@link #createComposite(Composite)}!</b> You do not need to take care about dispose then!
	 */
	void onDispose();

	/**
	 * A <code>SegmentEdit</code> should have the same context-menu for all of its
	 * {@link ArticleEdit}s. This allows to manage them as if they were only one
	 * <code>ArticleEdit</code>.
	 * <p>
	 * In order to achieve this behaviour, every <code>SegmentEdit</code> has one
	 * {@link org.eclipse.jface.action.MenuManager} which should be used to create
	 * all the <code>ArticleEdit</code>'s context menus. Hence, you must use this method
	 * in your <code>ArticleEdit</code> implementation.
	 * </p>
	 * <p>
	 * The <code>MenuManager</code> is configured to recreate all its <code>Action</code>s
	 * always before a menu is shown. This causes the method
	 * {@link #populateArticleEditContextMenu(IMenuManager)}
	 * to be triggered before a context menu pops up.
	 * </p>
	 * <p>
	 * This method calls {@link Control#setMenu(org.eclipse.swt.widgets.Menu)} with the result
	 * already.
	 * </p>
	 * @param parent The control for which to create the context menu.
	 */
	Menu createArticleEditContextMenu(Control parent);

	void addSegmentEditArticleSelectionListener(SegmentEditArticleSelectionListener listener);
	void removeSegmentEditArticleSelectionListener(SegmentEditArticleSelectionListener listener);
	void fireSegmentEditArticleSelectionEvent();

	void addCreateArticleEditListener(CreateArticleEditListener listener);
	void removeCreateArticleEditListener(CreateArticleEditListener listener);
	void fireCreateArticleEditEvent(Collection<? extends ArticleEdit> createdArticleEdits);

//	/**
//	 * This method iterates all {@link ArticleEdit}s and calls their
//	 * {@link ArticleEdit#populateArticleEditContextMenu(IMenuManager)}.
//	 *
//	 * @param manager
//	 */
//	void populateArticleEditContextMenu(IMenuManager manager);
}
