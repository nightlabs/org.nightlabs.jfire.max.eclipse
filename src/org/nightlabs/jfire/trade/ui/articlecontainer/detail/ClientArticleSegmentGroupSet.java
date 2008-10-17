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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.swt.widgets.Display;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.base.jdo.cache.Cache;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleEvent;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleListener;
import org.nightlabs.jfire.base.jdo.notification.JDOLifecycleManager;
import org.nightlabs.jfire.base.ui.jdo.notification.JDOLifecycleAdapterJob;
import org.nightlabs.jfire.jdo.notification.DirtyObjectID;
import org.nightlabs.jfire.jdo.notification.IJDOLifecycleListenerFilter;
import org.nightlabs.jfire.jdo.notification.JDOLifecycleState;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.id.DeliveryNoteID;
import org.nightlabs.jfire.store.id.ReceptionNoteID;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleCarrier;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleSegmentGroupSet;
import org.nightlabs.jfire.trade.FetchGroupsTrade;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.dao.ArticleDAO;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.id.OfferID;
import org.nightlabs.jfire.trade.id.OrderID;
import org.nightlabs.jfire.trade.notification.ArticleLifecycleListenerFilter;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.notification.NotificationAdapterCallerThread;
import org.nightlabs.notification.NotificationEvent;
import org.nightlabs.notification.NotificationListener;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.util.CollectionUtil;

/**
 * This is a {@link ArticleSegmentGroupSet} that manages listeners for the creation, change and deletion
 * of the {@link Article}s of the associated {@link ArticleContainer} in order to keep the set up-to-date.
 * <p>
 * Users of {@link ClientArticleSegmentGroupSet} can add {@link ArticleCreateListener}s and {@link ArticleChangeListener}s
 * to be notified of the changes made to the group.
 * </p>
 * <p>
 * Note that this class knows of some implementations of {@link ArticleContainer} (Order, Offer, Invoive, DeliveryNote)
 * and will handle those differently, but it does not know other implementations or the special needs
 * of sub-classes.
 * </p>
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * @author Marco Schulze - Marco at NightLabs dot de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ClientArticleSegmentGroupSet extends ArticleSegmentGroupSet
{
	private static final Logger logger = Logger.getLogger(ClientArticleSegmentGroupSet.class);

	private String[] fetchGroupsArticle;

	public ClientArticleSegmentGroupSet(ArticleContainer articleContainer,
			ArticleCreateListener ... articleCreateListeners)
	{
		this(articleContainer, articleCreateListeners, null);
	}
	public ClientArticleSegmentGroupSet(ArticleContainer articleContainer,
			ArticleChangeListener ... articleChangeListeners)
	{
		this(articleContainer, null, articleChangeListeners);
	}

	public ClientArticleSegmentGroupSet(ArticleContainer articleContainer,
			ArticleCreateListener[] articleCreateListeners, ArticleChangeListener[] articleChangeListeners)
	{
		super(articleContainer);

		if (articleContainer instanceof Order)
			fetchGroupsArticle = FETCH_GROUPS_ARTICLE_IN_ORDER_EDITOR;
		else if (articleContainer instanceof Offer)
			fetchGroupsArticle = FETCH_GROUPS_ARTICLE_IN_OFFER_EDITOR;
		else if (articleContainer instanceof Invoice)
			fetchGroupsArticle = FETCH_GROUPS_ARTICLE_IN_INVOICE_EDITOR;
		else if (articleContainer instanceof DeliveryNote)
			fetchGroupsArticle = FETCH_GROUPS_ARTICLE_IN_DELIVERY_NOTE_EDITOR;
		else
			throw new IllegalStateException("Unknown ArticleContainer implementation: " + articleContainer); //$NON-NLS-1$

		if (articleCreateListeners != null) {
			for (ArticleCreateListener listener : articleCreateListeners)
				addArticleCreateListener(listener);
		}

		if (articleChangeListeners != null) {
			for (ArticleChangeListener listener : articleChangeListeners)
				addArticleChangeListener(listener);
		}

		JDOLifecycleManager.sharedInstance().addLifecycleListener(lifecycleListenerNewArticles);
		JDOLifecycleManager.sharedInstance().addNotificationListener(Article.class, notificationListenerArticlesChanged);
	}

	/**
	 * This method is called by {@link ArticleContainerEditComposite} in the {@link org.eclipse.swt.events.DisposeListener}.
	 */
	public void onDispose()
	{
		JDOLifecycleManager.sharedInstance().removeLifecycleListener(lifecycleListenerNewArticles);
		JDOLifecycleManager.sharedInstance().removeNotificationListener(Article.class, notificationListenerArticlesChanged);
	}

	public static final String[] FETCH_GROUPS_ARTICLE_IN_OFFER_EDITOR = new String[] {
		FetchPlan.DEFAULT,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_OFFER_EDITOR
	};

	public static final String[] FETCH_GROUPS_ARTICLE_IN_ORDER_EDITOR = new String[] {
		FetchPlan.DEFAULT,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_ORDER_EDITOR
	};

	public static final String[] FETCH_GROUPS_ARTICLE_IN_INVOICE_EDITOR = new String[] {
		FetchPlan.DEFAULT,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_INVOICE_EDITOR
	};

	public static final String[] FETCH_GROUPS_ARTICLE_IN_DELIVERY_NOTE_EDITOR = new String[] {
		FetchPlan.DEFAULT,
		FetchGroupsTrade.FETCH_GROUP_ARTICLE_IN_DELIVERY_NOTE_EDITOR
	};

	private JDOLifecycleListener lifecycleListenerNewArticles = new JDOLifecycleAdapterJob(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.ClientArticleSegmentGroupSet.job.loadingNewArticles")) { //$NON-NLS-1$
//	private JDOLifecycleListener lifecycleListenerNewArticles = new JDOLifecycleAdapterCallerThread() { // TODO this must not be called on the caller thread - only temporarily for debugging!
		private ArticleLifecycleListenerFilter filter = null;

		public IJDOLifecycleListenerFilter getJDOLifecycleListenerFilter()
		{
			if (filter == null) {
				ArticleContainerID articleContainerID = getArticleContainerID();
				if (articleContainerID instanceof OrderID || articleContainerID instanceof OfferID)
					filter = new ArticleLifecycleListenerFilter(new JDOLifecycleState[] { JDOLifecycleState.NEW }, getArticleContainerID());
				else {
					// In this case, we need to use the DIRTY as well, because an Article is not freshly created when being added to an
					// Invoice, DeliveryNote etc.
					filter = new ArticleLifecycleListenerFilter(new JDOLifecycleState[] { JDOLifecycleState.NEW, JDOLifecycleState.DIRTY }, getArticleContainerID());
				}
			}

			return filter;
		}

		public void notify(final JDOLifecycleEvent event)
		{
			synchronized (ClientArticleSegmentGroupSet.this) {
				if (logger.isDebugEnabled())
					logger.debug("JDOLifecycleAdapterJob.notify: begin"); //$NON-NLS-1$

				if (getActiveJDOLifecycleEvent() != event) {
					logger.error("Job executed too late!!! How do we prevent this??? getActiveJDOLifecycleEvent()=" + getActiveJDOLifecycleEvent() + " event=" + event); //$NON-NLS-1$ //$NON-NLS-2$
				}

				Set<ArticleID> articleIDs = new HashSet<ArticleID>(event.getDirtyObjectIDs().size());
				for (DirtyObjectID dirtyObjectID : event.getDirtyObjectIDs()) {
					ArticleID articleID = (ArticleID) dirtyObjectID.getObjectID();

					if (!containsArticle(articleID)) {
						if (logger.isDebugEnabled())
							logger.debug("JDOLifecycleAdapterJob.notify: new ArticleID: " + articleID); //$NON-NLS-1$

						articleIDs.add(articleID);
					}
					else {
						if (logger.isDebugEnabled())
							logger.debug("JDOLifecycleListener.notify: Ignoring duplicate new-notification: " + articleID, new Exception()); //$NON-NLS-1$
						// In very rare situations, it can happen that the server notifies the client twice. For example in case,
						// the connection brakes and listeners get re-subscribed, it could theoretically happen. Additionally,
						// the UI might decide to add an Article itself and not wait for the server notification (in order to
						// improve the performance). Marco.
					}
				}

				if (logger.isDebugEnabled())
					logger.debug("JDOLifecycleAdapterJob.notify: loading Articles for " + articleIDs.size() + " ArticleIDs..."); //$NON-NLS-1$ //$NON-NLS-2$

				final Collection<Article> articles = ArticleDAO.sharedInstance().getArticles(
						articleIDs, fetchGroupsArticle, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor());

				ArticleContainerID articleContainerID = getArticleContainerID();
				if (!(articleContainerID instanceof OrderID || articleContainerID instanceof OfferID)) {
					for (Iterator<Article> itA = articles.iterator(); itA.hasNext();) {
						Article newArticle = itA.next();

						boolean addedToArticleContainer = false;
						if (articleContainerID instanceof InvoiceID)
							addedToArticleContainer = articleContainerID.equals(newArticle.getInvoiceID());
						if (articleContainerID instanceof DeliveryNoteID)
							addedToArticleContainer = articleContainerID.equals(newArticle.getDeliveryNoteID());
						if (articleContainerID instanceof ReceptionNoteID)
							addedToArticleContainer = articleContainerID.equals(newArticle.getReceptionNoteID());

						if (!addedToArticleContainer) {
							if (logger.isDebugEnabled())
								logger.debug("JDOLifecycleAdapterJob.notify: Article has NOT been added to current ArticleContainer, ignoring it: " + JDOHelper.getObjectId(newArticle)); //$NON-NLS-1$

							itA.remove();
						}
					}
				}

				addArticles(articles, true);

				if (logger.isDebugEnabled())
					logger.debug("JDOLifecycleAdapterJob.notify: end"); //$NON-NLS-1$
			} // synchronized
		}
	};

	/**
	 * This is a convenience method calling {@link #addArticles(Collection, boolean)} with <code>filterExisting = true</code>.
	 * @param articles The articles to be added.
	 * @return All those ArticleCarriers that have been newly created for the given articles - i.e. Articles that have been existing before
	 *		(due to double calls because of double notifications) will be ignored.
	 */
	public Collection<ArticleCarrier> addArticles(Collection<Article> articles)
	{
		return addArticles(articles, true);
	}

	@Override
	public synchronized Collection<ArticleCarrier> addArticles(Collection<Article> articles, boolean filterExisting)
	{
		if (logger.isDebugEnabled())
			logger.debug("JDOLifecycleAdapterJob.addArticles: adding " + articles.size() + " articles."); //$NON-NLS-1$ //$NON-NLS-2$

		Collection<ArticleCarrier> articleCarriers = super.addArticles(articles, filterExisting);

		// if we have no listeners, we return
		if (articleCreateListeners == null || articleCreateListeners.isEmpty())
			return articleCarriers;

		if (articleCarriers.size() != articles.size()) { // if they were filtered and some articles passed that already existed, this might happen
			Collection<Article> nArticles = new ArrayList<Article>(articleCarriers.size());
			for (ArticleCarrier articleCarrier : articleCarriers) {
				nArticles.add(articleCarrier.getArticle());
			}
			articles = nArticles;
		}

		final ArticleCreateEvent articleCreateEvent = new ArticleCreateEvent(ClientArticleSegmentGroupSet.this, articles, articleCarriers);

		final Object[] listeners = articleCreateListeners.getListeners();
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (logger.isDebugEnabled()) {
					logger.debug("addArticles: calling listeners with ArticleCreateEvent:"); //$NON-NLS-1$
					logger.debug("addArticles:     ArticleCreateEvent.articles (" + articleCreateEvent.getArticles().size() + "):"); //$NON-NLS-1$ //$NON-NLS-2$
					for (Article article : articleCreateEvent.getArticles())
						logger.debug("addArticles:       - " + article.getPrimaryKey()); //$NON-NLS-1$
				}
				for (int i = 0; i < listeners.length; i++) {
					if (logger.isDebugEnabled())
						logger.debug("addArticles:     listener: " + listeners[i]); //$NON-NLS-1$

					((ArticleCreateListener) listeners[i]).articlesCreated(articleCreateEvent);
				}
			}
		});

		return articleCarriers;
	}

//	private NotificationListener notificationListenerArticlesChanged = new NotificationAdapterJob("Reloading changed articles") {
	private NotificationListener notificationListenerArticlesChanged = new NotificationAdapterCallerThread() { // TODO this must not be called on the caller thread - only temporarily for debugging!
		public void notify(final NotificationEvent notificationEvent)
		{
			synchronized (ClientArticleSegmentGroupSet.this) { // there is no guarantee, that a job enqueued first is executed first or is there?!???!???
				if (getActiveNotificationEvent() != notificationEvent) {
					logger.error("notificationListenerArticlesChanged.notify: Job executed too late!!! How do we prevent this??? getActiveNotificationEvent()=" + getActiveNotificationEvent() + " event=" + notificationEvent); //$NON-NLS-1$ //$NON-NLS-2$
				}

				if (logger.isDebugEnabled())
					logger.debug("notificationListenerArticlesChanged.notify: begin"); //$NON-NLS-1$

				// find out, which ArticleID s are interesting (i.e. managed by us).
				Set<ArticleID> dirtyArticleIDs = new HashSet<ArticleID>(notificationEvent.getSubjects().size());
				Set<ArticleID> deletedArticleIDs = new HashSet<ArticleID>(notificationEvent.getSubjects().size());
//				for (Iterator it = notificationEvent.getSubjects().iterator(); it.hasNext(); ) {
//					DirtyObjectID dirtyObjectID = (DirtyObjectID) it.next();
				Collection<DirtyObjectID> subjects = CollectionUtil.castCollection(notificationEvent.getSubjects());
				for (DirtyObjectID dirtyObjectID : subjects) {
					ArticleID articleID = (ArticleID) dirtyObjectID.getObjectID();
					if (containsArticle(articleID)) {
						if (logger.isTraceEnabled()) {
							logger.trace("notificationListenerArticlesChanged.notify: is contained: " + dirtyObjectID); //$NON-NLS-1$
						}

						if (JDOLifecycleState.DIRTY.equals(dirtyObjectID.getLifecycleState()))
							dirtyArticleIDs.add(articleID);
						else if (JDOLifecycleState.DELETED.equals(dirtyObjectID.getLifecycleState()))
							deletedArticleIDs.add(articleID);
						else
							logger.warn("notificationListenerArticlesChanged.notify: Why the hell does this happen? dirtyObjectID.getLifecycleState()=" + dirtyObjectID.getLifecycleState(), new Exception()); //$NON-NLS-1$
					}
					else {
						if (logger.isDebugEnabled())
							logger.debug("notificationListenerArticlesChanged.notify: ignoring unknown ArticleID=" + articleID); //$NON-NLS-1$
					}
				}

				if (logger.isDebugEnabled())
					logger.debug("notificationListenerArticlesChanged.notify: before deduplication: dirtyArticleIDs.size()="+dirtyArticleIDs.size()+" deletedArticleIDs.size()=" + deletedArticleIDs.size()); //$NON-NLS-1$ //$NON-NLS-2$

				// in case there are some dirty and deleted - though I'm not sure whether the notification-mechanism (i.e. Cache) already prevents this.
				dirtyArticleIDs.removeAll(deletedArticleIDs);

				if (logger.isDebugEnabled())
					logger.debug("notificationListenerArticlesChanged.notify: after deduplication: dirtyArticleIDs.size()="+dirtyArticleIDs.size()+" deletedArticleIDs.size()=" + deletedArticleIDs.size()); //$NON-NLS-1$ //$NON-NLS-2$

				// if none of the changed articles is interesting, we return
				if (dirtyArticleIDs.isEmpty() && deletedArticleIDs.isEmpty())
					return;

				// reload the Article s and update the ArticleCarrier s
				final Collection<Article> dirtyArticles = ArticleDAO.sharedInstance().getArticles(
						dirtyArticleIDs, fetchGroupsArticle, getMaxFetchDepthArticle(), new NullProgressMonitor());

				updateArticles(deletedArticleIDs, dirtyArticles);

				if (logger.isTraceEnabled())
					logger.trace("notificationListenerArticlesChanged.notify: end"); //$NON-NLS-1$
			} // synchronized
		}
	};

	public String[] getFetchGroupsArticle() {
		return fetchGroupsArticle;
	}
	public int getMaxFetchDepthArticle() {
		return NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT;
	}

	public synchronized void updateArticles(final Collection<ArticleID> deletedArticleIDs, final Collection<Article> dirtyArticles)
	{
		if (logger.isTraceEnabled()) {
			logger.trace("updateArticles: Got " + dirtyArticles.size() + " articles from ArticleDAO."); //$NON-NLS-1$ //$NON-NLS-2$
			for (Article article : dirtyArticles) {
				logger.trace("updateArticles: * " + article); //$NON-NLS-1$
				StringBuilder sb = new StringBuilder();
				if (article.isAllocated())
					sb.append("allocated "); //$NON-NLS-1$
				else
					sb.append("not-allocated "); //$NON-NLS-1$

				if (article.isAllocationPending())
					sb.append("allocationPending "); //$NON-NLS-1$

				if (article.isReleasePending())
					sb.append("releasePending "); //$NON-NLS-1$

				logger.trace("updateArticles: articleStatus: " + sb.toString()); //$NON-NLS-1$
			}
		}

		final Collection<Article> deletedArticles = new ArrayList<Article>(deletedArticleIDs.size());
		final Collection<ArticleCarrier> deletedArticleCarriers = new ArrayList<ArticleCarrier>(deletedArticleIDs.size());
		for (ArticleID articleID : deletedArticleIDs) {
			ArticleCarrier articleCarrier = getArticleCarrier(articleID, true);

			if (logger.isDebugEnabled())
				logger.debug("NotificationAdapterJob.notify: deletedArticleID=" + articleID); //$NON-NLS-1$

			deletedArticleCarriers.add(articleCarrier);
			deletedArticles.add(articleCarrier.getArticle());
		}

		final ArrayList<ArticleCarrier> dirtyArticleCarriers = new ArrayList<ArticleCarrier>(dirtyArticles.size());
		ArticleContainerID articleContainerID = getArticleContainerID();
		for (Iterator<Article> itArticle = dirtyArticles.iterator(); itArticle.hasNext(); ) {
			Article newArticle = itArticle.next();
			ArticleCarrier articleCarrier = getArticleCarrier((ArticleID) JDOHelper.getObjectId(newArticle), true);
			Article oldArticle = articleCarrier.getArticle();
			Long oldVersion = (Long) JDOHelper.getVersion(oldArticle);
			Long newVersion = (Long) JDOHelper.getVersion(newArticle);
			if (newVersion.longValue() < oldVersion.longValue()) {
				logger.info("updateArticles: Article is older (version " + newVersion + ") than the one we already have (version "+ oldVersion +")! Ignoring it: " + newArticle); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				continue;
			}
			articleCarrier.setArticle(newArticle);

			// If we're in an ArticleContainer from which an Article can be removed without being deleted (e.g. in an Invoice),
			// we need to check, whether the still existing Article's field referencing the ArticleContainer is changed.
			boolean removedFromArticleContainer = false;
			if (articleContainerID instanceof InvoiceID)
				removedFromArticleContainer = !articleContainerID.equals(newArticle.getInvoiceID());
			if (articleContainerID instanceof DeliveryNoteID)
				removedFromArticleContainer = !articleContainerID.equals(newArticle.getDeliveryNoteID());
			if (articleContainerID instanceof ReceptionNoteID)
				removedFromArticleContainer = !articleContainerID.equals(newArticle.getReceptionNoteID());
			// The above is NOT necessary for Order and Offer, because it can only be removed from these ArticleContainers by being deleted from the datastore.

			if (!removedFromArticleContainer) {
				if (logger.isDebugEnabled())
					logger.debug("NotificationAdapterJob.notify: dirtyArticleID=" + JDOHelper.getObjectId(newArticle)); //$NON-NLS-1$

				dirtyArticleCarriers.add(articleCarrier);
			}
			else {
				if (logger.isDebugEnabled())
					logger.debug("NotificationAdapterJob.notify: dirty transformed to deletedArticleID=" + JDOHelper.getObjectId(newArticle)); //$NON-NLS-1$

				itArticle.remove();
				deletedArticles.add(newArticle);
				deletedArticleCarriers.add(articleCarrier);
			}
		}

		// We remove the deleted articles, before notifying the listeners, so that the
		// listeners are triggered with the new situation in place. In case they still need
		// the deleted articles/articleCarriers, they can access the references passed in the
		// event. Marco.
		ClientArticleSegmentGroupSet.super.removeArticles(deletedArticles);

		final ArticleChangeEvent articleChangeEvent = new ArticleChangeEvent(
				ClientArticleSegmentGroupSet.this,
				dirtyArticles, dirtyArticleCarriers,
				deletedArticles, deletedArticleCarriers);

		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				Object[] listeners = articleChangeListeners.getListeners();
				if (logger.isDebugEnabled()) {
					logger.debug("NotificationAdapterJob.notify: calling listeners with ArticleChangeEvent:"); //$NON-NLS-1$
					logger.debug("NotificationAdapterJob.notify:     ArticleChangeEvent.dirtyArticles (" + articleChangeEvent.getDirtyArticles().size() + "):"); //$NON-NLS-1$ //$NON-NLS-2$
					for (Article article : articleChangeEvent.getDirtyArticles())
						logger.debug("NotificationAdapterJob.notify:       - " + article.getPrimaryKey()); //$NON-NLS-1$

					logger.debug("NotificationAdapterJob.notify:     ArticleChangeEvent.deletedArticles (" + articleChangeEvent.getDeletedArticles().size() + "):"); //$NON-NLS-1$ //$NON-NLS-2$
					for (Article article : articleChangeEvent.getDeletedArticles())
						logger.debug("NotificationAdapterJob.notify:       - " + article.getPrimaryKey()); //$NON-NLS-1$
				}
				for (int i = 0; i < listeners.length; i++) {
					if (logger.isDebugEnabled())
						logger.debug("NotificationAdapterJob.notify:     listener: " + listeners[i]); //$NON-NLS-1$

					((ArticleChangeListener) listeners[i]).articlesChanged(articleChangeEvent);
				}
			}
		});
	}

	@Override
	public synchronized void removeArticles(Collection<Article> articles)
	{
		if (logger.isDebugEnabled())
			logger.debug("removeArticles: begin"); //$NON-NLS-1$

		Set<Article> deletedArticles = new HashSet<Article>(articles);
		Set<ArticleCarrier> deletedArticleCarriers = new HashSet<ArticleCarrier>(articles.size());
		for (Iterator<Article> it = deletedArticles.iterator(); it.hasNext(); ) {
			Article article = it.next();
			ArticleID articleID = (ArticleID) JDOHelper.getObjectId(article);
			ArticleCarrier articleCarrier = getArticleCarrier(articleID, false);
			if (articleCarrier == null)
				it.remove();
			else
				deletedArticleCarriers.add(articleCarrier);
		}

		super.removeArticles(deletedArticles);

		final ArticleChangeEvent articleChangeEvent = new ArticleChangeEvent(
				ClientArticleSegmentGroupSet.this,
				new ArrayList<Article>(0), new ArrayList<ArticleCarrier>(0),
				deletedArticles, deletedArticleCarriers);

		final Object[] listeners = articleChangeListeners.getListeners();

		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				if (logger.isDebugEnabled()) {
					logger.debug("removeArticles: calling listeners with ArticleChangeEvent:"); //$NON-NLS-1$
					logger.debug("removeArticles:     ArticleChangeEvent.deletedArticles (" + articleChangeEvent.getDeletedArticles().size() + "):"); //$NON-NLS-1$ //$NON-NLS-2$
					for (Article article : articleChangeEvent.getDeletedArticles())
						logger.debug("removeArticles:       - " + article.getPrimaryKey()); //$NON-NLS-1$
				}

				for (int i = 0; i < listeners.length; i++)
					((ArticleChangeListener) listeners[i]).articlesChanged(articleChangeEvent);
			}
		});

		if (logger.isDebugEnabled())
			logger.debug("removeArticles: end"); //$NON-NLS-1$
	}

	private ListenerList articleCreateListeners = new ListenerList();

	/**
	 * Adds a listener that will be notified on the UI Thread,
	 * whenever an Article is added to the ArticleContainer.
	 *
	 * @param articleCreateListener The listener to be added.
	 */
	public void addArticleCreateListener(ArticleCreateListener articleCreateListener)
	{
		articleCreateListeners.add(articleCreateListener);
	}
	public void removeArticleCreateListener(ArticleCreateListener articleCreateListener)
	{
		articleCreateListeners.remove(articleCreateListener);
	}

	private ListenerList articleChangeListeners = new ListenerList();

	/**
	 * Adds a listener that will be notified on the UI Thread,
	 * whenever an Article is changed or removed from the ArticleContainer.
	 *
	 * @param articleChangeListener The listener to be added.
	 */
	public void addArticleChangeListener(ArticleChangeListener articleChangeListener)
	{
		articleChangeListeners.add(articleChangeListener);
	}
	public void removeArticleChangeListener(ArticleChangeListener articleChangeListener)
	{
		articleChangeListeners.remove(articleChangeListener);
	}

	@Override
	protected void _addArticleCarrier(ArticleCarrier articleCarrier)
	{
		super._addArticleCarrier(articleCarrier);
		Cache.sharedInstance().put(null, articleCarrier.getArticle(), fetchGroupsArticle, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT);
	}

	@Override
	protected void _removeArticle(Article article)
	{
		super._removeArticle(article);
	}
}
