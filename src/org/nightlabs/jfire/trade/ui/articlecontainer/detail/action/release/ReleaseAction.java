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

package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.release;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.dao.ArticleDAO;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.articlecontainer.ArticleUtil;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleSelection;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ClientArticleSegmentGroupSet;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.ArticleEditAction;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

public class ReleaseAction extends ArticleEditAction
{
	@Override
	public boolean calculateVisible()
	{
		return true; // It must be always visible, because there might be a reversing article in the invoice/deliverynote
	}

	private List<Article> articles = null;

	protected static final String[] FETCH_GROUPS_ARTICLE_DELIVERY_NOTE_ID = new String[] {
		FetchPlan.DEFAULT,
		Article.FETCH_GROUP_DELIVERY_NOTE_ID
	};

	@Override
	public boolean calculateEnabled(Set<ArticleSelection> articleSelections)
	{
		this.articles = null;
		List<Article> articles = new ArrayList<Article>();
		for (ArticleSelection articleSelection : articleSelections) {

//			SegmentEdit segmentEdit = articleSelection.getArticleEdit().getSegmentEdit();
//			String articleContainerClass = segmentEdit.getArticleContainerClass();
//			ArticleContainer articleContainer = segmentEdit.getArticleContainer();

			for (Article article : articleSelection.getSelectedArticles()) {
				if (article.isAllocationPending() || article.isReleasePending() || !article.isAllocated())
					return false;

				if (article.isReversing()) {
					// reversing article
					Article reversingArticle = article;

					// If the reversed article is in a DeliveryNote, both - reversed and reversing - articles must be in a DeliveryNote.
					// The DeliveryNotes must be booked!

					Article reversedArticle = ArticleDAO.sharedInstance().getArticle(
							reversingArticle.getReversedArticleID(),
							FETCH_GROUPS_ARTICLE_DELIVERY_NOTE_ID,
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new NullProgressMonitor());
					if (reversedArticle.getDeliveryNoteID() != null) {
						if (reversingArticle.getDeliveryNoteID() == null)
							return false;

						if (!ArticleUtil.isDeliveryNoteBooked(reversedArticle, new NullProgressMonitor())) // TODO real progress monitor
							return false;

						if (!ArticleUtil.isDeliveryNoteBooked(reversingArticle, new NullProgressMonitor())) // TODO real progress monitor
							return false;
					}

				}
				else {
					// normal article (non-reversing)
					if (ArticleUtil.isOfferFinalized(article, new NullProgressMonitor())) // TODO real progress monitor
						return false;
				}

				articles.add(article);
			}
		}
		this.articles = articles;
		return true;
	}

	@Override
	public void run()
	{
		final ClientArticleSegmentGroupSet clientArticleSegmentGroupSet = getArticleContainerEdit().getArticleSegmentGroupSet();
//		final Collection<Article> oldArticles = articles;
		final Collection<ArticleID> articleIDs = NLJDOHelper.getObjectIDSet(articles);
//		final Display display = Display.getCurrent();

		Job job = new Job(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.release.ReleaseAction.job.name.releasingArticles")) { //$NON-NLS-1$
			@Override
			protected IStatus run(ProgressMonitor monitor) throws Exception
			{
				final List<Article> newArticles = ArticleDAO.sharedInstance().releaseArticles(
						articleIDs, false,
						true,
						clientArticleSegmentGroupSet.getFetchGroupsArticle(),
						clientArticleSegmentGroupSet.getMaxFetchDepthArticle(),
						new NullProgressMonitor()
				);

				Collection<ArticleID> deletedArticleIDs = Collections.emptyList();
				clientArticleSegmentGroupSet.updateArticles(deletedArticleIDs, newArticles);

//				display.asyncExec(new Runnable() {
//					public void run() {
//						if (articles == oldArticles) // prevent articles to be overwritten with older data - do it only, if it's not yet changed!
//							articles = newArticles;
//					}
//				});

				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}
}
