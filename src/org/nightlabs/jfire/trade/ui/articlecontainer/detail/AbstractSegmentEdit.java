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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.nightlabs.base.ui.extensionpoint.EPProcessorException;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.notification.NotificationAdapterJob;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.store.id.ProductTypeID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleProductTypeClassGroup;
import org.nightlabs.jfire.trade.ArticleSegmentGroup;
import org.nightlabs.jfire.trade.SegmentType;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.ProgressMonitor;

/**
 * This is the base implementation of {@link SegmentEdit} it should be sub-classed to create new ones.
 * Besides things like the management of members passed by the API in {@link #init(SegmentEditFactory, ArticleContainerEdit, Class, ArticleSegmentGroup)},
 * the management/creation of {@link ArticleSelection}s it does two important things:
 * <p>
 * It listens to the selection of a {@link ProductTypeID} in the {@link TradePlugin#ZONE_SALE} and
 * queries the appropriate {@link ArticleAdder} from the {@link ArticleAdderFactoryRegistry}.
 * It will create the {@link ArticleAdder} but note that it will delegate the creation of its
 * {@link Composite} to {@link #createArticleAdderComposite(ArticleAdder)}.
 * </p>
 * <p>
 * Additionally it implements the {@link #addArticles(Collection)} and {@link #removeArticles(Collection)}
 * methods of {@link SegmentEdit} (These methods are called by the {@link ClientArticleSegmentGroupSet}
 * when an {@link ArticleAdder} added new articles. {@link AbstractSegmentEdit} uses the {@link ArticleEditFactoryRegistry}
 * to lookup {@link ArticleEdit}s for the added articles. It then implements the iteration of
 * adding articles to {@link ArticleEdit} by creating as much of them are necessary so that
 * all articles were accepted by one. Note that here as well, {@link AbstractSegmentEdit} will
 * create the {@link ArticleEdit} instance but not its ui, this will be delegated
 * to {@link #createArticleEditComposite(ArticleEdit)}.
 * </p>
 *
 * @author Marco Schulze - marco at nightlabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class AbstractSegmentEdit
implements SegmentEdit
{
	private static final Logger logger = Logger.getLogger(AbstractSegmentEdit.class);

	private SegmentEditFactory segmentEditFactory;
	private ArticleContainerEdit articleContainerEdit;
	private Class<?> articleContainerClass;
	private ArticleSegmentGroup articleSegmentGroup;

	protected Class<? extends SegmentType> segmentTypeClass;
	protected ArticleEditFactoryRegistry articleEditFactoryRegistry;
	protected List<ArticleEdit> articleEdits;
	protected List<ArticleEdit> articleEditsReadOnly;

	@Override
	public void init(
			SegmentEditFactory segmentEditFactory,
			ArticleContainerEdit articleContainerEdit,
			Class<?> articleContainerClass,
			ArticleSegmentGroup articleSegmentGroup)
	{
		this.segmentEditFactory = segmentEditFactory;
		this.articleContainerEdit = articleContainerEdit;
		this.articleContainerClass = articleContainerClass;
		this.articleSegmentGroup = articleSegmentGroup;

		SelectionManager.sharedInstance().addNotificationListener(
				TradePlugin.ZONE_SALE, ProductTypeID.class, notificationListenerProductTypeSelected);

		try {
			articleEditFactoryRegistry = ArticleEditFactoryRegistry.sharedInstance();
		} catch (EPProcessorException e) {
			throw new RuntimeException(e);
		}

		segmentTypeClass = articleSegmentGroup.getSegment().getSegmentType().getClass();

		articleEdits = new ArrayList<ArticleEdit>();
		articleEditsReadOnly = Collections.unmodifiableList(articleEdits);

		Collection<ArticleProductTypeClassGroup> aptgs = articleSegmentGroup.getArticleProductTypeGroups();
		for (Iterator<ArticleProductTypeClassGroup> itAPTGroups = aptgs.iterator(); itAPTGroups.hasNext(); ) {
			ArticleProductTypeClassGroup aptg = itAPTGroups.next();

//			ArticleEditFactory aef;
//			try {
//				aef = articleEditFactoryRegistry.getArticleEditFactory(
//						articleContainerClass, segmentTypeClass, Class.forName(aptg.getProductTypeClassName()), true);
//			} catch (ClassNotFoundException e) {
//				throw new RuntimeException(e);
//			}
			ArticleEditFactory aef = articleEditFactoryRegistry.getArticleEditFactory(
					articleContainerClass, segmentTypeClass, aptg.getProductTypeClass(), true);

			Collection<? extends ArticleEdit> _articleEdits = aef.createArticleEdits(this, aptg, aptg.getArticleCarriers());
			for (Iterator<? extends ArticleEdit> itEdits = _articleEdits.iterator(); itEdits.hasNext(); ) {
				ArticleEdit edit = itEdits.next();
				edit.addArticleEditArticleSelectionListener(articleEditArticleSelectionListener);
				articleEdits.add(edit);
				// cannot create composites in init! Must first create this edit's composite before
				// adding child composites!
//				createArticleEditComposite(edit);
			}
		}
	}

	public ArticleContainerEdit getArticleContainerEdit()
	{
		return articleContainerEdit;
	}

	public ArticleContainer getArticleContainer()
	{
		return articleContainerEdit.getArticleContainer();
	}

	public ArticleContainerID getArticleContainerID()
	{
		return articleContainerEdit.getArticleContainerID();
	}

	public List<ArticleEdit> getArticleEdits()
	{
		return articleEditsReadOnly;
	}

	public SegmentEditFactory getSegmentEditFactory()
	{
		return segmentEditFactory;
	}

	public Menu createArticleEditContextMenu(Control parent)
	{
		return articleContainerEdit.getArticleContainerEditActionContributor().createArticleEditContextMenu(parent);
	}

	private Composite composite;

	/**
	 * You must not overwrite/extend this method! Implement {@link #_createComposite(Composite)}. This
	 * method adds the {@link DisposeListener}, so you don't have to think about it in your composite.
	 *
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit#createComposite(org.eclipse.swt.widgets.Composite)
	 */
	public Composite createComposite(Composite parent)
	{
		final Composite res = _createComposite(parent);
		if (res == null)
			throw new NullPointerException("_createComposite(...) returned null!"); //$NON-NLS-1$

		for (Iterator<ArticleEdit> it = articleEdits.iterator(); it.hasNext(); ) {
			ArticleEdit articleEdit = it.next();
			createArticleEditComposite(articleEdit);
		}
		fireCompositeContentChangeEvent();

		res.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e)
			{
				res.removeDisposeListener(this);
				onDispose();
			}
		});

		this.composite = res;

		fireCreateArticleEditEvent(articleEdits);

		// it may obviously happen that the productTypeSelected(...) method below is called before this createComposite(...) => select it now
		final ProductTypeID selProductTypeID = selectedProductTypeID;
		if (selProductTypeID != null) {
			new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractSegmentEdit.job.selectProductType")) { //$NON-NLS-1$
				@Override
				protected IStatus run(ProgressMonitor monitor)
						throws Exception
				{
					productTypeSelected(selProductTypeID, monitor);
					return Status.OK_STATUS;
				}
			}.schedule();
		}

		return res;
	}

	/**
	 * This method is called by {@link #createComposite(Composite)} and must return
	 * the main composite which reflects this <tt>SegmentEdit</tt>. It should NOT populate
	 * any <tt>ArticleEdit</tt>'s <tt>Composite</tt>s as they are created by
	 * {@link #createArticleEditComposite(ArticleEdit)}.
	 */
	protected abstract Composite _createComposite(Composite parent);

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit#getComposite()
	 */
	public Composite getComposite()
	{
		return composite;
	}

	private volatile ProductTypeID selectedProductTypeID = null;
	private volatile Class<? extends ProductType> selectedProductTypeClass = null;
	private ArticleAdderFactory articleAdderFactoryForSelectedProductType = null;
	private ArticleAdder articleAdderForSelectedProductType = null;

	/**
	 * This method is called (on a worker thread) by the {@link SelectionManager} by
	 * {@link #notificationListenerProductTypeSelected}.
	 */
	protected synchronized void productTypeSelected(ProductTypeID productTypeID, ProgressMonitor monitor) {
		try {
			if (productTypeID == null) {
				this.selectedProductTypeID = null;
				this.selectedProductTypeClass = null;
				if (articleAdderForSelectedProductType != null) {
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							articleAdderForSelectedProductType.dispose();
						}
					});
				}
			}
			else {
				this.selectedProductTypeID = productTypeID;

				@SuppressWarnings("unchecked")
				Class<? extends ProductType> ptc = (Class<? extends ProductType>) JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(productTypeID);
				this.selectedProductTypeClass = ptc;

//				// Only Order and Offer support ArticleAdders => manage ArticleAdders only for them
//				if (Order.class.isAssignableFrom(articleContainerClass) ||
//						Offer.class.isAssignableFrom(articleContainerClass))
//				{
				Class<? extends SegmentType> segmentTypeClass = articleSegmentGroup.getSegment().getSegmentType().getClass();
				ArticleAdderFactory factory = ArticleAdderFactoryRegistry.sharedInstance().getArticleAdderFactory(
						articleContainerClass, segmentTypeClass,
						selectedProductTypeClass, false);

				if (factory == null) {
					if (logger.isInfoEnabled())
						logger.info("There is no ArticleAdderFactory registered for articleContainerClass=" + articleContainerClass.getName() + " segmentTypeClass=" + segmentTypeClass.getName() + " selectedProductTypeClass=" + selectedProductTypeClass.getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}

					final ArticleAdder articleAdderToDispose = articleAdderForSelectedProductType;
					final ArticleAdder articleAdder = factory == null ? null : factory.createArticleAdder(this);
					articleAdderFactoryForSelectedProductType = factory;
					articleAdderForSelectedProductType = articleAdder;

					if (articleAdder != null)
						articleAdder.setProductTypeID(selectedProductTypeID, monitor);

					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							if (composite == null)
								return;

							if (articleAdderToDispose != null)
								articleAdderToDispose.dispose();

							if (articleAdder != null)
								createArticleAdderComposite(articleAdder);

							fireCompositeContentChangeEvent();
						}
					});
//				} // articleContainerClass is Order or Offer
			} // if (productTypeID != null) {
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}

	/**
	 * This method is called snychronously on the SWT GUI thread by
	 * {@link #productTypeSelected(ProductTypeID)} after the appropriate {@link ArticleAdder} was created.
	 * Implementations should use the {@link ArticleAdder#createComposite(Composite)} method
	 * to create its ui inside the ui of this {@link AbstractSegmentEdit}.
	 * This is not done directly in order to enable sub-classes
	 * to react on the creation of these composites, so now sub-classes have
	 * to do it themselves.
	 *
	 * @param articleAdder The {@link ArticleAdder} the ui should be added.
	 */
	protected abstract void createArticleAdderComposite(ArticleAdder articleAdder);

	private NotificationListener notificationListenerProductTypeSelected = new NotificationAdapterJob() {
		public void notify(NotificationEvent event) {
			if (event.getSubjects().isEmpty())
			productTypeSelected(null, getProgressMonitor());
		else
			productTypeSelected((ProductTypeID)event.getFirstSubject(), getProgressMonitor());
		}
	};

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit#getArticleSegmentGroup()
	 */
	public ArticleSegmentGroup getArticleSegmentGroup()
	{
		return articleSegmentGroup;
	}

	public ClientArticleSegmentGroupSet getClientArticleSegmentGroupSet()
	{
		return (ClientArticleSegmentGroupSet)getArticleSegmentGroup().getArticleSegmentGroupSet();
	}

	private ListenerList compositeContentChangeListeners = new ListenerList();

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit#addCompositeContentChangeListener(org.nightlabs.jfire.trade.ui.articlecontainer.CompositeContentChangeListener)
	 */
	public void addCompositeContentChangeListener(CompositeContentChangeListener listener)
	{
		compositeContentChangeListeners.add(listener);
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit#removeCompositeContentChangeListener(org.nightlabs.jfire.trade.ui.articlecontainer.CompositeContentChangeListener)
	 */
	public void removeCompositeContentChangeListener(
			CompositeContentChangeListener listener)
	{
		compositeContentChangeListeners.remove(listener);
	}

	/**
	 * @see org.nightlabs.jfire.trade.ui.articlecontainer.detail.SegmentEdit#fireCompositeContentChangeEvent()
	 */
	public void fireCompositeContentChangeEvent()
	{
		if (compositeContentChangeListeners.isEmpty())
			return;

		CompositeContentChangeEvent event = new CompositeContentChangeEvent(this);

		for (Object l : compositeContentChangeListeners.getListeners()) {
			CompositeContentChangeListener listener = (CompositeContentChangeListener) l;
			listener.changed(event);
		}
	}

	public Class<?> getArticleContainerClass()
	{
		return articleContainerClass;
	}

	public Set<ArticleSelection> getArticleSelections()
	{
		Set<ArticleSelection> res = new HashSet<ArticleSelection>();
		for (ArticleEdit edit : articleEdits) {
			Set<? extends Article> selectedArticles = edit.getSelectedArticles();
			if (!selectedArticles.isEmpty()) {
				ArticleSelection articleSelection = new ArticleSelection(edit, selectedArticles);
				res.add(articleSelection);
			}
		}
		return res;
	}

	public synchronized void setSelectedArticles(Collection<? extends Article> _articles)
	{
		if (_articles == null)
			throw new IllegalArgumentException("articles must not be null!"); //$NON-NLS-1$

		if (ignoreArticleEditArticleSelectionEvents)
			return;

		Set<? extends Article> articles = _articles instanceof Set ? (Set<? extends Article>)_articles : new HashSet<Article>(_articles);

		ignoreArticleEditArticleSelectionEvents = true;
		try {
			for (ArticleEdit edit : articleEdits) {
				articles = edit.setSelectedArticles(articles);
			}
		} finally {
			ignoreArticleEditArticleSelectionEvents = false;
		}
	}

	private boolean ignoreArticleEditArticleSelectionEvents = false;

	@Override
	public void removeArticles(Collection<ArticleCarrier> articleCarriers)
	{
//		for (ArticleEdit articleEdit : getArticleEdits()) {
		for (Iterator<ArticleEdit> it = articleEdits.iterator(); it.hasNext(); ) {
			ArticleEdit articleEdit = it.next();
			articleCarriers = articleEdit.removeArticles(articleCarriers);
			if (articleEdit.getArticleCarriers().isEmpty()) {
				articleEdit.dispose();
				it.remove();
				getComposite().layout(true, true);
			}
			if (articleCarriers == null || articleCarriers.isEmpty())
				break;
		}
	}

	@Override
	public void addArticles(Collection<ArticleCarrier> _articleCarriers)
	{
		if (_articleCarriers == null)
			throw new IllegalArgumentException("articleCarriers must not be null!"); //$NON-NLS-1$

		Set<? extends ArticleCarrier> articleCarriers = new HashSet<ArticleCarrier>(_articleCarriers);

// We don't add anything to the ArticleContainer - we work with articleSegmentGroup instead!
//		// add the articles to the ArticleContainer
//		for (Iterator it = articles.iterator(); it.hasNext(); ) {
//			Article article = (Article) it.next();
//			try {
//				getArticleContainerID().addArticle(article);
//			} catch (ArticleContainerException e) {
//				throw new RuntimeException(e);
//			}
//		}

		// We don't need to add it here anymore, because it's done by a JDOLifecycleListener in ClientArticleSegmentGroupSet.
		// 2006-11-24 Marco.
//		articleSegmentGroup.addArticles(articles);

		// now we try to add the articles to the existing ArticleEdits (they may accept or refuse)
		for (Iterator<ArticleEdit> it = articleEdits.iterator(); it.hasNext(); ) {
			ArticleEdit edit = it.next();
			articleCarriers = edit.addArticles(articleCarriers);
			if (articleCarriers == null || articleCarriers.isEmpty())
				break;
		}

		// If there are still articles left that have not been accepted by the existing ArticleEdits, we create a new
		// ArticleEdit.
		if (articleCarriers != null && !articleCarriers.isEmpty()) {
			Map<Class<? extends ProductType>, List<ArticleCarrier>> productTypeClass2articleCarriers = new HashMap<Class<? extends ProductType>, List<ArticleCarrier>>();
			for (ArticleCarrier articleCarrier : articleCarriers) {
				Article article = articleCarrier.getArticle();
				Class<? extends ProductType> productTypeClass = article.getProductType().getClass();
				List<ArticleCarrier> acList = productTypeClass2articleCarriers.get(productTypeClass);
				if (acList == null) {
					acList = new ArrayList<ArticleCarrier>();
					productTypeClass2articleCarriers.put(productTypeClass, acList);
				}
				acList.add(articleCarrier);
			}

			for (Map.Entry<Class<? extends ProductType>, List<ArticleCarrier>> me : productTypeClass2articleCarriers.entrySet()) {
				Class<? extends ProductType> productTypeClass = me.getKey();
				ArticleEditFactory aef = articleEditFactoryRegistry.getArticleEditFactory(articleContainerClass, segmentTypeClass, productTypeClass, true);

				ArticleProductTypeClassGroup articleProductTypeClassGroup = articleSegmentGroup.getArticleProductTypeClassGroup(productTypeClass.getName(), true);

//				ArticleProductTypeClassGroup articleProductTypeClassGroup = articleSegmentGroup.createArticleProductTypeClassGroup(firstArticle.getProductType().getClass());
//				Collection articleCarriers = articleProductTypeClassGroup.addArticles(articles);
//				Collection edits = aef.createArticleEdits(this, articleProductTypeClassGroup, articleCarriers); // articleCarriers here is wrong!
				Collection<? extends ArticleEdit> edits = aef.createArticleEdits(this, articleProductTypeClassGroup, me.getValue());
				for (Iterator<? extends ArticleEdit> it = edits.iterator(); it.hasNext(); ) {
					ArticleEdit edit = it.next();
					edit.addArticleEditArticleSelectionListener(articleEditArticleSelectionListener);
					articleEdits.add(edit);
					createArticleEditComposite(edit);
					fireCompositeContentChangeEvent();
				}

				fireCreateArticleEditEvent(edits);
			}
		} // if (articles != null && !articles.isEmpty()) {
	}

	private ListenerList segmentEditArticleSelectionListeners = new ListenerList();

	public void addSegmentEditArticleSelectionListener(SegmentEditArticleSelectionListener listener)
	{
		segmentEditArticleSelectionListeners.add(listener);
	}
	public void removeSegmentEditArticleSelectionListener(SegmentEditArticleSelectionListener listener)
	{
		segmentEditArticleSelectionListeners.remove(listener);
	}
	public void fireSegmentEditArticleSelectionEvent()
	{
		SegmentEditArticleSelectionEvent event = null;

		Object[] listeners = segmentEditArticleSelectionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			SegmentEditArticleSelectionListener listener = (SegmentEditArticleSelectionListener) listeners[i];

			if (event == null)
				event = new SegmentEditArticleSelectionEvent(this);

			listener.selected(event);
		}
	}

	private ArticleEditArticleSelectionListener articleEditArticleSelectionListener = new ArticleEditArticleSelectionListener() {
		public void selected(ArticleEditArticleSelectionEvent event)
		{
			if (ignoreArticleEditArticleSelectionEvents)
				return;

			fireSegmentEditArticleSelectionEvent();
		}
	};

	public ArticleAdderFactory getArticleAdderFactoryForSelectedProductType()
	{
		return articleAdderFactoryForSelectedProductType;
	}

	@Override
	public void onDispose()
	{
		SelectionManager.sharedInstance().removeNotificationListener(
				TradePlugin.ZONE_SALE, ProductTypeID.class, notificationListenerProductTypeSelected);
		composite = null;
	}

	/**
	 * This method is called synchronously on the SWT ui thread after
	 * the given {@link ArticleEdit} is created. Implementations
	 * should use the {@link ArticleEdit#createComposite(Composite)} method
	 * to create its ui inside the ui of this {@link AbstractSegmentEdit}.
	 * This is not done directly in order to enable sub-classes
	 * to react on the creation of these composites, so now sub-classes have
	 * to do it themselves.
	 *
	 * @param articleEdit The {@link ArticleEdit} the ui should be added.
	 */
	protected abstract void createArticleEditComposite(ArticleEdit articleEdit);


	private ListenerList createArticleEditListeners = new ListenerList();

	public void addCreateArticleEditListener(CreateArticleEditListener listener)
	{
		createArticleEditListeners.add(listener);
	}
	public void removeCreateArticleEditListener(CreateArticleEditListener listener)
	{
		createArticleEditListeners.remove(listener);
	}
	public void fireCreateArticleEditEvent(Collection<? extends ArticleEdit> createdArticleEdits)
	{
		CreateArticleEditEvent event = null;

		Object[] listeners = createArticleEditListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			CreateArticleEditListener listener = (CreateArticleEditListener) listeners[i];
			if (event == null)
				event = new CreateArticleEditEvent(this, createdArticleEdits);
			listener.createdArticleEdits(event);
		}
	}
}
