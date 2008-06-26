package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ui.QuickSalePerspective;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ArticleContainerQuickSaleEditor
extends EditorPart
implements IArticleContainerEditor
{
	public static final String ID_EDITOR = ArticleContainerQuickSaleEditor.class.getName();
	
	private ArticleContainerQuickSaleEditorComposite articleContainerQuickSaleEditorComposite;
	public ArticleContainerEditorComposite getArticleContainerEditorComposite() {
		return articleContainerQuickSaleEditorComposite.getArticleContainerEditorComposite();
	}
	
	private ArticleContainerEditorInput input;
	
	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException
	{
		if (!(input instanceof ArticleContainerEditorInput))
			throw new PartInitException("Invalid Input: Must be an instance of ArticleContainerEditorInput! But is: " + (input == null ? null : input.getClass().getName())); //$NON-NLS-1$
		
		this.input = (ArticleContainerEditorInput) input;

		setSite(site);
		setInput(input);

		setPartName(input.getName());
		ImageDescriptor img = input.getImageDescriptor();
		if (img != null)
			setTitleImage(img.createImage());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		articleContainerQuickSaleEditorComposite = new ArticleContainerQuickSaleEditorComposite(getSite(), parent, input);
		RCPUtil.getActiveWorkbenchPage().addPartListener(quickSaleEditorListener);
	}

	@Override
	public void setFocus() {

	}

	private IPartListener quickSaleEditorListener = new IPartListener()
	{
		public void partClosed(IWorkbenchPart part)
		{
			if (part.equals(ArticleContainerQuickSaleEditor.this)) {
				if (RCPUtil.getActiveWorkbenchPage() != null &&
						Login.sharedInstance().getLoginState() == LoginState.LOGGED_IN) 
				{
					QuickSalePerspective.checkOrderOpen(RCPUtil.getActivePerspectiveID());
				}
			}
		}
		public void partOpened(IWorkbenchPart part) {
		}
		public void partDeactivated(IWorkbenchPart part) {
		}
		public void partBroughtToTop(IWorkbenchPart part) {
		}
		public void partActivated(IWorkbenchPart part) {
		}
	};
}
