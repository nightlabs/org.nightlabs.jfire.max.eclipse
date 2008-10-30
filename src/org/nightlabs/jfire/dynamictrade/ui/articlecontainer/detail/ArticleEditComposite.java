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

package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.Article;

/**
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class ArticleEditComposite extends XComposite
{
//	private IWorkbenchPartSite site;
	private ArticleEdit articleEdit;
	private ArticleTable articleTable;

	/**
	 * @param parent
	 */
	public ArticleEditComposite(/* IWorkbenchPartSite site, */ Composite parent, ArticleEdit _articleEdit)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
//		this.site = site;
		this.articleEdit = _articleEdit;

		articleTable = new ArticleTable(this, SWT.NONE, articleEdit);
		articleTable.setInput(new Object());
		articleEdit.getSegmentEdit().createArticleEditContextMenu(articleTable);
//		hookContextMenu();

		articleTable.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event)
			{
				if (!articleEdit.isCtrlKeyDown()) {
					ignoreSetSelectedArticles = true;
					try {
						articleEdit.getSegmentEdit().setSelectedArticles(EMPTY_SET_ARTICLE);
					} finally {
						ignoreSetSelectedArticles = false;
					}
				} // if (!RCPUtil.isKeyDown(RCPUtil.KEY_CTRL)) {

				articleEdit.fireArticleEditArticleSelectionEvent();
			}
		});

		articleTable.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event)
			{

				if(articleEdit.isNonOrderArticleContainerFinilized())
					return;
				Collection<Article> c = articleTable.getSelectedElements();
				if (c.isEmpty())
					return;

				Article article = c.iterator().next();
				new ArticleEditDialog(getShell(), articleEdit, article).open();
			}
		});
	}

//	private void hookContextMenu() {
//	MenuManager menuMgr = new MenuManager("#PopupMenu");
//	menuMgr.setRemoveAllWhenShown(true);
//	menuMgr.addMenuListener(new IMenuListener() {
//	public void menuAboutToShow(IMenuManager manager) {
//	fillContextMenu(manager);
//	}
//	});
//	Menu menu = menuMgr.createContextMenu(articleTable);
//	articleTable.setMenu(menu);
//	// TODO we should somehow register this menu with a logical ID to allow other plugins to
//	// add some logic.
////	site.registerContextMenu(menuMgr, articleTable);
//	}

//	private void fillContextMenu(IMenuManager manager)
//	{
//	boolean hasRemovableItems = false;
//	boolean hasNonRemovableItems = false;

//	if (articleEdit.isInOffer()) {
//	Offer offer = (Offer)articleEdit.getSegmentEdit().getArticleContainer();
//	if (offer.isFinalized())
//	hasNonRemovableItems = true;

//	hasRemovableItems = !articleTable.getSelection().isEmpty();
//	}
//	else {
//	for (Iterator it = ((IStructuredSelection)articleTable.getSelection()).iterator(); it.hasNext(); ) {
//	Article article = (Article) it.next();
//	if (article.getOffer().isFinalized())
//	hasNonRemovableItems = true;
//	else
//	hasRemovableItems = true;
//	}
//	}

//	removeSelectedArticlesAction.setText("Remove Selected Articles");
//	manager.add(removeSelectedArticlesAction);
//	removeSelectedArticlesAction.setEnabled(hasRemovableItems && !hasNonRemovableItems);

//	//	manager.add(action2);
//	//	manager.add(new Separator());
////	manager.add(createProductAction);

////	manager.add(new TestAction());

////	drillDownAdapter.addNavigationActions(manager);
//	// Other plug-ins can contribute their actions here
//	manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//	}

//	private Action removeSelectedArticlesAction = new Action() {
//	public void run() {
//	IStructuredSelection sel = (IStructuredSelection) articleTable.getSelection();
//	if (sel.isEmpty())
//	return;

//	for (Iterator it = sel.iterator(); it.hasNext(); ) {
//	Article article = (Article) it.next();
//	System.out.println(article.getPrimaryKey());
//	}
//	}
//	};

	protected static final Set<Article> EMPTY_SET_ARTICLE = Collections.unmodifiableSet(new HashSet<Article>(0));

	/**
	 * This method is called by {@link ArticleEdit#getSelectedArticles()}.
	 *
	 * @return Returns all selected {@link Article}s.
	 */
	protected Set<Article> getSelectedArticles()
	{
		IStructuredSelection sel = (IStructuredSelection) articleTable.getSelection();
		if (sel.isEmpty())
			return EMPTY_SET_ARTICLE;

		Set<Article> res = new HashSet<Article>();
		for (Iterator<Article> it = sel.iterator(); it.hasNext(); ) {
			Article article = it.next();
			res.add(article);
		}
		return res;
	}

	/**
	 * This flag is set when a selection has occured HERE in order to prevent draw-backs.
	 * See the <code>ISelectionChangedListener</code> added by {@link #ArticleEditComposite(Composite, ArticleEdit)}.
	 */
	private boolean ignoreSetSelectedArticles = false;

	/**
	 * This method is called by {@link ArticleEdit#setSelectedArticles(Set)}.
	 */
	protected Set<? extends Article> setSelectedArticles(Set<? extends Article> articles)
	{
		if (ignoreSetSelectedArticles)
			return articles;

		articleTable.setSelection(new StructuredSelection(new ArrayList<Article>(articles)));
		for (Iterator<Article> it = getSelectedArticles().iterator(); it.hasNext(); ) {
			articles.remove(it.next());
		}
		return articles;
	}

	public void refreshUI()
	{
//		articleTable.setInput(new Object());
		articleTable.refresh();
	}

}
