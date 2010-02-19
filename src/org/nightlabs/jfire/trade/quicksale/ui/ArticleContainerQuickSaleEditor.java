package org.nightlabs.jfire.trade.quicksale.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.part.PartAdapter;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.AbstractArticleContainerEditor;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEdit;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorInput;
import org.nightlabs.jfire.trade.ui.articlecontainer.detail.ArticleContainerEditorPage;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ArticleContainerQuickSaleEditor
extends AbstractArticleContainerEditor
{
	public static final String ID_EDITOR = ArticleContainerQuickSaleEditor.class.getName();
	
	private ArticleContainerEdit articleContainerEdit;
	@Override
	public ArticleContainerEdit getArticleContainerEdit() {
		if (articleContainerEdit == null) {
			IFormPage page = findPage(ArticleContainerEditorPage.PAGE_ID);
			if (page instanceof ArticleContainerQuickSaleEditorPage)
				articleContainerEdit = ((ArticleContainerQuickSaleEditorPage) page).getArticleContainerEdit();
		}
		return articleContainerEdit;
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException
	{
		super.init(site, input);
		
		if (!(input instanceof ArticleContainerEditorInput))
			throw new PartInitException("Invalid Input: Must be an instance of ArticleContainerEditorInput! But is: " + (input == null ? null : input.getClass().getName())); //$NON-NLS-1$
		
		setPartName(input.getName());
		ImageDescriptor img = input.getImageDescriptor();
		if (img != null)
			setTitleImage(img.createImage());
		RCPUtil.getActiveWorkbenchPage().addPartListener(quickSaleEditorListener);
	}

	private IPartListener2 quickSaleEditorListener = new PartAdapter()
	{
		public void partClosed(IWorkbenchPartReference ref)
		{
			if (ref.getPart(true).equals(ArticleContainerQuickSaleEditor.this)) {
				if (RCPUtil.getActiveWorkbenchPage() != null &&
						Login.sharedInstance().getLoginState() == LoginState.LOGGED_IN) 
				{
					QuickSalePerspective.checkOrderOpen(RCPUtil.getActivePerspectiveID());
				}
				if (RCPUtil.getActiveWorkbenchPage() != null)
					RCPUtil.getActiveWorkbenchPage().removePartListener(this);
			}
		}
	};

}
