package org.nightlabs.jfire.voucher.ui.articlecontainer.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jfire.trade.Article;

public class ArticleEditComposite
extends XComposite
{
	private ArticleEdit articleEdit;
	private ArticleTable articleTable;

	public ArticleEditComposite(Composite parent, ArticleEdit _articleEdit)
	{
		super(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		this.articleEdit = _articleEdit;

		articleTable = new ArticleTable(this, SWT.NONE, articleEdit);
		articleTable.setInput(new Object());
		articleEdit.getSegmentEdit().createArticleEditContextMenu(articleTable);

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
	}

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
		articleTable.refresh();
	}
}
