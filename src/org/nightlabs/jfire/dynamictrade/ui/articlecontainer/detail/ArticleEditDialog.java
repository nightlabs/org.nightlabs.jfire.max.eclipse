package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.base.ui.dialog.CenteredDialog;
import org.nightlabs.jfire.dynamictrade.store.DynamicProductType;
import org.nightlabs.jfire.trade.Article;

public class ArticleEditDialog
extends CenteredDialog
{
	private ArticleEditDialogComposite articleEditDialogComposite;
	private ArticleEdit articleEdit;
	private Article article;

	public ArticleEditDialog(Shell parentShell, ArticleEdit articleEdit, Article article)
	{
		super(parentShell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.articleEdit = articleEdit;
		this.article = article;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite area = (Composite) super.createDialogArea(parent);
		articleEditDialogComposite = new ArticleEditDialogComposite(
				area, articleEdit.getSegmentEdit().getArticleContainer(), (DynamicProductType) article.getProductType());

		articleEditDialogComposite.setArticle(article);
		return articleEditDialogComposite;
	}

	@Override
	protected Point getInitialSize()
	{
		return new Point(800, 400);
	}

	@Override
	protected void okPressed()
	{
		articleEditDialogComposite.submit();
		super.okPressed();
	}
}
