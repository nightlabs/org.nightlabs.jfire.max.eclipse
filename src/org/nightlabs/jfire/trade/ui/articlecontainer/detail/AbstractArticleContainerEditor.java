/**
 * 
 */
package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;
import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.notification.NotificationEvent;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public abstract class AbstractArticleContainerEditor 
extends EditorPart 
implements IArticleContainerEditor 
{
	private static final Logger logger = Logger.getLogger(AbstractArticleContainerEditor.class);
	private static int numEditorsOpen = 0;
	private static boolean partInitialized = false;
	
	public AbstractArticleContainerEditor() {
		registerActivatePartListener();
	}

	private static synchronized void registerActivatePartListener() {
		if (partInitialized)
			return;

		RCPUtil.getActiveWorkbenchPage().addPartListener(partListener);
		partInitialized = true;
	}

	private static ActivateListener partListener = new ActivateListener();

	private static class ActivateListener implements IPartListener {

		private void fireEvent(AbstractArticleContainerEditor articleContainerEditor) {

			ArticleContainerID articleContainerID = null;

			if (articleContainerEditor != null && 
					articleContainerEditor.getEditorInput() != null) 
			{
				ArticleContainerEditorInput input = (ArticleContainerEditorInput) articleContainerEditor.getEditorInput();
				articleContainerID = input.getArticleContainerID();
			}
			if (logger.isDebugEnabled())
				logger.debug("ActivateListener.fireEvent: entered for " + articleContainerID); //$NON-NLS-1$

			NotificationEvent event = new NotificationEvent(this,
					TradePlugin.ZONE_SALE, articleContainerID,
					ArticleContainer.class);

			SelectionManager.sharedInstance().notify(event);
		}

		public void partActivated(final IWorkbenchPart part) {
			if (part instanceof AbstractArticleContainerEditor)
				fireEvent((AbstractArticleContainerEditor)part);
		}

		@Override
		public void partBroughtToTop(final IWorkbenchPart part) {
		}

		@Override
		public void partClosed(final IWorkbenchPart part) 
		{
			if(Login.sharedInstance().getLoginState() == LoginState.ABOUT_TO_LOG_OUT)
			{
				if (RCPUtil.getActiveWorkbenchPage() != null)
					RCPUtil.getActiveWorkbenchPage().removePartListener(partListener);
				
				partInitialized = false;
				return;
			}

			if (!(part instanceof AbstractArticleContainerEditor))
				return;

			AbstractArticleContainerEditor articleContainerEditor = (AbstractArticleContainerEditor) part;

//			if (!isRightEditorInstance(part))
//				return;
//
//			AbstractArticleContainerEditor articleContainerEditor = getArticleContainerEditorClass().cast(part);
			
			if (numEditorsOpen <= 0)
				throw new IllegalStateException(
						"Closing more editors as have been opened!!! How can this happen! AbstractArticleContainerEditor.editorInput: " //$NON-NLS-1$
						+ articleContainerEditor.getEditorInput());

			--numEditorsOpen;

			if (numEditorsOpen == 0  && RCPUtil.getActiveWorkbenchPage() != null) {
				// should only be fired when this was the last active editor in the current perspective
				// so an additional map with perspectiveID2numEditorsOpen should be added 
				fireEvent(null);

				if (RCPUtil.getActiveWorkbenchPage() != null)
					RCPUtil.getActiveWorkbenchPage().removePartListener(partListener);

				partInitialized = false;
			}
		}

		@Override
		public void partDeactivated(final IWorkbenchPart part) {
		}

		@Override
		public void partOpened(final IWorkbenchPart part) {
			if (!(part instanceof AbstractArticleContainerEditor))
				return;

			//if (logger.isDebugEnabled())
			logger.debug("Part Opened !!!!"); //$NON-NLS-1$

			numEditorsOpen++;
			registerActivatePartListener();
			//	fireEvent((ArticleContainerEditor) part);
		}
	}
		
	/**
	 * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
	}
	
	/**
	 * @see org.eclipse.ui.ISaveablePart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	/**
	 * @see org.eclipse.ui.IWorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}
	
}
