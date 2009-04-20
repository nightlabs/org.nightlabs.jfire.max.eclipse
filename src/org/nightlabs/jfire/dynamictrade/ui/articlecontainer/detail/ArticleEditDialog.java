package org.nightlabs.jfire.dynamictrade.ui.articlecontainer.detail;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.nightlabs.eclipse.ui.dialog.ResizableTrayDialog;
import org.nightlabs.jfire.dynamictrade.ui.resource.Messages;
import org.nightlabs.jfire.trade.Article;

public class ArticleEditDialog
extends ResizableTrayDialog
{
	private ArticleEditDialogComposite articleEditDialogComposite;
	private ArticleEdit articleEdit;
	private Article article;

	public ArticleEditDialog(Shell parentShell, ArticleEdit articleEdit, Article article)
	{
		super(parentShell, Messages.RESOURCE_BUNDLE);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		this.articleEdit = articleEdit;
		this.article = article;
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite area = (Composite) super.createDialogArea(parent);
		articleEditDialogComposite = new ArticleEditDialogComposite(
				area, articleEdit.getSegmentEdit().getArticleContainer(), article);
		return articleEditDialogComposite;
	}
	
	@Override
	protected void okPressed()
	{
		if(articleEditDialogComposite.submit())
		super.okPressed();
	}
}
