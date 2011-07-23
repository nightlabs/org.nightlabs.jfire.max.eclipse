package org.nightlabs.jfire.trade.ui.articlecontainer.detail;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.forms.editor.IFormPage;
import org.nightlabs.base.ui.entity.editor.EntityEditor;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageControllerModifyEvent;
import org.nightlabs.base.ui.entity.editor.EntityEditorPageSettings;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageController;
import org.nightlabs.base.ui.entity.editor.IEntityEditorPageControllerModifyListener;
import org.nightlabs.base.ui.login.LoginState;
import org.nightlabs.base.ui.notification.SelectionManager;
import org.nightlabs.base.ui.part.PartAdapter;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.jdo.JDOObjectID2PCClassMap;
import org.nightlabs.jfire.base.login.ui.Login;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.id.ArticleContainerID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.notification.NotificationEvent;

/**
 * Abstract base class for editors for {@link ArticleContainer}s.
 * It extends {@link EntityEditor} and can therefore be enriched with
 * custom pages using the <code>org.nightlabs.base.ui.entityEditor</code> extension point.
 * Additionally page-factories can be registered to as extensions to the
 * extension-point <code>org.nightlabs.jfire.trade.ui.articleContainerEditorPageFactory</code>
 * where different pages can be defined for specific implementations of {@link ArticleContainer}.
 * <p>
 * Note, however, that this Editor relies on the fact that one
 * Page of type {@link ArticleContainerEditorPage} is added using the page-id {@link ArticleContainerEditorPage#PAGE_ID}.
 * </p>
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public abstract class AbstractArticleContainerEditor
extends EntityEditor
implements IArticleContainerEditor
{
	private static final Logger logger = Logger.getLogger(AbstractArticleContainerEditor.class);
	private static int numEditorsOpen = 0;
	private static boolean partInitialized = false;

	private ArticleContainerEdit articleContainerEdit;

	public AbstractArticleContainerEditor() {
		registerActivatePartListener();
	}

	@Override
	protected void addPages() {
		super.addPages();
		// add a listener to the controller that will set the part name and image
		final IEntityEditorPageController controller = getController().getPageController(getArticleContainerEditorPage());
		final IEntityEditorPageControllerModifyListener listener = new IEntityEditorPageControllerModifyListener() {
			@Override
			public void controllerObjectModified(EntityEditorPageControllerModifyEvent modifyEvent) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						setPartName("Lala"); //$NON-NLS-1$
						setTitleImage(null);
//						controller.removeModifyListener(listener);
					}
				});
			}
		};
		controller.addModifyListener(listener);
	}

	private ArticleContainerEditorPage articleContainerEditorPage;

	protected ArticleContainerEditorPage getArticleContainerEditorPage() {
		if (articleContainerEditorPage == null) {
			IFormPage page = findPage(ArticleContainerEditorPage.PAGE_ID);
			if (page instanceof ArticleContainerEditorPage) {
				articleContainerEditorPage = (ArticleContainerEditorPage) page;
			}
		}
		return articleContainerEditorPage;
	}
	/**
	 * Overrides and returns the result of the processing of extensions to the
	 * entityEditor and articleContainerEditorPageFactory extension-points.
	 */
	@Override
	protected List<EntityEditorPageSettings> getPageSettingsOrdered() {
		ArticleContainerID articleContainerID = getArticleContainerEditorInput().getArticleContainerID();
		if (articleContainerID == null)
			throw new IllegalStateException("No articleContainerID was set"); //$NON-NLS-1$
		Class<?> articleContainerEditorClass = JDOObjectID2PCClassMap.sharedInstance().getPersistenceCapableClass(articleContainerID);
		if (articleContainerEditorClass == null)
			throw new IllegalStateException("The class of the ArticleContainer with id " + articleContainerID + " could not be determined"); //$NON-NLS-1$ //$NON-NLS-2$
		return ArticleContainerEditorPageFactoryRegistry.sharedInstance().getPagesSettingsOrdered(
				getEditorID(), articleContainerEditorClass);
	}

	/**
	 * Gets the {@link ArticleContainerEdit} from the {@link ArticleContainerEditorPage}
	 * registered to this editor. The page should be added with the page-id {@link ArticleContainerEditorPage#PAGE_ID}.
	 */
	@Override
	public ArticleContainerEdit getArticleContainerEdit() {
		if (articleContainerEdit == null) {
			articleContainerEdit = getArticleContainerEditorPage().getArticleContainerEdit();
		}
		return articleContainerEdit;
	}

	/**
	 * @return This editors input casted to an {@link ArticleContainerEditorInput}.
	 */
	protected ArticleContainerEditorInput getArticleContainerEditorInput() {
		return (ArticleContainerEditorInput) getEditorInput();
	}

	private static synchronized void registerActivatePartListener() {
		if (partInitialized)
			return;

		RCPUtil.getActiveWorkbenchPage().addPartListener(partListener);
		partInitialized = true;
	}

	private static ActivateListener partListener = new ActivateListener();

	private static class ActivateListener extends PartAdapter {

		private void fireEvent(AbstractArticleContainerEditor articleContainerEditor) {

			ArticleContainerID articleContainerID = null;

			if (articleContainerEditor != null &&
					articleContainerEditor.getEditorInput() != null)
			{
				articleContainerID = articleContainerEditor.getArticleContainerEditorInput().getArticleContainerID();
			}
			if (logger.isDebugEnabled())
				logger.debug("ActivateListener.fireEvent: entered for " + articleContainerID); //$NON-NLS-1$

			NotificationEvent event = new NotificationEvent(this,
					TradePlugin.ZONE_SALE, articleContainerID,
					ArticleContainer.class);

			SelectionManager.sharedInstance().notify(event);
		}

		@Override
		public void partActivated(IWorkbenchPartReference ref) {
			IWorkbenchPart part = ref.getPart(false);
			if (part instanceof AbstractArticleContainerEditor)
				fireEvent((AbstractArticleContainerEditor)part);
		}

		@Override
		public void partClosed(final IWorkbenchPartReference ref)
		{
			IWorkbenchPart part = ref.getPart(false);
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
		public void partOpened(final IWorkbenchPartReference ref) {
			IWorkbenchPart part = ref.getPart(false);
			if (!(part instanceof AbstractArticleContainerEditor))
				return;

			//if (logger.isDebugEnabled())
			logger.debug("Part Opened !!!!"); //$NON-NLS-1$

			numEditorsOpen++;
			registerActivatePartListener();
			//	fireEvent((ArticleContainerEditor) part);
		}
	}
}
